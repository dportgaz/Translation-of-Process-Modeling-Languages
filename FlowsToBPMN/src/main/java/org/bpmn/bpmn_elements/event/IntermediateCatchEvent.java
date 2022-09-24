package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.association.DataOutputAssociation;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashSet;

import static org.bpmn.steps.BPMN.doc;

public class IntermediateCatchEvent implements BPMNElement{

    String id;

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();

    Element elementCatchEvent;

    SequenceFlow outgoing;

    Element elementOutgoing;

    SequenceFlow incoming;

    Element elementIncoming;

    BPMNElement beforeElement;

    BPMNElement afterElement;

    ArrayList<DataInputAssociation> dataInputAssociations = new ArrayList<>();

    DataOutputAssociation dataOutputAssociation;

    boolean parallelMultiple;

    HashSet<DataObject> dataObjects = new HashSet<>();

    String name;

    public IntermediateCatchEvent(String name) {
        this.id = "Activity_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.name = name;
        this.elementCatchEvent = doc.createElement("bpmn:receiveTask");
        setElement();
    }

    public IntermediateCatchEvent(boolean parallelMultiple) {
        this.id = "Activity_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.name = "Receive ";
        this.elementCatchEvent = doc.createElement("bpmn:receiveTask");
        this.parallelMultiple = parallelMultiple;
        setElementMultiple();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.elementCatchEvent.setAttribute("name", this.name);

    }

    public HashSet<DataObject> getDataObjects() {
        return dataObjects;
    }

    private void setElementMultiple() {
        this.elementCatchEvent.setAttribute("id", this.id);
        this.elementCatchEvent.setAttribute("name", this.name);
        //this.elementCatchEvent.setAttribute("name","");
        //this.elementCatchEvent.setAttribute("parallelMultiple", String.valueOf(this.parallelMultiple));
        //Element cancelEventDefinition = doc.createElement("bpmn:cancelEventDefinition ");
        //cancelEventDefinition.setAttribute("id", "CancelEventDefinition_" + RandomIdGenerator.generateRandomUniqueId(6));
        //Element terminateEventDefinition = doc.createElement("bpmn:terminateEventDefinition ");
        //terminateEventDefinition.setAttribute("id", "TerminateEventDefinition_" + RandomIdGenerator.generateRandomUniqueId(6));
        //this.elementCatchEvent.appendChild(cancelEventDefinition);
        //this.elementCatchEvent.appendChild(terminateEventDefinition);
    }

    public void setElement() {
        this.elementCatchEvent.setAttribute("id", this.id);
        this.elementCatchEvent.setAttribute("name", this.name);

        //Element messageEventDefinition = doc.createElement("bpmn:messageEventDefinition");
        //messageEventDefinition.setAttribute("id", "MessageEventDefinition_" + RandomIdGenerator.generateRandomUniqueId(6));
        //this.elementCatchEvent.appendChild(messageEventDefinition);
    }

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
        if (incoming != null) {
            this.elementIncoming = doc.createElement("bpmn:incoming");
            this.elementIncoming.setTextContent(incoming.getId());
            this.elementCatchEvent.appendChild(this.elementIncoming);
        }
    }

    public void setOutgoing(SequenceFlow outgoing) {
        this.outgoing = outgoing;
        if (outgoing != null) {
            this.elementOutgoing = doc.createElement("bpmn:outgoing");
            this.elementOutgoing.setTextContent(outgoing.getId());
            this.elementCatchEvent.appendChild(this.elementOutgoing);
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
        return elementCatchEvent;
    }

    public void setElement(Element elementCatchEvent) {
        this.elementCatchEvent = elementCatchEvent;
    }
}
