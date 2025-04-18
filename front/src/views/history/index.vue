<template>
  <div class="chat-container">
    <div class="chat-header">
      <div>{{ chatTitle }}</div>
    </div>

    <div class="chat-messages" ref="chatMessages">
      <!-- 消息列表 -->
      <template v-if="messages.length > 0">
        <div v-for="(message, index) in messages" :key="index" 
             :class="['message-item', message.type === 'ai' ? 'ai-message' : 'user-message']">
          <!-- AI消息 -->
          <template v-if="message.type === 'ai'">
            <div class="message-content">
              <!-- 新增：可折叠的思考容器 -->
              <div class="think-container" v-if="message.thinkContent">
                <div class="think-header" @click="toggleThink(message)">
                  <i class="el-icon-magic-stick"></i> <!-- 改回图标 -->
                  <span>已深度思考</span> <!-- 暂时用静态文本 -->
                  <i :class="message.isThinkExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i> <!-- 箭头 -->
                </div>
                <div class="think-content" v-show="message.isThinkExpanded">
                  {{ message.thinkContent }}
                </div>
              </div>
              
              <!-- 显示回复内容 -->
              <div class="message-text" v-if="message.replyContent">
                {{ message.replyContent }}
              </div>
            </div>
          </template>
          
          <!-- 用户消息 -->
          <template v-else>
            <div class="message-content">
              <div class="message-text">
                {{ message.content }}
              </div>
            </div>
          </template>
        </div>
      </template>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <i class="el-icon-loading"></i>
        <span>加载历史记录中...</span>
      </div>
      
      <!-- 无消息提示 -->
      <div v-if="!loading && messages.length === 0" class="empty-message">
        <p>暂无消息记录</p>
      </div>
    </div>

    <!-- 聊天输入框 - 简化底部工具栏 -->
    <div class="chat-input-container">
      <div class="input-wrapper">
        <el-input
          type="textarea"
          v-model="messageInput"
          placeholder="给 DeepSeek 发送消息"
          :rows="1"
          @keyup.enter.native.prevent="sendMessage"
          resize="none"
          autosize
        ></el-input>
      </div>
      
      <el-button class="send-btn" type="primary" icon="el-icon-s-promotion" 
        @click="sendMessage">发送消息</el-button>
    </div>
  </div>
</template>

<script>
import { getChatSessions } from "@/utils/inputs";
import create from "@/utils/request";

// 定义API接口函数
const chatPage = function(params) {
  return create({
    url: "/chat/chatPage",
    method: "get",
    params: params
  });
};

const createSession = function(data) {
  return create({
    url: "/chat/createSession",
    method: "post",
    data: data
  });
};

