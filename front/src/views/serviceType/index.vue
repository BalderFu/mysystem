<template>
  <div class="service-type-container">
    <el-card class="service-type-card">
      <div slot="header" class="card-header">
        <span>服务类型管理</span>
        <el-button type="primary" size="small" @click="handleAdd">添加服务类型</el-button>
      </div>
      <div class="service-type-content">
        <!-- 服务类型列表 -->
        <el-table
          :data="serviceTypeList"
          border
          v-loading="loading"
          style="width: 100%">
          <el-table-column
            prop="id"
            label="ID"
            width="80"
            align="center">
          </el-table-column>
          <el-table-column
            prop="name"
            label="服务类型名称"
            align="center">
          </el-table-column>
          <el-table-column
            prop="description"
            label="描述"
            align="center">
          </el-table-column>
          <el-table-column
            prop="price"
            label="价格(元/小时)"
            width="120"
            align="center">
          </el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            width="160"
            align="center">
          </el-table-column>
          <el-table-column
            label="操作"
            width="180"
            align="center">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="primary"
                @click="handleEdit(scope.row)">编辑</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          class="pagination">
        </el-pagination>
      </div>
    </el-card>
    
    <!-- 添加/编辑服务类型对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px">
      <el-form :model="serviceTypeForm" :rules="formRules" ref="serviceTypeForm" label-width="100px">
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="serviceTypeForm.name" placeholder="请输入服务类型名称"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="serviceTypeForm.description" placeholder="请输入描述"></el-input>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="serviceTypeForm.price" :min="0" :precision="2" :step="10" controls-position="right"></el-input-number>
          <span class="price-unit">元/小时</span>
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
import request from '@/utils/request'

export default {
  name: 'ServiceType',
  data() {
    return {
      // 服务类型列表数据
      serviceTypeList: [],
      // 加载状态
      loading: false,
      // 分页参数
      currentPage: 1,
      pageSize: 10,
      total: 0,
      // 对话框控制
      dialogVisible: false,
      dialogTitle: '添加服务类型',
      submitLoading: false,
      // 表单数据
      serviceTypeForm: {
        id: null,
        name: '',
        description: '',
        price: 0
      },
      // 表单验证规则
      formRules: {
        name: [
          { required: true, message: '请输入服务类型名称', trigger: 'blur' },
          { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入描述', trigger: 'blur' },
          { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 获取服务类型列表
    getServiceTypeList() {
      this.loading = true;
      
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize
      };
      
      
      // 实际API调用示例
      request({
        url: '/serviceType/page',
        method: 'get',
        params: params
      }).then(response => {
        this.loading = false;
        if (response.code === 200) {
          this.serviceTypeList = response.data.records;
          this.total = response.data.total;
        } else {
          this.$message.error(response.message || '获取服务类型列表失败');
        }
      }).catch(error => {
        this.loading = false;
        this.$message.error('获取服务类型列表失败');
        console.error('获取服务类型列表失败:', error);
      });
    },
    
    // 处理添加按钮点击
    handleAdd() {
      this.dialogTitle = '添加服务类型';
      this.serviceTypeForm = {
        id: null,
        name: '',
        description: '',
        price: 0
      };
      this.dialogVisible = true;
      
      // 等待DOM更新后重置表单验证
      this.$nextTick(() => {
        if (this.$refs.serviceTypeForm) {
          this.$refs.serviceTypeForm.clearValidate();
        }
      });
    },
    
    // 处理编辑按钮点击
    handleEdit(row) {
      this.dialogTitle = '编辑服务类型';
      this.serviceTypeForm = { ...row };
      this.dialogVisible = true;
      
      // 等待DOM更新后重置表单验证
      this.$nextTick(() => {
        if (this.$refs.serviceTypeForm) {
          this.$refs.serviceTypeForm.clearValidate();
        }
      });
    },
    
    // 处理删除按钮点击
    handleDelete(row) {
      this.$confirm('确认删除该服务类型？删除后不可恢复', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: `/serviceType?id=${row.id}`,
          method: 'delete'
        }).then(response => {
          if (response.code === 200) {
            this.$message.success('删除成功');
            this.getServiceTypeList();
          } else {
            this.$message.error(response.message || '删除失败');
          }
        }).catch(error => {
          this.$message.error('删除失败');
          console.error('删除失败:', error);
        });
        
      
      }).catch(() => {
        // 取消删除操作
      });
    },
    
    // 提交表单
    submitForm() {
      this.$refs.serviceTypeForm.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          
          const isEdit = this.serviceTypeForm.id !== null;
          const method = isEdit ? 'put' : 'post';
          
          request({
            url: '/serviceType',
            method: method,
            data: this.serviceTypeForm
          }).then(response => {
            this.submitLoading = false;
            if (response.code === 200) {
              this.$message.success(isEdit ? '更新成功' : '添加成功');
              this.dialogVisible = false;
              this.getServiceTypeList();
            } else {
              this.$message.error(response.message || (isEdit ? '更新失败' : '添加失败'));
            }
          }).catch(error => {
            this.submitLoading = false;
            this.$message.error(isEdit ? '更新失败' : '添加失败');
            console.error(isEdit ? '更新失败:' : '添加失败:', error);
          });
          
        
        }
      });
    },
    
    // 每页条数变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.getServiceTypeList();
    },
    
    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.getServiceTypeList();
    }
  },
  mounted() {
    this.getServiceTypeList();
  }
}
</script>

<style lang="scss" scoped>
.service-type-container {
  padding: 20px;
  
  .service-type-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      span {
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .service-type-content {
      padding: 10px 0;
      
      .pagination {
        margin-top: 20px;
        text-align: right;
      }
    }
  }
  
  .price-unit {
    margin-left: 10px;
    color: #606266;
  }
}
</style> 