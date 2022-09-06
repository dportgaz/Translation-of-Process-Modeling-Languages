package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import static org.bpmn.fillxml.ExecSteps.doc;

public class StartEvent {

    String id;
    Double createdEntityId;
    // SequenceFlow outgoing;
    Element elementStartEvent;

    SequenceFlow outgoing;

    public StartEvent() {
        this.id = "Event_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementStartEvent = doc.createElement("bpmn:startEvent");
        setElementStartEvent();
    }

    public Element getElementStartEvent() {
        return elementStartEvent;
    }

    public void setElementStartEvent() {
        this.elementStartEvent.setAttribute("id", this.id);
    }

    public void setOutgoing(SequenceFlow outgoing) {
        this.outgoing = outgoing;
    }

    public SequenceFlow getOutgoing() {
        return outgoing;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "StartEvent=" + this.id;
    }
}