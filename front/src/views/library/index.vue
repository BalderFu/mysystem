<template>
  <div class="library-container">
    <el-card class="library-card">
      <div slot="header" class="card-header">
        <span>算法资料库</span>
        <el-button type="primary" size="small" @click="handleAdd">新增算法资料</el-button>
      </div>
      
      <!-- 搜索区域 -->
      <div class="search-section">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="算法类型">
            <el-select v-model="searchForm.arithmeticId" placeholder="请选择算法类型" clearable style="width: 220px">
              <el-option
                v-for="item in algorithmList"
                :key="item.key"
                :label="item.value"
                :value="item.key">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="用户">
            <el-select v-model="searchForm.userId" placeholder="请选择用户" clearable style="width: 220px">
              <el-option
                v-for="item in userList"
                :key="item.key"
                :label="item.value"
                :value="item.key">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格区域 -->
      <el-table
        v-loading="tableLoading"
        :data="tableData"
        border
        style="width: 100%">
        <el-table-column
          prop="id"
          label="ID"
          width="80"
          align="center">
        </el-table-column>
        <el-table-column
          prop="username"
          label="用户"
          width="120"
          align="center">
        </el-table-column>
        <el-table-column
          label="算法类型"
          width="120"
          align="center">
          <template slot-scope="scope">
            {{ getAlgorithmName(scope.row.arithmeticName) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="hashKey"
          label="明文"
          width="180"
          align="center"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="hashValue"
          label="密文"
          min-width="200"
          align="center"
          show-overflow-tooltip>
        </el-table-column>
        <el-table-column
          prop="desc"
          label="描述"
          min-width="150"
          align="left"
          show-overflow-tooltip>
          <template slot-scope="scope">
            <span style="padding: 0 10px">{{ scope.row.desc || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="添加时间"
          width="180"
          align="center">
        </el-table-column>
        <el-table-column
          label="操作"
          width="180"
          align="center">
          <template slot-scope="scope">
            <div class="operation-buttons">
              <el-button
                size="mini"
                type="primary"
                @click="handleEdit(scope.row)">编辑</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>

      <!-- 新增/编辑弹窗 -->
      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
        <el-form :model="form" :rules="rules" ref="form" label-width="100px">
          <el-form-item label="算法类型" prop="arithmeticId">
            <el-select v-model="form.arithmeticId" placeholder="请选择算法类型" style="width: 100%">
              <el-option
                v-for="item in algorithmList"
                :key="item.key"
                :label="item.value"
                :value="item.key">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="明文" prop="hashKey">
            <el-input v-model="form.hashKey" placeholder="请输入明文"></el-input>
          </el-form-item>
          <el-form-item label="自动获取密文">
            <el-switch v-model="autoGetHash" @change="handleAutoGetHashChange"></el-switch>
          </el-form-item>
          <el-form-item label="密文" prop="hashValue" v-if="!autoGetHash">
            <el-input v-model="form.hashValue" placeholder="请输入密文"></el-input>
          </el-form-item>
          <el-form-item label="描述" prop="desc">
            <el-input type="textarea" v-model="form.desc" placeholder="请输入描述"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script>
import { getDictPage, addArithmeticHouse, updateArithmeticHouse, deleteArithmeticHouse, hashList } from '@/utils/inputs'
import { getUserList } from '@/utils/user'

export default {
  name: 'Library',
  data() {
    return {
      // 搜索表单
      searchForm: {
        arithmeticId: '',
        userId: ''
      },
      // 下拉选项
      algorithmList: [],
      userList: [],
      // 表格数据
      tableData: [],
      tableLoading: false,
      // 分页
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },
      // 弹窗
      dialogVisible: false,
      dialogTitle: '新增算法',
      // 是否自动获取密文
      autoGetHash: false,
      // 表单
      form: {
        id: '',
        arithmeticId: '',
        hashKey: '',
        hashValue: '',
        desc: ''
      },
      // 表单验证规则
      rules: {
        arithmeticId: [
          { required: true, message: '请选择算法类型', trigger: 'change' }
        ],
        hashKey: [
          { required: true, message: '请输入明文', trigger: 'blur' }
        ],
        hashValue: [
          { required: true, message: '请输入密文', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 获取算法列表
    getAlgorithmList() {
      hashList().then(res => {
        if (res.code === 200 && res.data) {
          this.algorithmList = Object.entries(res.data).map(([id, name]) => ({
            key: id,
            value: name
          }));
        } else {
          this.$message.error(res.message || '获取算法列表失败');
        }
      }).catch(error => {
        console.error('获取算法列表失败:', error);
        this.$message.error('获取算法列表失败');
      });
    },

    // 获取用户列表
    getUserList() {
      getUserList().then(res => {
        if (res.code === 200 && res.data) {
          this.userList = Object.entries(res.data).map(([id, name]) => ({
            key: id,
            value: name
          }));
        } else {
          this.$message.error(res.message || '获取用户列表失败');
        }
      }).catch(error => {
        console.error('获取用户列表失败:', error);
        this.$message.error('获取用户列表失败');
      });
    },

    // 获取表格数据
    getTableData() {
      this.tableLoading = true;
      const params = {
        page: this.pagination.currentPage,
        size: this.pagination.pageSize,
        arithmeticId: this.searchForm.arithmeticId,
        userId: this.searchForm.userId
      };
      
      getDictPage(params).then(res => {
        if (res.code === 200 && res.data) {
          this.tableData = res.data.records || [];
          this.pagination.total = res.data.total || 0;
        } else {
          this.$message.error(res.message || '获取数据失败');
        }
      }).catch(error => {
        console.error('获取数据失败:', error);
        this.$message.error('获取数据失败');
      }).finally(() => {
        this.tableLoading = false;
      });
    },

    // 搜索
    handleSearch() {
      this.pagination.currentPage = 1;
      this.getTableData();
    },

    // 重置
    handleReset() {
      this.searchForm.arithmeticId = '';
      this.searchForm.userId = '';
      this.handleSearch();
    },

    // 处理自动获取密文开关变化
    handleAutoGetHashChange(val) {
      if (val) {
        // 开启自动获取密文，清空密文字段
        this.form.hashValue = '';
        // 修改验证规则，密文不再是必填项
        this.rules.hashValue = [];
      } else {
        // 关闭自动获取密文，恢复密文的必填验证
        this.rules.hashValue = [
          { required: true, message: '请输入密文', trigger: 'blur' }
        ];
      }
    },

    // 新增
    handleAdd() {
      this.form = {
        id: '',
        arithmeticId: '',
        hashKey: '',
        hashValue: '',
        desc: ''
      };
      this.autoGetHash = false; // 重置开关状态
      this.dialogTitle = '新增';
      this.dialogVisible = true;
    },

    // 获取算法名称
    getAlgorithmName(id) {
      const algorithm = this.algorithmList.find(item => item.key === id);
      return algorithm ? algorithm.value : id;
    },

    // 编辑
    handleEdit(row) {
      this.form = {
        id: row.id,
        arithmeticId: row.arithmeticId,
        hashKey: row.hashKey,
        hashValue: row.hashValue,
        desc: row.desc
      };
      this.autoGetHash = false; // 编辑时默认关闭自动获取
      this.dialogTitle = '编辑';
      this.dialogVisible = true;
    },

    // 删除
    handleDelete(row) {
      this.$confirm('确认删除该算法吗？', '提示', {
        type: 'warning'
      }).then(() => {
        deleteArithmeticHouse({ id: row.id }).then(res => {
          if (res.code === 200) {
            this.$message.success('删除成功');
            this.getTableData();
          } else {
            this.$message.error(res.message || '删除失败');
          }
        }).catch(error => {
          console.error('删除失败:', error);
          this.$message.error('删除失败');
        });
      }).catch(() => {});
    },

    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return false;
        }
        
        // 如果开启了自动获取密文但是没有输入明文，提示错误
        if (this.autoGetHash && !this.form.hashKey) {
          this.$message.warning('请输入明文');
          return false;
        }
        
        // 检查密文字段验证规则
        if (!this.autoGetHash && !this.form.hashValue) {
          this.$message.warning('请输入密文');
          return false;
        }
        
        // 构建请求参数
        const data = { ...this.form };
        
        // 处理自动获取密文的情况
        if (this.autoGetHash) {
          // 在提交时，如果开启了自动获取密文，会在后端自动计算
          data.autoGenerateHash = true;
        }
        
        if (this.form.id) {
          // 更新
          updateArithmeticHouse(data).then(res => {
            if (res.code === 200) {
              this.$message.success('更新成功');
              this.dialogVisible = false;
              this.getTableData();
            } else {
              this.$message.error(res.message || '更新失败');
            }
          }).catch(error => {
            console.error('更新失败:', error);
            this.$message.error('更新失败');
          });
        } else {
          // 新增
          addArithmeticHouse(data).then(res => {
            if (res.code === 200) {
              this.$message.success('新增成功');
              this.dialogVisible = false;
              this.getTableData();
            } else {
              this.$message.error(res.message || '新增失败');
            }
          }).catch(error => {
            console.error('新增失败:', error);
            this.$message.error('新增失败');
          });
        }
      });
    },

    // 分页
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.getTableData();
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.getTableData();
    }
  },
  mounted() {
    this.getTableData();
    this.getAlgorithmList();
    this.getUserList();
  }
}
</script>

<style lang="scss" scoped>
.library-container {
  padding: 20px;
  
  .library-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      span {
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .search-section {
      margin-bottom: 20px;
      
      .search-form {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        gap: 10px;
      }
    }
    
    .operation-buttons {
      display: flex;
      gap: 8px;
      justify-content: center;
      align-items: center;

      .el-button {
        padding: 5px 8px;
        margin: 0;
      }
    }
    
    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}
</style>
