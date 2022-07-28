package org.bpmn.step1.process.activity;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.process.flow.SequenceFlow;

public class Task {

    String id;

    String name;

    Double CreatedEntityId;

    SequenceFlow incoming;

    SequenceFlow outcoming;

    public Task() {
        this.id = RandomIdGenerator.generateRandomUniqueId(6);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
    }

    public void setOutcoming(SequenceFlow outcoming) {
        this.outcoming = outcoming;
    }

    public void setCreatedEntityId(Double createdEntityId) {
        CreatedEntityId = createdEntityId;
    }

    public Double getCreatedEntityId() {
        return CreatedEntityId;
    }

    public String getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }

    public SequenceFlow getIncoming() {
        return this.incoming;
    }

    public SequenceFlow getOutcoming() {
        return this.outcoming;
    }
}
