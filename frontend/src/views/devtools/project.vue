<template>
  <div>
    <el-space style="justify-content: space-between; width: 100%; border-bottom: 1px solid #ebeef5; margin-top: -10px; margin-bottom: 15px; padding-bottom: 10px">
      <span>项目名和包名修改</span>
    </el-space>
  </div>
  <div style="width: 600px">
    <el-form :model="dataForm" :rules="rules" ref="dataFormRef" label-width="120px">
      <el-form-item label="原项目路径" prop="projectPath">
        <el-input v-model="dataForm.projectPath" placeholder="待修改的项目所在路径"></el-input>
      </el-form-item>
      <el-form-item label="原项目名称" prop="projectName">
        <el-input v-model="dataForm.projectName" placeholder="待修改的项目名称"></el-input>
      </el-form-item>
      <el-form-item label="新项目名称" prop="newProjectName">
        <el-input v-model="dataForm.newProjectName" placeholder="新项目名称"></el-input>
      </el-form-item>
      <el-form-item label="新项目包名" prop="newProjectPackage">
        <el-input v-model="dataForm.newProjectPackage" placeholder="新项目包名"></el-input>
      </el-form-item>
      <el-form-item label="新项目标识" prop="newProjectCode">
        <el-input v-model="dataForm.newProjectCode" placeholder="用于替换renren标识"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="dataFormSubmitHandle()">生成新项目</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from "vue";
import { getToken } from "@/utils/cache";
import app from "@/constants/app";
import qs from "qs";

const dataFormRef = ref();
const dataForm = reactive({
  projectPath: "D:\\renrenio\\Zenith Flow",
  projectName: "Zenith Flow",
  newProjectName: "zhongma-erp",
  newProjectPackage: "com.zhongma",
  newProjectCode: "zhongma"
});

const rules = ref({
  projectPath: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  projectName: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  newProjectName: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  newProjectPackage: [{ required: true, message: "必填项不能为空", trigger: "blur" }],
  newProjectCode: [{ required: true, message: "必填项不能为空", trigger: "blur" }]
});

// 表单提交
const dataFormSubmitHandle = () => {
  dataFormRef.value.validate((valid: boolean) => {
    if (!valid) {
      return false;
    }

    location.href = `${app.api}/devtools/project?${qs.stringify({
      ...dataForm,
      token: getToken()
    })}`;
  });
};
</script>
