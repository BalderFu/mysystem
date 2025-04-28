# 哈希破解系统前端

这是一个哈希破解系统的前端项目，基于Vue.js开发。

## 项目结构

项目主要包含以下模块：

- Dashboard: 系统概览
- 算法管理: 对系统算法的管理
- 算法破解: 提供密码破解功能
- 算法资料库: 存储算法相关资料
- 用户管理: 用户信息管理
- 日志管理: 系统操作日志

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发环境运行

```bash
npm run serve
```

### 生产环境构建

```bash
npm run build
```

## 系统配置服务

本项目实现了一个灵活的系统配置服务，可以方便地更改系统名称、菜单项、API基础地址以及默认头像等。所有这些配置集中在一个文件中，便于维护和切换。

### 配置文件位置

系统配置文件位于：`src/services/systemService.js`

### 可配置项目

在系统配置文件中，你可以修改以下内容：

1. **系统基本信息**：
   - 系统名称
   - 系统版本
   - 系统logo
   - 系统主题色
   - API基础地址
   - 默认头像路径
   - 用户头像URL格式

2. **系统菜单**：
   - 菜单标题
   - 菜单路径
   - 菜单图标
   - 菜单权限

### 如何修改系统名称

修改 `systemService.js` 文件中的 `systemInfo.name` 属性：

```js
systemInfo: {
  // 系统名称
  name: '新系统名称',
  ...
}
```

### 如何修改菜单项

修改 `systemService.js` 文件中的 `menus` 数组：

```js
menus: [
  {
    title: '首页',  // 菜单标题
    path: '/dashboard',  // 路由路径
    icon: 'el-icon-s-home'  // 图标
  },
  // 添加或修改其他菜单项...
]
```

若要添加新的菜单项，只需在数组中添加新的对象，例如：

```js
{
  title: '新功能',
  path: '/new-feature',
  icon: 'el-icon-star-off',
  roles: ['admin']  // 可选，限制只有admin角色可见
}
```

### 如何修改API基础地址

修改 `systemService.js` 文件中的 `systemInfo.apiBaseUrl` 属性：

```js
systemInfo: {
  ...
  // 后台API基础地址
  apiBaseUrl: 'https://new-api.example.com',
}
```

### 如何修改默认头像和头像URL格式

修改 `systemService.js` 文件中的默认头像和头像URL格式相关配置：

```js
systemInfo: {
  ...
  // 默认头像路径
  defaultAvatar: require('@/assets/your-default-avatar.png'),
  
  // 用户头像路径格式，用于拼接用户头像URL
  // 使用 {baseUrl} 表示API基础地址，{avatar} 表示用户头像文件名，{timestamp} 表示时间戳
  userAvatarFormat: '{baseUrl}/custom-path/{avatar}?t={timestamp}'
}
```

系统提供了以下方法来获取头像：

- `getDefaultAvatar()`: 获取默认头像路径
- `getUserAvatarUrl(avatar)`: 根据用户头像文件名获取完整的头像URL

在代码中使用这些方法来获取头像：

```js
// 获取默认头像
const defaultAvatar = systemService.getDefaultAvatar();

// 获取用户头像URL
const userAvatar = systemService.getUserAvatarUrl('avatar-filename.jpg');
```

## Dashboard数据服务

本项目实现了一个灵活的Dashboard数据服务层，可以方便地更换后端API数据源。

### 数据服务设计

数据服务是一个中间层，它抽象了数据获取的逻辑，使得页面组件只需关心数据的展示，而不需要关心数据从哪里来，以及如何获取。

当需要更换数据源时，只需要修改`src/services/dashboardService.js`文件中的API调用部分，而不需要修改Dashboard组件本身。

### 数据服务提供的方法

数据服务提供了以下方法：

- `getStatisticsCards()`: 获取统计卡片数据
- `getAlgorithmUsage()`: 获取算法使用统计数据
- `getSuccessRates()`: 获取破解成功率数据
- `getAccessTrend()`: 获取访问量趋势数据
- `getRecentLogs()`: 获取最近日志数据

每个方法都会返回标准格式的数据对象，即使API调用失败，也会返回默认数据，确保页面不会因为API异常而崩溃。

### 如何更换数据源

当需要更换为新的数据源时，只需要修改`src/services/dashboardService.js`文件中的相应方法，确保返回的数据格式一致即可。

例如，要修改统计卡片的数据源，只需要修改`getStatisticsCards()`方法：

```js
async getStatisticsCards() {
  try {
    // 初始化统计卡片数据结构保持不变
    const statisticsCards = [...]

    // 修改这里的API调用
    // const response = await request({...})
    
    // 改为新的API调用
    const response = await fetch('https://new-api.example.com/statistics')
    const data = await response.json()
    
    // 映射API返回的数据到统计卡片
    if (data && data.success) {
      statisticsCards[0].value = data.algorithmTotal
      statisticsCards[1].value = data.tasks
      statisticsCards[2].value = data.users
      statisticsCards[3].value = data.dailyVisits
    }

    return statisticsCards
  } catch (error) {
    console.error('获取统计卡片数据失败:', error)
    // 返回默认数据（保持不变）
    return [...]
  }
}
```

只要确保返回的数据结构和原来一致，Dashboard组件就能正常工作，无需任何修改。

### 数据格式规范

为确保更换数据源后Dashboard组件能正常工作，必须遵循以下数据格式规范：

1. 统计卡片数据：
```js
[
  {
    title: '算法总数',
    value: 5, // 数值
    icon: 'el-icon-s-platform',
    bgColor: '#67C23A'
  },
  // 其他卡片...
]
```

2. 算法使用统计数据：
```js
[
  { name: 'MD5', value: 120 },
  { name: 'SHA-1', value: 80 },
  // 其他算法...
]
```

3. 破解成功率数据：
```js
[
  { name: 'MD5', success: 75, fail: 25 },
  { name: 'SHA-1', success: 60, fail: 40 },
  // 其他算法...
]
```

4. 访问量趋势数据：
```js
{
  dates: ['5/1', '5/2', '5/3'...], // 日期数组
  counts: [100, 120, 90...] // 对应的访问量数组
}
```

5. 最近日志数据：
```js
[
  { username: '用户1', url: '/api/test', createTime: '2023-05-20 10:30:00' },
  // 其他日志...
]
```

## 开发指南

### 组件开发规范

1. 所有组件使用.vue文件格式
2. 组件名称使用PascalCase命名法
3. 组件应当有明确的职责，避免过于复杂的组件
4. 组件应当有完善的注释和文档

### 样式规范

1. 使用scoped CSS保证样式隔离
2. 优先使用Element UI提供的样式和组件
3. 使用变量定义颜色和尺寸，便于统一修改

### API请求规范

1. 所有API请求应当封装在api目录或services目录下
2. 使用统一的请求工具函数
3. 处理请求错误和加载状态
4. 为API请求提供默认数据，确保页面不会因为API异常而崩溃
