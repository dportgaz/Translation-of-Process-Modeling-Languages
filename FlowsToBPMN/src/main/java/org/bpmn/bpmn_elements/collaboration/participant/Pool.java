package org.bpmn.bpmn_elements.collaboration.participant;

import org.bpmn.flows_entities.AbstractFlowsEntity;

import org.bpmn.process.FlowsProcessObject;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.transformation.LifecycleTransformation.Participants;

public class Pool extends Participant {

    String id;
    Double key;
    String name;
    FlowsProcessObject processRef;
    Element participantElement;


    public Pool(Collaboration collaboration, Double key, String name) {

        super(collaboration, key, name);
        this.collaboration = collaboration;
        this.id = super.getId();
        this.key = key;
        this.name = name;
        this.participantElement = super.getParticipantElement();
        Participants.add(this);

    }

    public void setProcessRef(HashMap<Double, ArrayList<AbstractFlowsEntity>> objectTypeObjects, boolean adHoc, boolean expandedSubprocess) {
        this.processRef = new FlowsProcessObject(this, objectTypeObjects, adHoc, expandedSubprocess);
        this.participantElement.setAttribute("processRef", this.processRef.getId());
    }

    public String getId() {
        return this.id;
    }

    public Double getKey() {
        return key;
    }

    public FlowsProcessObject getProcessRef() {
        return this.processRef;
    }

    public String getName() {
        return this.name;
    }

    public Element getParticipantElement() {
        return this.participantElement;
    }

}