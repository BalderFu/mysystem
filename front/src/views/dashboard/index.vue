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
                  <div class="stat-value">{{ item.value }}</div>
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
            <span>算法使用统计</span>
          </div>
          <div ref="algorithmChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>破解成功率</span>
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

    <!-- <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>算法管理</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToAlgorithms">查看更多</el-button>
          </div>
          <el-table :data="tableData.slice(0, 5)" style="width: 100%" v-loading="tableLoading">
            <el-table-column prop="name" label="算法名称" width="180"></el-table-column>
            <el-table-column prop="description" label="算法描述" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>最近日志</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="goToLogs">查看更多</el-button>
          </div>
          <el-table :data="recentLogs" style="width: 100%" v-loading="logsLoading">
            <el-table-column prop="username" label="用户" width="100"></el-table-column>
            <el-table-column prop="url" label="操作路径" width="120"></el-table-column>
            <el-table-column prop="createTime" label="时间" width="160"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row> -->
  </div>
</template>

<script>
import * as echarts from 'echarts'
import request from '@/utils/request'
import { arithmeticPage, getArithmeticStats } from '@/api/arithmetic'

export default {
  name: 'Dashboard',
  data() {
    return {
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
          title: '算法总数',
          value: 0,
          icon: 'el-icon-s-platform',
          bgColor: '#67C23A'
        },
        {
          title: '破解任务',
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
  mounted() {
    // 初始化图表
    this.$nextTick(() => {
      this.initCharts()
    })
    
    // 获取数据
    // this.fetchTableData()
    this.fetchRecentLogs()
    this.fetchStatisticsData()
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
      
      // 加载示例数据
      this.loadDemoData()
      
      // 尝试获取真实数据
      this.fetchChartData()
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
        { name: 'MD5', value: 120 },
        { name: 'SHA-1', value: 80 },
        { name: 'SHA-256', value: 60 },
        { name: 'DES', value: 40 },
        { name: 'AES', value: 30 }
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
            name: '算法使用',
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
        { name: 'MD5', success: 75, fail: 25 },
        { name: 'SHA-1', success: 60, fail: 40 },
        { name: 'SHA-256', success: 42, fail: 58 },
        { name: 'DES', success: 88, fail: 12 },
        { name: 'AES', success: 35, fail: 65 }
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
          data: ['成功', '失败'],
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
            name: '成功',
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
            name: '失败',
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
      
      // 更新统计卡片数据
      this.statisticsCards[0].value = 5  // 算法总数
      this.statisticsCards[1].value = 168  // 破解任务
      this.statisticsCards[2].value = 24  // 用户数量
      this.statisticsCards[3].value = 82  // 今日访问量
    },
    
    // 获取图表数据
    async fetchChartData() {
      try {
        this.chartsLoading = true
        
        // 尝试调用后端获取统计数据
        try {
          const response = await getArithmeticStats()
          
          if (response && response.code === 200 && response.data) {
            // 处理算法使用统计
            if (response.data.usageStats && Array.isArray(response.data.usageStats)) {
              this.updateAlgorithmChart(response.data.usageStats)
            }
            
            // 处理破解成功率
            if (response.data.successRates && Array.isArray(response.data.successRates)) {
              this.updateSuccessRateChart(response.data.successRates)
            }
            
            // 处理访问量趋势
            if (response.data.accessTrend && 
                response.data.accessTrend.dates && 
                response.data.accessTrend.counts) {
              this.updateAccessTrendChart(
                response.data.accessTrend.dates,
                response.data.accessTrend.counts
              )
            }
          }
        } catch (error) {
          console.warn('获取统计数据失败，使用模拟数据', error)
          // 如果API不存在，继续使用模拟数据
        }
      } finally {
        this.chartsLoading = false
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
      if (!this.successRateChart || !data || !data.length) return
      
      this.successRateChart.setOption({
        yAxis: {
          data: data.map(item => item.name)
        },
        series: [
          {
            data: data.map(item => item.success)
          },
          {
            data: data.map(item => item.fail)
          }
        ]
      })
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

    
    // 获取最近日志
    async fetchRecentLogs() {
      try {
        this.logsLoading = true
        const response = await request({
          url: '/log',
          method: 'get',
          params: {
            pageNo: 1,
            pageSize: 5
          }
        })
        
        if (response.code === 200) {
          this.recentLogs = response.data.records || []
        }
      } catch (error) {
        console.error('获取日志列表失败:', error)
      } finally {
        this.logsLoading = false
      }
    },
    
    // 获取统计数据
    async fetchStatisticsData() {
      try {
        // 尝试获取真实的统计数据
        const response = await request({
          url: '/arithmetic/dashboard',
          method: 'get'
        }).catch(() => null)
        
        if (response && response.code === 200 && response.data) {
          // 更新统计卡片
          if (response.data.algorithmCount !== undefined) {
            this.statisticsCards[0].value = response.data.algorithmCount
          }
          
          if (response.data.crackTaskCount !== undefined) {
            this.statisticsCards[1].value = response.data.crackTaskCount
          }
          
          if (response.data.userCount !== undefined) {
            this.statisticsCards[2].value = response.data.userCount
          }
          
          if (response.data.todayVisits !== undefined) {
            this.statisticsCards[3].value = response.data.todayVisits
          }
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    },
    
    // 跳转到算法管理页面
    goToAlgorithms() {
      this.$router.push('/algorithm')
    },
    
    // 跳转到日志管理页面
    goToLogs() {
      this.$router.push('/log')
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
