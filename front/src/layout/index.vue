<template>
  <div class="app-wrapper">
    <el-container>
      <el-aside width="210px" class="sidebar-container">
        <div class="logo-container">
          <img src="@/assets/logo.png" alt="Logo" class="logo">
          <span class="title">文案生成系统</span>
        </div>
        <el-scrollbar wrap-class="scrollbar-wrapper">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            background-color="var(--menu-background)"
            text-color="var(--menu-text)"
            active-text-color="var(--menu-active-text)"
            @select="handleSelect"
          >
            <el-menu-item index="/dashboard" class="menu-item">
              <div class="menu-icon-container">
                <i class="el-icon-s-home"></i>
              </div>
              <span slot="title">内容生成</span>
            </el-menu-item>
            
            <!-- 菜单分隔线 -->
            <div class="menu-divider"></div>
            
            <!-- 会话列表标题 -->
            <div class="menu-section-title">
              <i class="el-icon-chat-line-round"></i>
              <span>{{ sessionMenuTitle }}</span>
              <div class="add-session-btn" @click.stop="createNewSession" title="创建新会话">
                <i class="el-icon-plus"></i>
              </div>
            </div>
            
            <!-- 会话列表菜单项 -->
            <div v-loading="sessionsLoading" class="session-list">
              <!-- 新增：临时新会话项 -->
              <div 
                v-if="isNewSessionActive"
                class="session-item session-active session-disabled"
                @click="() => {}"  
              >
                <div class="session-icon-container">
                  <i class="el-icon-chat-dot-round"></i> <!-- 可以用不同的图标 -->
                </div>
                <span class="session-name">新会话...</span>
              </div>

              <!-- 现有会话列表 -->
              <template v-if="sessionData.length > 0">
                <div 
                  v-for="session in sessionData" 
                  :key="session.id"
                  @click="handleSessionClick(session)"
                  class="session-item"
                  :class="{ 
                    'session-active': isActiveSession(session), 
                    'session-disabled': isActiveSession(session) 
                  }"
                >
                  <div class="session-icon-container">
                    <i class="el-icon-chat-dot-round"></i>
                  </div>
                  <span class="session-name">{{ session.displayTitle }}</span>
              </div>
              </template>
              
              <!-- 空状态提示 (仅当没有加载，没有真实会话，且不在临时新会话状态时显示) -->
              <div v-if="!sessionsLoading && sessionData.length === 0 && !isNewSessionActive" class="empty-session-tip">
                <i class="el-icon-info"></i>
                <span>暂无会话记录</span>
                <el-button type="primary" size="small" class="create-session-btn" @click="createNewSession">
                  <i class="el-icon-plus"></i> 创建新会话
                </el-button>
              </div>
              </div>
            
          </el-menu>
          
        </el-scrollbar>
      </el-aside>
      <el-container class="main-container">
        <el-header class="app-header">
          <div class="header-left">
            <div class="toggle-sidebar" @click="toggleSidebar">
              <i class="el-icon-s-fold collapse-btn"></i>
            </div>
            <div class="breadcrumb">
              <i class="el-icon-location-outline"></i>
              <span class="current-page">{{ currentPageTitle }}</span>
            </div>
          </div>
          <div class="header-right">
            <div class="time-display">
              <i class="el-icon-time"></i>
              <span>{{ currentTime }}</span>
            </div>
            <el-dropdown trigger="click">
              <div class="avatar-wrapper">
                <img :src="avatarUrl" class="user-avatar" alt="用户头像">
                <span class="user-name">{{ userName }}</span>
                <i class="el-icon-caret-bottom"></i>
              </div>
              <el-dropdown-menu slot="dropdown" class="user-dropdown">
                <el-dropdown-item @click.native="goToPersonalCenter">
                  <i class="el-icon-user"></i>
                  <span>个人中心</span>
                </el-dropdown-item>
                <!-- <el-dropdown-item>
                  <i class="el-icon-setting"></i>
                  <span>账户设置</span>
                </el-dropdown-item> -->
                <el-dropdown-item divided @click.native="logout">
                  <i class="el-icon-switch-button"></i>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        <el-main class="app-main">
          <transition name="fade-transform" mode="out-in">
            <router-view />
          </transition>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import Constants from "@/utils/constants";
