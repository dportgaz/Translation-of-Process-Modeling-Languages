package org.bpmn.flows_objects;

import java.util.ArrayList;

public class RelationTypeData extends AbstractRelation{

    public RelationTypeData(ArrayList<Double> Parameters_, Double CreatedActorId_) {
        Parameters = Parameters_;
        CreatedActorId = CreatedActorId_;
    }

    @Override
    public String toString() {
        return "Object { " + CreatedActorId + " }";
    }

    @Override
    public Double getCreatedActorId() {
        return this.CreatedActorId;
    }

}
