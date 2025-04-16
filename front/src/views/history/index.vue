<template>
    <div>
        <div>
        <el-form :inline="true" :model="formData" class="demo-form-inline">
  <el-form-item label="使用来源">
    <el-select v-model="formData.selected" placeholder="来源">
      <el-option v-for="(t,i) in sources" :key="i" :label="t.label" :value="t.value"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="onSubmit">查询</el-button>
    <el-button type="primary" @click="reset">重置</el-button>
  </el-form-item>
</el-form></div>

        <el-table
        :data="tableData" max-height="700" fix
        style="width: 100%">

        <el-table-column
          prop="id"
          label="ID"
          align="center"
      >
      </el-table-column>
      <el-table-column
          prop="type"
          label="检测类型"
          align="center"
      ></el-table-column>


      <el-table-column
          prop="username"
          label="用户"
          align="center"
      ></el-table-column>

      <el-table-column
          prop="source"
          label="来源"
          align="center"
      ></el-table-column>


      <el-table-column
          prop="origin"
          label="原始内容"
          align="center" 
          :show-overflow-tooltip='true' 
      ></el-table-column>

      <el-table-column
          prop="content"
          label="解析后内容"
          align="center"
          :show-overflow-tooltip='true' 
      ></el-table-column>
      
    </el-table>

    <div>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10,20,40,100]"
        :page-size="pagesize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="count">
      </el-pagination>
    </div>
    </div>
  </template>
  
  
  <script>
import Constants from "@/utils/constants";
import { lishipager } from '@/utils/inputs'

export default {
  name: "History",
  data() {
    return {
        tableData: [],
        currentPage: 1,
        count: 0,
        pagesize: 10,
        pageNo: 1,
        t:"",
        sources: [
        {
            label: "外部开放接口",
            value:"openApi"
        },
        {
            label: "内部接口",
            value:"innerApi"
        }],
        formData: {
            selected: ""
        }
    }
  },
  methods: {
    onSubmit(){
        this.getList();
    },
    handleSizeChange(val) {
        this.pagesize = val;
        this.getList();
      },
    handleCurrentChange(val) {
      this.pageNo = val;
      this.getList();
    },
    getList() {
        lishipager({ s: this.pagesize, n: this.pageNo ,t: this.formData.selected})
        .then((resp=>{
          console.log(resp)
          this.count = resp.total; //总行数
          this.pageNo = resp.current; //当前页号
          this.pagesize = resp.size; //一页几条
          this.tableData = resp.records; // 渲染到页面上
        }))
    },
    reset(){
        this.formData = {
            selected: ""
        }
    }
  },
  mounted() {
    this.getList();
  },
}
  </script>
  
  <style scoped lang="scss">
    div div {
        margin-top: 10px;
    }
   
  </style>
  