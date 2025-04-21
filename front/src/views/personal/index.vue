<template>
  <div class="personal-container">
    <el-card class="profile-card">
      <div slot="header" class="profile-header">
        <span class="title">个人中心</span>
        <el-button type="primary" size="small" @click="saveChanges" :disabled="!isEditing">保存修改</el-button>
      </div>
      
      <div class="profile-avatar-section">
        <div class="avatar-wrapper">
          <template v-if="avatarUrl">
            <img :src="avatarUrl" alt="用户头像" class="user-avatar">
          </template>
          <template v-else>
            <div class="default-avatar">
              <i class="el-icon-user-solid"></i>
            </div>
          </template>
          <div class="avatar-overlay" @click="uploadAvatar">
            <i class="el-icon-camera"></i>
            <span>更换头像</span>
          </div>
        </div>
      </div>
      
      <div class="profile-info-section">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item" @click="editField('username')">
              <div class="item-label">账号</div>
              <div class="item-content" v-if="!editingFields.username">{{ userInfo.username || '-' }}</div>
              <el-input 
                v-else 
                v-model="userInfo.username" 
                size="small"
                placeholder="请输入账号"
                @input="onFieldChanged"
                @blur="editingFields.username = false"
                ref="usernameInput"
              ></el-input>
            </div>
          </el-col>
          
          <el-col :span="12">
            <div class="info-item" @click="editField('nickname')">
              <div class="item-label">用户名</div>
              <div class="item-content" v-if="!editingFields.nickname">{{ userInfo.nickname || '-' }}</div>
              <el-input 
                v-else 
                v-model="userInfo.nickname" 
                size="small"
                placeholder="请输入用户名"
                @input="onFieldChanged"
                @blur="editingFields.nickname = false"
                ref="nicknameInput"
              ></el-input>
            </div>
          </el-col>
          
          <el-col :span="12">
            <div class="info-item" @click="editField('phone')">
              <div class="item-label">手机号</div>
              <div class="item-content" v-if="!editingFields.phone">{{ userInfo.phone || '-' }}</div>
              <el-input 
                v-else 
                v-model="userInfo.phone" 
                size="small"
                placeholder="请输入手机号"
                @input="onFieldChanged"
                @blur="editingFields.phone = false"
                ref="phoneInput"
              ></el-input>
            </div>
          </el-col>
          
          <el-col :span="12">
            <div class="info-item" @click="editField('email')">
              <div class="item-label">邮箱</div>
              <div class="item-content" v-if="!editingFields.email">{{ userInfo.email || '-' }}</div>
              <el-input 
                v-else 
                v-model="userInfo.email" 
                size="small"
                placeholder="请输入邮箱"
                @input="onFieldChanged"
                @blur="editingFields.email = false"
                ref="emailInput"
              ></el-input>
            </div>
          </el-col>
        </el-row>
        
        <el-divider></el-divider>
        
        <div class="security-section">
          <div class="section-title">安全设置</div>
          
          <div class="security-item" @click="showPasswordDialog">
            <div class="item-left">
              <i class="el-icon-lock security-icon"></i>
              <div class="item-info">
                <div class="item-title">账户密码</div>
                <div class="item-desc">定期修改密码有助于账户安全</div>
              </div>
            </div>
            <div class="item-action">
              <span>修改</span>
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      :visible.sync="passwordDialogVisible"
      width="400px"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="passwordDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="changePassword">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import Constants from "@/utils/constants";
import { updateUserInfo, uploadPic, resetPassword } from "@/utils/inputs";

