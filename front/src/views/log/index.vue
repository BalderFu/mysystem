<template>
  <div class="log-management">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>日志管理</span>
        <el-button-group style="float: right;">
          <!-- <el-button size="small" type="primary" @click="handleExport">导出日志</el-button> -->
          <!-- <el-button size="small" type="danger" @click="handleClear">清空日志</el-button> -->
        </el-button-group>
      </div>
      
      <!-- <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作人">
          <el-input v-model="searchForm.operator" placeholder="请输入操作人" clearable></el-input>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="请选择操作类型" clearable>
            <el-option label="登录" value="LOGIN"></el-option>
            <el-option label="登出" value="LOGOUT"></el-option>
            <el-option label="新增" value="CREATE"></el-option>
            <el-option label="修改" value="UPDATE"></el-option>
            <el-option label="删除" value="DELETE"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form> -->

      <el-table :data="logList" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="操作人" width="120"></el-table-column>
        <el-table-column prop="url" label="请求路径" width="100"></el-table-column>
        <el-table-column prop="request" label="请求参数" show-overflow-tooltip></el-table-column>
        <el-table-column prop="response" label="响应参数" width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="used" label="耗时(ms)" width="180"></el-table-column>
        <el-table-column prop="createTime" label="处理时间" width="180"></el-table-column>
        <!-- <el-table-column label="" width="100" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column> -->
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog title="日志详情" :visible.sync="dialogVisible" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ currentLog.username }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getTagType(currentLog.type)">{{ currentLog.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作内容">{{ currentLog.operation }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentLog.method }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre>{{ currentLog.params }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'LogManagement',
  data() {
    return {
      loading: false,
      searchForm: {
        operator: '',
        operationType: ''
      },
      logList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      currentLog: {}
    }
  },
  created() {
    this.fetchLogList()
  },
  methods: {
    async fetchLogList() {
      this.loading = true
      try {
        const response = await request({
          url: '/log',
          method: 'get',
          params: {
            pageNo: this.currentPage,
            pageSize: this.pageSize,
            operator: this.searchForm.operator,
            operationType: this.searchForm.operationType
          }
        })
        
        if (response.code === 200) {
          this.logList = response.data.records
          this.total = response.data.total
        } else {
          this.$message.error(response.message || '获取日志列表失败')
        }
      } catch (error) {
        console.error('获取日志列表失败:', error)
        this.$message.error('获取日志列表失败')
      } finally {
        this.loading = false
      }
    },
    getTagType(type) {
      const typeMap = {
        LOGIN: 'success',
        OPERATION: 'warning',
        SYSTEM: 'danger'
      }
      return typeMap[type] || 'info'
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchLogList()
    },
    handleReset() {
      this.searchForm = {
        operator: '',
        operationType: ''
      }
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchLogList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchLogList()
    },
    handleDetail(row) {
      this.currentLog = { ...row }
      this.dialogVisible = true
    },
    async handleExport() {
      try {
        // TODO: 替换为实际的API调用
        const response = await this.$http.get('/api/logs/export', {
          responseType: 'blob'
        })
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' })
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = '系统日志.xlsx'
        link.click()
        window.URL.revokeObjectURL(link.href)
      } catch (error) {
        this.$message.error('导出日志失败')
        console.error('导出日志失败:', error)
      }
    },
    async handleClear() {
      try {
        await this.$confirm('确认清空所有日志记录?', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        // TODO: 替换为实际的API调用
        await this.$http.delete('/api/logs')
        this.$message.success('清空日志成功')
        this.fetchLogList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('清空日志失败')
          console.error('清空日志失败:', error)
        }
      }
    },
    getOperationTypeTag(type) {
      const map = {
        LOGIN: 'success',
        LOGOUT: 'info',
        CREATE: 'primary',
        UPDATE: 'warning',
        DELETE: 'danger'
      }
      return map[type] || 'info'
    },
    getOperationTypeText(type) {
      const map = {
        LOGIN: '登录',
        LOGOUT: '登出',
        CREATE: '新增',
        UPDATE: '修改',
        DELETE: '删除'
      }
      return map[type] || type
    }
  }
}
</script>

<style scoped>
.log-management {
  padding: 20px;
}
.search-form {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  text-align: right;
}
pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style> 