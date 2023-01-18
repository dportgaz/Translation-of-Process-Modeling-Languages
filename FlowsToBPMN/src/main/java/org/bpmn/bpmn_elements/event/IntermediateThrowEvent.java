package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.association.DataOutputAssociation;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class IntermediateThrowEvent {

    String id;

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();
    Element elementThrowEvent;

    SequenceFlow outgoing;

    Element elementOutgoing;

    SequenceFlow incoming;

    Element elementIncoming;


    BPMNElement beforeElement;

    BPMNElement afterElement;

    ArrayList<DataInputAssociation> dataInputAssociations = new ArrayList<>();

    DataOutputAssociation dataOutputAssociation;

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

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
        if (incoming != null) {
            this.elementIncoming = doc.createElement("bpmn:incoming");
            this.elementIncoming.setTextContent(incoming.getId());
            this.elementThrowEvent.appendChild(this.elementIncoming);
        }
    }

    public void setOutgoing(SequenceFlow outgoing) {
        this.outgoing = outgoing;
        if (outgoing != null) {
            this.elementOutgoing = doc.createElement("bpmn:outgoing");
            this.elementOutgoing.setTextContent(outgoing.getId());
            this.elementThrowEvent.appendChild(this.elementOutgoing);
        }
    }

    public Element getElement() {
        return elementThrowEvent;
    }

    public void setElement(Element elementThrowEvent) {
        this.elementThrowEvent = elementThrowEvent;
    }


}
