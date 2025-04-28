<template>
    <div class="project-container">
      <el-card class="project-card">
        <div slot="header" class="card-header">
          <span>家政服务项目管理</span>
          <el-button type="primary" size="small" @click="handleAdd">添加项目</el-button>
        </div>
        <div class="project-content">
          <!-- 搜索区域 -->
          <div class="search-container">
            <el-form :inline="true" :model="searchForm" class="search-form" size="small">
              <el-form-item label="项目名称">
                <el-input v-model="searchForm.name" placeholder="请输入项目名称" clearable></el-input>
              </el-form-item>
              <el-form-item label="所属家政">
                <el-select v-model="searchForm.companyId" placeholder="请选择家政公司" clearable filterable>
                  <el-option
                    v-for="item in companyOptions"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="服务类型">
                <el-select v-model="searchForm.serviceId" placeholder="请选择服务类型" clearable filterable>
                  <el-option
                    v-for="item in serviceTypeOptions"
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
  
          <!-- 项目列表 -->
          <el-table
            :data="projectList"
            border
            v-loading="loading"
            style="width: 100%">
            <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
            <el-table-column prop="name" label="项目名称" align="center"></el-table-column>
            <el-table-column prop="companyName" label="所属家政" align="center"></el-table-column>
            <el-table-column prop="serviceTypeName" label="服务类型" align="center"></el-table-column>
            <el-table-column label="图片" width="100" align="center">
              <template slot-scope="scope">
                <el-image 
                  v-if="scope.row.pic"
                  style="width: 60px; height: 60px"
                  :src="getImageUrl(scope.row.pic)" 
                  :preview-src-list="[getImageUrl(scope.row.pic)]"
                  fit="cover">
                   <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格(元)" width="100" align="center">
               <template slot-scope="scope">
                {{ scope.row.price ? scope.row.price.toFixed(2) : '0.00' }}
              </template>
            </el-table-column>
            <el-table-column prop="time" label="可约时间" width="120" align="center"></el-table-column>
            <el-table-column prop="content" label="服务内容" show-overflow-tooltip align="center"></el-table-column>
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
      
      <!-- 添加/编辑项目对话框 -->
      <el-dialog
        :title="dialogTitle"
        :visible.sync="dialogVisible"
        width="650px"
        :close-on-click-modal="false"
        @close="dialogClose">
        <el-form :model="projectForm" :rules="formRules" ref="projectFormRef" label-width="100px" size="small">
          <el-form-item label="所属家政" prop="companyId">
            <el-select v-model="projectForm.companyId" placeholder="请选择家政公司" filterable style="width: 100%;">
              <el-option
                v-for="item in companyOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="服务类型" prop="serviceId">
            <el-select v-model="projectForm.serviceId" placeholder="请选择服务类型" filterable style="width: 100%;">
              <el-option
                v-for="item in serviceTypeOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="项目名称" prop="name">
            <el-input v-model="projectForm.name" placeholder="请输入项目名称"></el-input>
          </el-form-item>
          <el-form-item label="图片" prop="pic">
             <el-upload
              class="avatar-uploader"
              action="http://localhost:8090/project/upload" 
              :show-file-list="false"
              :on-success="handlePictureSuccess"
              :before-upload="beforePictureUpload"
              :headers="uploadHeaders">
              <img v-if="projectForm.pic" :src="getImageUrl(projectForm.pic)" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
            <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input-number v-model="projectForm.price" :min="0" :precision="2" :step="10" controls-position="right"></el-input-number>
            <span class="price-unit">元</span>
          </el-form-item>
          <el-form-item label="可约时间" prop="time">
            <el-input v-model="projectForm.time" placeholder="例如: 9:00-18:00"></el-input>
          </el-form-item>
          <el-form-item label="服务内容" prop="content">
            <el-input type="textarea" :rows="3" v-model="projectForm.content" placeholder="请输入服务内容"></el-input>
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
  
  export default {
    name: 'Project',
    data() {
      // 初始化表单数据
      const initForm = () => ({
        id: null,
        companyId: null,
        serviceId: null,
        name: '',
        pic: '', 
        price: 0,
        time: '',
        content: ''
      });
  
      return {
        // 搜索表单
        searchForm: {
          name: '',
          companyId: '',
          serviceId: ''
        },
        // 项目列表数据
        projectList: [],
        // 家政公司选项
        companyOptions: [],
        // 服务类型选项
        serviceTypeOptions: [],
        // 加载状态
        loading: false,
        // 分页参数
        currentPage: 1,
        pageSize: 10,
        total: 0,
        // 对话框控制
        dialogVisible: false,
        dialogTitle: '添加项目',
        submitLoading: false,
        // 表单数据
        projectForm: initForm(),
        initForm: initForm, // 保存初始化函数引用，方便重置
        // 表单验证规则
        formRules: {
          companyId: [
            { required: true, message: '请选择所属家政公司', trigger: 'change' }
          ],
          serviceId: [
            { required: true, message: '请选择服务类型', trigger: 'change' }
          ],
          name: [
            { required: true, message: '请输入项目名称', trigger: 'blur' },
            { min: 2, max: 36, message: '长度在 2 到 36 个字符', trigger: 'blur' }
          ],
          // pic 字段非必填，但可以添加格式等校验
          price: [
            { required: true, message: '请输入价格', trigger: 'blur' },
            { type: 'number', message: '价格必须为数字值'}
          ],
          time: [
            { required: true, message: '请输入可约时间', trigger: 'blur' }
          ],
          content: [
            { required: true, message: '请输入服务内容', trigger: 'blur' },
            { max: 200, message: '服务内容不能超过200个字符（建议数据库字段加长）', trigger: 'blur' } 
          ]
        },
        // 上传文件请求头
        uploadHeaders: {
            Authorization: `Bearer ${localStorage.getItem(Constants.ID.USER_TOKEN_KEY)}` || ''
        }
      }
    },
    created() {
      // 初始化加载数据
      this.getCompanyOptions();
      this.getServiceTypeOptions();
      this.getProjectList();
    },
    methods: {
      // 获取家政公司选项
      getCompanyOptions() {
        request({
          url: '/company/list',
          method: 'get'
        }).then(response => {
          if (response && response.code === 200) {
            this.companyOptions = response.data || [];
          } else {
            this.$message.warning(response.message || '获取家政公司列表失败');
            this.useMockCompanyOptions(); // API失败时使用模拟数据
          }
        }).catch(() => {
          this.$message.error('请求家政公司列表接口失败');
        });
  
      },
      
  
      // 获取服务类型选项
      getServiceTypeOptions() {
        request({
          url: '/serviceType/list',
          method: 'get'
        }).then(response => {
          if (response && response.code === 200) {
            this.serviceTypeOptions = response.data || [];
          } else {
             this.$message.warning(response.message || '获取服务类型列表失败');
          }
        }).catch(() => {
           this.$message.error('请求服务类型列表接口失败');
        });
  
      },
      
  
      // 获取项目列表
      getProjectList() {
        this.loading = true;
        const params = {
          pageNum: this.currentPage,
          pageSize: this.pageSize,
          name: this.searchForm.name || null,
          companyId: this.searchForm.companyId || null,
          serviceId: this.searchForm.serviceId || null
        };
        
        request({
          url: '/project/page', // 假设获取项目分页列表的接口
          method: 'get',
          params: params
        }).then(response => {
          this.loading = false;
          if (response && response.code === 200) {
            const data = response.data || {};
            this.projectList = data.records || [];
            this.total = data.total || 0;
            this.handleDataNames(); // 处理家政和类型名称显示
          } else {
             this.$message.warning(response.message || '获取项目列表失败');
             this.useMockProjectList(); // 使用模拟数据
          }
        }).catch((error) => {
          this.loading = false;
          this.$message.error('请求项目列表接口失败');
          console.error("获取项目列表失败:", error);
          this.useMockProjectList(); // 使用模拟数据
        });

      },
  
      
  
      // 处理表格中家政和类型名称的显示
      handleDataNames() {
        this.projectList.forEach(item => {
          const company = this.companyOptions.find(opt => opt.id === item.companyId);
          item.companyName = company ? company.name : '未知家政';
          const serviceType = this.serviceTypeOptions.find(opt => opt.id === item.serviceId);
          item.serviceTypeName = serviceType ? serviceType.name : '未知类型';
        });
      },
  
      // 搜索
      handleSearch() {
        this.currentPage = 1; // 重置到第一页
        this.getProjectList();
      },
      
      // 重置搜索
      resetSearch() {
        this.searchForm = { name: '', companyId: '', serviceId: '' };
        this.currentPage = 1;
        this.getProjectList();
      },
      
      // 每页条数变化
      handleSizeChange(size) {
        this.pageSize = size;
        this.currentPage = 1; // 修改每页大小时，重置到第一页
        this.getProjectList();
      },
      
      // 当前页变化
      handleCurrentChange(page) {
        this.currentPage = page;
        this.getProjectList();
      },
      
      // 添加项目按钮点击
      handleAdd() {
        this.dialogTitle = '添加项目';
        this.projectForm = this.initForm(); 
        this.dialogVisible = true;
        this.$nextTick(() => {
           if (this.$refs.projectFormRef) {
              this.$refs.projectFormRef.clearValidate();
           }
        });
      },
      
      // 编辑项目按钮点击
      handleEdit(row) {
        this.dialogTitle = '编辑项目';
        this.projectForm = {
          id: row.id,
          companyId: row.companyId,
          serviceId: row.serviceId,
          name: row.name,
          pic: row.pic, 
          price: row.price,
          time: row.time,
          content: row.content
        }
        this.projectForm.price = Number(row.price);
        this.projectForm.companyId = Number(row.companyId);
        this.projectForm.serviceId = Number(row.serviceId);
  
        this.dialogVisible = true;
         // 清除表单验证状态
        this.$nextTick(() => {
           if (this.$refs.projectFormRef) {
             this.$refs.projectFormRef.clearValidate();
           }
        });
      },
  
      // 对话框关闭时的回调
      dialogClose() {
          // 重置表单（可选，看需求是否需要在关闭时清空）
          // this.projectForm = this.initForm();
          // 清除验证状态
          if (this.$refs.projectFormRef) {
              this.$refs.projectFormRef.clearValidate();
          }
      },
      
      // 删除项目按钮点击
      handleDelete(row) {
        this.$confirm(`确定要删除项目 "${row.name}" 吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true; 
          request({
            url: `/project?id=${row.id}`, 
            method: 'delete'
          }).then(response => {
            this.loading = false;
            if (response && response.code === 200) {
              this.$message.success('删除成功');
              this.getProjectList(); // 刷新列表
            } else {
              this.$message.error(response.message || '删除失败');
            }
          }).catch((error) => {
            this.loading = false;
            this.$message.error('请求删除接口失败');
            console.error("删除项目失败:", error);
          });
  
        }).catch(() => {
          // 用户取消删除
          this.$message.info('已取消删除');
        });
      },
      
      // 提交表单（添加/编辑）
      submitForm() {
        this.$refs.projectFormRef.validate(valid => {
          if (valid) {
            this.submitLoading = true;
            
            const isEdit = this.projectForm.id !== null;
            const method = isEdit ? 'put' : 'post';
            const url = '/project';
            
            request({
              url: url,
              method: method,
              data: this.projectForm 
            }).then(response => {
              this.submitLoading = false;
              if (response && response.code === 200) {
                this.$message.success(isEdit ? '更新成功' : '添加成功');
                this.dialogVisible = false;
                this.getProjectList(); // 刷新列表
              } else {
                this.$message.error(response.message || (isEdit ? '更新失败' : '添加失败'));
              }
            }).catch((error) => {
              this.submitLoading = false;
              this.$message.error(isEdit ? "更新项目失败:" : "添加项目失败:");
              console.error(isEdit ? "更新项目失败:" : "添加项目失败:", error);
            });
  
  
          } else {
            console.log('表单验证失败');
            return false;
          }
        });
      },
  
      handlePictureSuccess(res, file) {
        if (res && res.code === 200) {
            console.log("handlePictureSuccess",res);
          this.projectForm.pic = res.data;
          this.$message.success('图片上传成功');
        } else {
          this.$message.error(res.message || '图片上传失败');
        }
      },
  
      // 图片上传前校验
      beforePictureUpload(file) {
        const isJPG = file.type === 'image/jpeg';
        const isPNG = file.type === 'image/png';
        const isLt2M = file.size / 1024 / 1024 < 2;
  
        if (!isJPG && !isPNG) {
          this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
        }
        if (!isLt2M) {
          this.$message.error('上传图片大小不能超过 2MB!');
        }
        return (isJPG || isPNG) && isLt2M;
      },
  
      // 获取图片完整URL（处理基础路径和相对路径）
      getImageUrl(picPath) {
        if (picPath) {
            console.log("picPath",picPath);
          const url = `http://localhost:8090/uploads/${picPath}?t=${Date.now()}`; 
          console.log(url);
          return url; 
        }
      }
    }
  }
  </script>
  
  <style lang="scss" scoped>
  .project-container {
    padding: 20px;
  
    .project-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        span {
          font-size: 18px;
          font-weight: 600;
        }
      }
      
      .project-content {
        padding-top: 10px; // 给搜索栏留出一点空间
  
         .search-container {
           margin-bottom: 15px;
           .search-form .el-form-item {
               margin-bottom: 5px; // 紧凑搜索表单
           }
         }
  
        .el-table {
           .el-image {
               border-radius: 4px;
           }
            .image-slot {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
                background: #f5f7fa;
                color: #909399;
                font-size: 20px;
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
  
    // 图片上传样式
    .avatar-uploader ::v-deep .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: border-color 0.3s;
    }
    .avatar-uploader ::v-deep .el-upload:hover {
      border-color: #409EFF;
    }
    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 120px; // 调整上传框大小
      height: 120px;
      line-height: 120px;
      text-align: center;
    }
    .avatar {
      width: 120px;
      height: 120px;
      display: block;
    }
     .el-upload__tip {
        font-size: 12px;
        color: #999;
        line-height: 1.5;
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