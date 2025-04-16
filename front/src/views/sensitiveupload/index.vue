<template>
  <div>
    <el-row :gutter="10">
      <el-col :span="1.5">
        <el-button size="small" type="primary" @click="handleShowUpload">文件上传</el-button>
      </el-col>
    </el-row>

    <el-dialog
        title="敏感词检测"
        :visible.sync="uploadShow"
        width="400px">
      <el-upload
          ref="upload"
          class="upload-demo"
          drag
          action="#"
          :multiple="false"
          :auto-upload="false"
          :limit="1"
          :on-change="handleFileChange"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只能上传 DOCX 文件，且不超过500kb</div>
      </el-upload>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" type="danger" @click="addShow = false">取 消</el-button>
        <el-button size="small" type="primary" @click="handleSubmitFile">确 定</el-button>
      </span>
    </el-dialog>

    <el-dialog
        title=" "
        :visible.sync="processShow"
        width="400px">
      <el-progress :text-inside="true" :stroke-width="26" :percentage="progress"/>
    </el-dialog>

    <el-dialog
        title="检测结果"
        :visible.sync="checkShow"
        width="400px">
        <p>敏感词数量: {{ nums }}</p>
      <p><span v-html="fileContent"/></p>
    </el-dialog>
  </div>
</template>


<script>
import mammoth from "mammoth";
import Constants from "@/utils/constants";
import {checkMinganci} from "@/utils/inputs";

export default {
  name: "",
  data() {
    return {
      processShow: false,
      checkShow: false,
      progress: 0,
      uploadShow: false,
      file: null,
      fileContent: "",
      tableData: [],
      words:[],
      needCheckText:'',
      nums: null
    }
  },
  methods: {
    handleShowUpload() {
      this.uploadShow = true;
      this.$nextTick(() => {
        this.$refs.upload.clearFiles()
      });
    },

    handleSubmitFile() {
      const reader = new FileReader();
      reader.onload = (e) => {
        const arrayBuffer = e.target.result;

        // 使用 mammoth 将 .docx 文件转为文本
        mammoth.extractRawText({arrayBuffer: arrayBuffer})
            .then((result) => {
              this.fileContent = result.value;
              this.uploadShow = false;
              this.processShow = true;
              this.startProgress()
            })
            .catch((err) => {
              console.error('解析错误', err);
            });
      };
      reader.onerror = (e) => {
        console.error('文件读取错误', e);
      };

      // 以 ArrayBuffer 方式读取文件，这样 mammoth 可以处理
      reader.readAsArrayBuffer(this.file.raw);
    },

    handleFileChange(file) {
      this.file = file;
    },

    delay(ms) {
      return new Promise(resolve => setTimeout(resolve, ms));
    },

    async startProgress() {
      for (let i = 1; i <= 10; i++) {
        // 等待 1 秒钟
        await this.delay(300);

        // 更新进度
        this.progress = i * 10;

        // 当进度完成时，隐藏进度条
        if (i === 10) {
          this.processShow = false;

           await checkMinganci({content: this.fileContent, type: 'file'}).then(resp => {
              console.log(resp);
              this.words = resp.words;
              this.nums = resp.nums;
              this.needCheckText = resp.content;
            }).catch(e=>{})

            console.log("----------------------")
            console.log(this.needCheckText);
            console.log(typeof(this.needCheckText));

            console.log(this.words);
            console.log(typeof(this.words));

            this.fileContent = this.highlightKeywords(this.needCheckText, this.words);
            console.log(this.fileContent)
            console.log("----------------------")
          this.checkShow = true;
        }
      }
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

    getList() {
      const list = JSON.parse(localStorage.getItem(Constants.ID.SENSITIVE_KEY) || []);
      console.log(list)
      this.tableData = list;
    },

  },
  mounted() {
    this.getList();

  },
}
</script>

<style scoped lang="scss">

</style>
