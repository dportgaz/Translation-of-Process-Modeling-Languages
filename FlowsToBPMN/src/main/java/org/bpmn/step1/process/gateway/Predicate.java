package org.bpmn.step1.process.gateway;

import org.bpmn.randomidgenerator.RandomIdGenerator;

public class Predicate {

    String Id;

    Double createdEntityId;

    public Predicate() {
        this.Id = "Predicate_" + RandomIdGenerator.generateRandomUniqueId(6);
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
        return "Id= " + this.createdEntityId;
    }
}
