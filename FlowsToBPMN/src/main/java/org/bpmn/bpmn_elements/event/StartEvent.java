package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.steps.Execution.doc;

public class StartEvent extends Event{

    String id;
    Double createdEntityId;

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();
    Element elementStartEvent;

    SequenceFlow outgoing;

    Element elementOutgoing;

    public StartEvent() {
        this.id = "StartEvent_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementStartEvent = doc.createElement("bpmn:startEvent");
        setElement();
    }

    @Override
    public Element getElement() {
        return elementStartEvent;
    }

    @Override
    public void setElement() {
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

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public ArrayList<BPMNElement> getAfter() {
        return after;
    }

    @Override
    public ArrayList<BPMNElement> getBefore() {
        return before;
    }

    @Override
    public String toString() {
        return this.id;
    }
}