<template>
  <div class="tempo-container">
    <el-card class="tempo-card">
      <div slot="header" class="card-header">
        <span>服务进度管理</span>
        <!-- <el-button type="primary" size="small" @click="handleAdd">更新进度</el-button>  -->
      </div>
      <div class="tempo-content">
        <!-- 搜索区域 -->
        <div class="search-container">
          <el-form :inline="true" :model="searchForm" class="search-form" size="small">
            <el-form-item label="订单名称">
              <el-select v-model="searchForm.projectId" placeholder="请选择项目" clearable filterable>
                <el-option
                  v-for="item in projectOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="进度状态">
              <el-select v-model="searchForm.state" placeholder="请选择状态" clearable>
                <el-option
                  v-for="(item, key) in statusMap"
                  :key="key"
                  :label="item.text"
                  :value="parseInt(key)">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
              <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 进度列表 -->
        <el-table
          :data="tempoList"
          border
          v-loading="loading"
          style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
          <el-table-column prop="projectName" label="订单名称" align="center" show-overflow-tooltip></el-table-column>
          <el-table-column prop="state" label="进度状态" width="120" align="center">
             <template slot-scope="scope">
                <el-tag :type="getStatusTagType(scope.row.state)" size="small">
                  {{ getStatusText(scope.row.state) }}
                </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" align="center"></el-table-column>
          <el-table-column prop="updateTime" label="更新时间" width="160" align="center"></el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" icon="el-icon-edit" @click="handleEdit(scope.row)">更新状态</el-button>
              <!-- <el-button size="mini" type="danger" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button> -->
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
    
    <!-- 添加/编辑服务进度对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
      @close="dialogClose">
      <el-form :model="tempoForm" :rules="formRules" ref="tempoFormRef" label-width="100px" size="small">
        <el-form-item label="订单名称" prop="projectId">
           <!-- 新增时可选，编辑时通常只显示 -->
           <el-select v-model="tempoForm.projectId" placeholder="请选择项目" filterable style="width: 100%;" :disabled="isEditMode">
             <el-option
                v-for="item in projectOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
             </el-option>
           </el-select>
           <!-- <span v-if="isEditMode">{{ tempoForm.projectName }}</span> -->
        </el-form-item>
        <el-form-item label="进度状态" prop="state">
          <el-select v-model="tempoForm.state" placeholder="请选择状态" style="width: 100%">
             <el-option
                v-for="(item, key) in statusMap"
                :key="key"
                :label="item.text"
                :value="parseInt(key)">
             </el-option>
          </el-select>
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
import Constants from '@/utils/constants';

// 服务进度状态映射
const statusMap = {
  1: { text: '上门中', type: 'warning' },
  2: { text: '服务中', type: 'primary' },
  3: { text: '服务完成', type: 'success' }
};

