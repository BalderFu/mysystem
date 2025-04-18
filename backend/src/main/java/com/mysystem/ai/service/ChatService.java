package com.mysystem.ai.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysystem.ai.entity.HistorySession;
import com.mysystem.ai.entity.LocalChat;
import com.mysystem.ai.mapper.HistorySessionMapper;
import com.mysystem.ai.mapper.LocalChatMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private static final String SYSTEM_PROMPT = "你是一个只使用中文回答问题的AI助手。无论用户使用哪种语言提问，都只用中文回答。";
    private final LocalChatMapper localChatMapper;
    private final HistorySessionMapper historySessionMapper;
    private final OllamaChatModel chatModel;

    public Long createSession(String title) {
        HistorySession entity = new HistorySession();
        entity.setTitle(title);
        historySessionMapper.insert(entity);
        return entity.getId();
    }

    public Page<HistorySession> chatHistoryPage(Long curr, Long limit) {
        Page<HistorySession> page = new Page<>(curr, limit);
        return historySessionMapper.selectPage(page, new LambdaQueryWrapper<HistorySession>().orderByDesc(HistorySession::getCreateTime));
    }

    public Page<LocalChat> localChaPage(Long curr, Long limit, Long sessionId) {
        Page<LocalChat> page = new Page<>(curr, limit);
        Page<LocalChat> localChatPage = localChatMapper.selectPage(page, new LambdaQueryWrapper<LocalChat>().eq(LocalChat::getSessionId, sessionId).orderByDesc(LocalChat::getCreateTime));
        localChatPage.getRecords().forEach(localChat -> localChat.setReply(localChat.getThink() + localChat.getReply()));
        return localChatPage;
    }


    public boolean existSession(Long sessionId) {
        return historySessionMapper.selectById(sessionId) != null;
    }

    public boolean existSession(String title) {
        return historySessionMapper.selectCount(new LambdaQueryWrapper<HistorySession>().eq(HistorySession::getTitle, title)) > 0;
    }

    public Long createSession(String prompt, Long session) {
        Long sessionId = session;
        if (null == sessionId && (sessionId = sessionId(prompt)) == null) {
            sessionId = createSession(prompt);
        }
        if (ObjectUtil.isEmpty(sessionId)) {
            throw new RuntimeException("会话创建失败");
        }
        return sessionId;
    }


    public String chat(String prompt, Long sessionId) {
        if (ObjectUtil.isEmpty(sessionId)) {
            throw new RuntimeException("会话为空");
        }
        String response = chatModel.call(this.readFullPrompt(sessionId, prompt).toArray(Message[]::new));
        log.info("【Ask:{}, response:{}】", prompt, response);
        this.saveChatRecord(prompt, response, sessionId);
        return response;
    }

    private void saveChatRecord(String prompt, String response, Long sessionId) {
        LocalChat entity = new LocalChat();
        entity.setUserId(StpUtil.getLoginIdAsLong());
        entity.setSessionId(sessionId);
        entity.setAsk(prompt);
        entity.setReply(response);
        List<String> split = StrUtil.split(response, "</think>");
        if (CollectionUtil.isNotEmpty(split)) {
            entity.setThink(split.get(0) + "</think>");
            entity.setReply(split.get(1));
        }
        localChatMapper.insert(entity);
    }


    private List<Message> readFullPrompt(Long sessionId, String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_PROMPT));
        if (ObjectUtil.isEmpty(sessionId)) {
            messages.add(new UserMessage(prompt));
            return messages;
        }
        localChatMapper.selectList(new LambdaQueryWrapper<LocalChat>().eq(LocalChat::getSessionId, sessionId).orderByAsc(LocalChat::getCreateTime)).forEach(chat -> {
            messages.add(new UserMessage(chat.getAsk()));
            messages.add(new AssistantMessage(chat.getReply()));
        });
        messages.add(new UserMessage(prompt));
        return messages;
    }

    private Long sessionId(String prompt) {
        HistorySession session = historySessionMapper.selectOne(new LambdaQueryWrapper<HistorySession>().eq(HistorySession::getTitle, prompt).last("limit 1"));
        return session != null ? session.getId() : null;
    }

}