import { getChatSessions } from "@/utils/inputs";
import Vue from 'vue';

export default {
  name: "Layout",
  data() {
    return {
      baseUrl: "http://localhost:8090",
      avatarUrl: "",
      userName: "",
      isSidebarCollapsed: false,
      currentTime: "",
      sessionMenuTitle: "会话列表",
      sessionData: [],
      sessionsLoading: false,
      isNewSessionActive: false
    }
  },
  computed: {
    activeMenu() {
      // 返回当前激活的顶级菜单项（如 /dashboard）
      // 如果在 /history，则不属于顶级菜单激活状态
      if (this.$route.path.startsWith('/history')) {
        return ''; // 或者返回一个特定的值表示会话区激活
      }
      return this.$route.path;
    },
    currentPageTitle() {
      const path = this.$route.path;
      const routeMap = {
        '/dashboard': '内容生成',
        '/personal': '个人中心',
        '/sensitiveupload': '生成文案',
        '/sensitivetextcheck': '文案导出',
        '/history': '聊天记录' // 添加history标题
      };
      
      // 如果是聊天记录页，并且有标题，显示会话标题
      if (path === '/history' && this.$route.query.id && this.sessionData.length > 0) {
        const currentSession = this.sessionData.find(s => s.id.toString() === this.$route.query.id.toString());
        if (currentSession) {
          return currentSession.title || '聊天记录';
        }
      } else if (path === '/history' && !this.$route.query.id) {
          return '新会话'; // 无ID时显示新会话
      }
      
      return routeMap[path] || '文案生成系统';
    },
    // 新增计算属性：判断是否正在查看新会话页面
    isViewingNewChat() {
      return this.$route.path === '/history' && !this.$route.query.id;
    }
  },
  methods: {
    handleSelect(index, indexPath) {
      if (this.$route.fullPath === index) {
        return;
      }
      this.$router.push({
        path: index,
      });
    },

    goToPersonalCenter() {
      if (this.$route.path === '/personal') {
        return;
      }
      this.$router.push({
        path: "/personal"
      });
    },

    logout() {
      Cookies.remove(Constants.ID.USER_TOKEN_KEY);
      localStorage.removeItem(Constants.ID.USER_TOKEN_KEY);
      this.$router.push({
        path: "/login"
      })
    },
    
    toggleSidebar() {
      this.isSidebarCollapsed = !this.isSidebarCollapsed;
      document.body.classList.toggle('hideSidebar', this.isSidebarCollapsed);
    },
    
    updateTime() {
      const now = new Date();
      const hours = now.getHours().toString().padStart(2, '0');
      const minutes = now.getMinutes().toString().padStart(2, '0');
      const seconds = now.getSeconds().toString().padStart(2, '0');
      this.currentTime = `${hours}:${minutes}:${seconds}`;
    },
    
    // 获取会话信息，用于菜单标题
    fetchChatSessionsForMenu() {
      // 获取真实列表前，清除临时新会话状态
      this.isNewSessionActive = false;
      console.log(
        "fetchChatSessionsForMenu: isNewSessionActive 设置为 false"
      );
      this.sessionsLoading = true;
      
      // 添加时间戳参数以防止浏览器缓存
      const params = {
        page: 1,
        size: 10,
        _t: Date.now() // Cache-busting timestamp
      };
      
      getChatSessions(params).then(response => {
        this.sessionsLoading = false;
        console.log("菜单会话信息：", response);
        
        // 检查响应结构
        if (response && response.code === 200 && response.data) {
          // 从data中获取记录和总数
          const sessions = response.data.records || [];
          console.log("获取到会话数量:", sessions.length);
          
          // 处理每个会话的标题，确保显示正常
          this.sessionData = sessions.map(session => {
            // 确保每个会话都有有效的ID
            if (!session.id) {
              console.warn("发现没有ID的会话:", session);
              session.id = session._id || `session_${Date.now()}`;
            }
            
            // 如果标题为空，设置为"未命名会话"
            if (!session.title || session.title.trim() === '') {
              session.title = '未命名会话';
              session.displayTitle = '未命名会话';
            }
            // 如果标题过长，截断并添加省略号
            else if (session.title.length > 12) {
              session.displayTitle = session.title.substring(0, 12) + '...';
            } else {
              session.displayTitle = session.title;
            }
            
            console.log("处理会话项：", session.id, session.title);
            return session;
          });
          
          // 如果有会话数据，更新菜单标题
          if (response.data.total > 0) {
            this.sessionMenuTitle = `会话列表 (${response.data.total})`;
          } else {
            this.sessionMenuTitle = '会话列表'; // 没有会话时重置标题
          }
        } else {
          console.error('获取会话列表失败: 响应格式不正确', response);
          this.$message.error('获取会话列表失败');
          this.sessionData = [];
          this.sessionMenuTitle = '会话列表'; // 出错时重置标题
        }
      }).catch(error => {
        this.sessionsLoading = false;
        console.error(
          "获取会话信息失败:",
          error
        );
        this.$message.error("获取会话信息失败");
        this.sessionData = [];
        this.sessionMenuTitle = "会话列表"; // 出错时重置标题
      });
    },
    
    // 创建新会话
    createNewSession() {
      console.log('准备创建新会话');
      
      // 检查是否已经在目标页面（避免重复导航和状态设置）
      if (this.$route.path === '/history' && !this.$route.query.id && this.isNewSessionActive) {
        console.log('已经在新会话页面，不重复操作');
        return;
      }
      
      // 设置新会话激活状态，但不清空现有列表
      this.isNewSessionActive = true;
      console.log('isNewSessionActive 设置为 true');
      
      // 使用router.replace导航到无ID的history页面
      const route = {
        path: '/history',
        query: {} // 确保没有ID
      };
      
      console.log('执行导航到新会话页:', route);
      
      this.$router.replace(route).catch(err => {
        if (err.name === 'NavigationCancelled') {
          console.log('导航被取消，可能已有其他导航正在进行');
          return;
        }
        console.error('导航错误:', err);
        this.isNewSessionActive = false; // 导航失败时重置状态
        // 导航失败可能不需要刷新列表，取决于具体场景
        // this.fetchChatSessionsForMenu(); 
      });
    },
    
    // 判断会话是否是当前活动会话
    isActiveSession(session) {
      // 如果临时新会话项是激活的，则所有真实会话项都不是激活的
      if (this.isNewSessionActive) {
        return false;
      }
      
      // 确保 session 和 route.query.id 都存在
      if (!session || !session.id || !this.$route.query.id) return false;
      
      // 仅当路径为 /history 且 session id 匹配 route id 时才返回 true
      return this.$route.path === "/history" && 
             session.id.toString() === this.$route.query.id.toString();
    },
    
    // 跳转到聊天会话页面
    goToSessionChat(session) {
      if (!session || !session.id) {
        console.error('会话对象无效或缺少ID');
        return;
      }
      
      // 如果是当前活动会话，不允许再次点击
      if (this.isActiveSession(session)) {
        console.log('已经在当前会话页面，忽略点击');
        return;
      }
      
      console.log('准备跳转到会话', session.id);
      
      // 使用router.replace方法，避免在历史记录中创建太多条目
      const route = {
        path: '/history',
        query: { id: session.id }
      };
      
      console.log('执行导航到:', route);
      
      this.$router.replace(route).catch(err => {
        // 处理导航错误
        if (err.name === 'NavigationCancelled' || err.name === 'NavigationDuplicated') {
          console.log(`导航${err.name}，忽略错误`);
          return;
        }
        console.error('导航错误:', err);
      });
    },

    handleSessionClick(session) {
      // 如果是当前活动会话，忽略点击
      if (this.isActiveSession(session)) {
        console.log(
          `已经在当前会话 (${session.id}) 页面，忽略点击`
        );
        return;
      }
      this.goToSessionChat(session);
    },

    handleNewSessionClick() {
      console.log("点击了新会话菜单项");
      // 调用创建新会话的逻辑（该逻辑已包含导航）
      this.createNewSession();
    }
  },
  watch: {
    // 监听路由变化以更新新会话状态 和 确保列表刷新
    $route(to, from) {
      console.log(`Route changed from ${from.fullPath} to ${to.fullPath}`);
      
      const isNavigatingToNewSessionView = to.path === 
'/history' && !to.query.id;
      const wasInNewSessionView = from.path === 
'/history' && !from.query.id;
      
      if (isNavigatingToNewSessionView) {
        // 导航到新会话页面时，激活临时项状态
        if (!this.isNewSessionActive) {
          console.log("Watcher: Activating new session state");
          this.isNewSessionActive = true;
        }
      } else {
        // 导航到其他页面或带有ID的会话页面时
        if (this.isNewSessionActive) {
          console.log(
            "Watcher: Deactivating new session state because navigating away"
          );
          this.isNewSessionActive = false;
          // 从临时新会话导航到有ID的会话时，确保列表已刷新
          // （事件总线应该已处理，这里作为保险）
          if (to.path === 
'/history' && to.query.id) {
             console.log("Watcher: Navigating from new session to existing, ensuring list refresh");
             this.fetchChatSessionsForMenu(); 
          }
        }
      }
    }
  },
  created() {
    // 添加事件监听器，用于在创建新会话后刷新会话列表
    this.$eventBus.$on('refresh-sessions', this.fetchChatSessionsForMenu);
  },
  mounted() {
    const user = localStorage.getItem(Constants.ID.USER_KEY);
    if (user) {
      const userInfo = JSON.parse(user);
      this.avatarUrl = `${this.baseUrl}/upload/${userInfo.avatar}?t=${new Date().getTime()}`;
      this.userName = userInfo.username || '用户';
    }
    
    this.updateTime();
    this.timer = setInterval(this.updateTime, 1000);
    
    // 获取会话信息用于菜单
    this.fetchChatSessionsForMenu();
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
    
    // 移除事件监听器
    this.$eventBus.$off('refresh-sessions', this.fetchChatSessionsForMenu);
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100vh;
  width: 100%;
}

.main-container {
  min-height: 100vh;
  transition: margin-left 0.3s;
  margin-left: 210px;
  position: relative;
}

.app-header {
  height: 60px;
  background: linear-gradient(135deg, var(--header-dark) 0%, #1a2236 100%);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  position: relative;
  color: white;
  border-bottom: 1px solid var(--dark-border);
  
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, transparent, var(--accent-color), transparent);
    opacity: 0.7;
  }

  .header-left {
    display: flex;
    align-items: center;
    
    .toggle-sidebar {
      cursor: pointer;
      font-size: 20px;
      color: var(--menu-text);
      margin-right: 20px;
      padding: 8px;
      border-radius: 8px;
      transition: all 0.3s;
      background-color: rgba(255, 255, 255, 0.05);
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.1);
        
        .collapse-btn {
          color: var(--accent-color);
          transform: rotate(90deg);
        }
      }
      
      .collapse-btn {
        transition: all 0.3s;
      }
    }
    
    .breadcrumb {
      font-size: 16px;
      display: flex;
      align-items: center;
      
      i {
        color: var(--accent-color);
        margin-right: 8px;
        font-size: 18px;
      }
      
      .current-page {
        color: white;
        font-weight: 500;
        position: relative;
        padding-bottom: 2px;
        
        &::after {
          content: '';
          position: absolute;
          left: 0;
          right: 0;
          bottom: 0;
          height: 2px;
          background-color: var(--accent-color);
          transform: scaleX(0);
          transition: transform 0.3s ease;
        }
        
        &:hover::after {
          transform: scaleX(1);
        }
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    
    .time-display {
      margin-right: 20px;
      display: flex;
      align-items: center;
      font-size: 14px;
      color: var(--menu-text);
      padding: 6px 12px;
      background-color: rgba(255, 255, 255, 0.05);
      border-radius: 20px;
      border: 1px solid rgba(255, 255, 255, 0.1);
      transition: all 0.3s;
      
      &:hover {
        background-color: rgba(255, 255, 255, 0.1);
        transform: translateY(-2px);
      }
      
      i {
        color: var(--accent-color);
        margin-right: 6px;
      }
    }
  }
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 12px;
  border-radius: 20px;
  transition: all 0.3s;
  background-color: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
    border-color: var(--accent-color);
    transform: translateY(-2px);
  }

  .user-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid var(--accent-color);
    box-shadow: 0 0 10px var(--accent-glow);
    transition: all 0.3s;
    
    &:hover {
      transform: scale(1.1);
    }
  }
  
  .user-name {
    color: white;
    margin: 0 10px;
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .el-icon-caret-bottom {
    margin-left: 4px;
    font-size: 12px;
    color: var(--menu-text);
    transition: transform 0.3s;
  }
  
  &:hover .el-icon-caret-bottom {
    transform: rotate(180deg);
  }
}

