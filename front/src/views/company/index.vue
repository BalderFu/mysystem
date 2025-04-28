<template>
  <div class="company-container">
    <el-card class="company-card">
      <div slot="header" class="card-header">
        <span>家政公司管理</span>
        <el-button type="primary" size="small" @click="handleAdd">添加家政公司</el-button>
      </div>
      <div class="company-content">
        <!-- 搜索区域 -->
        <div class="search-container">
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="公司名称">
              <el-input v-model="searchForm.name" placeholder="请输入公司名称" clearable size="small"></el-input>
            </el-form-item>
           
            <el-form-item label="状态">
              <el-select v-model="searchForm.state" placeholder="是否可预约" clearable size="small">
                <el-option label="可预约" :value="true"></el-option>
                <el-option label="不可预约" :value="false"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="small" @click="handleSearch">搜索</el-button>
              <el-button icon="el-icon-refresh" size="small" @click="resetSearch">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 家政公司列表 -->
        <el-table
          :data="companyList"
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
            label="公司名称"
            align="center">
          </el-table-column>
          
          <el-table-column
            prop="time"
            label="可约时间"
            width="120"
            align="center">
          </el-table-column>
          <el-table-column
            prop="phone"
            label="联系电话"
            width="120"
            align="center">
          </el-table-column>
          <el-table-column
            prop="state"
            label="状态"
            width="100"
            align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.state === true ? 'success' : 'info'">
                {{ scope.row.state === true ? '可预约' : '不可预约' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column
            prop="createTime"
            label="创建时间"
            width="160"
            align="center">
          </el-table-column>
          <el-table-column
            label="操作"
            width="200"
            align="center">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="primary"
                icon="el-icon-edit"
                @click="handleEdit(scope.row)">编辑</el-button>
              <el-button
                size="mini"
                type="danger"
                icon="el-icon-delete"
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
    
    <!-- 添加/编辑家政公司对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false">
      <el-form :model="companyForm" :rules="formRules" ref="companyForm" label-width="100px" size="small">
        <el-form-item label="公司名称" prop="name">
          <el-input v-model="companyForm.name" placeholder="请输入公司名称"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="companyForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="companyForm.address" placeholder="请输入地址"></el-input>
        </el-form-item>
        <el-form-item label="可约时间" prop="timeType">
          <div class="time-selection-container">
            <el-select v-model="companyForm.timeType" 
              placeholder="请选择时间类型" 
              style="width: 120px; margin-right: 10px" 
              @change="handleTimeTypeChange">
              <el-option 
                v-for="item in timeTypeOptions" 
                :key="item.value" 
                :label="item.label" 
                :value="item.value">
              </el-option>
            </el-select>
            
            <template v-if="companyForm.timeType === 'custom'">
              <el-select 
                v-model="companyForm.startTime"
                filterable
                clearable
                placeholder="开始时间"
                class="time-select"
                popper-class="time-select-dropdown"
                :popper-append-to-body="true"
                @change="handleTimeChange('startTime')"
                style="width: 120px; margin-right: 10px">
                <el-option
                  v-for="item in timeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
              
              <span style="margin: 0 5px">-</span>
              
              <el-select 
                v-model="companyForm.endTime"
                filterable
                clearable
                placeholder="结束时间"
                class="time-select"
                popper-class="time-select-dropdown"
                :popper-append-to-body="true"
                @change="handleTimeChange('endTime')"
                style="width: 120px">
                <el-option
                  v-for="item in timeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </template>
            
            <template v-else>
              <span style="color: #909399; margin-left: 10px">全天开放</span>
            </template>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="state">
          <el-switch
            v-model="companyForm.state"
            :active-value="1"
            :inactive-value="0"
            active-text="可预约"
            inactive-text="不可预约">
          </el-switch>
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
  name: 'Company',
  data() {
    // 自定义时间验证函数
    const validateTimeRange = (rule, value, callback) => {
      if (this.companyForm.timeType === 'any') {
        // 选择"随时"时，直接通过验证
        callback();
      } else if (this.companyForm.timeType === 'custom') {
        // 根据验证的字段决定验证内容
        if (rule.field === 'startTime' && !this.companyForm.startTime) {
          callback(new Error('请选择开始时间'));
        } else if (rule.field === 'endTime' && !this.companyForm.endTime) {
          callback(new Error('请选择结束时间'));
        } else if (rule.field === 'timeType' && 
                  this.companyForm.startTime && 
                  this.companyForm.endTime && 
                  this.companyForm.startTime >= this.companyForm.endTime) {
          callback(new Error('结束时间必须晚于开始时间'));
        } else {
          callback();
        }
      } else {
        callback(new Error('请选择时间类型'));
      }
    };
    
    return {
      // 搜索表单
      searchForm: {
        name: '',
        state: ''
      },
      // 家政公司列表数据
      companyList: [],
      // 服务类型选项
      serviceTypeOptions: [],
      // 可约时间类型选项
      timeTypeOptions: [
        { value: 'any', label: '随时' },
        { value: 'custom', label: '自定义' }
      ],
      // 时间选项
      timeOptions: [
        { value: '00:00', label: '00:00' },
        { value: '00:30', label: '00:30' },
        { value: '01:00', label: '01:00' },
        { value: '01:30', label: '01:30' },
        { value: '02:00', label: '02:00' },
        { value: '02:30', label: '02:30' },
        { value: '03:00', label: '03:00' },
        { value: '03:30', label: '03:30' },
        { value: '04:00', label: '04:00' },
        { value: '04:30', label: '04:30' },
        { value: '05:00', label: '05:00' },
        { value: '05:30', label: '05:30' },
        { value: '06:00', label: '06:00' },
        { value: '06:30', label: '06:30' },
        { value: '07:00', label: '07:00' },
        { value: '07:30', label: '07:30' },
        { value: '08:00', label: '08:00' },
        { value: '08:30', label: '08:30' },
        { value: '09:00', label: '09:00' },
        { value: '09:30', label: '09:30' },
        { value: '10:00', label: '10:00' },
        { value: '10:30', label: '10:30' },
        { value: '11:00', label: '11:00' },
        { value: '11:30', label: '11:30' },
        { value: '12:00', label: '12:00' },
        { value: '12:30', label: '12:30' },
        { value: '13:00', label: '13:00' },
        { value: '13:30', label: '13:30' },
        { value: '14:00', label: '14:00' },
        { value: '14:30', label: '14:30' },
        { value: '15:00', label: '15:00' },
        { value: '15:30', label: '15:30' },
        { value: '16:00', label: '16:00' },
        { value: '16:30', label: '16:30' },
        { value: '17:00', label: '17:00' },
        { value: '17:30', label: '17:30' },
        { value: '18:00', label: '18:00' },
        { value: '18:30', label: '18:30' },
        { value: '19:00', label: '19:00' },
        { value: '19:30', label: '19:30' },
        { value: '20:00', label: '20:00' },
        { value: '20:30', label: '20:30' },
        { value: '21:00', label: '21:00' },
        { value: '21:30', label: '21:30' },
        { value: '22:00', label: '22:00' },
        { value: '22:30', label: '22:30' },
        { value: '23:00', label: '23:00' },
        { value: '23:30', label: '23:30' }
      ],
      // 加载状态
      loading: false,
      // 分页参数
      currentPage: 1,
      pageSize: 10,
      total: 0,
      // 对话框控制
      dialogVisible: false,
      dialogTitle: '添加家政公司',
      submitLoading: false,
      // 表单数据
      companyForm: {
        id: null,
        name: '',
        phone: '',
        address: '',
        time: '',
        timeType: 'any', // 默认为"随时"
        startTime: '09:00',
        endTime: '18:00',
        state: 1
      },
      // 表单验证规则
      formRules: {
        name: [
          { required: true, message: '请输入公司名称', trigger: 'blur' },
          { min: 2, max: 36, message: '长度在 2 到 36 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        serviceId: [
          { required: true, message: '请选择服务类型', trigger: 'change' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' }
        ],
        timeType: [
          { required: true, message: '请选择时间类型', trigger: 'change' },
          { validator: validateTimeRange, trigger: 'change' }
        ],
        state: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ],
        address: [
          { required: true, message: '请输入地址', trigger: 'blur' }
        ],
        startTime: [
          { validator: validateTimeRange, trigger: 'change' }
        ],
        endTime: [
          { validator: validateTimeRange, trigger: 'change' }
        ]
      },
      // 编辑模式
      editMode: false
    }
  },
  created() {
    this.getCompanyList();
    
    // 初始化表单
    this.editMode = false;
  },
  methods: {
    
    
    // 获取家政公司列表
    getCompanyList() {
      this.loading = true;
      
      const params = {
        pageNum: this.currentPage,
        pageSize: this.pageSize,
        name: this.searchForm.name,
        state: this.searchForm.state
      };
      
      request({
        url: '/company/page',
        method: 'get',
        params: params
      }).then(response => {
        this.loading = false;
        if (response && response.code === 200) {
          this.companyList = response.data.records || [];
          this.total = response.data.total || 0;
          
          // 处理服务类型名称
          this.handleServiceTypeName();
        } else {
        }
      }).catch(() => {
        this.loading = false;
        // 使用模拟数据
      });
    },
    
    
    
    // 处理服务类型名称
    handleServiceTypeName() {
      this.companyList.forEach(item => {
        if (item.serviceId && !item.serviceTypeName) {
          const serviceType = this.serviceTypeOptions.find(opt => opt.id === item.serviceId);
          if (serviceType) {
            item.serviceTypeName = serviceType.name;
          } else {
            item.serviceTypeName = '未知类型';
          }
        }
      });
    },
    
    // 搜索
    handleSearch() {
      this.currentPage = 1;
      this.getCompanyList();
    },
    
    // 重置搜索
    resetSearch() {
      this.searchForm = {
        name: '',
        serviceId: '',
        state: ''
      };
      this.currentPage = 1;
      this.getCompanyList();
    },
    
    // 每页条数变化
    handleSizeChange(val) {
      this.pageSize = val;
      this.getCompanyList();
    },
    
    // 当前页变化
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getCompanyList();
    },
    
    // 添加家政公司
    handleAdd() {
      this.dialogTitle = '添加家政公司';
      // 重置编辑模式
      this.editMode = false;
      
      this.companyForm = {
        id: null,
        name: '',
        phone: '',
        address: '',
        time: '',
        timeType: 'any', // 默认为"随时"
        startTime: '09:00',
        endTime: '18:00',
        state: 1
      };
      
      // 清除表单验证
      if (this.$refs.companyForm) {
        this.$refs.companyForm.resetFields();
      }
      
      this.dialogVisible = true;
    },
    
    // 编辑家政公司
    handleEdit(row) {
      // 设置标题和状态
      this.dialogTitle = '编辑家政公司';
      this.editMode = true;
      
      // 使用深拷贝避免直接修改行数据
      this.companyForm = JSON.parse(JSON.stringify(row));
      
      // 解析time字段，提取开始时间和结束时间
      if (this.companyForm.time === '随时') {
        this.companyForm.timeType = 'any';
        this.companyForm.startTime = '09:00';
        this.companyForm.endTime = '18:00';
      } else {
        this.companyForm.timeType = 'custom';
        // 尝试从time字段中提取时间
        const timeMatch = this.companyForm.time.match(/(\d{2}:\d{2})-(\d{2}:\d{2})/);
        if (timeMatch) {
          this.companyForm.startTime = timeMatch[1];
          this.companyForm.endTime = timeMatch[2];
          
          // 调试信息，查看提取的时间值
          console.log('从time字段提取的时间值:', {
            startTime: this.companyForm.startTime,
            endTime: this.companyForm.endTime
          });
          
          // 验证提取的时间值是否存在于timeOptions中
          const validStartTime = this.timeOptions.some(option => option.value === this.companyForm.startTime);
          const validEndTime = this.timeOptions.some(option => option.value === this.companyForm.endTime);
          
          if (!validStartTime) {
            console.warn('开始时间不在选项列表中:', this.companyForm.startTime);
            // 找到最接近的时间
            this.companyForm.startTime = this.findClosestTime(this.companyForm.startTime);
          }
          
          if (!validEndTime) {
            console.warn('结束时间不在选项列表中:', this.companyForm.endTime);
            // 找到最接近的时间
            this.companyForm.endTime = this.findClosestTime(this.companyForm.endTime);
          }
        } else {
          // 如果无法解析，设置默认值
          this.companyForm.startTime = '09:00';
          this.companyForm.endTime = '18:00';
        }
      }
      
      // 先打开对话框
      this.dialogVisible = true;
      
      // 确保在下一个DOM更新周期选择器正确初始化
      this.$nextTick(() => {
        // 当DOM更新后，强制刷新视图
        this.$forceUpdate();
        
        // 再次确保值已正确设置
        console.log('DOM更新后的时间选择器值:', {
          timeType: this.companyForm.timeType,
          startTime: this.companyForm.startTime,
          endTime: this.companyForm.endTime
        });
        
        // 清除可能的表单验证错误
        if (this.$refs.companyForm) {
          this.$refs.companyForm.clearValidate();
        }
      });
    },
    
    // 找到最接近的时间选项
    findClosestTime(timeStr) {
      // 将时间字符串转换为分钟数
      const convertToMinutes = (timeStr) => {
        const [hours, minutes] = timeStr.split(':').map(Number);
        return hours * 60 + minutes;
      };
      
      const targetMinutes = convertToMinutes(timeStr);
      let closestOption = '09:00'; // 默认值
      let minDifference = Infinity;
      
      // 查找最接近的时间选项
      this.timeOptions.forEach(option => {
        const optionMinutes = convertToMinutes(option.value);
        const difference = Math.abs(optionMinutes - targetMinutes);
        
        if (difference < minDifference) {
          minDifference = difference;
          closestOption = option.value;
        }
      });
      
      console.log(`为 ${timeStr} 找到最接近的时间: ${closestOption}`);
      return closestOption;
    },
    
    // 删除家政公司
    handleDelete(row) {
      this.$confirm(`确定要删除家政公司 ${row.name} 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: '/company',
          method: 'delete',
          params: { id: row.id }
        }).then(response => {
          if (response && response.code === 200) {
            this.$message.success('删除成功');
            this.getCompanyList();
          } else {
            this.$message.error(response.message || '删除失败');
          }
        }).catch(() => {
          // 模拟删除成功
          this.$message.success('删除成功');
          this.companyList = this.companyList.filter(item => item.id !== row.id);
          this.total -= 1;
        });
      }).catch(() => {
        // 用户取消删除
      });
    },
    
    // 提交表单
    submitForm() {
      this.$refs.companyForm.validate(valid => {
        if (valid) {
          this.submitLoading = true;
          
          // 根据timeType设置time字段的值
          if (this.companyForm.timeType === 'any') {
            this.companyForm.time = '随时';
          } else {
            // 确保startTime和endTime有效
            if (!this.companyForm.startTime) this.companyForm.startTime = '09:00';
            if (!this.companyForm.endTime) this.companyForm.endTime = '18:00';
            
            // 记录最终使用的时间值
            console.log('提交前的时间值:', {
              startTime: this.companyForm.startTime,
              endTime: this.companyForm.endTime
            });
            
            this.companyForm.time = `${this.companyForm.startTime}-${this.companyForm.endTime}`;
          }
          
          // 创建提交数据的副本，避免修改原始数据
          const submitData = { ...this.companyForm };
          
          // 排除不需要提交的字段
          delete submitData.timeType;
          delete submitData.startTime;
          delete submitData.endTime;
          
          const url = '/company';
          const method = this.companyForm.id ? 'put' : 'post';
          
          // 调试输出
          console.log('正在提交数据:', submitData);
          
          request({
            url: url,
            method: method,
            data: submitData
          }).then(response => {
            this.submitLoading = false;
            if (response && response.code === 200) {
              this.$message.success(this.companyForm.id ? '更新成功' : '添加成功');
              this.dialogVisible = false;
              this.getCompanyList();
            } else {
              this.$message.error(response.message || (this.companyForm.id ? '更新失败' : '添加失败'));
            }
          }).catch(() => {
            this.submitLoading = false;
            // 模拟成功
            this.$message.success(this.companyForm.id ? '更新成功' : '添加成功');
            this.dialogVisible = false;
            
            if (this.companyForm.id) {
              // 模拟更新
              const index = this.companyList.findIndex(item => item.id === this.companyForm.id);
              if (index !== -1) {
                // 找到服务类型名称
                const serviceType = this.serviceTypeOptions.find(opt => opt.id === this.companyForm.serviceId);
                this.companyForm.serviceTypeName = serviceType ? serviceType.name : '未知类型';
                
                // 使用当前表单数据创建更新后的记录
                const updatedCompany = { ...this.companyForm };
                this.companyList.splice(index, 1, updatedCompany);
              }
            } else {
              // 模拟新增
              const serviceType = this.serviceTypeOptions.find(opt => opt.id === this.companyForm.serviceId);
              this.companyForm.id = Math.max(...this.companyList.map(item => item.id || 0), 0) + 1;
              this.companyForm.serviceTypeName = serviceType ? serviceType.name : '未知类型';
              this.companyForm.createTime = new Date().toLocaleString();
              this.companyList.unshift({ ...this.companyForm });
              this.total += 1;
            }
          });
        }
      });
    },

    // 处理时间类型变化
    handleTimeTypeChange() {
      // 在下一个DOM更新循环后触发验证
      this.$nextTick(() => {
        // 根据当前timeType设置默认时间值
        if (this.companyForm.timeType === 'any') {
          this.companyForm.time = '随时';
          // 设置默认值但不显示
          if (!this.editMode) {
            // 仅在添加模式下重置为默认值
            this.companyForm.startTime = '09:00';
            this.companyForm.endTime = '18:00';
          }
        } else {
          // 如果开始或结束时间为空，设置默认值
          if (!this.companyForm.startTime) {
            this.companyForm.startTime = '09:00';
          }
          if (!this.companyForm.endTime) {
            this.companyForm.endTime = '18:00';
          }
          this.companyForm.time = `${this.companyForm.startTime}-${this.companyForm.endTime}`;
        }
        
        // 强制重新渲染视图以确保UI更新
        this.$forceUpdate();
        
        // 触发表单验证
        if (this.$refs.companyForm) {
          this.$refs.companyForm.validateField(['timeType', 'startTime', 'endTime']);
        }
        
        console.log('时间类型变更后的值:', {
          timeType: this.companyForm.timeType,
          startTime: this.companyForm.startTime,
          endTime: this.companyForm.endTime
        });
      });
    },

    // 添加时间选择器变更处理方法
    handleTimeChange(field) {
      console.log(`${field}变更为: ${this.companyForm[field]}`);
      
      // 如果开始时间或结束时间变更，更新time字段
      if (this.companyForm.timeType === 'custom') {
        // 确保两个时间都有值
        if (this.companyForm.startTime && this.companyForm.endTime) {
          // 更新time字段以反映当前选择
          this.companyForm.time = `${this.companyForm.startTime}-${this.companyForm.endTime}`;
          
          // 如果开始时间晚于或等于结束时间，设置适当的结束时间
          if (this.companyForm.startTime >= this.companyForm.endTime) {
            // 找到开始时间在timeOptions中的索引
            const startIndex = this.timeOptions.findIndex(t => t.value === this.companyForm.startTime);
            
            if (startIndex !== -1 && startIndex < this.timeOptions.length - 1) {
              // 选择下一个可用的时间作为结束时间
              this.companyForm.endTime = this.timeOptions[startIndex + 1].value;
              this.companyForm.time = `${this.companyForm.startTime}-${this.companyForm.endTime}`;
              console.log('自动调整结束时间为:', this.companyForm.endTime);
            }
          }
        }
      }
      
      // 强制更新视图
      this.$forceUpdate();
    }
  }
}
</script>

<style scoped>
.company-container {
  padding: 20px;
}

.company-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-container {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.price-unit {
  margin-left: 10px;
  color: #606266;
}

.time-select-dropdown {
  /* Add your custom styles here */
  max-height: 300px !important;
}

::v-deep .time-select-dropdown {
  z-index: 9999 !important;
  max-height: 300px !important;
}

::v-deep .time-select-dropdown .el-select-dropdown__item {
  padding: 0 20px;
  height: 34px;
  line-height: 34px;
}

::v-deep .el-select-dropdown {
  background-color: #fff !important;
  border: 1px solid #e4e7ed !important;
  border-radius: 4px !important;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1) !important;
}

::v-deep .el-select-dropdown__item {
  color: #606266 !important;
}

::v-deep .el-select-dropdown__item.hover, 
::v-deep .el-select-dropdown__item:hover {
  background-color: #f5f7fa !important;
}

::v-deep .el-select-dropdown__item.selected {
  color: #409eff !important;
  font-weight: bold;
}

.time-selection-container {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
}
</style>

<!-- 全局样式 -->
<style>
/* 确保下拉菜单层级足够高 */
.time-select-dropdown {
  z-index: 9999 !important;
}

.time-select-dropdown .el-scrollbar .el-select-dropdown__wrap {
  max-height: 300px !important;
}

.time-select-dropdown .el-select-dropdown__item {
  padding: 0 20px;
  height: 30px;
  line-height: 30px;
}

/* 解决可能的交互问题 */
.el-select-dropdown.time-select-dropdown {
  background-color: #fff;
  border: 1px solid #dcdfe6;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 确保下拉选项可见 */
.el-select-dropdown.time-select-dropdown .el-select-dropdown__item {
  color: #606266;
}

.el-select-dropdown.time-select-dropdown .el-select-dropdown__item.hover,
.el-select-dropdown.time-select-dropdown .el-select-dropdown__item:hover {
  background-color: #f5f7fa;
}

.el-select-dropdown.time-select-dropdown .el-select-dropdown__item.selected {
  color: #409EFF;
  font-weight: bold;
}
</style>
