package org.bpmn.step_one.collaboration.participant;

import org.bpmn.process.FlowsProcessOne;
import org.bpmn.process.FlowsProcessUser;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import static org.bpmn.fillxml.ExecSteps.doc;

public class ParticipantUser {

    String id;
    String key;
    String name;
    Double updatedEntityId;
    FlowsProcessUser processRef;
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

    public FlowsProcessUser getProcessRef() {
        return processRef;
    }

    public String getName() {
        return this.name;
    }

    public Element getParticipantElement() {
        return this.participantElement;
    }

    public ParticipantUser(String key, String name, Double updatedEntityId) {

        this.key = key;
        this.id = "Participant_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.name = name;
        this.updatedEntityId = updatedEntityId;
        this.processRef = new FlowsProcessUser(this);
        this.participantElement = doc.createElement("bpmn:participant");
        setParticipantElement();

    }

    private void setParticipantElement() {

        this.participantElement.setAttribute("id", this.id);
        this.participantElement.setAttribute("name", this.name);
        this.participantElement.setAttribute("processRef", this.processRef.getId());

    }

}
