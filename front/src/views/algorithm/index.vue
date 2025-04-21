<template>
  <div class="algorithm-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>算法管理</span>
        <el-button style="float: right;" size="small" type="primary" @click="handleAdd">新增算法</el-button>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="算法名称">
          <el-input v-model="searchForm.name" placeholder="请输入算法名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <div class="custom-table-wrapper">
        <el-table 
          :data="tableData" 
          style="width: 100%" 
          v-loading="tableLoading"
          :show-header="true"
          :header-cell-style="{background:'transparent',color:'#fff'}"
          :header-row-style="{background:'transparent'}"
          :border="false">
          <el-table-column prop="name" label="算法名称" width="160"></el-table-column>
          <el-table-column prop="keyword" label="算法关键词" width="160"></el-table-column>
          <el-table-column prop="description" label="算法描述" min-width="200" show-overflow-tooltip></el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160"></el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template slot-scope="scope">
              <el-button type="text" size="small" style="color: #409EFF; margin: 0 5px;" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="text" size="small" style="color: #F56C6C; margin: 0 5px;" @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pageParams.page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageParams.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="算法名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入算法名称"></el-input>
        </el-form-item>
        <el-form-item label="算法关键词" prop="keyword">
          <el-input v-model="form.keyword" placeholder="系统目前只支持md5,sha1,sha256,sha512"></el-input>
        </el-form-item>
        <el-form-item label="算法描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="4" placeholder="请输入算法描述"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { arithmeticPage, getArithmeticById, addArithmetic, updateArithmetic, deleteArithmetic } from '@/utils/inputs'