export default {
  name: "Personal",
  data() {
    // 确认密码的验证规则
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    };
    
    return {
      baseUrl: "http://localhost:8090",
      userInfo: {
        username: "",
        nickname: "",
        phone: "",
        email: "",
        avatar: ""
      },
      originalUserInfo: null,
      hasChanges: false,
      avatarUrl: "",
      editingFields: {
        username: false,
        nickname: false,
        phone: false,
        email: false
      },
      // 会话信息相关数据
      chatSessions: [],
      sessionsLoading: false,
      currentPage: 1,
      pageSize: 10,
      totalSessions: 0,
      passwordDialogVisible: false,
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getUserInfo();
 
  },
  computed: {
    // 判断是否有任何字段正在编辑或数据发生变化
    isEditing() {
      return Object.values(this.editingFields).some(val => val === true) || this.hasChanges;
    }
  },
  methods: {
    // 字段值变化时触发
    onFieldChanged() {
      this.checkChanges();
    },
    
    // 检查是否有数据变化
    checkChanges() {
      if (!this.originalUserInfo) return;
      
      const { username, nickname, phone, email, avatar } = this.userInfo;
      const original = this.originalUserInfo;
      
      this.hasChanges = 
        username !== original.username ||
        nickname !== original.nickname ||
        phone !== original.phone ||
        email !== original.email ||
        avatar !== original.avatar;
    },
    
    // 获取用户信息
    getUserInfo() {
      const user = localStorage.getItem(Constants.ID.USER_KEY);
      if (user) {
        // 尝试解析JSON数据
        let userInfo;
        try {
          userInfo = JSON.parse(user);
          console.log("从localStorage获取的用户信息:", userInfo);
          
          // 检查userInfo是否有data字段，这表明存储的是完整的响应对象
          if (userInfo.data) {
            userInfo = userInfo.data;
          }
          
          // 将用户信息赋值给组件
          this.userInfo = {
            username: userInfo.username || "",
            nickname: userInfo.nickname || "",
            phone: userInfo.phone || "",
            email: userInfo.email || "",
            avatar: userInfo.avatar || ""
          };
          
          // 保存原始数据用于比较是否有修改
          this.originalUserInfo = { ...this.userInfo };
          this.hasChanges = false;
          
          // 设置头像URL
          if (this.userInfo.avatar) {
            this.avatarUrl = `${this.baseUrl}/upload/${this.userInfo.avatar}?t=${new Date().getTime()}`;
          } else {
            // 使用Element UI的用户图标作为默认头像
            this.avatarUrl = '';
          }
        } catch (error) {
          console.error("解析用户信息时出错:", error);
          this.$message.error("获取用户信息失败，请重新登录");
        }
      }
    },
    
    // 编辑字段
    editField(field) {
      // 设置当前字段为编辑状态
      this.$set(this.editingFields, field, true);
      
      // 在下一个DOM更新周期后，聚焦输入框
      this.$nextTick(() => {
        if (this.$refs[field + 'Input']) {
          this.$refs[field + 'Input'].focus();
        }
      });
    },
    
    // 保存修改
    saveChanges() {
      // 调用API将修改保存到后端
      // const loading = this.$loading({
      //   lock: true,
      //   text: '保存中...',
      //   spinner: 'el-icon-loading',
      //   background: 'rgba(0, 0, 0, 0.7)'
      // });
      
      // 创建一个不包含avatar字段的用户信息对象
      const userInfoToSave = {
        username: this.userInfo.username,
        nickname: this.userInfo.nickname,
        phone: this.userInfo.phone,
        email: this.userInfo.email
      };
      
      updateUserInfo(userInfoToSave).then(response => {
        // loading.close();
        this.$message({
          message: '内容生成修改成功',
          type: 'success'
        })
        
        // 更新本地存储
        const user = JSON.parse(localStorage.getItem(Constants.ID.USER_KEY) || "{}");
        Object.assign(user, this.userInfo);
        localStorage.setItem(Constants.ID.USER_KEY, JSON.stringify(user));
        
        // 更新原始数据
        this.originalUserInfo = { ...this.userInfo };
        this.hasChanges = false;
        
        // 重置编辑状态
        for (const key in this.editingFields) {
          this.$set(this.editingFields, key, false);
        }
      }).catch(error => {
        loading.close(); // 确保在错误时关闭loading
        this.$message({
          message: '保存失败，请稍后重试',
          type: 'error'
        });
        console.error('更新用户信息失败:', error);
      });
    },
    
    // 上传头像
    uploadAvatar() {
      // 创建文件输入框
      const input = document.createElement('input');
      input.type = 'file';
      input.accept = 'image/*';
      
      // 监听文件选择事件
      input.onchange = (e) => {
        const file = e.target.files[0];
        if (file) {
          // 调用API上传头像
          const formData = new FormData();
          formData.append('file', file);
          
          const loading = this.$loading({
            lock: true,
            text: '上传中...',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });
          
          uploadPic(formData).then(response => {
            loading.close();
            
            // 假设后端返回的数据中包含了上传后的文件名
            if (response && response.filename) {
              // 更新用户信息中的头像
              this.userInfo.avatar = response.filename;
              this.avatarUrl = `${this.baseUrl}/upload/${response.filename}?t=${new Date().getTime()}`;
              this.checkChanges(); // 检测变化
              
              // 提示上传成功
              this.$message({
                message: '头像上传成功',
                type: 'success'
              });
            } else {
              this.$message({
                message: '头像上传失败，请稍后重试',
                type: 'error'
              });
            }
          }).catch(error => {
            loading.close();
            this.$message({
              message: '头像上传失败，请稍后重试',
              type: 'error'
            });
            console.error('上传头像失败:', error);
          });
        }
      };
      
      // 触发文件选择
      input.click();
    },
    
    // 显示修改密码对话框
    showPasswordDialog() {
      this.passwordDialogVisible = true;
      this.passwordForm = {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      };
    },
    
    // 修改密码
    changePassword() {
      this.$refs.passwordForm.validate((valid) => {
        if (valid) {
          
          // 构造重置密码请求参数
          const resetData = {
            originPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          };
          
          // 调用重置密码API
          resetPassword(resetData).then(response => {
            this.$message({
              message: '密码修改成功',
              type: 'success'
            });
            this.passwordDialogVisible = false;
            localStorage.clear();
            this.$router.push('/login');
          }).catch(error => {
            loading.close(); // 确保在错误时关闭loading
            this.$message({
              message: '密码修改失败，请检查当前密码是否正确',
              type: 'error'
            });
            console.error('修改密码失败:', error);
          });
        } else {
          return false;
        }
      });
    },
    
    
    
    // 分页大小改变
    handleSizeChange(size) {
      this.pageSize = size;
      this.fetchChatSessions();
    },
    
    // 当前页改变
    handleCurrentChange(page) {
      this.currentPage = page;
      this.fetchChatSessions();
    },
    
    // 查看会话
    handleViewSession(row) {
      console.log('查看会话', row);
      // 实际操作可根据需求添加
    },
    
    // 删除会话
    handleDeleteSession(row) {
      this.$confirm('确认删除此会话?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        console.log('删除会话', row);
        // 实际删除逻辑可根据需求添加
        this.$message({
          type: 'success',
          message: '删除成功!'
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });          
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.personal-container {
  padding: 20px;
  
  .profile-card {
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    background-color: var(--background-card);
    border: 1px solid var(--border-color);
    
    .profile-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .title {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
      }
    }
    
    .profile-avatar-section {
      display: flex;
      justify-content: center;
      margin: 20px 0;
      
      .avatar-wrapper {
        position: relative;
        width: 100px;
        height: 100px;
        border-radius: 50%;
        overflow: hidden;
        cursor: pointer;
        
        .user-avatar {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .default-avatar {
          width: 100%;
          height: 100%;
          display: flex;
          justify-content: center;
          align-items: center;
          background-color: var(--background-lighter);
          
          i {
            font-size: 50px;
            color: var(--accent-color);
          }
        }
        
        .avatar-overlay {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background-color: rgba(0, 0, 0, 0.5);
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          opacity: 0;
          transition: opacity 0.3s;
          
          i {
            font-size: 24px;
            color: white;
            margin-bottom: 5px;
          }
          
          span {
            font-size: 12px;
            color: white;
          }
        }
        
        &:hover .avatar-overlay {
          opacity: 1;
        }
      }
    }
    
    .profile-info-section {
      padding: 0 20px 20px;
      
      .info-item {
        margin-bottom: 20px;
        padding: 10px;
        border-radius: 6px;
        transition: background-color 0.3s;
        cursor: pointer;
        
        &:hover {
          background-color: rgba(255, 255, 255, 0.05);
        }
        
        .item-label {
          font-size: 14px;
          color: var(--text-tertiary);
          margin-bottom: 8px;
        }
        
        .item-content {
          font-size: 16px;
          color: var(--text-primary);
          word-break: break-all;
        }
      }
      
      .chat-sessions-section {
        margin-top: 20px;
        
        .section-title {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 15px;
        }
        
        .pagination-container {
          margin-top: 20px;
          display: flex;
          justify-content: center;
        }
      }
      
      .security-section {
        margin-top: 20px;
        
        .section-title {
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 15px;
        }
        
        .security-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 15px;
          border-radius: 6px;
          transition: background-color 0.3s;
          cursor: pointer;
          
          &:hover {
            background-color: rgba(255, 255, 255, 0.05);
          }
          
          .item-left {
            display: flex;
            align-items: center;
            
            .security-icon {
              font-size: 24px;
              color: var(--accent-color);
              margin-right: 12px;
            }
            
            .item-info {
              .item-title {
                font-size: 16px;
                color: var(--text-primary);
                margin-bottom: 4px;
              }
              
              .item-desc {
                font-size: 12px;
                color: var(--text-tertiary);
              }
            }
          }
          
          .item-action {
            display: flex;
            align-items: center;
            color: var(--text-tertiary);
            
            span {
              margin-right: 5px;
            }
          }
        }
      }
    }
  }
}

// 修改ElementUI样式
::v-deep .el-dialog {
  background-color: var(--background-card);
  border-radius: 8px;
  
  .el-dialog__header {
    background-color: var(--background-lighter);
    padding: 15px 20px;
    border-radius: 8px 8px 0 0;
    
    .el-dialog__title {
      color: var(--text-primary);
      font-weight: 600;
    }
    
    .el-dialog__headerbtn .el-dialog__close {
      color: var(--text-tertiary);
      
      &:hover {
        color: var(--accent-color);
      }
    }
  }
  
  .el-dialog__body {
    padding: 20px;
    color: var(--text-primary);
  }
  
  .el-dialog__footer {
    padding: 10px 20px 20px;
    border-top: 1px solid var(--border-color);
  }
}
</style> 