export default {
  name: 'ChatHistory',
  data() {
    return {
      chatTitle: '智能助手',
      messageInput: '',
      isThinking: false,
      messages: [],
      sessionId: null,
      sessionDetail: null,
      loading: false
    }
  },
  methods: {
    // 初始化消息列表
    initMessages() {
      // 清空现有消息
      this.messages = [];
      this.loading = true;
      this.chatTitle = '加载中...'; // 设置临时标题

      // 检查 sessionId 是否有效
      if (!this.sessionId) {
        console.error("initMessages 错误: 无效的 sessionId");
        this.loading = false;
        this.chatTitle = '错误';
        this.$message.error('无法加载聊天记录：无效的会话ID');
        return;
      }

      // 获取聊天历史记录
      this.getChatHistory().then(response => {
        this.loading = false;
        console.log("对话历史记录响应：", response);

        if (response && response.code === 200 && response.data && response.data.records) {
          const records = response.data.records;
          console.log("获取到聊天记录数量：", records.length);

          // 设置实际标题 (暂时用默认的)
          this.chatTitle = '聊天记录'; // TODO: 修复标题获取

          if (records.length > 0) {
            // 使用 flatMap 将每个 record 转换为用户和AI两条消息
            this.messages = records.flatMap(record => {
              const messages = [];
              // 使用 record.createTime 作为基准时间戳
              const timestamp = new Date(record.createTime || Date.now()).getTime();

              // 1. 添加用户消息 (ask)
              if (record.ask) {
                messages.push({
                  type: 'user',
                  content: record.ask,
                  timestamp: timestamp - 1 // 用户消息时间戳稍微提前一点
                });
              }

              // 2. 处理并添加 AI 消息 (reply)
              if (record.reply) {
                let thinkContent = null;
                let replyContent = '';
                const thinkRegex = /<think>(.*?)<\/think>(.*)/s;
                const match = record.reply.match(thinkRegex);

                if (match && match.length === 3) {
                  thinkContent = match[1].trim();
                  replyContent = match[2].trim();
                } else {
                  replyContent = record.reply.trim();
                }
                
                // 确保至少有回复或思考内容才添加 AI 消息
                if (replyContent || thinkContent) {
                  messages.push({
                    type: 'ai',
                    thinkContent: thinkContent,
                    replyContent: replyContent,
                    timestamp: timestamp,
                    isThinkExpanded: false // 为历史消息也添加此属性
                  });
                }
              }
              return messages;
            });

            // 按时间戳排序所有消息
            this.messages.sort((a, b) => a.timestamp - b.timestamp);

          } else {
            // 没有历史记录
            console.log('该会话没有历史消息记录');
            this.chatTitle = '新对话'; // 或者根据情况设置
          }
        } else {
          // 响应格式不正确或获取失败
          console.error("获取聊天记录失败: 响应格式不正确或失败", response);
          this.$message.error("获取聊天记录失败");
          this.chatTitle = '加载失败';
        }
      }).catch(error => {
        this.loading = false;
        console.error("获取聊天记录异常:", error);
        this.$message.error("加载聊天记录时出错: " + (error.message || "未知网络错误"));
        this.chatTitle = '加载异常';
      }).finally(() => {
        // 确保滚动到底部显示最新消息
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      });
    },
    
    // 获取会话详情
    getSessionDetail() {
      return create({
        url: "/chat/sessionPage",
        method: "get",
        params: {
          page: 1,
          size: 1,
          id: this.sessionId
        }
      });
    },
    
    // 获取聊天历史记录
    getChatHistory() {
      return create({
        url: "/chat/chatPage",
        method: "get",
        params: {
          sessionId: this.sessionId,
          page: 1,
          size: 50
        }
      });
    },
    
    // 发送消息到后端
    postMessage(content, fromUser = true) {
      if (!this.sessionId) {
        // 如果没有会话ID，可能需要先创建一个会话
        console.error('没有会话ID，无法发送消息');
        this.$message.error('没有有效的会话，无法发送消息');
        return Promise.reject(new Error('没有会话ID'));
      }
      
      return create({
        url: "/chat/send",
        method: "post",
        data: {
          sessionId: this.sessionId,
          content: content
        }
      });
    },
    
    // AI自我介绍
    introduceAI() {
      // 添加AI自我介绍消息
      const introMessage = '您好！我是由中国的深度求索（DeepSeek）公司开发的智能助手DeepSeek-R1。如您有任何问题，我会尽我所能为您提供帮助。';
      
      this.messages.push({
        type: 'ai',
        content: introMessage,
        timestamp: new Date().getTime()
      });
      
      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom();
      });
      
      // 如果已经有会话ID，则保存这条消息
      if (this.sessionId) {
        this.postMessage(introMessage, false).catch(error => {
          console.error('保存介绍消息失败:', error);
        });
      }
    },
    
    sendMessage() {
      if (!this.messageInput.trim()) return;
      
      // 保存用户输入的内容
      const userInput = this.messageInput.trim();
      
      // 添加用户消息到列表
      this.messages.push({
        type: 'user',
        content: userInput,
        timestamp: new Date().getTime()
      });
      
      // 清空输入框
      this.messageInput = '';
      
      // 滚动到底部
      this.$nextTick(() => {
        this.scrollToBottom();
      });
      
      // 显示AI思考状态
      this.isThinking = true;
      
      // 如果没有会话ID，先创建新会话
      if (!this.sessionId) {
        console.log('创建新会话并发送消息:', userInput);
        this.isThinking = true; // 确保在API调用前设置为true
        
        // 1. 调用创建会话API
        createSession({
          content: userInput
        }).then(response => {
          console.log('创建会话响应:', response);
          
          console.log('Checking createSession response condition...');
          console.log('response exists:', !!response);
          console.log('response.code === 200:', response ? response.code === 200 : 'N/A');
          console.log('response.data is truthy:', response ? !!response.data : 'N/A');
          
          if (response && response.code === 200 && response.data) {
            console.log('Entered createSession SUCCESS block.'); 
            
            // 2. 获取新创建的会话ID
            const newSessionId = response.data; 
            this.sessionId = newSessionId;
            console.log('新会话创建成功，ID:', this.sessionId);
            
            this.chatTitle = '新会话 ' + this.sessionId; 
            localStorage.setItem('currentSessionId', this.sessionId);
            
            console.log('About to call postMessage for the first message...');
            
            // 3. 使用新的 sessionId 发送第一条消息
            this.postMessage(userInput) 
              .then(sendResponse => {
                 console.log('postMessage (first msg) SUCCESS block. Response:', sendResponse);
                 this.isThinking = false;
                 if (sendResponse && sendResponse.code === 200) {
                   this.processAndAddAiResponse(sendResponse.data);
                 } else {
                   console.warn('postMessage success but invalid response', sendResponse);
                   this.$message.info('AI助手暂时没有回复');
                 }
              })
              .catch(sendError => {
                 console.error('postMessage (first msg) FAILED:', sendError);
                 this.isThinking = false;
                 this.$message.error('发送消息失败');
              });

            console.log('About to call $router.replace...');
            
            // 5. 更新URL (异步)
            const newRoute = { path: '/history', query: { id: this.sessionId } };
            this.$router.replace(newRoute)
              .then(() => {
                 console.log('Route replaced successfully. About to emit refresh-sessions...');
                 this.$nextTick(() => { 
                   this.$eventBus.$emit('refresh-sessions');
                   console.log('Emitted refresh-sessions.');
                 });
              })
              .catch(err => {
                 console.error('Route replace FAILED:', err);
                 if (err.name === 'NavigationCancelled' || err.name === 'NavigationDuplicated') {
                   console.log(`导航${err.name}，仍将刷新列表`);
                   this.$nextTick(() => {
                      this.$eventBus.$emit('refresh-sessions');
                      console.log('Emitted refresh-sessions after ignored nav error.');
                   });
                 } else {
                   this.$message.error('会话已创建，但更新URL时出错');
                 }
              });

          } else {
            console.error('Entered createSession FAILURE block! Removing user message.'); 
            this.isThinking = false;
            this.$message.error('创建会话失败: ' + (response.msg || '未知错误'));
            this.messages.pop(); 
          }
        }).catch(error => {
          console.error('createSession CATCH block! Removing user message.', error);
          this.isThinking = false;
          this.$message.error('创建会话失败: ' + (error.message || '未知错误'));
          this.messages.pop();
        });
      } else { // -------- 以下是处理已有会话的逻辑 --------
        this.isThinking = true; // 发送前设置
        this.postMessage(userInput).then(response => { 
          console.log("发送消息响应：", response);
          this.isThinking = false; // 收到响应后设置
          if (response && response.code === 200) { 
             this.processAndAddAiResponse(response.data);
          } else {
            console.warn(
              "发送消息成功，但响应格式不正确或未收到AI响应数据",
              response
            );
            this.$message.info("AI助手暂时没有回复"); 
          }
        }).catch(error => {
          this.isThinking = false;
          console.error('发送消息失败:', error);
          this.$message.error('发送消息失败');
        });
      }
    },
    
    scrollToBottom() {
      const container = this.$refs.chatMessages;
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    },
    
    copyChat() {
      this.$message.success('聊天内容已复制到剪贴板');
    },
    
    editTitle() {
      this.$prompt('请输入新的对话标题', '编辑标题', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValue: this.chatTitle
      }).then(({ value }) => {
        this.chatTitle = value;
        this.$message({
          type: 'success',
          message: '标题已更新'
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消编辑'
        });
      });
    },
    // 从路由初始化会话
    initFromRoute() {
      // 从URL参数获取会话id
      const routeSessionId = this.$route.query.id;
      console.log('从路由获取会话ID:', routeSessionId);
      
      // 更新组件的sessionId
      this.sessionId = routeSessionId;
      
      if (!this.sessionId) {
        console.log('没有会话ID，这是一个新会话');
        
        // 清除旧数据
        this.messages = []; // 清空消息列表
        this.isThinking = false;
        this.chatTitle = '新会话'; // 设置默认标题
        
        // 确保滚动（如果需要的话，虽然现在列表是空的）
        this.$nextTick(() => {
          this.scrollToBottom();
        });
      } else {
        console.log('有会话ID，获取历史记录:', this.sessionId);
        
        // 保存到localStorage以便在页面刷新时恢复
        localStorage.setItem('currentSessionId', this.sessionId);
        
        // 清除现有消息，避免内容闪烁
        this.messages = [];
        this.isThinking = false;
        
        // 初始化消息（获取历史记录）
        this.initMessages();
      }
    },

    // 新增：处理并添加 AI 响应到消息列表
    processAndAddAiResponse(responseData) {
      if (!responseData || typeof responseData !== 'string') {
        console.warn("Received invalid AI response data:", responseData);
        this.$message.info('AI助手暂时没有有效回复');
        return;
      }

      let thinkContent = null;
      let replyContent = '';
      const thinkRegex = /<think>(.*?)<\/think>(.*)/s; // s flag for multiline
      const match = responseData.match(thinkRegex);

      if (match && match.length === 3) {
        thinkContent = match[1].trim();
        replyContent = match[2].trim();
        console.log("Parsed AI response: Think=", thinkContent, "Reply=", replyContent);
      } else {
        // 没有找到 think 标签，整个作为回复
        replyContent = responseData.trim();
        console.log("Parsed AI response (no think): Reply=", replyContent);
      }

      // 确保至少有回复内容才添加消息
      if (replyContent || thinkContent) { 
        this.messages.push({
          type: 'ai',
          thinkContent: thinkContent, // 可能为 null
          replyContent: replyContent, // 可能为空字符串
          timestamp: new Date().getTime(),
          isThinkExpanded: false // 新增：默认不展开思考内容
        });
        this.$nextTick(() => { this.scrollToBottom(); });
      } else {
          console.warn("Parsed AI response resulted in empty content.");
          this.$message.info('AI助手的回复内容为空'); 
      }
    },

    // 新增：处理思考内容的展开和折叠
    toggleThink(message) {
      // 直接修改 message 对象上的状态
      message.isThinkExpanded = !message.isThinkExpanded;
      // 切换后可能需要重新计算滚动条
      this.$nextTick(() => {
        this.scrollToBottom(); 
      });
    }
  },
  created() {
    this.initFromRoute();
  },
  mounted() {
    // 页面加载后滚动到底部
    this.$nextTick(() => {
      this.scrollToBottom();
    });
  },
  // 监听路由变化，以便在URL参数变化时更新内容
  watch: {
    // 直接监听$route对象，确保捕获所有路由变化
    $route: {
      handler: function(newRoute, oldRoute) {
        console.log('路由变化监听触发:', 
          '旧:', oldRoute.path + (oldRoute.query.id ? '?id=' + oldRoute.query.id : ''),
          '新:', newRoute.path + (newRoute.query.id ? '?id=' + newRoute.query.id : '')
        );
        
        // 只有当路径变化或者id查询参数变化时才重新初始化
        const pathChanged = newRoute.path !== oldRoute.path;
        const idChanged = newRoute.query.id !== oldRoute.query.id;
        
        if (pathChanged || idChanged) {
          console.log(`会话页面有效路由变化，重新初始化组件`);
          this.initFromRoute();
        } else {
          console.log('路由变化不影响会话ID，忽略');
        }
      },
      deep: true
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 100px);
  background-color: #292c33;
  border-radius: 8px;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  
  .chat-actions {
    display: flex;
    align-items: center;
    margin-left: auto;
    
    .intro-btn {
      border-radius: 20px;
      margin-right: 8px;
    }
    
    .el-button {
      color: #a8abb2;
      &:hover {
        color: white;
      }
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  
  .message-item {
    display: flex;
    margin-bottom: 20px;
    width: 100%;
    
    .message-content {
      max-width: 80%;
      
      /* 新增 思考容器样式 */
      .think-container {
        margin-bottom: 8px; /* 和回复气泡的间距 */
      }

      /* 新增 思考头部样式 (药丸形状) */
      .think-header {
        display: inline-flex; /* 使用 flex 布局 */
        align-items: center; /* 垂直居中 */
        padding: 6px 12px;
        background-color: #3e4247; /* 背景色 */
        border-radius: 16px; /* 圆角 */
        color: #a8abb2; /* 文字颜色 */
        font-size: 13px;
        cursor: pointer; /* 可点击手势 */
        transition: background-color 0.3s;
        
        &:hover {
          background-color: #4a4e53;
        }

        i {
          margin: 0 4px; /* 图标间距 */
        }
        
        i:first-child {
           margin-left: 0;
        }
        
        i:last-child {
          margin-right: 0;
          margin-left: 8px; /* 箭头和文字间距 */
        }
        
        span {
          margin: 0 4px; /* 文字和图标间距 */
        }
      }

      /* 新增 思考内容区域样式 */
      .think-content {
        background-color: rgba(62, 66, 71, 0.7); /* 半透明背景 */
        padding: 10px 15px;
        border-radius: 8px; /* 轻微圆角 */
        color: #c0c4cc;
        font-size: 13px;
        margin-top: 6px; /* 与头部的间距 */
        border: 1px solid rgba(255, 255, 255, 0.1);
        font-style: italic;
        line-height: 1.5;
        white-space: pre-wrap; /* 保留换行和空格 */
        word-break: break-word; /* 允许长单词换行 */
      }
      
      .message-text {
        padding: 12px 16px;
        border-radius: 8px;
        font-size: 14px;
        line-height: 1.6;
      }
    }
  }
  
  .ai-message {
    align-self: flex-start;
    justify-content: flex-start;
    
    .message-text {
      background-color: #3e4247;
      color: white;
    }
  }
  
  .user-message {
    align-self: flex-end;
    justify-content: flex-end;
    
    .message-text {
      background-color: #4992ff;
      color: white;
    }
  }
  
  .loading-container,
  .empty-message {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px 0;
    color: #a8abb2;
    
    i {
      font-size: 32px;
      margin-bottom: 16px;
    }
    
    span, p {
      font-size: 14px;
    }
  }
  
  .empty-message {
    i {
      color: #4992ff;
      opacity: 0.6;
    }
  }
}

.chat-input-container {
  padding: 15px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  background-color: #1e1e1e;
  
  .input-wrapper {
    flex: 1;
    position: relative;
    margin-right: 15px;
    
    .el-textarea {
      ::v-deep .el-textarea__inner {
        background-color: #3e4247;
        border: none;
        border-radius: 20px;
        color: white;
        padding: 12px 15px;
        overflow-y: auto;
        resize: none;
        max-height: 120px;
        
        &:focus {
          box-shadow: 0 0 0 2px rgba(73, 146, 255, 0.3);
        }
        
        &::placeholder {
          color: #a8abb2;
        }
      }
    }
  }
  
  .send-btn {
    border-radius: 20px;
  }
}
</style>