export default {
  name: 'AlgorithmManagement',
  data() {
    return {
      // 搜索表单
      searchForm: {
        name: ''
      },
      
      // 表格数据
      tableData: [],
      tableLoading: false,
      
      // 分页参数
      pageParams: {
        page: 1,
        size: 10
      },
      total: 0,
      
      // 弹窗控制
      dialogVisible: false,
      dialogTitle: '新增算法',
      submitLoading: false,
      
      // 表单数据
      form: {
        id: null,
        name: '',
        keyword: '',
        description: ''
      },
      
      // 表单验证规则
      rules: {
        name: [
          { required: true, message: '请输入算法名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        keyword: [
          { required: true, message: '请输入算法关键词', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入算法描述', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchTableData()
  },
  methods: {
    // 获取表格数据
    async fetchTableData() {
      try {
        this.tableLoading = true
        const params = {
          ...this.pageParams,
          name: this.searchForm.name
        }
        
        const response = await arithmeticPage(params)
        console.log('获取算法列表响应:', response)
        
        if (response.data) {
          this.tableData = response.data.records || []
          this.total = response.data.total || 0
        }
      } catch (error) {
        console.error('获取算法列表失败:', error)
        this.$message.error('获取算法列表失败')
      } finally {
        this.tableLoading = false
      }
    },
    
    // 搜索
    handleSearch() {
      this.pageParams.page = 1
      this.fetchTableData()
    },
    
    // 重置
    handleReset() {
      this.searchForm.name = ''
      this.pageParams.page = 1
      this.fetchTableData()
    },
    
    // 新增
    handleAdd() {
      this.dialogTitle = '新增算法'
      this.form = {
        id: null,
        name: '',
        keyword: '',
        description: ''
      }
      this.dialogVisible = true
    },
    
    // 编辑
    async handleEdit(row) {
      try {
        const response = await getArithmeticById({ id: row.id })
        if (response.data) {
          this.form = { ...response.data }
          this.dialogTitle = '编辑算法'
          this.dialogVisible = true
        }
      } catch (error) {
        console.error('获取算法详情失败:', error)
        this.$message.error('获取算法详情失败')
      }
    },
    
    // 删除
    handleDelete(row) {
      this.$confirm('此操作将永久删除该算法，是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteArithmetic({ id: row.id })
          this.$message.success('删除成功')
          this.fetchTableData()
        } catch (error) {
          console.error('删除算法失败:', error)
          this.$message.error('删除算法失败')
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    
    // 提交表单
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
            this.submitLoading = true
            
            if (this.form.id) {
              // 编辑
              await updateArithmetic(this.form).then(res => { 
                if (res.code === 200) {
                  this.$message.success('更新成功')
                  this.dialogVisible = false  
                  this.fetchTableData()
                } else {
                  this.$message.error(res.message || '更新失败')
                }
              })
            } else {
              // 新增
              await addArithmetic(this.form).then(res => { 
                if (res.code === 200) {
                  this.$message.success('新增成功')
                  this.dialogVisible = false
                  this.fetchTableData()
                } else {
                  this.$message.error(res.message || '新增失败')
                }
              })
            }
          } finally {
            this.submitLoading = false
          }
        } else {
          return false
        }
      })
    },
    
    // 分页大小变化
    handleSizeChange(val) {
      this.pageParams.size = val
      this.fetchTableData()
    },
    
    // 当前页变化
    handleCurrentChange(val) {
      this.pageParams.page = val
      this.fetchTableData()
    }
  }
}
</script>

<style scoped>
.algorithm-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  text-align: right;
}

.delete-btn {
  color: #F56C6C !important;
}

.custom-table-wrapper {
  overflow: hidden;
}

/* 表格相关样式覆盖 */
::v-deep .el-table,
::v-deep .el-table__header-wrapper,
::v-deep .el-table__body-wrapper {
  background-color: transparent !important;
}

::v-deep .el-table::before,
::v-deep .el-table::after {
  display: none !important;
}

::v-deep .el-table td,
::v-deep .el-table th.is-leaf {
  border-bottom: 1px solid rgba(255, 255, 255, 0.1) !important;
  background-color: transparent !important;
}

::v-deep .el-table td {
  border-right: none !important;
}

::v-deep .el-table th {
  border-right: none !important;
  background-color: transparent !important;
}

::v-deep .el-table__row:hover > td {
  background-color: transparent !important;
}

::v-deep .el-table .cell {
  line-height: 23px;
}

/* 表头文字颜色 */
::v-deep .el-table th > .cell {
  color: var(--text-primary, #FFFFFF) !important;
  font-weight: bold;
}

/* 表头容器背景色设置 */
::v-deep .el-table__header {
  background-color: transparent !important;
}

::v-deep .el-table tr,
::v-deep .el-table th,
::v-deep .el-table__header,
::v-deep .el-table__footer {
  background-color: transparent !important;
  color: var(--text-primary, #FFFFFF) !important;
}

/* 表格行样式 */
::v-deep .el-table__body tr {
  background-color: transparent !important;
}

/* 禁用所有边框 */
::v-deep .el-table--border,
::v-deep .el-table--group {
  border: none !important;
}

::v-deep .el-table--border::after,
::v-deep .el-table--border::before,
::v-deep .el-table--group::after,
::v-deep .el-table--group::before {
  display: none !important;
}

/* 文本按钮样式 */
::v-deep .el-button--text {
  padding: 0 5px;
  font-size: 12px;
  background: transparent !important;
  border: none !important;
}

::v-deep .el-button--text:focus {
  color: inherit;
}

::v-deep .el-button--text:hover,
::v-deep .el-button--text:focus,
::v-deep .el-button--text:active {
  background-color: transparent !important;
}

/* 对话框样式 */
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

/* 表单样式 */
::v-deep .el-form-item__label {
  color: var(--text-primary, #ffffff) !important;
}

::v-deep .el-input__inner {
  background-color: rgba(0, 0, 0, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: var(--text-primary, #ffffff) !important;
}

::v-deep .el-textarea__inner {
  background-color: rgba(0, 0, 0, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.1) !important;
  color: var(--text-primary, #ffffff) !important;
}
</style> 