package org.bpmn.flows_objects;

import java.util.ArrayList;

public abstract class AbstractObjectType {

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

    public Object getObjectName() {
        return this.Parameters.get(0);
    }

    public Double getCreatedEntityId() {
        return this.CreatedEntityId;
    }

    public Double getUpdatedEntityId() {
        return this.UpdatedEntityId;
    }

}
