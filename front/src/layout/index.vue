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
              <span slot="title">个人信息</span>
            </el-menu-item>
            <!-- <el-menu-item index="/sensitiveupload" class="menu-item">
              <div class="menu-icon-container">
                <i class="el-icon-upload"></i>
              </div>
              <span slot="title">敏感词文件上传检测</span>
            </el-menu-item>
            <el-menu-item index="/sensitivetextcheck" class="menu-item">
              <div class="menu-icon-container">
                <i class="el-icon-search"></i>
              </div>
              <span slot="title">敏感词检测</span>
            </el-menu-item>
            <el-menu-item index="/openapi" class="menu-item">
              <div class="menu-icon-container">
                <i class="el-icon-connection"></i>
              </div>
              <span slot="title">开放API</span>
            </el-menu-item>
            <el-menu-item index="/sensitivetext" class="menu-item">
              <div class="menu-icon-container">
                <i class="el-icon-document-copy"></i>
              </div>
              <span slot="title">敏感文本管理</span>
            </el-menu-item>
            <el-menu-item index="/history" class="menu-item">
              <div class="menu-icon-container">
                <i class="el-icon-time"></i>
              </div>
              <span slot="title">敏感词使用历史</span>
            </el-menu-item> -->
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
                <el-dropdown-item>
                  <div class="dropdown-profile">
                    <img :src="avatarUrl" class="dropdown-avatar" alt="用户头像">
                    <div class="dropdown-info">
                      <div class="dropdown-name">{{ userName }}</div>
                      <div class="dropdown-role">管理员</div>
                    </div>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item divided>
                  <i class="el-icon-user"></i>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item>
                  <i class="el-icon-setting"></i>
                  <span>账户设置</span>
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

export default {
  name: "Layout",
  data() {
    return {
      baseUrl: "http://localhost:8090",
      avatarUrl: "",
      userName: "",
      isSidebarCollapsed: false,
      currentTime: ""
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path;
    },
    currentPageTitle() {
      const path = this.$route.path;
      const routeMap = {
        '/dashboard': '个人信息',
        '/sensitiveupload': '生成文案',
        '/sensitivetextcheck': '文案导出',
        '/openapi': '聊天',
        // '/sensitivetext': '敏感文本管理',
        // '/history': '敏感词使用历史'
      };
      return routeMap[path] || '文案生成系统';
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
    }
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
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
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

.user-dropdown {
  .dropdown-profile {
    display: flex;
    align-items: center;
    padding: 5px 0;
    
    .dropdown-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      margin-right: 10px;
      object-fit: cover;
      border: 2px solid var(--accent-color);
    }
    
    .dropdown-info {
      .dropdown-name {
        font-weight: 500;
        font-size: 14px;
        color: var(--text-dark);
      }
      
      .dropdown-role {
        font-size: 12px;
        color: var(--text-tertiary);
        margin-top: 2px;
      }
    }
  }
  
  .el-dropdown-item {
    i {
      margin-right: 8px;
      color: var(--accent-color);
    }
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
</style>
