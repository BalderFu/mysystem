<template>
  <div>
    <el-col :span="1.5">
      <el-button size="small" type="primary" @click="handleShowUpload">敏感文本检测</el-button>
    </el-col>

    <el-dialog 
        title="敏感文本检测"
        :visible.sync="textCheckShow"
        width="500px" :show-close="false"

    >
      <el-form label-width="100px" v-loading="checkLoading">
        <el-form-item label="请输入文本">
          <el-input v-model.trim="needCheckText"
                    type="textarea"
                    :autosize="{ minRows: 2}"
          />
        </el-form-item>
        <el-divider/>
        <el-form-item label="敏感词数量">
          {{ nums }}
        </el-form-item>
        <el-form-item label="比较结果">
          <el-input v-html="checkResult"
                    readonly
                    type="textarea"
                    :autosize="{ minRows: 2}"
          />
        </el-form-item>
        
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" type="danger" @click="closeDialog">取 消</el-button>
        <el-button size="small" type="primary" @click="handleSubmitCheck">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>


<script>
import Constants from "@/utils/constants";
import {checkMinganci} from "@/utils/inputs";

export default {
  name: "",
  data() {
    return {
      textCheckShow: false,
      needCheckText: "",
      checkResult: "",
      tableData: [],
      checkLoading: false,
      words:['1','2','10'],
      nums: null
    }
  },
  methods: {

    handleShowUpload() {
      this.textCheckShow = true;
    },

    getList() {
      const list = JSON.parse(localStorage.getItem(Constants.ID.SENSITIVE_TEXT_KEY) || JSON.stringify([]));
      this.tableData = list;
    },

    async handleSubmitCheck() {
      this.checkLoading = true;
      this.checkResult = this.needCheckText;
      await checkMinganci({content: this.needCheckText, type: 'text'}).then(resp => {
        console.log(resp);
        this.words = resp.words;
        this.nums = resp.nums;
      }).catch(e=>{})

      console.log("----------------------")
      console.log(this.needCheckText);
      console.log(typeof(this.needCheckText));

      console.log(this.words);
      console.log(typeof(this.words));

      this.checkResult = this.highlightKeywords(this.needCheckText, this.words);
      console.log(this.checkResult)
      console.log("----------------------")
      this.checkLoading = false;

    },
    highlightKeywords(content, words) {
        if (!content || !Array.isArray(words) || words.length === 0) {
            return content; // 如果没有关键字，则返回原文本
        }

        // 创建正则表达式，匹配所有关键字（不区分大小写）
        const regex = new RegExp(`(${words.map(word => this.escapeRegExp(word)).join('|')})`, 'gi');

        // 使用 replace 进行高亮处理
        return content.replace(regex, '<span style="color:red;">$1</span>');
    },
    escapeRegExp(text) {
        return text.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
    },

    closeDialog(){
      this.checkResult = null;
      this.needCheckText = null;
      this.textCheckShow = false;
    }
  },
  mounted() {
    this.getList();
  },
}
</script>

<style scoped lang="scss">

</style>
