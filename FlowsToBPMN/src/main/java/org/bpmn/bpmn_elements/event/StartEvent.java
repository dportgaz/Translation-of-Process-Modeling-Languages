package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import static org.bpmn.steps.Execution.doc;

public class StartEvent extends Event{

    String id;
    Double createdEntityId;
    Element elementStartEvent;

    SequenceFlow outgoing;

    Element elementOutgoing;

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
        if (outgoing != null) {
            this.elementOutgoing = doc.createElement("bpmn:outgoing");
            this.elementOutgoing.setTextContent(outgoing.getId());
            this.elementStartEvent.appendChild(this.elementOutgoing);
        }
    }

    public Element getElementOutgoing() {
        return elementOutgoing;
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