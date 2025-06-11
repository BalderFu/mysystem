import request from '@/utils/request'


const dashboardService = {

  getAlgorithmChartTitle() {
    return '服务类型使用占比统计'
  },


  getSuccessRateChartTitle() {
    return '订单统计'
  },


  getSuccessLabel() {
    return '完成订单'
  },


  getFailLabel() {
    return '未完成订单'
  },

  async getStatisticsCards() {
    try {
      // 初始化统计卡片数据结构
      const statisticsCards = [
        {
          title: '订单总数',
          value: Number(0),
          icon: 'el-icon-s-platform',
          bgColor: '#67C23A'
        },
        {
          title: '服务中数量',
          value: Number(0),
          icon: 'el-icon-key',
          bgColor: '#409EFF'
        },
        {
          title: '用户数量',
          value: Number(0),
          icon: 'el-icon-user',
          bgColor: '#E6A23C'
        },
        {
          title: '今日访问量',
          value: Number(0),
          icon: 'el-icon-view',
          bgColor: '#F56C6C'
        }
      ]

      try {
        const response = await request({
          url: '/statistic/dashboard',
          method: 'get'
        }).catch(() => null)

        if (response && response.code === 200 && response.data) {
          if (response.data.count1 !== undefined) {
            statisticsCards[0].value = Number(response.data.count1);
          }
          
          if (response.data.count2 !== undefined) {
            statisticsCards[1].value = Number(response.data.count2);
          }
          
          if (response.data.count3 !== undefined) {
            statisticsCards[2].value = Number(response.data.count3);
          }
          
          if (response.data.count4 !== undefined) {
            statisticsCards[3].value = Number(response.data.count4);
          }
          
          console.log('API返回的数据:', response.data);
          console.log('处理后的卡片数据:', JSON.stringify(statisticsCards, null, 2));
        }
      } catch (innerError) {
        console.error('API调用失败，使用默认数据:', innerError)
      }

      console.log('返回的统计卡片数据:', statisticsCards)
      return statisticsCards
    } catch (error) {
      console.error('获取统计卡片数据失败:', error)
      return [
        {
          title: '订单总数',
          value: Number(0),
          icon: 'el-icon-s-platform',
          bgColor: '#67C23A'
        },
        {
          title: '服务中数量',
          value: Number(0),
          icon: 'el-icon-key',
          bgColor: '#409EFF'
        },
        {
          title: '用户数量',
          value: Number(0),
          icon: 'el-icon-user',
          bgColor: '#E6A23C'
        },
        {
          title: '今日访问量',
          value: Number(0),
          icon: 'el-icon-view',
          bgColor: '#F56C6C'
        }
      ]
    }
  },

  async getleaveTableData() {
    try {
      const response = await request({
        url: '/statistic/stats',
        method: 'get'
      }).catch(() => null)
      console.log("获取左侧表格数据",response);
      if (response && response.code === 200 && response.data) {
        return response.data
      }

      return []
    } catch (error) {
      console.error('获取左侧表格数据失败:', error)
      return []
    }
  },


  // async getAlgorithmUsage() {
  //   try {
  //     const response = await request({
  //       url: '/serviceType/usage',
  //       method: 'get'
  //     }).catch(() => null)
      
  //     if (response && response.code === 200 && response.data && 
  //         response.data.usageStats && Array.isArray(response.data.usageStats)) {
  //       return response.data.usageStats
  //     }
      
  //     // 如果API调用失败或数据格式不符合要求，返回默认数据
  //     return [
  //       { name: '日常保洁', value: 120 },
  //       { name: '深度清洁', value: 80 },
  //       { name: '除螨除菌', value: 60 },
  //       { name: '家居整理', value: 40 },
  //       { name: '厨房清洁', value: 30 }
  //     ]
  //   } catch (error) {
  //     console.warn('获取服务类型使用统计数据失败:', error)
  //     // 返回默认数据
  //     return [
  //       { name: '日常保洁', value: 120 },
  //       { name: '深度清洁', value: 80 },
  //       { name: '除螨除菌', value: 60 },
  //       { name: '家居整理', value: 40 },
  //       { name: '厨房清洁', value: 30 }
  //     ]
  //   }
  // },


  // async getSuccessRates() {
  //   try {
  //     // 当前API调用方式
  //     const response = await request({
  //       url: '/serviceType/satisfaction',
  //       method: 'get'
  //     }).catch(() => null)
      
  //     if (response && response.code === 200 && response.data && 
  //         response.data.satisfactionRates && Array.isArray(response.data.satisfactionRates)) {
  //       return response.data.satisfactionRates
  //     }
      
  //     // 如果API调用失败或数据格式不符合要求，返回默认数据
  //     return [
  //       { name: '日常保洁', success: 75, fail: 25 },
  //       { name: '深度清洁', success: 60, fail: 40 },
  //       { name: '除螨除菌', success: 42, fail: 58 },
  //       { name: '家居整理', success: 88, fail: 12 },
  //       { name: '厨房清洁', success: 35, fail: 65 }
  //     ]
  //   } catch (error) {
  //     console.warn('获取服务满意度数据失败:', error)
  //     // 返回默认数据
  //     return [
  //       { name: '日常保洁', success: 75, fail: 25 },
  //       { name: '深度清洁', success: 60, fail: 40 },
  //       { name: '除螨除菌', success: 42, fail: 58 },
  //       { name: '家居整理', success: 88, fail: 12 },
  //       { name: '厨房清洁', success: 35, fail: 65 }
  //     ]
  //   }
  // },


  // async getAccessTrend() {
  //   try {
  //     // 当前API调用方式
  //     const response = await request({
  //       url: '/statistic/accessTrend',
  //       method: 'get'
  //     }).catch(() => null)
      
  //     if (response && response.code === 200 && response.data && 
  //         response.data.accessTrend && 
  //         response.data.accessTrend.dates && 
  //         response.data.accessTrend.counts) {
  //       return {
  //         dates: response.data.accessTrend.dates,
  //         counts: response.data.accessTrend.counts
  //       }
  //     }
      
  //     // 如果API调用失败或数据格式不符合要求，返回默认数据
  //     const dates = []
  //     const counts = []
  //     const now = new Date()
      
  //     for (let i = 29; i >= 0; i--) {
  //       const date = new Date(now)
  //       date.setDate(date.getDate() - i)
  //       dates.push([date.getMonth() + 1, date.getDate()].join('/'))
  //       counts.push(Math.floor(Math.random() * 100) + 20)
  //     }
      
  //     return { dates, counts }
  //   } catch (error) {
  //     console.warn('获取访问量趋势数据失败:', error)
  //     // 返回默认数据
  //     const dates = []
  //     const counts = []
  //     const now = new Date()
      
  //     for (let i = 29; i >= 0; i--) {
  //       const date = new Date(now)
  //       date.setDate(date.getDate() - i)
  //       dates.push([date.getMonth() + 1, date.getDate()].join('/'))
  //       counts.push(Math.floor(Math.random() * 100) + 20)
  //     }
      
  //     return { dates, counts }
  //   }
  // },


  // async getRecentLogs() {
  //   try {
  //     // 当前API调用方式
  //     const response = await request({
  //       url: '/log',
  //       method: 'get',
  //       params: {
  //         pageNo: 1,
  //         pageSize: 5
  //       }
  //     })
      
  //     if (response && response.code === 200) {
  //       return response.data.records || []
  //     }
      
  //     return []
  //   } catch (error) {
  //     console.error('获取日志列表失败:', error)
  //     return []
  //   }
  // }
}

export default dashboardService 