package org.bpmn.flows_entities;

import java.util.ArrayList;

public abstract class AbstractRelationship {

    protected String MethodName;
    protected ArrayList<Object> Parameters;
    protected Double CreatedActorId;
    public String getMethodName() {
        return this.MethodName;
    }

    public ArrayList<Object> getParameters() {
        return this.Parameters;
    }

    public Object getObjectName() {
        return this.Parameters.get(0);
    }

    public Double getCreatedEntityId() {
        return this.CreatedActorId;
    }

}
