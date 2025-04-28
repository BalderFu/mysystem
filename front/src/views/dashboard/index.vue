<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card overview-card">
          <div slot="header" class="clearfix">
            <span>系统概览</span>
          </div>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="(item, index) in statisticsCards" :key="index">
              <div class="stat-card" :style="{ backgroundColor: item.bgColor }">
                <div class="stat-icon">
                  <i :class="item.icon"></i>
                </div>
                <div class="stat-info">
                  <div class="stat-value">{{ item.value !== undefined ? item.value : '0' }}</div>
                  <div class="stat-title">{{ item.title }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>{{ algorithmChartTitle }}</span>
          </div>
          <div ref="algorithmChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>{{ successRateChartTitle }}</span>
          </div>
          <div ref="successRateChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>系统访问量趋势</span>
          </div>
          <div ref="accessTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script>
import * as echarts from 'echarts'
import dashboardService from '@/services/dashboardService'

export default {
  name: 'Dashboard',
  data() {
    return {
      // 图表标题
      algorithmChartTitle: dashboardService.getAlgorithmChartTitle(),
      successRateChartTitle: dashboardService.getSuccessRateChartTitle(),
      
      // 图表标签
      successLabel: dashboardService.getSuccessLabel(),
      failLabel: dashboardService.getFailLabel(),
      
      // 图表实例
      algorithmChart: null,
      successRateChart: null,
      accessTrendChart: null,
      
      // 数据加载状态
      tableLoading: false,
      logsLoading: false,
      chartsLoading: false,
      
      // 表格数据
      tableData: [],
      recentLogs: [],
      
      // 数据概览卡片
      statisticsCards: [
        {
          title: '订单总数',
          value: 0,
          icon: 'el-icon-s-platform',
          bgColor: '#67C23A'
        },
        {
          title: '服务中数量',
          value: 0,
          icon: 'el-icon-key',
          bgColor: '#409EFF'
        },
        {
          title: '用户数量',
          value: 0,
          icon: 'el-icon-user',
          bgColor: '#E6A23C'
        },
        {
          title: '今日访问量',
          value: 0,
          icon: 'el-icon-view',
          bgColor: '#F56C6C'
        }
      ],
      
      // 图表数据
      algorithmData: [],
      successRateData: [],
      accessTrendData: {
        dates: [],
        counts: []
      }
    }
  },
  created() {
    console.log('Dashboard组件已创建')
    // 设置默认值，确保页面加载时有显示
    this.statisticsCards[0].value = 10
    this.statisticsCards[1].value = 3
    this.statisticsCards[2].value = 5
    this.statisticsCards[3].value = 88
  },
  mounted() {
    console.log('Dashboard组件已挂载')
    // 初始化图表
    this.$nextTick(() => {
      this.initCharts()
    })
    
    // 获取数据
    this.fetchDashboardData()
  },
  beforeDestroy() {
    // 销毁图表实例
    if (this.algorithmChart) {
      this.algorithmChart.dispose()
    }
    if (this.successRateChart) {
      this.successRateChart.dispose()
    }
    if (this.accessTrendChart) {
      this.accessTrendChart.dispose()
    }
    
    // 移除窗口大小变化事件
    window.removeEventListener('resize', this.resizeCharts)
  },
  methods: {
    // 初始化所有图表
    initCharts() {
      // 创建图表时检测当前主题
      const isDarkMode = document.body.classList.contains('dark-mode') || 
                          window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
      
      // 设置图表主题
      const theme = isDarkMode ? 'dark' : null;
      
      // 初始化算法使用统计图表
      this.algorithmChart = echarts.init(this.$refs.algorithmChart, theme)
      
      // 初始化破解成功率图表
      this.successRateChart = echarts.init(this.$refs.successRateChart, theme)
      
      // 初始化系统访问量趋势图表
      this.accessTrendChart = echarts.init(this.$refs.accessTrendChart, theme)
      
      // 添加窗口大小变化事件，自动调整图表大小
      window.addEventListener('resize', this.resizeCharts)
      
      // 加载示例数据（在真实数据加载前显示）
      this.loadDemoData()
    },
    
    // 自适应调整图表大小
    resizeCharts() {
      if (this.algorithmChart) {
        this.algorithmChart.resize()
      }
      if (this.successRateChart) {
        this.successRateChart.resize()
      }
      if (this.accessTrendChart) {
        this.accessTrendChart.resize()
      }
    },
    
    // 加载示例数据
    loadDemoData() {
      // 算法使用统计数据
      const algorithmData = [
        
      ]
      
      // 更新算法使用统计图表
      this.algorithmChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: algorithmData.map(item => item.name),
          textStyle: {
            color: 'var(--text-primary, #606266)'
          }
        },
        series: [
          {
            name: '服务类型',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: 'var(--background-card, #fff)',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center',
              color: 'var(--text-primary, #606266)'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: algorithmData
          }
        ]
      })
      
      // 破解成功率数据
      const successRateData = [
        
      ]
      
      // 更新破解成功率图表
      this.successRateChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: [this.successLabel, this.failLabel],
          textStyle: {
            color: '#fff',
            fontSize: 12,
            fontWeight: 'bold'
          }
        },
        grid: {
          left: '5%',
          right: '5%',
          bottom: '5%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          max: 100,
          axisLabel: {
            formatter: '{value}%',
            color: '#fff',
            fontSize: 12,
            fontWeight: 'bold'
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.5)',
              width: 2
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.2)',
              type: 'dashed',
              width: 1
            }
          }
        },
        yAxis: {
          type: 'category',
          data: successRateData.map(item => item.name),
          axisLabel: {
            color: '#fff',
            fontSize: 12,
            fontWeight: 'bold',
            margin: 16
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.5)',
              width: 2
            }
          }
        },
        series: [
          {
            name: this.successLabel,
            type: 'bar',
            stack: 'total',
            label: {
              show: true,
              formatter: '{c}%',
              color: '#fff',
              fontSize: 14,
              fontWeight: 'bold',
              textShadowColor: 'rgba(0, 0, 0, 0.3)',
              textShadowBlur: 3,
              textShadowOffsetX: 1,
              textShadowOffsetY: 1
            },
            emphasis: {
              focus: 'series'
            },
            data: successRateData.map(item => item.success),
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: this.failLabel,
            type: 'bar',
            stack: 'total',
            label: {
              show: true,
              formatter: '{c}%',
              color: '#fff',
              fontSize: 14,
              fontWeight: 'bold',
              textShadowColor: 'rgba(0, 0, 0, 0.3)',
              textShadowBlur: 3,
              textShadowOffsetX: 1,
              textShadowOffsetY: 1
            },
            emphasis: {
              focus: 'series'
            },
            data: successRateData.map(item => item.fail),
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      })
      
      // 系统访问量趋势数据
      const dates = []
      const counts = []
      const now = new Date()
      
      for (let i = 29; i >= 0; i--) {
        const date = new Date(now)
        date.setDate(date.getDate() - i)
        dates.push([date.getMonth() + 1, date.getDate()].join('/'))
        counts.push(Math.floor(Math.random() * 100) + 20)
      }
      
      // 更新系统访问量趋势图表
      this.accessTrendChart.setOption({
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '5%',
          right: '5%',
          bottom: '5%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: {
            color: '#fff',
            fontSize: 12,
            margin: 12,
            fontWeight: 'bold',
            showMaxLabel: true,
            interval: 'auto',
            formatter: function(value) {
              return value;
            }
          },
          axisLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.5)',
              width: 2
            }
          },
          splitLine: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '{value}',
            color: '#fff',
            fontSize: 12,
            fontWeight: 'bold',
            margin: 16
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.5)',
              width: 2
            }
          },
          splitLine: {
            lineStyle: {
              color: 'rgba(255, 255, 255, 0.2)',
              type: 'dashed',
              width: 1
            }
          }
        },
        series: [
          {
            name: '访问量',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            showSymbol: false,
            lineStyle: {
              width: 3,
              color: 'rgba(128, 155, 247, 1)'
            },
            itemStyle: {
              color: '#fff',
              borderColor: 'rgba(128, 155, 247, 1)',
              borderWidth: 2
            },
            areaStyle: {
              opacity: 0.8,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(128, 155, 247, 0.7)'
                },
                {
                  offset: 1,
                  color: 'rgba(128, 155, 247, 0.3)'
                }
              ])
            },
            emphasis: {
              focus: 'series',
              itemStyle: {
                color: '#fff',
                borderColor: 'rgba(128, 155, 247, 1)',
                borderWidth: 3,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
                shadowBlur: 10
              }
            },
            data: counts
          }
        ]
      })
    },
    
    // 获取所有Dashboard数据
    async fetchDashboardData() {
      try {
        this.chartsLoading = true
        this.logsLoading = true
        
        // 并行获取所有数据
        const [statisticsCards, statsData] = await Promise.all([
          dashboardService.getStatisticsCards(),
          dashboardService.getleaveTableData() 
        ])
        
        // 更新统计卡片
        if (statisticsCards && statisticsCards.length) {
          this.statisticsCards = statisticsCards
        } else {
          console.warn('未获取到统计卡片数据或数据为空')
          // 确保显示默认值或空状态
          this.statisticsCards = this.statisticsCards.map(card => ({ ...card, value: 0 }))
        }
        
        // 处理统计数据
        if (statsData) {
          // 更新服务类型使用统计 (饼图)
          if (statsData.usageStats && statsData.usageStats.length) {
            this.updateAlgorithmChart(statsData.usageStats)
          } else {
            // 如果没有数据，可以显示空状态或默认示例
            console.warn('未获取到服务类型使用统计数据')
            this.updateAlgorithmChart([]) // 传递空数组以清空图表
          }
          
          // 更新订单完成率 (堆叠条形图)
          if (statsData.successRates && statsData.successRates.length) {
             // 注意：API返回的success/fail是数量，需要转换为百分比
             const processedSuccessRates = statsData.successRates.map(item => {
                const total = item.success + item.fail;
                const successPercentage = total > 0 ? Math.round((item.success / total) * 100) : 0;
                const failPercentage = 100 - successPercentage;
                return {
                  name: item.name,
                  success: successPercentage,
                  fail: failPercentage
                }
             });
             this.updateSuccessRateChart(processedSuccessRates)
          } else {
            console.warn('未获取到订单完成率数据')
            this.updateSuccessRateChart([]) // 清空图表
          }
          
          // 更新访问量趋势 (折线图)
          if (statsData.accessTrend && statsData.accessTrend.dates && statsData.accessTrend.counts) {
            this.updateAccessTrendChart(statsData.accessTrend.dates, statsData.accessTrend.counts)
          } else {
             console.warn('未获取到访问量趋势数据')
             this.updateAccessTrendChart([], []) // 清空图表
          }
          
        } else {
          console.warn('未获取到统计图表数据')
          // 获取数据失败时，清空所有图表或显示默认示例
          this.loadDemoData() // 或清空: this.updateAlgorithmChart([]), etc.
        }
      } catch (error) {
        console.error('获取Dashboard数据失败:', error)
        // 出错时显示默认数据或空状态
        this.statisticsCards = this.statisticsCards.map(card => ({ ...card, value: 0 }))
        this.loadDemoData()
      } finally {
        this.chartsLoading = false
        this.logsLoading = false
      }
    },
    
    // 更新算法使用统计图表
    updateAlgorithmChart(data) {
      if (!this.algorithmChart || !data || !data.length) return
      
      this.algorithmChart.setOption({
        legend: {
          data: data.map(item => item.name)
        },
        series: [{
          data: data
        }]
      })
    },
    
    // 更新破解成功率图表
    updateSuccessRateChart(data) {
      if (!this.successRateChart) return;
      // 更新图表配置以处理空数据
      const options = {
        backgroundColor: 'transparent',
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        legend: { data: [this.successLabel, this.failLabel], textStyle: { color: '#fff', fontSize: 12, fontWeight: 'bold' } },
        grid: { left: '5%', right: '5%', bottom: '5%', containLabel: true },
        xAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%', color: '#fff', fontSize: 12, fontWeight: 'bold' }, axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.5)', width: 2 } }, splitLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.2)', type: 'dashed', width: 1 } } },
        yAxis: { type: 'category', data: data.map(item => item.name), axisLabel: { color: '#fff', fontSize: 12, fontWeight: 'bold', margin: 16 }, axisLine: { lineStyle: { color: 'rgba(255, 255, 255, 0.5)', width: 2 } } },
        series: [
          { name: this.successLabel, type: 'bar', stack: 'total', label: { show: true, formatter: '{c}%', color: '#fff', fontSize: 14, fontWeight: 'bold', textShadowColor: 'rgba(0, 0, 0, 0.3)', textShadowBlur: 3, textShadowOffsetX: 1, textShadowOffsetY: 1 }, emphasis: { focus: 'series' }, data: data.map(item => item.success), itemStyle: { color: '#67C23A' } },
          { name: this.failLabel, type: 'bar', stack: 'total', label: { show: true, formatter: '{c}%', color: '#fff', fontSize: 14, fontWeight: 'bold', textShadowColor: 'rgba(0, 0, 0, 0.3)', textShadowBlur: 3, textShadowOffsetX: 1, textShadowOffsetY: 1 }, emphasis: { focus: 'series' }, data: data.map(item => item.fail), itemStyle: { color: '#F56C6C' } }
        ]
      };
      // 如果没有数据，显示空提示或默认配置
      if (!data || data.length === 0) {
        options.yAxis.data = []; // 清空y轴
        options.series.forEach(s => s.data = []); // 清空系列数据
        // 可以添加一个空状态提示，例如 title.text = '暂无数据'
      }
      this.successRateChart.setOption(options);
    },
    
    // 更新访问量趋势图表
    updateAccessTrendChart(dates, counts) {
      if (!this.accessTrendChart || !dates || !counts) return
      
      this.accessTrendChart.setOption({
        xAxis: {
          data: dates
        },
        series: [{
          data: counts
        }]
      })
    },
    
    // 跳转到算法管理页面
    goToAlgorithms() {
      this.$router.push('/algorithm')
    },
    
    // 跳转到日志管理页面
    goToLogs() {
      this.$router.push('/log')
    }
  },
  watch: {
    // 监听统计卡片数据变化
    statisticsCards: {
      handler(newVal) {
        console.log('统计卡片数据已更新:', JSON.stringify(newVal, null, 2))
      },
      deep: true
    }
  }
}
</script>

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
