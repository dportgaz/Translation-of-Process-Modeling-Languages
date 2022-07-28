package org.bpmn.step1.process.flow;

import org.bpmn.randomidgenerator.RandomIdGenerator;

public class SequenceFlow {

    String id;

    String sourceRef;

    String targetRef;

    public SequenceFlow(){
        this.id = RandomIdGenerator.generateRandomUniqueId(6);
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
}
