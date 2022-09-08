package org.bpmn.step_one.collaboration.participant;

import org.bpmn.flowsObjects.AbstractObjectType;

import org.bpmn.process.FlowsProcessObject;
import org.bpmn.process.FlowsProcessUser;
import org.bpmn.step_one.collaboration.Collaboration;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Object extends Participant {

    String id;
    String key;
    String name;
    FlowsProcessObject processRef;
    Element participantElement;

    // ArrayList<Task> tasks = new ArrayList<Task>();

    /*
    public ArrayList<Task> getTasks() {
        return tasks;
    }

     */


    public Object(Collaboration collaboration, String key, String name, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        super(collaboration, key, name);
        this.collaboration = collaboration;
        this.id = super.getId();
        this.key = key;
        this.name = name;
        this.participantElement = super.getParticipantElement();

    }

    public void setProcessRef(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {
        this.processRef = new FlowsProcessObject(this, objectTypeObjects);
        this.participantElement.setAttribute("processRef", this.processRef.getId());
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
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