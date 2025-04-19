<template>
  <div class="user-management">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>用户管理</span>
        <el-button-group style="float: right;">
          <el-button size="small" type="primary" @click="handleAdd">
            <i class="el-icon-plus"></i> 新增用户
          </el-button>
          <el-button size="small" type="danger" @click="handleBatchDelete" :disabled="!selectedUsers.length">
            <i class="el-icon-delete"></i> 批量删除
          </el-button>
        </el-button-group>
      </div>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <!-- <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
            <el-option label="管理员" value="admin"></el-option>
            <el-option label="普通用户" value="user"></el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="userList"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="120"></el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'success'">
              {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
            <!-- <el-button size="mini" type="text" @click="handleResetPassword(scope.row)">重置密码</el-button> -->
            <el-button size="mini" type="text" class="delete-btn" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 用户表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="userForm" :rules="rules" ref="userForm" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin"></el-option>
            <el-option label="普通用户" value="normal"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'UserManagement',
  data() {
    return {
      loading: false,
      submitting: false,
      searchForm: {
        username: '',
        role: ''
      },
      userList: [],
      selectedUsers: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogType: 'add', // 'add' 或 'edit'
      dialogTitle: '新增用户',
      userForm: {
        username: '',
        email: '',
        phone: '',
        role: 'normal',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        role: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchUserList()
  },
  methods: {
    async fetchUserList() {
      this.loading = true
      try {
        const response = await request({
          url: '/user/pager',
          method: 'get',
          params: {
            pageNo: this.currentPage,
            pageSize: this.pageSize,
            username: this.searchForm.username
          }
        })
        
        if (response.code === 200) {
          this.userList = response.data.records
          this.total = response.data.total
        } else {
          this.$message.error(response.message || '获取用户列表失败')
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        this.$message.error('获取用户列表失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchUserList()
    },
    handleReset() {
      this.searchForm = {
        username: '',
        role: ''
      }
      this.handleSearch()
    },
    handleSelectionChange(val) {
      this.selectedUsers = val
    },
    handleAdd() {
      this.dialogType = 'add'
      this.dialogTitle = '新增用户'
      this.userForm = {
        username: '',
        email: '',
        phone: '',
        role: 'normal',
        password: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogType = 'edit'
      this.dialogTitle = '编辑用户'
      this.userForm = { ...row }
      delete this.userForm.password
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.userForm.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            const url = this.dialogType === 'add' ? '/user/add' : '/user/update'
            const type = this.dialogType === 'add' ? 'post' : 'put'
            const response = await request({
              url,
              method: type,
              data: this.userForm
            })
            
            if (response.code === 200) {
              this.$message.success(this.dialogType === 'add' ? '添加成功' : '更新成功')
              this.dialogVisible = false
              this.fetchUserList()
            } else {
              this.$message.error(response.message || (this.dialogType === 'add' ? '添加失败' : '更新失败'))
            }
          } catch (error) {
            console.error(this.dialogType === 'add' ? '添加用户失败:' : '更新用户失败:', error)
            this.$message.error(this.dialogType === 'add' ? '添加失败' : '更新失败')
          } finally {
            this.submitting = false
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该用户?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await request({
            url: '/user/delete',
            method: 'get',
            params: { userId: row.id }
          })
          
          if (response.code === 200) {
            this.$message.success('删除成功')
            this.fetchUserList()
          } else {
            this.$message.error(response.message || '删除失败')
          }
        } catch (error) {
          console.error('删除用户失败:', error)
          this.$message.error('删除失败')
        }
      }).catch(() => {})
    },
    handleBatchDelete() {
      if (!this.selectedUsers.length) {
        this.$message.warning('请选择要删除的用户')
        return
      }
      
      this.$confirm(`确认删除选中的 ${this.selectedUsers.length} 个用户?`, '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await request({
            url: '/user/delete',
            method: 'post',
            data: { userIds: this.selectedUsers.map(user => user.id) }
          })
          
          if (response.code === 200) {
            this.$message.success('批量删除成功')
            this.fetchUserList()
          } else {
            this.$message.error(response.message || '批量删除失败')
          }
        } catch (error) {
          console.error('批量删除用户失败:', error)
          this.$message.error('批量删除失败')
        }
      }).catch(() => {})
    },
    handleResetPassword(row) {
      this.$confirm('确认重置该用户的密码?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await request({
            url: '/user/resetPassword',
            method: 'post',
            data: { id: row.id }
          })
          
          if (response.code === 200) {
            this.$message.success('密码重置成功')
          } else {
            this.$message.error(response.message || '密码重置失败')
          }
        } catch (error) {
          console.error('重置密码失败:', error)
          this.$message.error('密码重置失败')
        }
      }).catch(() => {})
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchUserList()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchUserList()
    }
  }
}
</script>

<style lang="scss" scoped>
.user-management {
  padding: 20px;
  
  .search-form {
    margin-bottom: 20px;
  }
  
  .el-pagination {
    margin-top: 20px;
    text-align: right;
  }

  :deep(.el-table) {
    background-color: transparent;
    
    &::before,
    &::after {
      display: none;
    }
    
    .el-table__header-wrapper {
      background-color: transparent;
      
      th {
        background-color: transparent;
        border-bottom-color: rgba(255, 255, 255, 0.1);
        color: #fff;
      }
    }
    
    .el-table__body-wrapper {
      background-color: transparent;
      
      tr {
        background-color: transparent;
        
        td {
          background-color: transparent;
          border-bottom-color: rgba(255, 255, 255, 0.1);
          color: #fff;
        }
        
        &:hover > td {
          background-color: transparent !important;
        }
      }
    }
    
    .el-table__cell {
      background-color: transparent;
    }
    
    .el-button {
      border: none;
      background: transparent !important;
      padding: 0;
      margin: 0 10px;
      font-size: 14px;
      color: #409EFF;
      
      &:hover, &:focus, &:active {
        background: transparent !important;
        color: #66b1ff;
      }
      
      &.delete-btn {
        color: #F56C6C;
        
        &:hover {
          color: #ff7875;
        }
      }
    }
  }
  
  .el-tag {
    text-transform: capitalize;
  }
}
</style> 