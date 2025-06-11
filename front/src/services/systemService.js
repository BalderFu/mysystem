
const systemService = {

  systemInfo: {
    name: '脚手架管理系统',
    version: '1.0.0',
    logo: null,
    primaryColor: '#409EFF',
    apiBaseUrl: 'http://localhost:8090',
    defaultAvatar: require('@/assets/111.jpg'),
    userAvatarFormat: '{baseUrl}/uploads/{avatar}?t={timestamp}'
  },


  menus: [
    {
      title: '首页',
      path: '/dashboard',
      icon: 'el-icon-s-home',
      roles: ['管理员']
    },

    {
      title: '用户管理',
      path: '/user',
      icon: 'el-icon-user',
      roles: ['管理员','普通用户']
    },
    {
      title: '系统管理',
      path: '/system',
      icon: 'el-icon-s-tools',
      roles: ['管理员'],
      children: [
        {
          title: '日志管理',
          path: '/log',
          icon: 'el-icon-tickets'
        }
        // 这里可以添加更多系统管理相关的子菜单
      ]
    }
  ],

  getSystemName() {
    return this.systemInfo.name;
  },


  getSystemVersion() {
    return this.systemInfo.version;
  },


  getMenus(role) {
    if (role) {
      return this.menus.filter(menu => {
        // 没有配置roles或者roles包含当前角色
        return !menu.roles || menu.roles.includes(role);
      });
    }
    return this.menus;
  },


  getApiBaseUrl() {
    return this.systemInfo.apiBaseUrl;
  },


  getDefaultAvatar() {
    return this.systemInfo.defaultAvatar;
  },


  getUserAvatarUrl(avatar) {
    if (!avatar) {
      return this.getDefaultAvatar();
    }
    
    const timestamp = new Date().getTime();
    const baseUrl = this.getApiBaseUrl();
    
    return this.systemInfo.userAvatarFormat
      .replace('{baseUrl}', baseUrl)
      .replace('{avatar}', avatar)
      .replace('{timestamp}', timestamp);
  }
};

export default systemService; 