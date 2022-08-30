package org.bpmn.bpmn_elements.event;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class StartEvent {

    String id;
    Double createdEntityId;
    // SequenceFlow outgoing;
    Element elementStartEvent;

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

    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "StartEvent=" + this.id;
    }
}