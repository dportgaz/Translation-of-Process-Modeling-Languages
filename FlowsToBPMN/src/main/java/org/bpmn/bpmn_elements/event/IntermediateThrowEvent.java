package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.association.DataOutputAssociation;
import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.transition.SequenceFlow;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class IntermediateThrowEvent implements BPMNElement {

    String id;

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();
    Element elementThrowEvent;

    SequenceFlow outgoing;

    Element elementOutgoing;

    SequenceFlow incoming;

    Element elementIncoming;

    Lane lane;

    BPMNElement beforeElement;

    BPMNElement afterElement;

    ArrayList<DataInputAssociation> dataInputAssociations = new ArrayList<>();

    DataOutputAssociation dataOutputAssociation;


    Task sendingTask;

    public IntermediateThrowEvent(Task task) {
        this.id = "Event_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.lane = task.getUser();
        this.sendingTask = task;
        if(this.lane != null) {
            lane.getElements().add(this);
        }
        this.elementThrowEvent = doc.createElement("bpmn:intermediateThrowEvent");
        setElement();
        this.elementIncoming = doc.createElement("bpmn:incoming");
        this.elementThrowEvent.appendChild(this.elementIncoming);
        this.elementOutgoing = doc.createElement("bpmn:outgoing");
        this.elementThrowEvent.appendChild(this.elementOutgoing);
    }

    public IntermediateThrowEvent() {
        this.id = "Event_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementThrowEvent = doc.createElement("bpmn:intermediateThrowEvent");
        setElement();
    }

    public void setElement() {
        this.elementThrowEvent.setAttribute("id", this.id);
        Element messageEventDefinition = doc.createElement("bpmn:messageEventDefinition");
        messageEventDefinition.setAttribute("id", "MessageEventDefinition_" + RandomIdGenerator.generateRandomUniqueId(6));
        this.elementThrowEvent.appendChild(messageEventDefinition);
    }

    @Override
    public Lane getUser() {
        return this.lane;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Double getCreateId() {
        return null;
    }

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
        if (incoming != null) {
            this.elementIncoming.setTextContent(incoming.getId());
        }
    }

    @Override
    public SequenceFlow getOutgoing() {
        return this.outgoing;
    }

    @Override
    public SequenceFlow getIncoming() {
        return this.incoming;
    }

    public void setOutgoing(SequenceFlow outgoing) {
        this.outgoing = outgoing;
        if (outgoing != null) {
            this.elementOutgoing.setTextContent(outgoing.getId());
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public ArrayList<BPMNElement> getBefore() {
        return null;
    }

    @Override
    public ArrayList<BPMNElement> getAfter() {
        return null;
    }

    @Override
    public BPMNElement getBeforeElement() {
        return null;
    }

    @Override
    public BPMNElement getAfterElement() {
        return null;
    }

    @Override
    public void setBeforeElement(BPMNElement element) {

    }

    @Override
    public void setAfterElement(BPMNElement element) {

    }



    public Element getElement() {
        return elementThrowEvent;
    }

    public void setElement(Element elementThrowEvent) {
        this.elementThrowEvent = elementThrowEvent;
    }

    @Override
    public String toString() {
        return this.id;
    }

}
