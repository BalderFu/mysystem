<template>
  <div class="comment-container">
    <el-card class="comment-card">
      <div slot="header" class="card-header">
        <span>评论管理</span>
        <!-- 后台通常管理评论，不直接添加 -->
        <el-button type="primary" size="small" @click="handleAdd">添加评论</el-button>
      </div>
      <div class="comment-content">
        <!-- 搜索区域 -->
        <div class="search-container">
          <el-form :inline="true" :model="searchForm" class="search-form" size="small">
            <el-form-item label="用户">
              <el-select v-model="searchForm.userId" placeholder="请选择用户" clearable filterable>
                 <el-option
                   v-for="item in userOptions"
                   :key="item.id"
                   :label="item.name" 
                   :value="item.id">
                    <span>{{ item.name }}</span>
                 </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="预订单">
              <el-select v-model="searchForm.preOrderId" placeholder="请选择预订单" clearable filterable>
                 <el-option
                   v-for="item in preOrderOptions"
                   :key="item.id"
                   :label="`${item.name || '未知'}`" 
                   :value="item.id">
                    <span>{{ item.name || '未知' }}</span>
                 </el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
              <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 评论列表 -->
        <el-table
          :data="commentList"
          border
          v-loading="loading"
          style="width: 100%">
          <el-table-column prop="id" label="评论ID" width="80" align="center"></el-table-column>
          <el-table-column prop="userId" label="用户ID" width="100" align="center"></el-table-column>
          <el-table-column prop="preOrderId" label="预订单ID" width="100" align="center"></el-table-column>
          <el-table-column prop="content" label="评论内容" align="left" show-overflow-tooltip></el-table-column>
          <el-table-column prop="relay" label="管理员回复" align="left" show-overflow-tooltip>
             <template slot-scope="scope">
                {{ scope.row.relay || '-' }} 
             </template>
          </el-table-column>
          <el-table-column prop="createTime" label="评论时间" width="160" align="center"></el-table-column>
          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" icon="el-icon-edit-outline" @click="handleReply(scope.row)">
                {{ scope.row.relay ? '编辑回复' : '回复' }}
              </el-button>
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
    
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="dialogClose">
      <el-form :model="commentForm" :rules="formRules" ref="commentFormRef" label-width="100px" size="small">
        
        <el-form-item label="预订单" prop="preOrderId">
          <el-select v-model="commentForm.preOrderId" :disabled="isReplyMode" placeholder="请选择预订单" style="width: 100%">
            <el-option
              v-for="item in preOrderOptions"
              :key="item.id"
              :label="`${item.name || '未知'}`"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="评论内容" prop="content">
          <el-input type="textarea"  v-model="commentForm.content" :readonly="isReplyMode" :disabled="isReplyMode" :autosize="{ minRows: 5, maxRows: 10}" placeholder="请输入评论内容"></el-input>
        </el-form-item>
        <el-form-item label="回复内容" prop="relay" v-if="isReplyMode">
          <el-input type="textarea" :rows="5" v-model="commentForm.relay" placeholder="请输入回复内容"></el-input>
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