.app-main {
  padding: 20px;
  background-color: var(--background-color);
  position: relative;
  overflow: auto;
  min-height: calc(100vh - 60px);
}

/* 过渡动画 */
.fade-transform-enter-active, 
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.sidebar-container {
  background-color: var(--menu-background);
  transition: width 0.3s;
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 1001;
  overflow: hidden;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  
  .logo-container {
    height: 60px;
    padding: 10px 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--menu-dark);
    border-bottom: 1px solid var(--dark-border);
    
    .logo {
      width: 30px;
      height: 30px;
      margin-right: 8px;
      transition: all 0.3s;
    }
    
    .title {
      color: white;
      font-weight: 600;
      font-size: 16px;
      white-space: nowrap;
      transition: opacity 0.3s;
    }
  }
  
  .el-menu {
    border-right: none;
  }
  
  .scrollbar-wrapper {
    overflow-x: hidden !important;
  }
}

.menu-item {
  position: relative;
  border-radius: 6px;
  margin: 6px 10px;
  transition: all 0.3s;
  overflow: hidden;

  &:hover {
    background-color: rgba(64, 158, 255, 0.1);
    transform: translateX(5px);
    
    .menu-icon-container {
      background-color: rgba(64, 158, 255, 0.2);
      transform: rotate(5deg);
      
      i {
        color: #409EFF;
        transform: scale(1.2);
      }
    }
  }

  &.is-active {
    background-color: #409EFF !important;
    color: #fff !important;
    box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      height: 100%;
      width: 4px;
      background-color: #1890ff;
    }

    .menu-icon-container {
      background-color: rgba(255, 255, 255, 0.2);
      
      i {
        color: #fff !important;
      }
    }
  }
}

