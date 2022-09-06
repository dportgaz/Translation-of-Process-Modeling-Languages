package org.bpmn.step_one.collaboration.participant;

import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;

import org.bpmn.process.FlowsProcessOne;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.fillxml.ExecSteps.doc;

public class ParticipantObject {

    String id;
    String key;
    String name;
    Double updatedEntityId;
    FlowsProcessOne processRef;
    Element participantElement;

    // ArrayList<Task> tasks = new ArrayList<Task>();

    /*
    public ArrayList<Task> getTasks() {
        return tasks;
    }

     */


    public Double getUpdatedEntityId() {
        return updatedEntityId;
    }

    public String getId() {
        return this.id;
    }

    public String getKey() {
        return key;
    }

    public FlowsProcessOne getProcessRef() {
        return this.processRef;
    }

    public String getName() {
        return this.name;
    }

    public Element getParticipantElement() {
        return this.participantElement;
    }

    public ParticipantObject(String key, String name, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        this.key = key;
        this.id = "Participant_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.name = name;
        this.processRef = new FlowsProcessOne(this, objectTypeObjects);
        this.participantElement = doc.createElement("bpmn:participant");
        setParticipantElement();

    }

    private void setParticipantElement() {

        this.participantElement.setAttribute("id", this.id);
        this.participantElement.setAttribute("name", this.name);
        this.participantElement.setAttribute("processRef", this.processRef.getId());

    }

}