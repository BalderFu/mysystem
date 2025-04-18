package com.mysystem.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysystem.ai.entity.HistorySession;
import com.mysystem.ai.entity.LocalChat;
import com.mysystem.ai.model.Result;
import com.mysystem.ai.model.SendMsgReq;
import com.mysystem.ai.service.ChatService;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @Autowired
    private OllamaChatModel ollamaChatModel;

    @GetMapping(value = "chat")
    public Object ollama(@RequestParam(value = "msg") String msg) {
        String called = ollamaChatModel.call(msg);
        System.out.println(called);
        return called;
    }

    @GetMapping("/sessionPage")
    public Result<Page<HistorySession>> sessionPage(@RequestParam("page") Long page, @RequestParam("size") Long limit) {
        return Result.success(chatService.chatHistoryPage(page, limit));
    }

    @GetMapping("/chatPage")
    public Result<Page<LocalChat>> chatPage(@RequestParam("page") Long page, @RequestParam("size") Long limit,
                                            @RequestParam("sessionId")Long sessionId) {
        return Result.success(chatService.localChaPage(page, limit, sessionId));
    }

    @PostMapping("/send")
    public Result<String> chatPage(@RequestBody SendMsgReq sendMsg) {
        return Result.success(chatService.chat(sendMsg.getContent(), sendMsg.getSessionId()));
    }

    @PostMapping("/createSession")
    public Result<Long> createSession(@RequestBody SendMsgReq sendMsg) {
        return Result.success(chatService.createSession(sendMsg.getContent(), sendMsg.getSessionId()));
    }
}
