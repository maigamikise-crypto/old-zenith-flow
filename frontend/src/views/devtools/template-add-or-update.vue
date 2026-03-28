<template>
  <div class="template-code">
    <el-dialog v-model="visible" :title="!dataForm.id ? '新增' : '编辑'" :close-on-click-modal="false" :close-on-press-escape="false" :fullscreen="true">
      <el-form :model="dataForm" :rules="rules" ref="dataFormRef" label-width="80px" size="small">
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="模板名" prop="name">
              <el-input v-model="dataForm.name" placeholder="模板名"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文件名" prop="fileName">
              <el-input v-model="dataForm.fileName" placeholder="文件名"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="生成路径" prop="path">
              <el-input v-model="dataForm.path" placeholder="生成路径"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="dataForm.status">
                <el-radio :label="0">启用</el-radio>
                <el-radio :label="1">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="内容" prop="content">
          <monaco-editor class="editor" v-model="dataForm.content" theme="vs" language="java" style="width: 100%; height: 480px; border: 1px solid #ccc" />
        </el-form-item>
      </el-form>
      <template v-slot:footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="dataFormSubmitHandle()">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from "vue";
import baseService from "@/service/baseService";
import MonacoEditor from "@/components/monaco-editor";
import { useI18n } from "vue-i18n";
import { ElMessage } from "element-plus";
const { t } = useI18n();
const emit = defineEmits(["refreshDataList"]);

const visible = ref(false);
const dataFormRef = ref();

const dataForm = reactive({
  id: "",
  name: "",
  content: "",
  fileName: "",
  path: "",
  status: 0
});

const rules = ref({
  name: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  content: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  path: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  fileName: [{ required: true, message: "必填项不能为空", trigger: "blur" }]
});

const init = (id?: number) => {
  visible.value = true;
  dataForm.id = "";

  // 重置表单数据
  if (dataFormRef.value) {
    dataFormRef.value.resetFields();
  }

  if (id) {
    getInfo(id);
  }
};

// 获取信息
const getInfo = (id: number) => {
  baseService.get(`/devtools/template/${id}`).then((res) => {
    Object.assign(dataForm, res.data);
  });
};

// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }
    (!dataForm.id ? baseService.post : baseService.put)("/devtools/template", dataForm).then((res) => {
      ElMessage.success({
        message: t("prompt.success"),
        duration: 500,
        onClose: () => {
          visible.value = false;
          emit("refreshDataList");
        }
      });
    });
  });
};

defineExpose({
  init
});
</script>
<style lang="less">
.template-code .el-dialog__body {
  padding: 10px 30px 0 20px;
}
.template-code .el-dialog__footer {
  padding: 0 20px 10px;
}
</style>
