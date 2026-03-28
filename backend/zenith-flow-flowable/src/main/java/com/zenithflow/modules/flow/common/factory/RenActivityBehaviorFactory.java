package com.zenithflow.modules.flow.common.factory;

import com.zenithflow.modules.flow.common.behavior.RenUserTaskActivityBehavior;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.parser.factory.DefaultActivityBehaviorFactory;

public class RenActivityBehaviorFactory extends DefaultActivityBehaviorFactory {

    @Override
    public RenUserTaskActivityBehavior createUserTaskActivityBehavior(UserTask userTask) {
        return new RenUserTaskActivityBehavior(userTask);
    }
}
