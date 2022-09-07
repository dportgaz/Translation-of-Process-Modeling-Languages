package org.bpmn.bpmn_elements.flows;

import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.fillxml.ExecSteps.doc;

public class SequenceFlow {

    String id;

    String name;

    String sourceRef;

    String targetRef;

    SequenceFlow toGateway;

    SequenceFlow fromGateway;

    Element elementSequenceFlow;

    public SequenceFlow() {
        this.id = "Flow_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementSequenceFlow = doc.createElement("bpmn:sequenceFlow");
        setElementSequenceFlow();
    }

    public SequenceFlow(String sourceRef, String targetRef) {
        this.id = "Flow_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementSequenceFlow = doc.createElement("bpmn:sequenceFlow");
        setElementSequenceFlow();
        setSourceRef(sourceRef);
        setTargetRef(targetRef);
    }

    private void setElementSequenceFlow() {
        this.elementSequenceFlow.setAttribute("id", this.id);
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
        this.elementSequenceFlow.setAttribute("sourceRef", sourceRef);
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
        this.elementSequenceFlow.setAttribute("targetRef", targetRef);
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

    public static ArrayList<SequenceFlow> removeSequenzeFlow(SequenceFlow sequenceFlow, ArrayList<SequenceFlow> sfs) {

        ArrayList<SequenceFlow> tempFlows = sfs;
        for (int i = 0; i < sfs.size(); i++) {
            SequenceFlow sf = sfs.get(i);
            if (sf.getId().equals(sequenceFlow.getId())) {
                tempFlows.remove(i);
            }

        }
        return tempFlows;

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