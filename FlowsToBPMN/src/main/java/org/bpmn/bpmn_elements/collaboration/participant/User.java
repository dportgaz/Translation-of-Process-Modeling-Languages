package org.bpmn.bpmn_elements.collaboration.participant;

import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.process.FlowsProcessUser;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class User extends Participant {

    String id;
    String key;
    String name;
    Double updatedEntityId;
    FlowsProcessUser processRef;
    Element participantElement;
    Collaboration collaboration;

    public User(Collaboration collaboration, String key, String name, Double updatedEntityId, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects, HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects) {

        super(collaboration, key, name);
        this.collaboration = collaboration;
        this.id = super.getId();
        this.key = key;
        this.name = name;
        this.participantElement = super.getParticipantElement();
        this.updatedEntityId = updatedEntityId;

    }

    public void setProcessRef(HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects) {
        this.processRef = new FlowsProcessUser(this, userTypeObjects);
        this.participantElement.setAttribute("processRef", this.processRef.getId());
    }

    public Double getUpdatedEntityId() {
        return updatedEntityId;
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
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
