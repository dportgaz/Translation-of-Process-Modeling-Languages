package org.bpmn.step_one.process.event;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.process.flow.SequenceFlow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class StartEvent {

    String id;
    Double createdEntityId;
    SequenceFlow outgoing;

    Element elementStartEvent;

    public Element getElementStartEvent() {
        return elementStartEvent;
    }

    public StartEvent() {
        this.id = "Event_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public StartEvent(Document doc) {
        this.id = "Event_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementStartEvent = doc.createElement("bpmn:startEvent");
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
