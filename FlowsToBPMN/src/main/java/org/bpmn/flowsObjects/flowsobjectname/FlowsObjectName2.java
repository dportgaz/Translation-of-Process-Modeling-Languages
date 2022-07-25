package org.bpmn.flowsObjects.flowsobjectname;

import java.util.ArrayList;

public class FlowsObjectName2 extends AbstractFlowsObjectName2 {

    public FlowsObjectName2(String type, String id_, String methodname_, ArrayList<Object> parameters_, String createdEntityId_) {

        // __type = type;
        // Id = id_;
        MethodName = methodname_;
        Parameters = parameters_;
        CreatedEntityId = createdEntityId_;
    }

    @Override
    public String toString() {
        return "Object { " + MethodName + " " + Parameters + " " + CreatedEntityId + " }";
    }

}
