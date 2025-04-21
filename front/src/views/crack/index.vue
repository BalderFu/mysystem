<template>
  <div class="crack-container">
    <el-card class="crack-card">
      <div slot="header" class="card-header">
        <span>算法破解</span>
      </div>
      <div class="crack-content">
        <!-- 这里添加算法破解相关的内容 -->
        <el-form :model="crackForm" ref="crackForm" label-width="100px">
          <el-form-item label="算法类型">
            <el-select v-model="crackForm.type" placeholder="请选择算法类型">
              <el-option 
                v-for="item in crackList" 
                :key="item.key" 
                :label="item.value" 
                :value="item.key">
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="哈希值">
            <el-input v-model="crackForm.hash" placeholder="请输入需要破解的哈希值"></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="startCrack">开始破解</el-button>
          </el-form-item>
        </el-form>
        
        <!-- 破解结果显示 -->
        <template v-if="hasCracked">
          <div v-if="crackResults.length > 0" class="crack-results">
            <h3>破解结果：</h3>
            <el-table
              :data="crackResults.map(item => ({value: item}))"
              border
              style="width: 100%">
              <el-table-column
                prop="value"
                label="明文"
                align="center">
              </el-table-column>
            </el-table>
          </div>
          <div v-else class="no-result">
            <el-empty description="暂无破解结果，请更新现有算法库后再重试"></el-empty>
          </div>
        </template>
      </div>
    </el-card>
  </div>
</template>

<script>
import { hashList, crackArithmetic } from '@/utils/inputs'
export default {
  name: 'Crack',
  data() {
    return {
      crackForm: {
        type: '',
        hash: ''
      },
      crackResults: [],
      crackList: [],
      hasCracked: false  // 添加状态标记是否已进行破解
    }
  },
  methods: {
    startCrack() {
      if (!this.crackForm.type || !this.crackForm.hash) {
        this.$message.warning('请选择算法类型并输入哈希值');
        return;
      }
      
      const params = {
        id: this.crackForm.type,
        hashText: this.crackForm.hash
      };
      
      console.log('开始破解:', params);
      this.hasCracked = true;  // 开始破解时设置状态
      crackArithmetic(params).then(res => {
        console.log("crackArithmetic:", res)
        if (res.code === 200) {
          if (res.data && res.data.length > 0) {
            this.crackResults = res.data;
          } else {
            // this.$message.warning('更新现有算法库后再重试');
            this.crackResults = [];
          }
        } else {
          this.$message.error(res.message || '破解失败');
          this.crackResults = [];
        }
      }).catch(error => {
        console.error('破解失败:', error);
        this.$message.error('破解失败');
        this.crackResults = [];
      });
    },
    getCrackList() {
      hashList().then(res => {
        console.log("hashList:", res)
        if (res.code === 200 && res.data) {
          // 将对象转换为数组格式
          this.crackList = Object.entries(res.data).map(([id, name]) => ({
            key: id,     // 使用id作为key和value
            value: name  // 使用name作为显示标签
          }));
        } else {
          this.$message.error(res.message || '获取算法列表失败');
          this.crackList = [];
        }
      }).catch(error => {
        console.error('获取算法列表失败:', error);
        this.$message.error('获取算法列表失败');
        this.crackList = [];
      });
    }
  },
  mounted() {
    this.getCrackList()
  }
}
</script>

<style lang="scss" scoped>
.crack-container {
  padding: 20px;
  
  .crack-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      span {
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .crack-content {
      padding: 20px;
      
      .crack-results {
        margin-top: 20px;
        
        h3 {
          margin-bottom: 15px;
          color: #409EFF;
        }
      }

      .no-result {
        margin-top: 40px;
        text-align: center;
      }
    }
  }
}
</style> 