export default {
  name: 'ServiceTempo',
  data() {
     // 初始化表单数据
    const initForm = () => ({
      id: null, // 用于标识是编辑还是新增
      projectId: null,
      projectName: '', // 辅助显示字段
      state: null,
    });

    return {
      statusMap, // 供模板使用
      isEditMode: false, // 标记是否为编辑模式
      // 搜索表单
      searchForm: {
        projectId: '',
        state: ''
      },
      // 进度列表数据
      tempoList: [],
      // 项目选项
      projectOptions: [],
      // 加载状态
      loading: false,
      // 分页参数
      currentPage: 1,
      pageSize: 10,
      total: 0,
      // 对话框控制
      dialogVisible: false,
      dialogTitle: '添加/更新服务进度',
      submitLoading: false,
      // 表单数据
      tempoForm: initForm(),
      initForm: initForm,
      // 表单验证规则
      formRules: {
        projectId: [
          { required: true, message: '请选择关联项目', trigger: 'change' }
        ],
        state: [
          { required: true, message: '请选择进度状态', trigger: 'change' }
        ],
      }
    }
  },
  created() {
    // 初始化加载数据
    this.getProjectOptions();
    this.getTempoList();
  },
  methods: {
    // 获取项目选项
    getProjectOptions() {
      request({
        url: '/preOrder/list', // 假设获取所有项目列表的接口
        method: 'get'
      }).then(response => {
        if (response && response.code === 200) {
          this.projectOptions = response.data || [];
        } else {
          this.$message.warning(response.message || '获取项目列表失败');
          this.useMockProjectOptions(); 
        }
      }).catch(() => {
        this.$message.error('请求项目列表接口失败');
         this.useMockProjectOptions();
      });

    },
    // useMockProjectOptions() {
    //    this.projectOptions = [
    //       { id: 1, name: '标准日常保洁套餐' },
    //       { id: 2, name: '厨房重油污深度清洁' },
    //       { id: 3, name: '经济厨房清洁' },
    //       { id: 4, name: '全屋衣物整理（3小时）' },
    //       { id: 5, name: '卧室床品除螨套餐' },
    //       { id: 6, name: '卫生间全面消毒清洁' },
    //       { id: 7, name: '高层外窗清洁（安全作业）' },
    //    ];
    // },

    // 获取服务进度列表
    getTempoList() {
      this.loading = true;
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        projectId: this.searchForm.projectId || null,
        state: this.searchForm.state !== '' ? this.searchForm.state : null 
      };
      
      request({
        url: '/serviceTempo/page', 
        method: 'get',
        params: params
      }).then(response => {
        this.loading = false;
        if (response && response.code === 200) {
          const data = response.data || {};
          this.tempoList = data.records || [];
          this.total = data.total || 0;
          this.handleProjectNames(); // 处理项目名称显示
        } else {
           this.$message.warning(response.message || '获取服务进度列表失败');
           this.useMockTempoList(params); // 使用模拟数据
        }
      }).catch((error) => {
        this.loading = false;
        this.$message.error('请求服务进度列表接口失败');
        console.error("获取服务进度列表失败:", error);
        this.useMockTempoList(params); // 使用模拟数据
      });

    },


    handleProjectNames() {
      this.tempoList.forEach(item => {
        const project = this.projectOptions.find(opt => opt.id === item.projectId);
        item.projectName = project ? project.name : '未知项目';
      });
    },

    getStatusText(state) {
      return statusMap[state] ? statusMap[state].text : '未知状态';
    },

    getStatusTagType(state) {
      return statusMap[state] ? statusMap[state].type : 'info';
    },

    handleSearch() {
      this.currentPage = 1; 
      this.getTempoList();
    },
    
    resetSearch() {
      this.searchForm = { projectId: '', state: '' };
      this.currentPage = 1;
      this.getTempoList();
    },
    
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.getTempoList();
    },
    
    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.getTempoList();
    },
    
    
    handleEdit(row) {
      this.dialogTitle = '更新服务进度状态';
      this.isEditMode = true; 
      this.tempoForm = { ...row }; 
      this.tempoForm.state = Number(row.state);
      const project = this.projectOptions.find(opt => opt.id === row.projectId);
      this.tempoForm.projectName = project ? project.name : '未知项目';
      this.dialogVisible = true;
      this.$nextTick(() => {
         if (this.$refs.tempoFormRef) {
           this.$refs.tempoFormRef.clearValidate('state');
         }
      });
    },

    dialogClose() {
        if (this.$refs.tempoFormRef) {
            this.$refs.tempoFormRef.clearValidate();
        }
    },
    
    
    
    submitForm() {
      this.$refs.tempoFormRef.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          
          const isUpdate = this.isEditMode;
          const method = isUpdate ? 'put' : 'post';
          const submitData = isUpdate 
             ? { id: this.tempoForm.id, state: this.tempoForm.state } 
             : { projectId: this.tempoForm.projectId, state: this.tempoForm.state };

          const url =  '/serviceTempo'; 
          request({
            url: url,
            method: method,
            data: submitData 
          }).then(response => {
            this.submitLoading = false;
            if (response && response.code === 200) {
              this.$message.success(isUpdate ? '更新成功' : '添加成功');
              this.dialogVisible = false;
              this.getTempoList(); // 刷新列表
            } else {
              this.$message.error(response.message || (isUpdate ? '更新失败' : '添加失败'));
            }
          }).catch((error) => {
            this.submitLoading = false;
            this.$message.error(isUpdate ? '请求更新接口失败' : '请求添加接口失败');
            console.error(isUpdate ? "更新服务进度失败:" : "添加服务进度失败:", error);
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
.tempo-container {
  padding: 20px;

  .tempo-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      span {
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .tempo-content {
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
