package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.association.DataOutputAssociation;
import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.transition.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class StartEvent extends Event{

    String id;

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();
    Element elementStartEvent;

    SequenceFlow outgoing;

    Element elementOutgoing;

    SequenceFlow incoming;

    Element elementIncoming;

    BPMNElement beforeElement;

    BPMNElement afterElement;

    ArrayList<DataInputAssociation> dataInputAssociations = new ArrayList<>();

    DataOutputAssociation dataOutputAssociation;

    Lane lane;

    Element startMessage;

    public StartEvent() {
        this.id = "StartEvent_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementStartEvent = doc.createElement("bpmn:startEvent");
        this.elementOutgoing = doc.createElement("bpmn:outgoing");
        this.elementStartEvent.appendChild(this.elementOutgoing);
        setElement();
    }

    public void setMessage(){
        startMessage = doc.createElement("bpmn:messageEventDefinition");
        startMessage.setAttribute("id", RandomIdGenerator.generateRandomUniqueId(6));
        this.elementStartEvent.appendChild(startMessage);
    }

    public void setParallelMessage(){
        this.elementStartEvent.removeChild(startMessage);
        this.elementStartEvent.setAttribute("parallelMultiple", "true");
        Element cancelEventDefinition = doc.createElement("bpmn:cancelEventDefinition");
        cancelEventDefinition.setAttribute("id", "CancelEventDefinition_" + RandomIdGenerator.generateRandomUniqueId(6));
        Element terminateEventDefinition = doc.createElement("bpmn:terminateEventDefinition");
        terminateEventDefinition.setAttribute("id", "TerminateEventDefinition_" + RandomIdGenerator.generateRandomUniqueId(6));
        this.elementStartEvent.appendChild(cancelEventDefinition);
        this.elementStartEvent.appendChild(terminateEventDefinition);
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
        return elementStartEvent;
    }

    public ArrayList<DataInputAssociation> getDataInputAssociations() {
        return dataInputAssociations;
    }

    public DataOutputAssociation getDataOutputAssociation() {
        return dataOutputAssociation;
    }

    public void setDataOutputAssociation() {
        this.dataOutputAssociation = new DataOutputAssociation();
        this.elementStartEvent.appendChild(this.dataOutputAssociation.getElementDataOutputAssociation());
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
    public void setElement() {
        this.elementStartEvent.setAttribute("id", this.id);
    }

    public void setOutgoing(SequenceFlow outgoing) {
        this.outgoing = outgoing;
        if (outgoing != null) {
            this.elementOutgoing.setTextContent(outgoing.getId());
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