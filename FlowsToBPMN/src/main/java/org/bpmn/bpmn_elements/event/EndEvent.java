package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class EndEvent {

    String id;
    Double createdEntityId;
    // SequenceFlow outgoing;
    Element elementEndEvent;

    SequenceFlow incoming;

    public EndEvent() {
        this.id = "Event_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementEndEvent = doc.createElement("bpmn:endEvent");
        setElementEndEvent();
    }

    public Element getElementEndEvent() {
        return elementEndEvent;
    }

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
    }

    public SequenceFlow getIncoming() {
        return incoming;
    }

    public void setElementEndEvent() {
        this.elementEndEvent.setAttribute("id", this.id);
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "EndEvent=" + this.id;
    }

}

