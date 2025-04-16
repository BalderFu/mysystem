<template>
  <div>
    <table>
      <tr>
        <td>用户名：</td>
        <td class = "weigth">{{userInfo.username}}</td>
        
      </tr>
      <tr>
        <td>角色：</td>
        <td> {{userInfo.role}}</td>
      </tr>
      <tr>
        <td>手机号码：</td>
        <td> {{userInfo.phone}}</td>
       
      </tr>
      <tr>
        <td>头像：</td>
        <!-- <td> {{userInfo.avatar}}</td> -->
        <div class="demo-type">
        <div>
          <el-avatar :src="fullImageUrl"></el-avatar>
        </div>
      </div>
      </tr>
      <tr>
        <td>邮箱：</td>
        <td> {{userInfo.email}}</td>
       
      </tr>
    </table>
  </div>
</template>


<script>
import Constants from "@/utils/constants";
import {userInfo} from "@/utils/inputs";
// import mammoth from 'mammoth';

export default {
  name: "Dashboard",
  data() {
    return {
      baseUrl: "http://localhost:8090",
      userInfo:{
        username: "",
        role: "",
        createTime: "",
        avartar: "",
      },


    }
  },
  methods: {
    handleSubmit() {
      this.$refs.ruleForm.validate(valid => {
        if (!valid) return;

        if (this.title === "新增") {
          this.tableData.push(this.dataForm);
        } else {
          const index = this.tableData.findIndex(item => item.id === this.dataForm.id);
          this.tableData[index] = this.dataForm;
        }
        this.addShow = false;
        this.handlePersistence()
      })
    },

    handleEdit(row) {
      this.dataForm = row;
      this.title = "修改";
      this.addShow = true;
    },

    handleRemove(row) {
      const index = this.tableData.findIndex(item => item.id === row.id);
      this.tableData.splice(index, 1);
      this.handlePersistence()
    },

    handleAdd() {
      this.title = "新增";
      this.addShow = true;
      let id = this.generateRandomId(64);
      while (this.tableData.findIndex(item => item.id === id) > 0) {
        id = this.generateRandomId(64);
      }
      this.dataForm = {
        id,
        name: ""
      };
    },

    generateRandomId(length = 10) {
      const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      let randomId = '';

      for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * characters.length);
        randomId += characters[randomIndex];
      }

      return randomId;
    },

    handlePersistence() {
      localStorage.setItem(Constants.ID.SENSITIVE_KEY, JSON.stringify(this.tableData));
    },

    getList() {
      const list = JSON.parse(localStorage.getItem(Constants.ID.SENSITIVE_KEY) || []);
      console.log(list)
      this.tableData = list;
    },
    async loadUserInfo(){
      await userInfo().then(resp =>{
      const response = JSON.stringify(resp);
      console.log(response);
      if(response){
        this.userInfo = JSON.parse(response);
      }else{
        this.userInfo = {};
      }
      
    },
    error => {

    })
    }

  },
  mounted() {
    this.loadUserInfo();
  },
  activated() {
    this.loadUserInfo();
  },
  computed: {
    fullImageUrl() {
      if (!this.userInfo || !this.userInfo.avatar) {
        return require('@/assets/logo.png'); // 默认头像
      }
      return `${this.baseUrl}/upload/${this.userInfo.avatar}?t=${new Date().getTime()}`;
    }
  }
}
</script>

<style scoped lang="scss">
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;
   table tr td {
    font-size: 24px;
    color: $dark_gray;
    margin: 0px auto 50px auto;
   }

   .weigth {
     font-size: 36px
   }

</style>
