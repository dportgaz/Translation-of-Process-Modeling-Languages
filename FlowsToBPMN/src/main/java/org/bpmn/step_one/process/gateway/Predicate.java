package org.bpmn.step_one.process.gateway;

import org.bpmn.randomidgenerator.RandomIdGenerator;

public class Predicate {

    String Id;

    String condition;
    Double createdEntityId;

    public Predicate() {
        this.Id = "Predicate_" + RandomIdGenerator.generateRandomUniqueId(6);
    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Double getCreatedEntityId() {
        return createdEntityId;
    }

    public void setCreatedEntityId(Double createdEntityId) {
        this.createdEntityId = createdEntityId;
    }

    public String getId() {
        return Id;
    }

    @Override
    public String toString() {
        return "Id= " + this.createdEntityId + "Value= " + this.getCondition();
    }
}
