<template>
  <div>
    <el-row :gutter="10">
      <el-col :span="1.5">
        <el-button size="small" type="primary" @click="handleAdd">生成接口文档</el-button>
      </el-col>

    </el-row>

    <div><p v-html="htmlData"></p></div>
  </div>
</template>


<script>
import {preview} from "@/utils/inputs"

export default {
  name: "OpenAPi",
  data() {
    return {
      htmlData:""
    }
  },
  methods: {
    handleAdd() {
      preview().then(resp =>{
        console.log(resp)
        this.htmlData = resp;
      }).catch(e=>{})
    },
  },
  mounted() {

  },
}
</script>

<style scoped lang="scss">

</style>
