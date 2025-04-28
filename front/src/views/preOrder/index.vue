<template>
  <div class="pre-order-container">
    <el-card class="pre-order-card">
      <div slot="header" class="card-header">
        <span>家政预订单管理</span>
        <!-- 取消注释：显示添加按钮 -->
        <el-button type="primary" size="small" @click="handleAdd">添加预订单</el-button>
      </div>
      <div class="pre-order-content">
        <!-- 搜索区域 -->
        <div class="search-container">
          <el-form :inline="true" :model="searchForm" class="search-form" size="small">
            <el-form-item label="联系人姓名">
              <el-input v-model="searchForm.name" placeholder="请输入联系人姓名" clearable></el-input>
            </el-form-item>
             <el-form-item label="联系电话">
              <el-input v-model="searchForm.phone" placeholder="请输入联系电话" clearable></el-input>
            </el-form-item>
            <el-form-item label="服务项目">
              <el-select v-model="searchForm.projectId" placeholder="请选择项目" clearable filterable>
                <el-option
                  v-for="item in projectOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
              <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 预订单列表 -->
        <el-table
          :data="preOrderList"
          border
          v-loading="loading"
          style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
          <el-table-column prop="projectName" label="服务项目" align="center" show-overflow-tooltip></el-table-column>
          <el-table-column prop="name" label="联系人姓名" width="120" align="center"></el-table-column>
          <el-table-column prop="phone" label="联系电话" width="120" align="center"></el-table-column>
          <el-table-column prop="address" label="服务地址" align="center" show-overflow-tooltip></el-table-column>
          <el-table-column prop="time" label="预约时间" width="150" align="center"></el-table-column>
          <el-table-column prop="price" label="价格(元)" width="100" align="center">
             <template slot-scope="scope">
              {{ scope.row.price ? scope.row.price.toFixed(2) : '0.00' }}
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" align="center"></el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
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
    
    <!-- 添加/编辑预订单对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="dialogClose">
      <el-form :model="preOrderForm" :rules="formRules" ref="preOrderFormRef" label-width="100px" size="small">
        
        <!-- 仅编辑时显示订单ID -->
        <el-form-item label="订单ID" v-if="isEditMode">
          <span>{{ preOrderForm.id }}</span>
        </el-form-item>

         <!-- 添加/编辑时都可选项目 -->
         <el-form-item label="服务项目" prop="projectId">
           <el-select v-model="preOrderForm.projectId" placeholder="请选择服务项目" filterable style="width: 100%;" @change="handleProjectChange">
              <el-option
                 v-for="item in projectOptions"
                 :key="item.id"
                 :label="item.name"
                 :value="item.id">
                  <span>{{ item.name }} - {{ item.price ? item.price.toFixed(2) : 'N/A' }}元</span>
              </el-option>
            </el-select>
         </el-form-item>

         <!-- 添加/编辑时都可输入联系人姓名 -->
         <el-form-item label="联系人姓名" prop="name">
           <el-input v-model="preOrderForm.name" placeholder="请输入联系人姓名"></el-input>
         </el-form-item>

          <!-- 添加/编辑时都可输入联系电话 -->
         <el-form-item label="联系电话" prop="phone">
           <el-input v-model="preOrderForm.phone" placeholder="请输入联系电话"></el-input>
         </el-form-item>

         <!-- 添加/编辑时都可输入服务地址 -->
          <el-form-item label="服务地址" prop="address">
           <el-input type="textarea" :rows="2" v-model="preOrderForm.address" placeholder="请输入详细服务地址"></el-input>
         </el-form-item>

         <!-- 添加/编辑时都可输入预约时间 -->
         <el-form-item label="预约时间" prop="time">
           <el-input v-model="preOrderForm.time" placeholder="例如: 2023-12-25 14:30"></el-input>
         </el-form-item>

         <!-- 添加/编辑时都可输入价格 -->
          <el-form-item label="价格" prop="price">
           <el-input-number v-model="preOrderForm.price" :precision="2" :step="10" :min="0" controls-position="right"></el-input-number>
           <span class="price-unit">元</span>
         </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading" size="small">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import systemService from '@/services/systemService';
import Constants from '@/utils/constants';

// 订单状态映射
const statusMap = {
  1: { text: '待确认', type: 'warning' },
  2: { text: '已确认', type: 'primary' },
  3: { text: '服务中', type: 'info' },
  4: { text: '已完成', type: 'success' },
  5: { text: '已取消', type: 'danger' }
};

