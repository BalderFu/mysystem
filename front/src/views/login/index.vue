<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <img src="@/assets/logo.png" alt="Logo" class="logo">
        <h1 class="title">{{ systemName }}</h1>
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
        
        <!-- 登录方式切换器 -->
        <div class="login-type-switch">
          <div 
            class="switch-item" 
            :class="{ active: loginType === 'account' }" 
            @click="switchLoginType('account')"
          >
            账号密码登录
          </div>
          <div 
            class="switch-item" 
            :class="{ active: loginType === 'phone' }" 
            @click="switchLoginType('phone')"
          >
            邮箱验证码登录
          </div>
        </div>

        <!-- 账号密码登录 -->
        <template v-if="loginType === 'account'">
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
        </template>

        <!-- 邮箱验证码登录 -->
        <template v-else>
          <el-form-item prop="phone">
            <el-input
              v-model="loginForm.phone"
              placeholder="请输入邮箱"
              prefix-icon="el-icon-message"
              name="email"
              type="text"
              tabindex="1"
            />
          </el-form-item>

          <el-form-item prop="code">
            <div class="verify-code-container">
              <el-input
                v-model="loginForm.code"
                placeholder="请输入验证码"
                prefix-icon="el-icon-message"
                name="code"
                type="text"
                tabindex="2"
                @keyup.enter.native="handleLogin"
              />
              <el-button 
                type="primary" 
                class="verify-code-button"
                :disabled="codeBtnDisabled"
                @click="getVerifyCode"
              >
                {{ codeButtonText }}
              </el-button>
            </div>
          </el-form-item>
        </template>

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
import { login, loginWithPhone, registry, sendValidateCode, userInfo } from "@/utils/inputs";
import systemService from '@/services/systemService';

