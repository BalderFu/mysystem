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
      

      <el-table :data="logList" style="width: 100%" v-loading="loading">
        <el-table-column prop="username" label="操作人" width="120">
          <template slot-scope="scope">
            {{ formatEmptyValue(scope.row.username) }}
          </template>
        </el-table-column>
        <el-table-column prop="url" label="请求路径" width="100">
          <template slot-scope="scope">
            {{ formatEmptyValue(scope.row.url) }}
          </template>
        </el-table-column>
        <el-table-column prop="request" label="请求参数" min-width="300" show-overflow-tooltip>
          <template slot-scope="scope">
            <div style="height: 23px; line-height: 23px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
              {{ formatEmptyValue(scope.row.request) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="response" label="响应参数" min-width="300" show-overflow-tooltip>
          <template slot-scope="scope">
            <div style="height: 23px; line-height: 23px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
              {{ formatEmptyValue(scope.row.response) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="used" label="耗时(ms)" width="180">
          <template slot-scope="scope">
            {{ formatEmptyValue(scope.row.used) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="处理时间" width="180">
          <template slot-scope="scope">
            {{ formatEmptyValue(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleDetail(scope.row)"
            >查看详情</el-button>
          </template>
        </el-table-column>
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
    <el-dialog title="日志详情" :visible.sync="dialogVisible" width="800px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作人">{{ formatEmptyValue(currentLog.username) }}</el-descriptions-item>
        <el-descriptions-item label="请求路径">{{ formatEmptyValue(currentLog.url) }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre style="max-height: 200px; overflow-y: auto;">{{ formatEmptyValue(currentLog.request) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应参数">
          <pre style="max-height: 200px; overflow-y: auto;">{{ formatEmptyValue(currentLog.response) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ formatEmptyValue(currentLog.used) }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ formatEmptyValue(currentLog.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
      </span>
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
    },
    formatEmptyValue(value) {
      return value === null || value === undefined || value === '' ? '-' : value
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

/* 表格相关样式覆盖 */
::v-deep .el-table {
  background-color: transparent !important;
}

::v-deep .el-table tr {
  background-color: transparent !important;
}

::v-deep .el-table th,
::v-deep .el-table td {
  background-color: transparent !important;
}

::v-deep .el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: transparent !important;
}

::v-deep .el-table__body tr.current-row > td {
  background-color: transparent !important;
}

::v-deep .el-table__body tr:hover > td,
::v-deep .el-table__body tr.hover-row > td {
  background-color: transparent !important;
}

/* 去除文本按钮hover时的背景色 */
::v-deep .el-button--text {
  background: transparent !important;
}

::v-deep .el-button--text:hover,
::v-deep .el-button--text:focus,
::v-deep .el-button--text:active {
  background-color: transparent !important;
}

/* 去除表格单元格内所有元素的hover效果 */
::v-deep .el-table__body td .cell *:hover {
  background-color: transparent !important;
}

/* 操作列按钮样式 */
::v-deep .el-table__fixed-right-patch {
  background-color: transparent !important;
}

::v-deep .el-table__fixed-right {
  background-color: transparent !important;
}

::v-deep .el-table__fixed-right .el-table__fixed-body-wrapper {
  background-color: transparent !important;
}

::v-deep .el-table__fixed,
::v-deep .el-table__fixed-right {
  box-shadow: none !important;
}

/* 对话框样式调整 */
::v-deep .el-dialog {
  background-color: var(--background-card, #263445) !important;
}

::v-deep .el-dialog__header {
  background-color: var(--background-card, #263445) !important;
  color: var(--text-primary, #ffffff) !important;
}

::v-deep .el-dialog__title {
  color: var(--text-primary, #ffffff) !important;
}

::v-deep .el-dialog__body {
  background-color: var(--background-card, #263445) !important;
  color: var(--text-primary, #ffffff) !important;
}

::v-deep .el-dialog__footer {
  background-color: var(--background-card, #263445) !important;
}

::v-deep .el-descriptions {
  background-color: transparent !important;
}

::v-deep .el-descriptions__header {
  background-color: transparent !important;
}

::v-deep .el-descriptions__body {
  background-color: transparent !important;
}

::v-deep .el-descriptions-item__label {
  background-color: rgba(0, 0, 0, 0.2) !important;
  color: var(--text-primary, #ffffff) !important;
}

::v-deep .el-descriptions-item__content {
  background-color: transparent !important;
  color: var(--text-primary, #ffffff) !important;
}
</style> 