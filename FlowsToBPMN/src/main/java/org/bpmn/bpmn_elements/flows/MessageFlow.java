package org.bpmn.bpmn_elements.flows;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import static org.bpmn.steps.BPMN.doc;

public class MessageFlow extends SequenceFlow{

    String id;

    String name;

    BPMNElement sourceRef;

    BPMNElement targetRef;

    SequenceFlow toGateway;

    SequenceFlow fromGateway;

    Element elementMessageFlow;

    public MessageFlow() {
        this.id = "Flow_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementMessageFlow = doc.createElement("bpmn:messageFlow");
        setElementMessageFlow();
    }

    public MessageFlow(BPMNElement sourceRef, BPMNElement targetRef) {
        this.id = "Flow_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementMessageFlow = doc.createElement("bpmn:messageFlow");
        setElementMessageFlow();
        setSourceRef(sourceRef);
        setTargetRef(targetRef);
    }

    public void setSourceRef(BPMNElement sourceRef) {
        this.sourceRef = sourceRef;
        this.elementMessageFlow.setAttribute("sourceRef", sourceRef.getId());
    }

    public void setTargetRef(BPMNElement targetRef) {
        this.targetRef = targetRef;
        this.elementMessageFlow.setAttribute("targetRef", targetRef.getId());
    }

    private void setElementMessageFlow() {
        this.elementMessageFlow.setAttribute("id", this.id);
    }

    public Element getElement() {
        return elementMessageFlow;
    }
}