.menu-icon-container {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  margin-right: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: rgba(255, 255, 255, 0.05);
  
  i {
    font-size: 18px;
    transition: all 0.3s ease;
  }
}

// 覆盖Element UI下拉菜单样式
::v-deep .el-dropdown-menu {
  background-color: #1a2236 !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  border-radius: 8px !important;
  margin-top: 5px !important;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3) !important;
}

::v-deep .el-dropdown-menu__item {
  background-color: #1a2236 !important;
  color: #fff !important;
  padding: 12px 16px !important;
  line-height: 1.5 !important;
  font-size: 14px !important;
  
  &:hover, &:focus {
    background-color: rgba(64, 158, 255, 0.1) !important;
    color: #409EFF !important;
  }
  
  i {
    margin-right: 10px;
    font-size: 16px;
    color: #409EFF;
  }
}

::v-deep .el-dropdown-menu__item--divided:before {
  background-color: rgba(255, 255, 255, 0.05) !important;
  height: 1px !important;
  margin: 0 !important;
}

::v-deep .el-dropdown-menu__item.is-disabled {
  color: rgba(255, 255, 255, 0.4) !important;
}

.menu-divider {
  height: 1px;
  margin: 8px 15px;
  background: linear-gradient(to right, rgba(255, 255, 255, 0.02), rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.02));
}

