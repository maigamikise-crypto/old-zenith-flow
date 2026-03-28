package com.zenithflow.modules.flow.common.behavior;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import com.zenithflow.common.utils.SpringContextUtils;
import com.zenithflow.modules.flow.dao.FlowableUserDao;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.List;
import java.util.stream.Collectors;

public class RenUserTaskActivityBehavior extends UserTaskActivityBehavior {
    public RenUserTaskActivityBehavior(UserTask userTask) {
        super(userTask);
    }

    @Override
    protected void handleAssignments(TaskService taskService, String assignee, String owner, List<String> candidateUsers,
                                     List<String> candidateGroups, TaskEntity task, ExpressionManager expressionManager,
                                     DelegateExecution execution, ProcessEngineConfigurationImpl processEngineConfiguration) {

        if (CollectionUtil.isNotEmpty(candidateGroups)) {
            List<Long> roleIdList = candidateGroups.stream().map(Long::parseLong).collect(Collectors.toList());

            // 查询用户ID列表
            FlowableUserDao flowableUserDao = (FlowableUserDao) SpringContextUtils.getBean("flowableUserDao");
            List<Long> userIdList = flowableUserDao.getUserIdListByRoleIdList(roleIdList);
            candidateUsers = userIdList.stream().map(String::valueOf).collect(Collectors.toList());

            super.handleAssignments(taskService, assignee, owner, candidateUsers, ListUtil.empty(), task, expressionManager, execution, processEngineConfiguration);
        }

        super.handleAssignments(taskService, assignee, owner, candidateUsers, candidateGroups, task, expressionManager, execution, processEngineConfiguration);
    }
}
