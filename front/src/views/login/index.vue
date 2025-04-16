<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="@/assets/logo.png" alt="Logo" class="logo">
        <h1 class="title">文案生成系统</h1>
      </div>

      <!-- 登录表单 -->
      <el-form
        v-if="isLoginMode"
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        auto-complete="on"
      >
        <h3 class="form-title">用户登录</h3>

        <el-form-item prop="username">
          <el-input
            ref="username"
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            name="username"
            type="text"
            tabindex="1"
            auto-complete="on"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            ref="password"
            v-model="loginForm.password"
            :type="passwordType"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            name="password"
            tabindex="2"
            auto-complete="on"
            @keyup.enter.native="handleLogin"
          >
            <i
              slot="suffix"
              class="el-input__icon password-eye"
              :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-hide'"
              @click="showPwd"
            ></i>
          </el-input>
        </el-form-item>

        <div class="form-actions">
          <el-button
            :loading="loading"
            type="primary"
            class="login-button"
            @click.native.prevent="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
          <div class="register-link">
            <span>没有账号？</span>
            <a @click.prevent="switchToRegister">立即注册</a>
          </div>
        </div>
      </el-form>

      <!-- 注册表单 -->
      <el-form
        v-else
        ref="registerForm"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        auto-complete="on"
      >
        <h3 class="form-title">用户注册</h3>

        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            name="username"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            :type="passwordType"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            name="password"
          >
            <i
              slot="suffix"
              class="el-input__icon password-eye"
              :class="passwordType === 'password' ? 'el-icon-view' : 'el-icon-hide'"
              @click="showPwd"
            ></i>
          </el-input>
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入昵称"
            prefix-icon="el-icon-message"
            name="nickname"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="el-icon-mobile-phone"
            name="phone"
            type="text"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            prefix-icon="el-icon-message"
            name="email"
            type="text"
          />
        </el-form-item>

        

        <div class="form-actions">
          <el-button
            :loading="loading"
            type="primary"
            class="register-button"
            @click.native.prevent="handleRegister"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
          <div class="login-link">
            <span>已有账号？</span>
            <a @click.prevent="switchToLogin">返回登录</a>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { validUsername } from '@/utils/validate'
import Constants from "@/utils/constants";
import Cookies from 'js-cookie';
import { login, registry } from "@/utils/inputs"

