/**
 * 系统配置服务
 * 集中管理系统名称、菜单等全局配置
 * 当需要切换系统或更改菜单时，只需修改此文件
 */
const systemService = {
  /**
   * 系统基本信息
   */
  systemInfo: {
    // 系统名称
    name: '脚手架管理系统',
    // 系统版本
    version: '1.0.0',
    // 系统logo (如果有)
    logo: null,
    // 系统主题色
    primaryColor: '#409EFF',
    // 后台API基础地址，如果需要切换不同后台，可以修改此处
    apiBaseUrl: 'http://localhost:8090',
    // 默认头像路径
    defaultAvatar: require('@/assets/111.jpg'),
    // 用户头像路径格式，用于拼接用户头像URL
    // 使用 {baseUrl} 表示API基础地址，{avatar} 表示用户头像路径，{timestamp} 表示时间戳
    userAvatarFormat: '{baseUrl}/uploads/{avatar}?t={timestamp}'
  },

  /**
   * 系统菜单配置
   * 菜单结构说明：
   * - title: 菜单标题
   * - path: 路由路径，用于菜单点击跳转
   * - icon: 菜单图标，使用Element UI图标
   * - children: 子菜单列表
   * - roles: 角色权限控制，哪些角色可以看到此菜单 (如果启用了权限控制)
   */
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

  /**
   * 获取系统名称
   * @returns {string} 系统名称
   */
  getSystemName() {
    return this.systemInfo.name;
  },

  /**
   * 获取系统版本
   * @returns {string} 系统版本
   */
  getSystemVersion() {
    return this.systemInfo.version;
  },

  /**
   * 获取所有菜单
   * @param {string} role 当前用户角色，用于权限过滤 (可选)
   * @returns {Array} 菜单列表
   */
  getMenus(role) {
    // 如果需要按照角色过滤菜单，可以在这里实现
    if (role) {
      return this.menus.filter(menu => {
        // 没有配置roles或者roles包含当前角色
        return !menu.roles || menu.roles.includes(role);
      });
    }
    return this.menus;
  },

  /**
   * 获取API基础地址
   * @returns {string} API基础地址
   */
  getApiBaseUrl() {
    return this.systemInfo.apiBaseUrl;
  },

  /**
   * 获取默认头像
   * @returns {string} 默认头像路径
   */
  getDefaultAvatar() {
    return this.systemInfo.defaultAvatar;
  },

  /**
   * 获取用户头像URL
   * @param {string} avatar 用户头像文件名
   * @returns {string} 完整的用户头像URL
   */
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