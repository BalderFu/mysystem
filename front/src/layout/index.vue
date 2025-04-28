<template>
  <div class="app-wrapper">
    <el-container>
      <el-aside width="210px" class="sidebar-container">
        <div class="logo-container">
          <img src="@/assets/logo.png" alt="Logo" class="logo">
          <span class="title">{{ systemName }}</span>
        </div>
        <el-scrollbar wrap-class="scrollbar-wrapper">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            background-color="var(--menu-background)"
            text-color="var(--menu-text)"
            active-text-color="var(--menu-active-text)"
            @select="handleSelect"
            router
          >
            <!-- 渲染无子菜单的菜单项 -->
            <template v-for="(menu, index) in filteredMenus">
              <!-- 没有子菜单的菜单项 -->
              <el-menu-item 
                v-if="!menu.children || !menu.children.length" 
                :key="menu.path" 
                :index="menu.path" 
                class="menu-item"
              >
                <i :class="menu.icon"></i>
                <span slot="title">{{ menu.title }}</span>
              </el-menu-item>
              
              <!-- 有子菜单的菜单项 -->
              <el-submenu 
                v-else 
                :key="menu.path" 
                :index="menu.path"
              >
                <template slot="title">
                  <i :class="menu.icon"></i>
                  <span>{{ menu.title }}</span>
                </template>
                
                <!-- 渲染子菜单项 -->
                <el-menu-item 
                  v-for="child in menu.children" 
                  :key="child.path" 
                  :index="child.path"
                  class="sub-menu-item"
                >
                  <i :class="child.icon"></i>
                  <span>{{ child.title }}</span>
                </el-menu-item>
              </el-submenu>
            </template>
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
import systemService from '@/services/systemService';

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
      isNewSessionActive: false,
      isAdmin: false,
      // 系统名称
      systemName: systemService.getSystemName(),
      // 系统菜单
      menus: systemService.getMenus()
    }
  },
  computed: {
    // 过滤菜单，根据用户权限
    filteredMenus() {
      if (this.isAdmin) {
        return this.menus;
      } else {
        // 如果不是管理员，只显示普通用户可以看到的菜单
        return this.menus.filter(menu => {
          return !menu.roles || menu.roles.includes('普通用户');
        });
      }
    },
    activeMenu() {
      if (this.$route.path.startsWith('/history')) {
        return '';
      }
      return this.$route.path;
    },
    currentPageTitle() {
      const path = this.$route.path;
      
      // 先从主菜单中查找当前路径对应的标题
      const currentMenu = this.menus.find(menu => menu.path === path);
      if (currentMenu) {
        return currentMenu.title;
      }
      
      // 如果在主菜单中没找到，尝试在子菜单中查找
      for (const menu of this.menus) {
        if (menu.children && menu.children.length) {
          const childMenu = menu.children.find(child => child.path === path);
          if (childMenu) {
            return childMenu.title;
          }
        }
      }
      
      // 如果没有找到匹配的菜单，使用系统名称
      return this.systemName;
    },
    isViewingNewChat() {
      return this.$route.path === '/history' && !this.$route.query.id;
    }
  },
  methods: {
    init() {
      // 设置计时器更新时间
      this.updateTime();
      this.timer = setInterval(this.updateTime, 1000);
      
      // 获取用户信息
      const user = localStorage.getItem(Constants.ID.USER_KEY);
      if (user) {
        try {
          const userInfo = JSON.parse(user);
          this.userName = userInfo.data.username || '未知用户';
          // 使用systemService获取用户头像URL
          this.avatarUrl = userInfo.data.avatar 
            ? systemService.getUserAvatarUrl(userInfo.data.avatar)
            : systemService.getDefaultAvatar();
        } catch (error) {
          console.error('解析用户信息失败:', error);
          this.userName = '未知用户';
          this.avatarUrl = systemService.getDefaultAvatar();
        }
      } else {
        // 如果没有用户信息，使用systemService获取默认头像
        this.avatarUrl = systemService.getDefaultAvatar();
      }
    },
    checkAdminRole() {
      const user = localStorage.getItem(Constants.ID.USER_KEY);
      if (user) {
        const userInfo = JSON.parse(user);
        this.isAdmin = userInfo.data.role == '管理员';
      }
    },
    handleSelect(index, indexPath) {
      console.log('选择菜单项:', index, indexPath);
      if (this.$route.path === index) {
        console.log('已在当前页面，不重复导航');
        return;
      }
      
      this.$router.push(index).then(() => {
        console.log('导航成功');
      }).catch(err => {
        console.log('导航错误，但已被处理:', err);
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
    
    fetchChatSessionsForMenu() {
      this.isNewSessionActive = false;
      console.log(
        "fetchChatSessionsForMenu: isNewSessionActive 设置为 false"
      );
      this.sessionsLoading = true;
      
      const params = {
        page: 1,
        size: 10,
        _t: Date.now()
      };
      
      getChatSessions(params).then(response => {
        this.sessionsLoading = false;
        console.log("菜单会话信息：", response);
        
        if (response && response.code === 200 && response.data) {
          const sessions = response.data.records || [];
          console.log("获取到会话数量:", sessions.length);
          
          this.sessionData = sessions.map(session => {
            if (!session.id) {
              console.warn("发现没有ID的会话:", session);
              session.id = session._id || `session_${Date.now()}`;
            }
            
            if (!session.title || session.title.trim() === '') {
              session.title = '未命名会话';
              session.displayTitle = '未命名会话';
            }
            else if (session.title.length > 12) {
              session.displayTitle = session.title.substring(0, 12) + '...';
            } else {
              session.displayTitle = session.title;
            }
            
            console.log("处理会话项：", session.id, session.title);
            return session;
          });
          
          if (response.data.total > 0) {
            this.sessionMenuTitle = `会话列表 (${response.data.total})`;
          } else {
            this.sessionMenuTitle = '会话列表';
          }
        } else {
          console.error('获取会话列表失败: 响应格式不正确', response);
          this.$message.error('获取会话列表失败');
          this.sessionData = [];
          this.sessionMenuTitle = '会话列表';
        }
      }).catch(error => {
        this.sessionsLoading = false;
        console.error(
          "获取会话信息失败:",
          error
        );
        this.$message.error("获取会话信息失败");
        this.sessionData = [];
        this.sessionMenuTitle = "会话列表";
      });
    },
    
    createNewSession() {
      console.log('准备创建新会话');
      
      if (this.$route.path === '/history' && !this.$route.query.id && this.isNewSessionActive) {
        console.log('已经在新会话页面，不重复操作');
        return;
      }
      
      this.isNewSessionActive = true;
      console.log('isNewSessionActive 设置为 true');
      
      const route = {
        path: '/history',
        query: {}
      };
      
      console.log('执行导航到新会话页:', route);
      
      this.$router.replace(route).catch(err => {
        if (err.name === 'NavigationCancelled') {
          console.log('导航被取消，可能已有其他导航正在进行');
          return;
        }
        console.error('导航错误:', err);
        this.isNewSessionActive = false;
      });
    },
    
    isActiveSession(session) {
      if (this.isNewSessionActive) {
        return false;
      }
      
      if (!session || !session.id || !this.$route.query.id) return false;
      
      return this.$route.path === "/history" && 
             session.id.toString() === this.$route.query.id.toString();
    },
    
    goToSessionChat(session) {
      if (!session || !session.id) {
        console.error('会话对象无效或缺少ID');
        return;
      }
      
      if (this.isActiveSession(session)) {
        console.log('已经在当前会话页面，忽略点击');
        return;
      }
      
      console.log('准备跳转到会话', session.id);
      
      const route = {
        path: '/history',
        query: { id: session.id }
      };
      
      console.log('执行导航到:', route);
      
      this.$router.replace(route).catch(err => {
        if (err.name === 'NavigationCancelled' || err.name === 'NavigationDuplicated') {
          console.log(`导航${err.name}，忽略错误`);
          return;
        }
        console.error('导航错误:', err);
      });
    },

    handleSessionClick(session) {
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
      this.createNewSession();
    }
  },
  watch: {
    $route(to, from) {
      console.log(`Route changed from ${from.fullPath} to ${to.fullPath}`);
      
      const isNavigatingToNewSessionView = to.path === 
'/history' && !to.query.id;
      const wasInNewSessionView = from.path === 
'/history' && !from.query.id;
      
      if (isNavigatingToNewSessionView) {
        if (!this.isNewSessionActive) {
          console.log("Watcher: Activating new session state");
          this.isNewSessionActive = true;
        }
      } else {
        if (this.isNewSessionActive) {
          console.log(
            "Watcher: Deactivating new session state because navigating away"
          );
          this.isNewSessionActive = false;
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
    this.$eventBus.$on('refresh-sessions', this.fetchChatSessionsForMenu);
    this.checkAdminRole();
    this.init();
  },
  mounted() {
    // 组件挂载后检查用户角色
    this.checkAdminRole();
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
    
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

.el-menu-vertical {
  border-right: none !important;
  
  .el-menu-item {
    height: 50px;
    line-height: 50px;
    margin: 4px 10px;
    padding: 0 15px !important;
    border-radius: 4px;
    
    i {
      margin-right: 10px;
      font-size: 18px;
      color: var(--menu-text);
    }
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.1) !important;
    }
    
    &.is-active {
      background-color: var(--menu-active) !important;
      
      i {
        color: #fff;
      }
    }
  }
}

.menu-section-title {
  padding: 12px 15px;
  color: var(--menu-text);
  font-size: 13px;
  display: flex;
  align-items: center;
  opacity: 0.7;
  
  i {
    margin-right: 8px;
    font-size: 14px;
  }
  
  span {
    flex: 1;
  }
}

.menu-divider {
  height: 1px;
  margin: 8px 15px;
  background: linear-gradient(
    to right,
    rgba(255, 255, 255, 0.02),
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.02)
  );
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

<style scoped>
.dashboard-container {
  padding: 20px;
}

.chart-container {
  width: 100%;
  height: 350px;
}

.box-card {
  margin-bottom: 20px;
  background-color: var(--background-card);
  border: 1px solid var(--border-color);
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  color: white;
  margin-bottom: 15px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 56px;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  opacity: 0.9;
}

/* 菜单样式 */
.el-menu-vertical {
  border-right: none;
}

.menu-item {
  height: 50px;
  line-height: 50px;
}

.sub-menu-item {
  padding-left: 20px !important;
}

.el-submenu__title {
  height: 50px;
  line-height: 50px;
}

.el-submenu .el-menu-item {
  min-width: 0;
  background-color: var(--menu-sub-background, #1f2d3d);
}

.el-submenu .el-menu-item:hover,
.el-submenu .el-menu-item.is-active {
  background-color: var(--menu-sub-active-background, #000c17);
}

/* 响应式调整 */
@media (max-width: 992px) {
  .chart-container {
    height: 300px;
  }
}

@media (max-width: 768px) {
  .chart-container {
    height: 250px;
  }
}
</style>

