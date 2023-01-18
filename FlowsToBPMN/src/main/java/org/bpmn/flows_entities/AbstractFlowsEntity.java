package org.bpmn.flows_entities;

import java.util.ArrayList;

public abstract class AbstractFlowsEntity {

    // protected String __type;
    // protected String Id;
    protected String MethodName;
    protected ArrayList<Object> Parameters;
    protected Double CreatedEntityId;
    protected Double UpdatedEntityId;

    public String getMethodName() {
        return this.MethodName;
    }

    public ArrayList<Object> getParameters() {
        return this.Parameters;
    }

    public Double getCreatedEntityId() {
        return this.CreatedEntityId;
    }

}