export default {
  name: 'Comment',
  data() {
    const initForm = () => ({
      id: null,
      preOrderId: null,
      content: '',
      relay: ''
    });

    return {
      userOptions: [],
      preOrderOptions: [],
      searchForm: {
        userId: '',
        preOrderId: ''
      },
      // 评论列表数据
      commentList: [],
      // 加载状态
      loading: false,
      // 分页参数
      currentPage: 1,
      pageSize: 10,
      total: 0,
      // 对话框控制
      dialogVisible: false,
      dialogTitle: '回复评论',
      submitLoading: false,
      // 表单数据
      commentForm: initForm(),
      initForm: initForm,
      // 表单验证规则
      formRules: {
        relay: [
            { required: true, message: '请输入回复内容', trigger: 'blur' },
            { min: 1, max: 500, message: '回复内容长度在 1 到 500 个字符', trigger: 'blur' }
        ]
      },
      isReplyMode: false, // 新增字段，标识是否是回复模式
    }
  },
  created() {
    // 初始化加载数据
    this.getUserOptions();
    this.getPreOrderOptions();
    this.getCommentList();
  },
  methods: {
    getUserOptions() {
      request({
        url: '/user/list',
        method: 'get'
      }).then(response => {
        if (response && response.code === 200) {
          console.log(response.data);
          this.userOptions = Object.entries(response.data).map(([id, name]) => ({
            id,
            name
          }));
        } else {
          this.$message.warning(response.message || '获取用户列表失败');
        }
      }).catch(() => {
        this.$message.error('请求用户列表接口失败');
      });
    },


    getPreOrderOptions() {
      request({
        url: '/preOrder/list', 
        method: 'get'
      }).then(response => {
        if (response && response.code === 200) {
          this.preOrderOptions = response.data || [];
        } else {
          this.$message.warning(response.message || '获取预订单列表失败');
        }
      }).catch(() => {
        this.$message.error('请求预订单列表接口失败');
      });

    },
    

    // 获取评论列表
    getCommentList() {
      this.loading = true;
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        userId: this.searchForm.userId || null,
        preOrderId: this.searchForm.preOrderId || null,
      };
      
      request({
        url: '/comment/page', 
        method: 'get',
        params: params
      }).then(response => {
        this.loading = false;
        if (response && response.code === 200) {
          const data = response.data || {};
          this.commentList = data.records || [];
          this.total = data.total || 0;
        } else {
           this.$message.warning(response.message || '获取评论列表失败');
        }
      }).catch((error) => {
        this.loading = false;
        this.$message.error('请求评论列表接口失败');
        console.error("获取评论列表失败:", error);
      });


    },

    
    // 搜索
    handleSearch() {
      this.currentPage = 1; 
      this.getCommentList();
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = { userId: '', preOrderId: '' };
      this.currentPage = 1;
      this.getCommentList();
    },
    
    // 每页条数变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentPage = 1;
      this.getCommentList();
    },
    
    // 当前页变化
    handleCurrentChange(page) {
      this.currentPage = page;
      this.getCommentList();
    },

    handleAdd() {
      this.dialogTitle = '添加评论';
      this.dialogVisible = true;
      this.commentForm = this.initForm();
      this.commentForm.relay = null;
      this.commentForm.id = null;
      this.isReplyMode = false; // 添加评论模式，不显示回复框
      
      // 清除表单验证状态
      this.$nextTick(() => {
        if (this.$refs.commentFormRef) {
          this.$refs.commentFormRef.clearValidate();
        }
      });
    },
        
    handleReply(row) {
      this.dialogTitle = row.relay ? '编辑回复' : '回复评论';
      this.commentForm = { ...row }; 
      this.commentForm.relay = this.commentForm.relay || '';
      this.isReplyMode = true; // 回复模式，显示回复框

      this.dialogVisible = true;
      // 清除表单验证状态
      this.$nextTick(() => {
         if (this.$refs.commentFormRef) {
           this.$refs.commentFormRef.clearValidate();
         }
      });
    },

    // 对话框关闭时的回调
    dialogClose() {
        if (this.$refs.commentFormRef) {
            this.$refs.commentFormRef.clearValidate();
        }
    },
    
    // 删除按钮点击
    handleDelete(row) {
      this.$confirm(`确定要删除用户ID ${row.userId} 对订单ID ${row.preOrderId} 的这条评论吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true;
        request({
          url: `/comment?id=${row.id}`,
          method: 'delete'
        }).then(response => {
          this.loading = false;
          if (response && response.code === 200) {
            this.$message.success('删除成功');
            this.getCommentList();
          } else {
            this.$message.error(response.message || '删除失败');
          }
        }).catch((error) => {
          this.loading = false;
          this.$message.error('请求删除接口失败');
          console.error("删除评论失败:", error);
        });

      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },
    
    // 提交表单（回复/编辑回复）
    submitForm() {
      this.$refs.commentFormRef.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          
          let submitData;
          let method;
          const url = '/comment';
          
          if (this.isReplyMode) {
            // 回复评论模式
            submitData = {
              id: this.commentForm.id,
              relay: this.commentForm.relay
            };
            method = 'put';
          } else {
            // 添加评论模式
            submitData = {
              preOrderId: this.commentForm.preOrderId,
              content: this.commentForm.content
            };
            method = 'post';
          }
          
          request({
            url: url,
            method: method,
            data: submitData 
          }).then(response => {
            this.submitLoading = false;
            if (response && response.code === 200) {
              this.$message.success(this.isReplyMode ? '回复成功' : '添加成功');
              this.dialogVisible = false;
              this.getCommentList();
            } else {
              this.$message.error(this.isReplyMode? response.message || '回复失败': response.message || '添加失败');
            }
          }).catch((error) => {
            this.submitLoading = false;
            this.$message.error(this.isReplyMode? '请求回复接口失败': '请求添加接口失败');
            console.error(this.isReplyMode? "回复评论失败:" : "添加评论失败:", error);
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
.comment-container {
  padding: 20px;

  .comment-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      span {
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .comment-content {
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
  
  .comment-content-display {
      background-color: #f8f9fa;
      padding: 10px 15px;
      border-radius: 4px;
      border: 1px solid #e9ecef;
      min-height: 60px; 
      line-height: 1.5;
      word-break: break-all; // 长单词或链接换行
  }

  // 再次尝试覆盖 el-table 悬浮背景色
  ::v-deep .el-table--enable-row-hover .el-table__body tr:hover > td {
    background-color: transparent !important; 
    // 如果透明不行，试试直接指定一个深色
    // background-color: #1f232d !important; // 尝试一个更具体的深色背景
  }

  // 有些主题可能还会给 tr 本身设置背景，也覆盖一下
  ::v-deep .el-table__body tr.el-table__row:hover {
     background-color: transparent !important;
     // background-color: #1f232d !important;
  }
}

/* 对话框样式 */
::v-deep .el-dialog {
  background: #1f2d3d; // 深色背景
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
  
  .el-dialog__header {
    background: #1a2638; /* 将背景色从 #409EFF 改为更深的颜色 */
    padding: 15px 20px;
    border-bottom: 1px solid #2c3e50;
    
    .el-dialog__title {
      color: #e0e0e0;
      font-weight: bold;
    }
    
    .el-dialog__headerbtn .el-dialog__close {
      color: rgba(255, 255, 255, 0.9);
    }
  }
  
  .el-dialog__body {
    padding: 25px 20px;
    color: #e0e0e0; // 浅色文字
  }
  
  .el-form-item__label {
    color: #e0e0e0 !important; // 浅色标签文字
  }
  
  .el-form-item.is-required .el-form-item__label:before {
    color: #F56C6C;
  }
  
  /* 输入框样式 */
  .el-input__inner, .el-textarea__inner {
    background-color: #283446;
    border-color: #3a4a5e;
    color: #e0e0e0;
    
    &:focus {
      border-color: #409EFF;
    }
  }
  
  /* 下拉框样式 */
  .el-select .el-input.is-disabled .el-input__inner {
    background-color: #283446;
    border-color: #3a4a5e;
    color: #e0e0e0;
    opacity: 0.8;
  }
  
  /* 禁用/只读状态下的输入框样式 */
  .el-input.is-disabled .el-input__inner,
  .el-textarea.is-disabled .el-textarea__inner {
    background-color: #24303f;
    border-color: #2c394b;
    color: #b0b0b0;
  }
  
  /* 回复内容框的样式 */
  .el-form-item[label="回复内容"] {
    margin-top: 15px;
    
    .el-textarea__inner {
      background-color: #283446;
      border: 1px solid #3a4a5e;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      transition: all 0.3s;
      
      &:focus {
        border-color: #409EFF;
        box-shadow: 0 0 8px rgba(64, 158, 255, 0.3);
      }
    }
  }
  
  /* 按钮样式调整 */
  .el-button {
    background-color: #283446;
    border-color: #3a4a5e;
    color: #e0e0e0;
    
    &:hover, &:focus {
      background-color: #324558;
      border-color: #4a5d73;
      color: #fff;
    }
  }
  
  .el-button--primary {
    background-color: #409EFF;
    border-color: #409EFF;
    
    &:hover, &:focus {
      background-color: #66b1ff;
      border-color: #66b1ff;
    }
  }
}
</style>

<!-- 添加全局样式（非scoped）以提高优先级 -->
<style lang="scss">
/* 表格悬浮行背景色覆盖 - 全局样式 */
.el-table--enable-row-hover .el-table__body tr:hover > td {
  background-color: transparent !important;
}

.el-table--enable-row-hover .el-table__body tr:hover {
  background-color: transparent !important;
}

.el-table__body tr.hover-row > td, 
.el-table__body tr.hover-row.current-row > td, 
.el-table__body tr.hover-row.el-table__row > td,
.el-table__body tr.hover-row > td.el-table__cell {
  background-color: transparent !important;
}

/* 针对表格条纹样式也覆盖一下 */
.el-table--striped .el-table__body tr.el-table__row--striped.hover-row td,
.el-table--striped .el-table__body tr.el-table__row--striped:hover td {
  background-color: transparent !important;
}
</style>
