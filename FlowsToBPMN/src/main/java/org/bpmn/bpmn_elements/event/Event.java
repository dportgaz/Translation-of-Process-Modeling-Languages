package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;

import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.w3c.dom.Element;
import java.util.ArrayList;

public class Event implements BPMNElement {

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();

    String id;

    BPMNElement beforeElement;

    BPMNElement afterElement;

    SequenceFlow incoming;

    SequenceFlow outgoing;


    public String getId() {
        return id;
    }

    public ArrayList<BPMNElement> getAfter() {
        return after;
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

    @Override
    public Element getElement() {
        return null;
    }

    @Override
    public void setElement() {
    }

    @Override
    public Lane getUser() {
        return null;
    }

    @Override
    public String getName() {
        return this.id;
    }

    @Override
    public Double getCreateId() {
        return null;
    }

    @Override
    public void setOutgoing(SequenceFlow sf) {
        this.outgoing = sf;
    }

    @Override
    public void setIncoming(SequenceFlow sf) {
        this.incoming = sf;
    }

    @Override
    public SequenceFlow getOutgoing() {
        return this.outgoing;
    }

    @Override
    public SequenceFlow getIncoming() {
        return this.incoming;
    }

    public ArrayList<BPMNElement> getBefore() {
        return before;
    }

}
