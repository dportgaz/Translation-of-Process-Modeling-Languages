package org.bpmn.step1.process.flow;

import org.bpmn.randomidgenerator.RandomIdGenerator;

public class SequenceFlow {

    String id;

    String name;

    String sourceRef;

    String targetRef;

    SequenceFlow toGateway;

    SequenceFlow fromGateway;

    public SequenceFlow() {
        this.id = "Flow_" + RandomIdGenerator.generateRandomUniqueId(6);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFromGateway(SequenceFlow fromGateway) {
        this.fromGateway = fromGateway;
    }

    public void setToGateway(SequenceFlow toGateway) {
        this.toGateway = toGateway;
    }

    public SequenceFlow getFromGateway() {
        return fromGateway;
    }

    public SequenceFlow getToGateway() {
        return toGateway;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }

    public String getId() {
        return id;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    @Override
    public String toString() {
        return this.id + ", sourceRef: " + this.sourceRef + ", targetRef: " + this.targetRef;
    }
}