export default {
  name: 'Login',
  data() {
    // 密码验证规则
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('密码不能少于6位'));
      } else {
        callback();
      }
    };
    
    // 邮箱验证规则
    const validateEmail = (rule, value, callback) => {
      const emailReg = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
      if (!emailReg.test(value)) {
        callback(new Error('请输入正确的邮箱格式'));
      } else {
        callback();
      }
    };
    
    // 手机号验证规则
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号码'));
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号码'));
      } else {
        callback();
      }
    };
    
    return {
      isLoginMode: true,
      loginType: 'account', // 登录类型：account-账号密码登录，phone-手机验证码登录
      passwordType: 'password',
      loading: false,
      imageUrl: '',
      codeButtonText: '获取验证码',
      codeBtnDisabled: false,
      codeTimer: null,
      
      // 系统名称
      systemName: systemService.getSystemName(),
      
      // 登录表单
      loginForm: {
        username: '',
        password: '',
        phone: '',
        code: ''
      },
      loginRules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        phone: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
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
    // 切换登录类型（账号密码/手机验证码）
    switchLoginType(type) {
      if (this.loginType === type) return;
      
      this.loginType = type;
      this.$nextTick(() => {
        this.$refs.loginForm.clearValidate();
      });
    },
    
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
          this.loading = true;
          
          // 根据登录类型选择不同的登录API
          const loginApi = this.loginType === 'account' ? login : loginWithPhone;
          
          // 构造请求参数
          const loginData = {};
          if (this.loginType === 'account') {
            loginData.username = this.loginForm.username;
            loginData.password = this.loginForm.password;
          } else {
            loginData.email = this.loginForm.phone;
            loginData.code = this.loginForm.code;
          }
          
          loginApi(loginData).then(response => {
            // 检查服务器响应状态
            if (response.code !== 200) {
              // 有错误信息，取消加载状态并显示错误
              this.loading = false;
              this.$message.error(response.message || '登录失败');
              return;
            }
            
            // 登录成功
            this.loading = false;
            console.log("登录成功", response);
            if (response.data) {
              Cookies.set(Constants.ID.USER_TOKEN_KEY, response.data);
              localStorage.setItem(Constants.ID.USER_TOKEN_KEY, response.data);
              localStorage.setItem(Constants.ID.USER_KEY, JSON.stringify(response));


              userInfo().then(resp => {
                console.log("用户信息", resp);
                const userRole = resp.data.role;
                const accessibleMenus = systemService.getMenus(userRole);
                let firstAccessiblePath = '/dashboard'; // 默认路径
                if (accessibleMenus && accessibleMenus.length > 0) {
                  const firstMenu = accessibleMenus[0];
                  if (firstMenu.children && firstMenu.children.length > 0) {
                    firstAccessiblePath = firstMenu.children[0].path;
                  } else {
                    firstAccessiblePath = firstMenu.path;
                  }
                }
                this.$router.push({ path: firstAccessiblePath });
              })

            } else {
              this.$message.error('登录响应缺少用户信息');
            }
          }).catch(error => {
            this.loading = false;
            this.$message.error('登录失败，请稍后重试');
            console.error('登录失败:', error);
          });
        }
      });
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
    },

    // 获取验证码
    getVerifyCode() {
      // 获取邮箱输入值
      const email = this.loginForm.phone;
      
      // 1. 检查邮箱是否为空
      if (!email) {
        this.$message.error('请先输入邮箱地址');
        return;
      }
      
      // 2. 验证邮箱格式
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
        this.$message.error('请输入正确的邮箱地址');
        return;
      }
      
      // 3. 发送验证码
      this.codeBtnDisabled = true;
      this.codeButtonText = '发送中...';
      
      sendValidateCode({ email: email }).then(response => {
        console.log("验证码响应：", response)
        
        // 检查响应是否为null或undefined
        if (!response) {
          this.codeBtnDisabled = false
          this.codeButtonText = '获取验证码'
          this.$message.error('验证码发送失败，服务器无响应')
          return
        }
        
        // 检查响应状态
        if (response.code !== 200) {
          // 处理错误响应
          this.codeBtnDisabled = false
          this.codeButtonText = '获取验证码'
          this.$message.error(response.message || '验证码发送失败')
          return
        }
        
        // 成功发送验证码
        this.$message.success('验证码已发送，请注意查收')
        // 开始倒计时
        let countdown = 60
        this.codeButtonText = `${countdown}秒后重发`
        
        this.codeTimer = setInterval(() => {
          countdown--
          this.codeButtonText = `${countdown}秒后重发`
          if (countdown <= 0) {
            clearInterval(this.codeTimer)
            this.codeButtonText = '获取验证码'
            this.codeBtnDisabled = false
          }
        }, 1000)
      }).catch(error => {
        console.log("发送验证码失败===", error)
        this.codeBtnDisabled = false
        this.codeButtonText = '获取验证码'
        this.$message.error('验证码发送失败，请稍后重试')
        console.error('发送验证码失败:', error)
      })
    }
  },
  destroyed() {
    // 清除计时器
    if (this.codeTimer) {
      clearInterval(this.codeTimer)
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

.verify-code-container {
  display: flex;
  align-items: center;
  
  .el-input {
    flex: 1;
  }
  
  .verify-code-button {
    margin-left: 10px;
    width: 120px;
    height: 45px;
    border-radius: 8px;
    font-size: 14px;
    background: linear-gradient(135deg, var(--accent-color) 0%, var(--accent-dark) 100%);
    border: none;
    white-space: nowrap;
  }
}

.login-type-switch {
  display: flex;
  margin-bottom: 25px;
  border-radius: 8px;
  overflow: hidden;
  background-color: rgba(15, 23, 42, 0.4);
  border: 1px solid rgba(56, 189, 248, 0.2);
  
  .switch-item {
    flex: 1;
    text-align: center;
    padding: 12px 0;
    cursor: pointer;
    color: var(--text-secondary);
    font-size: 14px;
    transition: all 0.3s;
    position: relative;
    
    &:hover {
      color: white;
    }
    
    &.active {
      color: white;
      background-color: rgba(56, 189, 248, 0.2);
      font-weight: 500;
      
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 20%;
        right: 20%;
        height: 2px;
        background-color: var(--accent-color);
        border-radius: 2px;
      }
    }
    
    &:first-child {
      border-right: 1px solid rgba(56, 189, 248, 0.2);
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
