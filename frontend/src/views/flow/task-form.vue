<template>
  <el-card shadow="never" class="aui-card--fill">
    <el-tabs v-model="activeName" class="demo-tabs">
      <el-tab-pane label="表单信息" name="form">
        <v-form-render v-if="formVisible" :formJson="formJson" :formData="formData" ref="dataFormRef"></v-form-render>
        <el-row :gutter="640" v-if="!state.fieldDisabled">
          <el-col :span="4" :offset="1">
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t("process.createInstance") }}</el-button>
          </el-col>
        </el-row>
        <el-row style="margin: 20px 0 0 80px" v-if="state.runningHandleVisible">
          <el-col>
            <ren-process-running v-if="state.runningHandleVisible" ref="renProcessRunning"></ren-process-running>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane v-if="state.processDetailVisible" label="流转记录" name="detail">
        <ren-process-detail ref="renProcessDetail"></ren-process-detail>
      </el-tab-pane>
      <el-tab-pane v-if="state.processDetailVisible" label="流程图" name="image">
        <img :src="getDiagramImage()" class="image" />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script lang="ts" setup>
import baseService from "@/service/baseService";
import { nextTick, onMounted, reactive, ref, toRefs } from "vue";
import { useRoute } from "vue-router";
import { useI18n } from "vue-i18n";
import { ElMessage } from "element-plus";
import useView from "@/hooks/useView";
import qs from "qs";
import { getToken } from "@/utils/cache";
import app from "@/constants/app";
const { t } = useI18n();

const activeName = ref("form");
const dataFormRef = ref();
const dataForm = reactive({
  id: "",
  //必传
  processDefinitionId: "",
  processInstanceId: ""
});

const view = reactive({
  createdIsNeed: false,
  // 表单属性是否可编辑
  fieldDisabled: false,
  // 是否任务处理
  runningHandleVisible: false,
  // 是否显示流程处理详情
  processDetailVisible: false
});

const state = reactive({ ...useView(view), ...toRefs(view) });

onMounted(() => {
  init();
});

const init = () => {
  const route = useRoute();
  const showType = route.query.showType;
  dataForm.id = route.query.businessKey as string;
  dataForm.processDefinitionId = route.query.processDefinitionId as string;
  dataForm.processInstanceId = route.query.processInstanceId as string;

  //查看流程
  if (showType === "detail") {
    state.processDetailVisible = true;
    state.fieldDisabled = true;
  } else if (showType === "taskHandle") {
    state.processDetailVisible = true;
    state.fieldDisabled = true;
    //处理流程
    state.runningHandleVisible = true;
  } else {
    state.processDetailVisible = false;
    state.runningHandleVisible = false;
    state.fieldDisabled = false;
  }

  getForm();
};

const formVisible = ref(false);
const formJson = ref();
const formData = ref();

// 获取表单信息
const getFormData = async (instanceId: string) => {
  const { data } = await baseService.get(`/flow/common/form/instance/${instanceId}`);

  formData.value = data;
};

const getForm = async () => {
  const { data } = await baseService.get(`/flow/common/form/${dataForm.processDefinitionId}`);

  await getFormData(dataForm.processInstanceId);
  formJson.value = JSON.parse(data);
  formVisible.value = true;

  await nextTick(() => {
    if (state.fieldDisabled) {
      dataFormRef.value.disableForm();
    }
  });
};

const getDiagramImage = () => {
  const params = qs.stringify({
    token: getToken(),
    processInstanceId: dataForm.processInstanceId
  });
  return `${app.api}/flow/common/diagram/image?${params}`;
};

// 启动流程
const dataFormSubmitHandle = () => {
  dataFormRef.value
    .getFormData()
    .then((formData: any) => {
      baseService.post("/flow/common/start/instance/" + dataForm.processDefinitionId, formData).then((res) => {
        ElMessage.success({
          message: t("prompt.success"),
          duration: 500,
          onClose: () => {
            state.closeCurrentTab();
          }
        });
      });
    })
    .catch((error: any) => {
      ElMessage.error(error);
    });
};
</script>
