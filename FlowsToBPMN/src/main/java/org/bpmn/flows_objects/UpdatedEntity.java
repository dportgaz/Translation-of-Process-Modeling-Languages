package org.bpmn.flows_objects;

import java.util.ArrayList;

public class UpdatedEntity extends AbstractObjectType {

    public UpdatedEntity(String type, String id_, String methodname_, ArrayList<Object> parameters_, Double updatedEntityId_) {

        // __type = type;
        // Id = id_;
        MethodName = methodname_;
        Parameters = parameters_;
        UpdatedEntityId = updatedEntityId_;
    }

    @Override
    public String toString() {
        return "Object { " + MethodName + " " + Parameters + " " + UpdatedEntityId + " }";
    }
}
