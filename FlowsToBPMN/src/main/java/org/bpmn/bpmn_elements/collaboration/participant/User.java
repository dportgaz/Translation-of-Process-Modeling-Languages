package org.bpmn.bpmn_elements.collaboration.participant;

import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.process.FlowsProcessUser;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.steps.StepOne.allParticipants;

public class User extends Participant {

    String id;
    Double key;
    String name;
    Double updatedEntityId;
    FlowsProcessUser processRef;
    Element participantElement;
    Collaboration collaboration;

    public User(Collaboration collaboration, Double key, String name, Double updatedEntityId) {

        super(collaboration, key, name);
        this.collaboration = collaboration;
        this.id = super.getId();
        this.key = key;
        this.name = name;
        this.participantElement = super.getParticipantElement();
        this.updatedEntityId = updatedEntityId;
        allParticipants.add(this);

    }

    public void setProcessRef(HashMap<Double, ArrayList<AbstractObjectType>> userTypeObjects) {
        this.processRef = new FlowsProcessUser(this, userTypeObjects);
        this.participantElement.setAttribute("processRef", this.processRef.getId());
    }

    public Double getUpdatedEntityId() {
        return updatedEntityId;
    }

    public String getId() {
        return this.id;
    }

    public Double getKey() {
        return key;
    }

    public FlowsProcessUser getProcessRef() {
        return processRef;
    }

    public String getName() {
        return this.name;
    }

    public Element getParticipantElement() {
        return this.participantElement;
    }

}
