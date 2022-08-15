package org.bpmn.step_one.process.event;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.process.flow.SequenceFlow;

public class StartEvent {

    String id;
    Double createdEntityId;
    SequenceFlow outgoing;

    public StartEvent() {
        this.id = "Event_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public String getId() {
        return this.id;
    }

    public void setCreatedEntityId(Double createdEntityId) {
        this.createdEntityId = createdEntityId;
    }

    public Double getCreatedEntityId() {
        return createdEntityId;
    }

    @Override
    public String toString() {
        return "StartEvent=" + this.id;
    }
}
