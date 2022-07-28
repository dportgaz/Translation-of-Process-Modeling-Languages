package org.bpmn.step1.process.event;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.process.flow.SequenceFlow;

public class StartEvent {

    String id;
    Double createdEntityId;
    SequenceFlow outgoing;
    public StartEvent() {
        this.id = RandomIdGenerator.generateRandomUniqueId(6);
    }
    public String getId(){
        return this.id;
    }

    public void setCreatedEntityId(Double createdEntityId) {
        this.createdEntityId = createdEntityId;
    }

    public Double getCreatedEntityId() {
        return createdEntityId;
    }
}
