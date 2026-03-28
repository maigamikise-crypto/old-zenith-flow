<template>
  <div>
    <el-form-item label="用户类型">
      <el-select v-model="userTaskForm.type">
        <el-option label="指定人" :value="0" />
        <el-option label="候选用户" :value="1" />
        <el-option label="候选角色" :value="2" />
      </el-select>
    </el-form-item>
    <el-form-item label="处理用户" v-if="userTaskForm.type === 0">
      <el-input v-model="userTaskForm.assigneeName">
        <template #append>
          <el-button :icon="Search()" @click="userSelect" type="danger" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="候选用户" v-if="userTaskForm.type === 1">
      <el-input v-model="userTaskForm.candidateUsersName">
        <template #append>
          <el-button :icon="Search()" @click="userGroupSelect" type="danger" />
        </template>
      </el-input>
    </el-form-item>
    <el-form-item label="候选角色" v-if="userTaskForm.type === 2">
      <el-input v-model="userTaskForm.candidateGroupsName">
        <template #append>
          <el-button :icon="Search()" @click="roleGroupSelect" type="danger" />
        </template>
      </el-input>
    </el-form-item>
    <!--    <el-form-item label="候选分组" v-if="userTaskForm.type === 2">-->
    <!--      <el-select v-model="userTaskForm.candidateGroups" multiple collapse-tags @change="updateElementTask('candidateGroups')">-->
    <!--        <el-option v-for="gk in mockData" :key="'ass-' + gk" :label="`分组${gk}`" :value="`group${gk}`" />-->
    <!--      </el-select>-->
    <!--    </el-form-item>-->

    <select-user v-if="visibleSelect" ref="selectUserRef"></select-user>
    <select-role v-if="visibleRole" ref="selectRoleRef"></select-role>
  </div>
</template>

<script>
import { Search } from "@element-plus/icons-vue";
import SelectUser from "@/components/ren-process-running/src/select-user.vue";
import SelectRole from "@/components/ren-process-running/src/select-role.vue";
import baseService from "@/service/baseService";
import { nextTick } from "vue";
export default {
  name: "UserTask",
  props: {
    id: String,
    type: String
  },
  components: {
    SelectUser,
    SelectRole
  },
  data() {
    return {
      visibleSelect: false,
      visibleRole: false,
      userQuery: {},
      defaultTaskForm: {
        assignee: "",
        candidateUsers: [],
        candidateGroups: [],
        dueDate: "",
        followUpDate: "",
        priority: ""
      },
      userTaskForm: {
        type: 0
      }
    };
  },
  watch: {
    id: {
      immediate: true,
      handler() {
        this.bpmnElement = window.bpmnInstances.bpmnElement;
        this.$nextTick(() => this.resetTaskForm());
      }
    }
  },
  methods: {
    Search() {
      return Search;
    },
    userSelect() {
      this.visibleSelect = true;
      nextTick(() => {
        this.$refs.selectUserRef.init(this.setUserInfo, false);
      });
    },
    setUserInfo(userInfo) {
      this.userTaskForm.assigneeName = userInfo.realName;
      this.userTaskForm.assignee = userInfo.id;
      this.updateAssignee("assignee");
    },
    userGroupSelect() {
      this.visibleSelect = true;
      nextTick(() => {
        this.$refs.selectUserRef.init(this.setUserGroupInfo, true);
      });
    },
    setUserGroupInfo(users) {
      const candidateUsersName = [];
      this.userTaskForm.candidateUsers = [];
      for (const user of users) {
        candidateUsersName.push(user.realName);
        this.userTaskForm.candidateUsers.push(user.id);
      }
      this.userTaskForm.candidateUsersName = candidateUsersName.join(",");
      this.updateCandidateUsers("candidateUsers");
    },
    roleGroupSelect() {
      this.visibleRole = true;
      nextTick(() => {
        this.$refs.selectRoleRef.init(this.setRoleGroupInfo);
      });
    },
    setRoleGroupInfo(roles) {
      const candidateGroupsName = [];
      this.userTaskForm.candidateGroups = [];
      for (const role of roles) {
        candidateGroupsName.push(role.name);
        this.userTaskForm.candidateGroups.push(role.id);
      }
      this.userTaskForm.candidateGroupsName = candidateGroupsName.join(",");
      this.updateCandidateRole("candidateGroups");
    },
    cancelHandle() {
      console.log(22);
    },
    submitHandle() {
      console.log(22);
    },
    async getUserName(ids) {
      const { data } = await baseService.post("/flow/sys/user/list", ids);
      return data.join(",");
    },
    async getRoleName(ids) {
      const { data } = await baseService.post("/flow/sys/role/list", ids);
      return data.join(",");
    },
    async resetTaskForm() {
      const assignee = this.bpmnElement?.businessObject["assignee"];
      const candidateUsers = this.bpmnElement?.businessObject["candidateUsers"];
      const candidateGroups = this.bpmnElement?.businessObject["candidateGroups"];

      this.userTaskForm["assignee"] = "";
      this.userTaskForm["candidateUsers"] = [];
      this.userTaskForm["candidateGroups"] = [];

      if (assignee) {
        this.userTaskForm["assignee"] = assignee;
        this.userTaskForm.assigneeName = await this.getUserName([assignee]);

        return;
      }

      if (candidateUsers) {
        this.userTaskForm["candidateUsers"] = candidateUsers.split(",");
        this.userTaskForm.candidateUsersName = await this.getUserName(candidateUsers.split(","));

        this.userTaskForm.type = 1;

        return;
      }

      if (candidateGroups) {
        this.userTaskForm["candidateGroups"] = candidateGroups.split(",");
        this.userTaskForm.candidateGroupsName = await this.getRoleName(candidateGroups.split(","));

        this.userTaskForm.type = 2;
      }

      // for (let key in this.defaultTaskForm) {
      //   let value;
      //   if (key === "candidateUsers" || key === "candidateGroups") {
      //     value = this.bpmnElement?.businessObject[key] ? this.bpmnElement.businessObject[key].split(",") : [];
      //   } else {
      //     value = this.bpmnElement?.businessObject[key] || this.defaultTaskForm[key];
      //   }
      //   this.userTaskForm[key] = value;
      // }
    },
    updateAssignee(key) {
      const taskAttr = Object.create(null);
      taskAttr["candidateUsers"] = null;
      taskAttr["candidateGroups"] = null;
      taskAttr[key] = this.userTaskForm[key] || null;

      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, taskAttr);
    },
    updateCandidateUsers(key) {
      const taskAttr = Object.create(null);
      taskAttr["assignee"] = null;
      taskAttr["candidateGroups"] = null;
      taskAttr[key] = this.userTaskForm[key] && this.userTaskForm[key].length ? this.userTaskForm[key].join(",") : null;

      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, taskAttr);
    },
    updateCandidateRole(key) {
      const taskAttr = Object.create(null);
      taskAttr["assignee"] = null;
      taskAttr["candidateUsers"] = null;
      taskAttr[key] = this.userTaskForm[key] && this.userTaskForm[key].length ? this.userTaskForm[key].join(",") : null;

      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, taskAttr);
    },
    updateElementTask(key) {
      const taskAttr = Object.create(null);
      if (key === "candidateUsers" || key === "candidateGroups") {
        taskAttr[key] = this.userTaskForm[key] && this.userTaskForm[key].length ? this.userTaskForm[key].join(",") : null;
      } else {
        taskAttr[key] = this.userTaskForm[key] || null;
      }
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, taskAttr);
    }
  },
  beforeUnmount() {
    this.bpmnElement = null;
  }
};
</script>
