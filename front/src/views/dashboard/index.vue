<template>
  <div class="dashboard-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>手机信息录入</span>
      </div>
      <el-form ref="phoneForm" :model="phoneForm" label-width="120px">
        <el-form-item label="手机名字">
          <el-input v-model="phoneForm.name" placeholder="请输入手机型号名称"></el-input>
        </el-form-item>
        <el-form-item label="手机配置参数" class="textarea-item">
          <el-input 
            type="textarea" 
            :rows="6" 
            v-model="phoneForm.config"
            placeholder="请输入详细配置参数，例如：CPU: Snapdragon 8 Gen 2, RAM: 12GB, Storage: 256GB, Screen: 6.7 inch AMOLED"
          ></el-input>
        </el-form-item>
        <el-form-item label="手机特征" class="textarea-item features-textarea">
          <el-input 
            type="textarea" 
            :rows="8"
            v-model="phoneForm.features"
            placeholder="请输入手机的主要特征或卖点，例如：1亿像素主摄, 120W快充, IP68防水"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="loading">{{ loading ? '提交中...' : '提交' }}</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
// 导入请求函数 (假设路径正确)
import create from "@/utils/request"; 

export default {
  name: 'Dashboard',
  data() {
    return {
      phoneForm: {
        name: '',
        config: '',
        features: ''
      },
      loading: false // 添加 loading 状态
    };
  },
  methods: {
    onSubmit() {
      // 可选：添加表单验证
      this.$refs.phoneForm.validate(valid => {
        if (valid) {
          console.log('表单验证通过，准备提交:', this.phoneForm);
          this.loading = true; // 开始加载
          
          create({
            url: '/phoneInfo', // 后端接口地址
            method: 'post',
            data: this.phoneForm // 发送表单数据
          }).then(response => {
            console.log('提交响应:', response);
            this.loading = false; // 结束加载
            
            // 根据后端响应判断是否成功
            if (response && response.code === 200) { 
              this.$message.success('提交成功！');
              // 成功后可以选择清空表单
              this.onReset(); 
            } else {
              // 处理后端返回的错误信息
              this.$message.error(response.message || '提交失败，请稍后重试');
            }
          }).catch(error => {
            console.error('提交错误:', error);
            this.loading = false; // 结束加载
            this.$message.error('提交时发生网络错误，请检查连接');
          });
          
        } else {
          console.log('表单验证失败');
          this.$message.warning('请检查表单内容是否填写完整');
          return false;
        }
      });
    },
    onReset() {
      this.$refs.phoneForm.resetFields();
      this.phoneForm = {
        name: '',
        config: '',
        features: ''
      };
      console.log('表单已重置');
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 20px;
}

.box-card {
  // 可以添加卡片样式
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

// 通用 textarea 样式
.textarea-item ::v-deep .el-textarea__inner {
  min-height: 120px !important; /* 配置参数的最小高度 */
}

// 单独设置手机特征的高度
.features-textarea ::v-deep .el-textarea__inner {
  min-height: 160px !important; /* 设置一个更高的高度 */
}
</style>