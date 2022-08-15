package org.bpmn.flowsObjects;

import java.util.ArrayList;

public class CreatedEntity extends AbstractObjectType {

    public CreatedEntity(String type, String id_, String methodname_, ArrayList<Object> parameters_, Double createdEntityId_) {

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
