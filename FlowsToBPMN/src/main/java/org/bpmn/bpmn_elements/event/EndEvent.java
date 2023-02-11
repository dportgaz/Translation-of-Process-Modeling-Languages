package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.transition.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class EndEvent extends Event{

    String id;
    Double createdEntityId;
    // SequenceFlow outgoing;
    Element elementEndEvent;

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();

    SequenceFlow incoming;

    Element elementIncoming;

    BPMNElement beforeElement;

    BPMNElement afterElement;

    Lane lane;

    public EndEvent() {
        this.id = "EndEvent_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementEndEvent = doc.createElement("bpmn:endEvent");
        this.elementIncoming = doc.createElement("bpmn:incoming");
        this.elementEndEvent.appendChild(this.elementIncoming);
        setElement();
    }

    public void setMessage(){
        Element endMessage = doc.createElement("bpmn:messageEventDefinition");
        endMessage.setAttribute("id", RandomIdGenerator.generateRandomUniqueId(6));
        this.elementEndEvent.appendChild(endMessage);
    }

    public void setUser(Lane lane) {
        this.lane = lane;
    }

    public Lane getUser() {
        return lane;
    }

    @Override
    public String getName() {
        return this.id;
    }

    @Override
    public Element getElement() {
        return elementEndEvent;
    }

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
        if (incoming != null) {
            this.elementIncoming.setTextContent(incoming.getId());
        }
    }

    public Element getElementIncoming() {
        return elementIncoming;
    }

    @Override
    public BPMNElement getBeforeElement() {
        return beforeElement;
    }

    @Override
    public BPMNElement getAfterElement() {
        return afterElement;
    }

    @Override
    public void setBeforeElement(BPMNElement element) {
        this.beforeElement = element;
    }

    @Override
    public void setAfterElement(BPMNElement element) {
        this.afterElement = element;
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