export default {
  name: 'PreOrder',
  data() {
     // 初始化表单数据
    const initForm = () => ({
      id: null,
      projectId: null,
      projectName: '',
      price: 0,
      time: '',
      address: '',
      phone: '',
      name: ''
    });

    return {
      isEditMode: false, // 添加编辑模式标记
      statusMap, // 在模板中使用
      // 搜索表单
      searchForm: {
        name: '',
        phone: '',
        projectId: ''
      },
      // 预订单列表数据
      preOrderList: [],
      // 项目选项 (用于搜索和可能的添加)
      projectOptions: [],
      // 加载状态
      loading: false,
      // 分页参数
      currentPage: 1,
      pageSize: 10,
      total: 0,
      // 对话框控制
      dialogVisible: false,
      dialogTitle: '编辑预订单状态', // 后台通常是编辑状态
      submitLoading: false,
      // 表单数据
      preOrderForm: initForm(),
      initForm: initForm,
      // 表单验证规则
      formRules: {
         projectId: [
          { required: true, message: '请选择服务项目', trigger: 'change' }
        ],
        name: [
            { required: true, message: '请输入联系人姓名', trigger: 'blur' },
            { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
            { required: true, message: '请输入联系电话', trigger: 'blur' },
            // 更宽松的电话校验，允许固话等
            { pattern: /^[0-9\-\s\(\)]+$/, message: '请输入有效的联系电话', trigger: 'blur' }
            // 如果只允许手机号：
            // { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        address: [
            { required: true, message: '请输入服务地址', trigger: 'blur' },
            { max: 100, message: '地址不能超过100个字符', trigger: 'blur' }
        ],
        time: [
            { required: true, message: '请输入预约时间', trigger: 'blur' }
            // 可以添加更复杂的格式校验
        ],
        price: [
            { required: true, message: '请输入价格', trigger: 'blur' },
            { type: 'number', message: '价格必须为数字值'}
        ]
      }
    }
  },
  created() {
    this.getProjectOptions();
    this.getPreOrderList();
  },
  methods: {
    getProjectOptions() {
      request({
        url: '/project/list',
        method: 'get'
      }).then(response => {
        if (response && response.code === 200) {
          this.projectOptions = response.data || [];
        } else { 
            this.$message.error('请求预订单列表接口失败');
            console.error("获取预订单列表失败:", error);
        }
      }).catch(() => { 
        this.$message.error('请求预订单列表接口失败');
        console.error("获取预订单列表失败:", error);
      });

    
    },
    


    // 获取预订单列表
    getPreOrderList() {
      this.loading = true;
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        name: this.searchForm.name || null,
        phone: this.searchForm.phone || null,
        projectId: this.searchForm.projectId || null
      };
      

      request({
        url: '/preOrder/page',
        method: 'get',
        params: params
      }).then(response => {
        this.loading = false;
        if (response && response.code === 200) {
          const data = response.data || {};
          this.preOrderList = data.records || [];
          this.total = data.total || 0;
          this.handleProjectNames();
        } else {
           this.$message.warning(response.message || '获取预订单列表失败');
        }
      }).catch((error) => {
        this.loading = false;
        this.$message.error('请求预订单列表接口失败');
        console.error("获取预订单列表失败:", error);
      });
    },

    

    // 处理表格中项目名称的显示
    handleProjectNames() {
      this.preOrderList.forEach(item => {
        const project = this.projectOptions.find(opt => opt.id === item.projectId);
        item.projectName = project ? project.name : '未知项目';
      });
    },

     // 获取状态文字
    getStatusText(state) {
      return statusMap[state] ? statusMap[state].text : '未知状态';
    },

    // 获取状态标签类型
    getStatusTagType(state) {
      return statusMap[state] ? statusMap[state].type : 'info';
    },

    // 搜索
    handleSearch() {
      this.currentPage = 1; 
      this.getPreOrderList();
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = { name: '', phone: '', projectId: '', state: '' };
      this.currentPage = 1;
      this.getPreOrderList();
    },
    
    // 每页条数变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.getPreOrderList();
    },
    
    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.getPreOrderList();
    },
    
    // 添加按钮点击
    handleAdd() {
      this.isEditMode = false; 
      this.dialogTitle = '添加预订单';
      this.preOrderForm = this.initForm(); // 重置表单
      this.dialogVisible = true;
      this.$nextTick(() => {
         if (this.$refs.preOrderFormRef) {
            this.$refs.preOrderFormRef.clearValidate();
         }
      });
    },
    
    // 编辑按钮点击
    handleEdit(row) {
      this.isEditMode = true; // 设置为编辑模式
      this.dialogTitle = '编辑预订单状态';
      this.preOrderForm = { ...row }; 
      this.preOrderForm.state = Number(row.state);
      const project = this.projectOptions.find(opt => opt.id === row.projectId);
      this.preOrderForm.projectName = project ? project.name : '未知项目';

      this.dialogVisible = true;
       // 清除表单验证状态
      this.$nextTick(() => {
         if (this.$refs.preOrderFormRef) {
           this.$refs.preOrderFormRef.clearValidate();
         }
      });
    },

    // 对话框关闭时的回调
    dialogClose() {
        // 重置编辑模式标记
        // this.isEditMode = false; // 也可以在这里重置，看习惯
        if (this.$refs.preOrderFormRef) {
            this.$refs.preOrderFormRef.clearValidate();
        }
    },

    // 选择项目时，自动填充价格 (可选功能)
    handleProjectChange(projectId) {
        const selectedProject = this.projectOptions.find(opt => opt.id === projectId);
        if (selectedProject && selectedProject.price !== undefined) {
            this.preOrderForm.price = selectedProject.price;
        }
    },
    
    // 删除按钮点击
    handleDelete(row) {
      this.$confirm(`确定要删除ID为 ${row.id} 的预订单吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true;
        request({
          url: `/preOrder?id=${row.id}`,
          method: 'delete'
        }).then(response => {
          this.loading = false;
          if (response && response.code === 200) {
            this.$message.success('删除成功');
            this.getPreOrderList();
          } else {
            this.$message.error(response.message || '删除失败');
          }
        }).catch((error) => {
          this.loading = false;
          this.$message.error('请求删除接口失败');
          console.error("删除预订单失败:", error);
        });

    

      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    
    // 提交表单（添加 或 编辑状态）
    submitForm() {
      this.$refs.preOrderFormRef.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          
          const isUpdate = this.isEditMode;
          const method = isUpdate ? 'put' : 'post';
          const data = {
            id: this.preOrderForm.id,
            projectId: this.preOrderForm.projectId,
            name: this.preOrderForm.name,
            phone: this.preOrderForm.phone,
            address: this.preOrderForm.address,
            time: this.preOrderForm.time,
            price: this.preOrderForm.price
          }
          const url = `/preOrder`;
          
          request({
            url: url,
            method: method,
            data: data 
          }).then(response => {
            this.submitLoading = false;
            if (response && response.code === 200) {
              this.$message.success(isUpdate ? '更新成功' : '添加成功');
              this.dialogVisible = false;
              this.getPreOrderList(); // 刷新列表
            } else {
              this.$message.error(response.message || (isUpdate ? '更新失败' : '添加失败'));
            }
          }).catch((error) => {
            this.submitLoading = false;
            this.$message.error(isUpdate ? '请求更新接口失败' : '请求添加接口失败');
            console.error(isUpdate ? "更新预订单失败:" : "添加预订单失败:", error);
          });
        } else {
          console.log('表单验证失败');
          return false;
        }
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.pre-order-container {
  padding: 20px;

  .pre-order-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      span {
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .pre-order-content {
      padding-top: 10px;

       .search-container {
         margin-bottom: 15px;
         .search-form .el-form-item {
             margin-bottom: 5px;
         }
       }
      
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

// 修改table样式，去掉白色背景
::v-deep .el-table {
  background-color: transparent !important;
  
  &::before {
    height: 0; // 移除底部边框线
  }
  
  tr {
    background-color: transparent !important;
  }
  
  th.el-table__cell {
    background-color: #1f2d3d !important;
    border-color: #3a4a5e !important;
    color: #e0e0e0 !important;
  }
  
  td.el-table__cell {
    background-color: transparent !important;
    border-color: #3a4a5e !important;
    color: #e0e0e0 !important;
  }
  
  .el-table__body tr:hover > td.el-table__cell {
    background-color: rgba(45, 55, 72, 0.8) !important;
  }
  
  .el-table__body tr.hover-row > td.el-table__cell {
    background-color: rgba(45, 55, 72, 0.8) !important;
  }
  
  // 斑马纹样式处理
  .el-table__body tr.el-table__row--striped td.el-table__cell {
    background-color: rgba(45, 55, 72, 0.3) !important;
  }
  
  // 表格内的分割线颜色
  .el-table__border-left-patch, 
  .el-table__border-right-patch, 
  .el-table__border-top-patch {
    background-color: #3a4a5e !important;
  }
  
  .el-table__fixed-right::before, 
  .el-table__fixed::before {
    background-color: #3a4a5e !important;
  }
  
  // 空数据样式
  .el-table__empty-block {
    background-color: transparent !important;
    
    .el-table__empty-text {
      color: #e0e0e0 !important;
    }
  }
}

// 分页组件样式调整
::v-deep .el-pagination {
  background-color: transparent !important;
  color: #e0e0e0 !important;
  
  .btn-prev, 
  .btn-next, 
  .el-pager li {
    background-color: transparent !important;
    color: #e0e0e0 !important;
  }
  
  .el-pager li:not(.disabled).active {
    background-color: #409EFF !important;
    color: #fff !important;
  }
  
  .el-pagination__total, 
  .el-pagination__jump {
    color: #e0e0e0 !important;
  }
}
</style>
