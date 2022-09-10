package org.bpmn.bpmn_elements.gateway;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.steps.Execution.doc;

public class ExclusiveGateway implements BPMNElement {

    String id;

    ArrayList<SequenceFlow> incomings = new ArrayList<>();

    ArrayList<SequenceFlow> outgoings = new ArrayList<>();

    ArrayList<BPMNElement> beforeElements = new ArrayList<>();

    ArrayList<BPMNElement> afterElements = new ArrayList<>();

    Element elementExclusiveGateway;

    BPMNElement beforeElement;

    BPMNElement afterElement;

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

    public void addBeforeElement(BPMNElement element){
        beforeElements.add(element);
    }

    public void addAfterElement(BPMNElement element){
        afterElements.add(element);
    }

    public ArrayList<BPMNElement> getAfterElements() {
        return afterElements;
    }

    public ArrayList<BPMNElement> getBeforeElements() {
        return beforeElements;
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