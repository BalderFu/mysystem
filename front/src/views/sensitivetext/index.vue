<template>
  <div>

    <el-row :gutter="10">
      <el-col :span="1.5">
        <el-button size="small" type="primary" @click="handleAdd">新增</el-button>
      </el-col>

    </el-row>


    <el-table
        :data="tableData"
        style="width: 100%">

        <el-table-column
          prop="id"
          label="ID"
          align="center"
      >
      </el-table-column>
      <el-table-column
          prop="keyword"
          label="敏感词"
          align="center"
      >
      </el-table-column>


      <el-table-column
          prop="description"
          label="描述"
          align="center"
      >
      </el-table-column>


      <el-table-column
          label="操作"
          align="center"
      >
        <template slot-scope="scope"> <!-- 用于在父组件中动态地传递数据到子组件的插槽内 -->
          <el-button size="small" type="text" @click="handleEdit(scope.row)">修改</el-button>
          <el-button size="small" type="text" @click="handleRemove(scope.row)">删除</el-button>
        </template>

      </el-table-column>
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
    <el-dialog :show-close="false" @close="resetValidate"
        :title="title"
        :visible.sync="addShow"
        width="500px">
      <el-form :model="dataForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
        <el-form-item label="敏感词名称" prop="keyword">
          <el-input size="small" v-model.trim="dataForm.keyword" :disabled="gray"/>
        </el-form-item>
        <el-form-item label="敏感词描述" prop="description">
          <el-input size="small" v-model.trim="dataForm.description"/>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" type="danger" @click="closeDialog">取 消</el-button>
        <el-button size="small" type="primary" @click="handleSubmit">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>


<script>
import Constants from "@/utils/constants";
import mammoth from 'mammoth';
import { mingancis,addMinganci,updateMinganci,daleteMinganci } from '@/utils/inputs'

export default {
  name: "SensitiveText",
  data() {
    return {
      tableData: [],
      addShow: false,
      dataForm: {
        keyword: "",
        description: ""
      },
      gray: false,
      title: "",
      rules: {
        keyword: [
          {required: true, message: '请输入敏感词', trigger: 'blur'},
        ],
        description: [
          {required: true, message: '请输入敏感词描述', trigger: 'blur'},
        ],
      },
      currentPage: 1,
      count: 0,
      pagesize: 10,
      pageNo: 1,
    }
  },
  methods: {
    handleSizeChange(val) {
        this.pagesize = val;
        this.getList();
      },
    handleCurrentChange(val) {
      this.pageNo = val;
      this.getList();
    },
    async handleSubmit() {
      const valid = await this.$refs.ruleForm.validate();
      if (!valid) return;

      try {
        if (this.title === "新增") {
          await addMinganci({
            keyword: this.dataForm.keyword,
            description: this.dataForm.description
          });
          this.$message.success("添加成功");
        } else {
          console.log('更新开始执行', this.dataForm.keyword, ' - ', this.dataForm.description);
          await updateMinganci({
            keyword: this.dataForm.keyword,
            description: this.dataForm.description
          });
          this.$message.success("修改成功");
        }

        this.closeDialog();
      } catch (e) {
        console.error(e);
        this.$message.error("操作失败");
      }
    },

    handleEdit(row) {
      this.dataForm = row;
      this.title = "修改";
      this.addShow = true;
      this.gray = true;
    },

    async handleRemove(row) {
      await daleteMinganci({id: row.id});
      this.getList();
    },

    handleAdd() {
      this.title = "新增";
      this.addShow = true;
      
    },

    closeDialog(){
      // 重置对话框
      Object.assign(this.dataForm, this.$options.data().dataForm);
      this.addShow = false;
      this.gray = false;
      
      this.getList();
    },
    resetValidate(){
      this.$refs['ruleForm'].clearValidate();
    },



    getList() {
      mingancis({ s: this.pagesize, n: this.pageNo })
        .then((resp => {
          console.log(resp)
          this.count = resp.total; //总行数
          this.pageNo = resp.current; //当前页号
          this.pagesize = resp.size; //一页几条
          this.tableData = resp.records; // 渲染到页面上
        }))
    },


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
