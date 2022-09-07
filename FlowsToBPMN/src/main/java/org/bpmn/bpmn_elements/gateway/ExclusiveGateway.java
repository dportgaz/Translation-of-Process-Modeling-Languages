package org.bpmn.bpmn_elements.gateway;

import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.fillxml.ExecSteps.doc;

public class ExclusiveGateway {

    String id;

    ArrayList<SequenceFlow> incomings = new ArrayList<>();

    ArrayList<SequenceFlow> outgoings = new ArrayList<>();

    Element elementExclusiveGateway;

    public ExclusiveGateway() {
        this.id = "Gateway_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementExclusiveGateway = doc.createElement("bpmn:exclusiveGateway");
        setElementExclusiveGateway();
    }

    private void setElementExclusiveGateway() {
        this.elementExclusiveGateway.setAttribute("id", this.id);
    }

    public Element getElementExclusiveGateway() {
        return this.elementExclusiveGateway;
    }

    public String getId() {
        return id;
    }

    public void addIncoming(SequenceFlow incoming) {
        incomings.add(incoming);
    }

    public void addOutgoing(SequenceFlow outgoing) {
        outgoings.add(outgoing);
    }

    @Override
    public String toString() {
        return this.id;
    }
}