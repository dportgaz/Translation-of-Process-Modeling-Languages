package org.bpmn.flows_objects;

import java.util.ArrayList;

public abstract class AbstractRelation {

    protected ArrayList<Double> Parameters;
    protected Double CreatedActorId;

    public Double getCreatedActorId() {
        return this.CreatedActorId;
    }

}
