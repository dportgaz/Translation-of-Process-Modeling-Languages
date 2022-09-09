package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.steps.Execution.doc;

public class EndEvent extends Event{

    String id;
    Double createdEntityId;
    // SequenceFlow outgoing;
    Element elementEndEvent;

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();

    SequenceFlow incoming;

    Element elementIncoming;

    public EndEvent() {
        this.id = "EndEvent_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementEndEvent = doc.createElement("bpmn:endEvent");
        setElement();
    }

    @Override
    public Element getElement() {
        return elementEndEvent;
    }

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
        if (incoming != null) {
            this.elementIncoming = doc.createElement("bpmn:incoming");
            this.elementIncoming.setTextContent(incoming.getId());
            this.elementEndEvent.appendChild(this.elementIncoming);
        }
    }

    public Element getElementIncoming() {
        return elementIncoming;
    }

    public SequenceFlow getIncoming() {
        return incoming;
    }

    @Override
    public void setElement() {
        this.elementEndEvent.setAttribute("id", this.id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.id;
    }

}

