const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  // 设置页面标题
  chainWebpack: config => {
    config
      .plugin('html')
      .tap(args => {
        args[0].title = '文案生成系统'
        return args
      })
  }
}) 