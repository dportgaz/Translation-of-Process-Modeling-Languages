package org.bpmn.bpmn_elements.transition;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class SequenceFlow {

    String id;

    String name;

    BPMNElement sourceRef;

    BPMNElement targetRef;

    SequenceFlow toGateway;

    SequenceFlow fromGateway;

    Element elementSequenceFlow;

    Double xStart;

    Double xEnd;

    Double yStart;

    Double yEnd;


    public SequenceFlow() {
        this.id = "Flow_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementSequenceFlow = doc.createElement("bpmn:sequenceFlow");
        setElementSequenceFlow();
    }

    public SequenceFlow(BPMNElement sourceRef, BPMNElement targetRef) {
        this.id = "Flow_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementSequenceFlow = doc.createElement("bpmn:sequenceFlow");
        setElementSequenceFlow();
        setSourceRef(sourceRef);
        setTargetRef(targetRef);
    }
    public void setXEnd(Double xEnd) {
        this.xEnd = xEnd;
    }

    public void setXStart(Double xStart) {
        this.xStart = xStart;
    }

    public void setYStart(Double yStart) {
        this.yStart = yStart;
    }

    public void setYEnd(Double yEnd) {
        this.yEnd = yEnd;
    }

    private void setElementSequenceFlow() {
        this.elementSequenceFlow.setAttribute("id", this.id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(final java.lang.Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SequenceFlow other = (SequenceFlow) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void setSourceRef(BPMNElement sourceRef) {
        this.sourceRef = sourceRef;
        this.elementSequenceFlow.setAttribute("sourceRef", sourceRef.getId());
    }

    public void setTargetRef(BPMNElement targetRef) {
        this.targetRef = targetRef;
        this.elementSequenceFlow.setAttribute("targetRef", targetRef.getId());
    }

    public Element getElementSequenceFlow() {
        return this.elementSequenceFlow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public BPMNElement getSourceRef() {
        return sourceRef;
    }

    public BPMNElement getTargetRef() {
        return targetRef;
    }

    @Override
    public String toString() {
        return this.id + ", sourceRef: " + this.sourceRef + ", targetRef: " + this.targetRef;
    }

    public static SequenceFlow getFlowBySource(String source, ArrayList<SequenceFlow> sfs) {

        for (SequenceFlow sf : sfs) {

            if (sf.getTargetRef().equals(source)) {
                return sf;
            }

        }

        return null;

    }

    public static SequenceFlow getFlowBySource(Task source, ArrayList<SequenceFlow> sfs) {

        for (SequenceFlow sf : sfs) {

            if (sf.getTargetRef().equals(source.getId())) {
                return sf;
            }

        }

        return null;

    }

    public static SequenceFlow getFlowByTarget(String target, ArrayList<SequenceFlow> sfs) {

        for (SequenceFlow sf : sfs) {

            if (sf.getSourceRef().equals(target)) {
                return sf;
            }

        }

        return null;

    }

    public static SequenceFlow getFlowByTarget(Task target, ArrayList<SequenceFlow> sfs) {

        for (SequenceFlow sf : sfs) {

            if (sf.getSourceRef().equals(target.getId())) {
                return sf;
            }

        }

        return null;

    }
}