.menu-section-title {
  padding: 12px 15px;
  color: var(--accent-color);
  font-size: 14px;
  display: flex;
  align-items: center;
  font-weight: 500;
  position: relative;
  
  i {
    margin-right: 10px;
    font-size: 16px;
  }
  
  span {
    opacity: 0.9;
    flex: 1;
  }
  
  .add-session-btn {
    width: 28px;
    height: 28px;
    border-radius: 50%;
    background-color: rgba(64, 158, 255, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 0 8px rgba(64, 158, 255, 0.2);
    border: 1px solid rgba(64, 158, 255, 0.5);
    
    i {
      margin: 0;
      font-size: 16px;
      color: var(--accent-color);
      transition: all 0.3s;
    }
    
    &:hover {
      background-color: var(--accent-color);
      transform: rotate(90deg) scale(1.1);
      box-shadow: 0 0 12px rgba(64, 158, 255, 0.4);
      
      i {
        color: white;
      }
    }
  }
}

.session-list {
  padding: 0 5px;
  .session-item {
    margin: 4px 10px;
    padding: 8px 12px;
    border-radius: 6px;
    position: relative;
    transition: all 0.3s;
    font-size: 13px;
    display: flex;
    align-items: center;
    line-height: 1.5;
    cursor: pointer;
    background-color: rgba(22, 33, 58, 0.5);
    border: 1px solid rgba(255, 255, 255, 0.05);
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
    
    &:hover {
      background-color: rgba(64, 158, 255, 0.1);
      transform: translateX(5px);
      
      .session-icon-container {
        background-color: rgba(64, 158, 255, 0.2);
      }
    }
    
    &.session-active {
      background-color: rgba(64, 158, 255, 0.3) !important;
      border-left: 3px solid #409EFF;
      font-weight: bold;
      transform: none !important;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        height: 100%;
        width: 3px;
        background-color: #409EFF;
      }
      
      .session-icon-container {
        background-color: rgba(64, 158, 255, 0.5);
        
        i {
          color: #fff;
        }
      }
      
      .session-name {
        color: #fff;
      }
    }
    
    &.session-disabled {
      cursor: default;
      pointer-events: none;
      opacity: 0.9;
    }
    
    .session-icon-container {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 28px;
      height: 28px;
      margin-right: 8px;
      border-radius: 6px;
      transition: all 0.3s ease;
      background-color: rgba(255, 255, 255, 0.05);
      
      i {
        font-size: 16px;
        transition: all 0.3s ease;
      }
    }
    
    .session-name {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      max-width: 150px;
    }
  }
  
  .empty-session-tip {
    padding: 15px;
    text-align: center;
    color: var(--text-secondary);
    font-size: 13px;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    i {
      font-size: 20px;
      margin-bottom: 8px;
      opacity: 0.7;
    }
    
    .create-session-btn {
      margin-top: 15px;
      border-radius: 20px;
      padding: 8px 16px;
      background-color: var(--accent-color);
      border-color: transparent;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 5px 10px rgba(64, 158, 255, 0.3);
      }
      
      i {
        margin-right: 5px;
        font-size: 12px;
        opacity: 1;
      }
    }
  }
}
</style>

