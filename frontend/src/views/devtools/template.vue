<template>
  <div class="mod-template">
    <el-form :inline="true" :model="state.dataForm" @keyup.enter="state.getDataList()">
      <el-form-item>
        <el-input v-model="state.dataForm.name" placeholder="模板名" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="state.getDataList()">查询</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="addOrUpdateHandle()">新增</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="enabledHandle()">启用</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="warning" @click="disabledHandle()">禁用</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="danger" @click="state.deleteHandle()">删除</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="state.dataListLoading" :data="state.dataList" border @selection-change="state.dataListSelectionChangeHandle" style="width: 100%">
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="name" label="模板名" header-align="center" align="center" width="150"></el-table-column>
      <el-table-column prop="fileName" label="文件名" header-align="center" align="center" width="200"></el-table-column>
      <el-table-column prop="path" label="生成路径" header-align="center"></el-table-column>
      <el-table-column prop="status" label="状态" sortable="custom" header-align="center" align="center" width="120">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 0" size="small" type="success">启用</el-tag>
          <el-tag v-else size="small" type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" header-align="center" align="center" width="120">
        <template v-slot="scope">
          <el-button type="primary" link @click="addOrUpdateHandle(scope.row.id)">编辑</el-button>
          <el-button type="primary" link @click="state.deleteHandle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination :current-page="state.page" :page-sizes="[10, 20, 50, 100]" :page-size="state.limit" :total="state.total" layout="total, sizes, prev, pager, next, jumper" @size-change="state.pageSizeChangeHandle" @current-change="state.pageCurrentChangeHandle"> </el-pagination>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update ref="addOrUpdateRef" @refreshDataList="state.getDataList"></add-or-update>
  </div>
</template>

<script lang="ts" setup>
import useView from "@/hooks/useView";
import { nextTick, reactive, ref, toRefs } from "vue";
import AddOrUpdate from "./template-add-or-update.vue";
import baseService from "@/service/baseService";
import { IObject } from "@/types/interface";
import { useI18n } from "vue-i18n";
import { ElMessage } from "element-plus";
const { t } = useI18n();

const view = reactive({
  getDataListURL: "/devtools/template/page",
  getDataListIsPage: true,
  deleteURL: "/devtools/template",
  deleteIsBatch: true,
  dataForm: {
    name: ""
  }
});

const state = reactive({ ...useView(view), ...toRefs(view) });

const enabledHandle = (id?: string) => {
  if (!id && state.dataListSelections && state.dataListSelections.length <= 0) {
    return ElMessage({
      message: "请选择记录",
      type: "warning",
      duration: 500
    });
  }
  baseService.put("/devtools/template/enabled", id ? [id] : state.dataListSelections && state.dataListSelections.map((item: IObject) => item.id)).then((res) => {
    ElMessage.success({
      message: t("prompt.success"),
      duration: 500,
      onClose: () => {
        state.getDataList();
      }
    });
  });
};

const disabledHandle = (id?: string) => {
  if (!id && state.dataListSelections && state.dataListSelections.length <= 0) {
    return ElMessage({
      message: "请选择记录",
      type: "warning",
      duration: 500
    });
  }
  baseService.put("/devtools/template/disabled", id ? [id] : state.dataListSelections && state.dataListSelections.map((item: IObject) => item.id)).then((res) => {
    ElMessage({
      message: t("prompt.success"),
      type: "success",
      duration: 500,
      onClose: () => {
        state.getDataList();
      }
    });
  });
};

const addOrUpdateRef = ref();
const addOrUpdateHandle = (id?: number) => {
  nextTick(() => {
    addOrUpdateRef.value.init(id);
  });
};
</script>