export default {
  name: 'Login',
  data() {
    return {
      isLoginMode: true,
      passwordType: 'password',
      loading: false,
      imageUrl: '',
      
      // 登录表单
      loginForm: {
        username: 'admin',
        password: '123456'
      },
      loginRules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      
      // 注册表单
      registerForm: {
        username: '',
        password: '',
        phone: '',
        email: '',
        nickname: '',
        avatar: ''
      },
      registerRules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号码', trigger: 'blur' }],
        email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
      },
      
      uploadHeaders: {
        // 如果需要添加上传headers
      },
      fileList: []
    }
  },
  methods: {
    showPwd() {
      this.passwordType = this.passwordType === 'password' ? '' : 'password'
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    
    switchToRegister() {
      // 先切换模式，避免校验触发
      this.isLoginMode = false;
      
      // 重置表单数据，防止之前的验证状态保留
      if (this.$refs.loginForm) {
        this.$refs.loginForm.resetFields();
      }
      
      // 延迟执行，确保DOM更新后再清除校验
      this.$nextTick(() => {
        if (this.$refs.registerForm) {
          this.$refs.registerForm.clearValidate();
        }
      });
    },
    
    switchToLogin() {
      // 先切换模式，避免校验触发
      this.isLoginMode = true;
      
      // 重置注册表单数据，防止之前的验证状态保留
      if (this.$refs.registerForm) {
        this.$refs.registerForm.resetFields();
      }
      
      // 延迟执行，确保DOM更新后再清除校验
      this.$nextTick(() => {
        if (this.$refs.loginForm) {
          this.$refs.loginForm.clearValidate();
        }
      });
    },
    
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          login(this.loginForm).then(response => {
            this.loading = false
            console.log("登录成功",response)
            // if (response.code === 200) {
              Cookies.set(Constants.ID.USER_TOKEN_KEY, response)
              localStorage.setItem(Constants.ID.USER_TOKEN_KEY, response)
              localStorage.setItem(Constants.ID.USER_KEY, JSON.stringify(response))
              this.$router.push({ path: '/dashboard' })
            // } else {
            //   this.$message.error(response.msg || '登录失败')
            // }
          }).catch(() => {
            this.loading = false
            this.$message.error('登录失败，请稍后重试')
          })
        }
      })
    },
    
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          registry(this.registerForm).then(response => {
            this.loading = false
            if (response.code === 200) {
              this.$message.success('注册成功')
              this.switchToLogin()
            } else {
              this.$message.error(response.msg || '注册失败')
            }
          }).catch(() => {
            this.loading = false
            this.$message.error('注册失败，请稍后重试')
          })
        }
      })
    },
    
    beforeUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 或 PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    },
    
    handleSuccess(response, file) {
      if (response.code === 200) {
        this.imageUrl = URL.createObjectURL(file.raw);
        this.registerForm.avatar = response.data;
      } else {
        this.$message.error('上传失败');
      }
    },
    
    handleError() {
      this.$message.error('上传失败，请稍后重试');
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #0F172A 0%, #1E293B 100%);
  overflow: hidden;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      radial-gradient(circle at 15% 50%, rgba(56, 189, 248, 0.1) 0%, transparent 25%),
      radial-gradient(circle at 85% 30%, rgba(79, 70, 229, 0.15) 0%, transparent 30%);
    z-index: 0;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%"><pattern id="pattern" x="0" y="0" width="40" height="40" patternUnits="userSpaceOnUse"><circle cx="20" cy="20" r="0.5" fill="rgba(56, 189, 248, 0.2)"/></pattern><rect x="0" y="0" width="100%" height="100%" fill="url(%23pattern)"/></svg>');
    opacity: 0.2;
    z-index: 0;
  }
  
  .login-box {
    width: 400px;
    background-color: rgba(30, 41, 59, 0.8);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(56, 189, 248, 0.2);
    border-radius: 12px;
    padding: 40px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.5), 0 0 20px rgba(56, 189, 248, 0.15);
    position: relative;
    z-index: 1;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 5px;
      background: linear-gradient(90deg, var(--accent-color), var(--primary-light));
    }
    
    .login-header {
      text-align: center;
      margin-bottom: 40px;
      
      .logo {
        height: 70px;
        margin-bottom: 20px;
        filter: drop-shadow(0 0 8px rgba(56, 189, 248, 0.6));
      }
      
      .title {
        font-size: 28px;
        margin: 0;
        font-weight: 600;
        letter-spacing: 1px;
        background: linear-gradient(to right, var(--accent-light), var(--primary-light));
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        text-shadow: 0 0 20px rgba(56, 189, 248, 0.3);
      }
    }
    
    .form-title {
      font-size: 18px;
      color: white;
      margin-bottom: 30px;
      font-weight: 500;
      text-align: center;
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -10px;
        left: 50%;
        transform: translateX(-50%);
        width: 50px;
        height: 2px;
        background: var(--accent-gradient);
        border-radius: 2px;
      }
    }
    
    .login-form,
    .register-form {
      .el-form-item {
        margin-bottom: 25px;
        position: relative;
      }
      
      .el-input {
        height: 45px;
        
        input {
          height: 45px;
          padding-left: 45px !important;
          background-color: rgba(15, 23, 42, 0.6);
          border: 1px solid rgba(56, 189, 248, 0.3);
          border-radius: 8px;
          color: white;
          font-size: 15px;
          box-sizing: border-box;
          
          &:focus {
            background-color: rgba(15, 23, 42, 0.8);
            border-color: var(--accent-color);
            box-shadow: 0 0 0 2px rgba(56, 189, 248, 0.2);
          }
          
          &::placeholder {
            color: rgba(255, 255, 255, 0.4);
          }
        }
        
        .el-input__prefix {
          left: 15px;
          color: var(--accent-color);
          display: flex;
          align-items: center;
          height: 100%;
          
          i {
            font-size: 18px;
            line-height: 1;
          }
        }
      }
      
      .password-eye {
        cursor: pointer;
        font-size: 18px;
        color: var(--accent-color);
        margin-right: 10px;
        transition: all 0.2s;
        height: 100%;
        display: flex;
        align-items: center;
        
        &:hover {
          color: white;
          transform: scale(1.1);
        }
      }
      
      .el-form-item__error {
        padding-top: 4px;
        font-size: 12px;
      }
    }
    
    .form-actions {
      margin-top: 35px;
      
      .login-button,
      .register-button {
        width: 100%;
        padding: 12px 20px;
        font-size: 16px;
        border-radius: 8px;
        background: var(--accent-gradient);
        border-color: transparent;
        position: relative;
        overflow: hidden;
        transition: all 0.3s ease;
        
        &::before {
          content: '';
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
          transition: all 0.6s ease;
        }
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 7px 14px rgba(0, 0, 0, 0.2), 0 0 10px rgba(56, 189, 248, 0.3);
          
          &::before {
            left: 100%;
          }
        }
      }
      
      .register-link,
      .login-link {
        margin-top: 20px;
        text-align: center;
        font-size: 14px;
        color: var(--text-secondary);
        
        a {
          color: var(--accent-light);
          cursor: pointer;
          margin-left: 5px;
          font-weight: 500;
          transition: all 0.2s;
          
          &:hover {
            color: white;
            text-shadow: 0 0 8px rgba(56, 189, 248, 0.6);
          }
        }
      }
    }
    
    .form-bottom-text {
      margin-top: 15px;
      font-size: 14px;
      color: var(--text-secondary);
      text-align: center;
    }
  }
  
  .avatar-uploader {
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .avatar-uploader-icon {
      border: 2px dashed rgba(56, 189, 248, 0.3);
      border-radius: 8px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      font-size: 28px;
      color: var(--accent-color);
      width: 110px;
      height: 110px;
      line-height: 110px;
      text-align: center;
      background-color: rgba(15, 23, 42, 0.6);
      transition: all 0.3s;
      
      &:hover {
        border-color: var(--accent-color);
        color: white;
        background-color: rgba(56, 189, 248, 0.1);
        transform: scale(1.03);
        box-shadow: 0 0 15px rgba(56, 189, 248, 0.2);
      }
    }
    
    .avatar-preview {
      width: 110px;
      height: 110px;
      border-radius: 8px;
      display: block;
      object-fit: cover;
      border: 2px solid var(--accent-color);
      box-shadow: 0 0 15px rgba(56, 189, 248, 0.3);
      transition: all 0.3s;
      
      &:hover {
        transform: scale(1.03);
        box-shadow: 0 0 20px rgba(56, 189, 248, 0.5);
      }
    }
    
    .el-upload__tip {
      margin-top: 12px;
      color: var(--text-secondary);
    }
  }
}

@media (max-width: 576px) {
  .login-container {
    padding: 20px;
    
    .login-box {
      width: 100%;
      padding: 30px 20px;
    }
  }
}
</style>
