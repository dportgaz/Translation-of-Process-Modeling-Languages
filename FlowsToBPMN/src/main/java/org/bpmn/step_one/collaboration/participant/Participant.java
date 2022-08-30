package org.bpmn.step_one.collaboration.participant;

import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.randomidgenerator.RandomIdGenerator;

import org.bpmn.step_one.process.FlowsProcess;
import org.w3c.dom.Element;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class Participant {

    String id;
    String name;
    FlowsProcess processRef;
    Double updatedEntityId;
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

    public FlowsProcess getProcessRef() {
        return this.processRef;
    }

    public String getName() {
        return this.name;
    }

    public Element getParticipantElement() {
        return this.participantElement;
    }

    public Participant(String name) {
        this.id = "Participant_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.processRef = new FlowsProcess();
        this.name = name;
        this.participantElement = doc.createElement("bpmn:participant");
        setParticipantElement();
    }

    private void setParticipantElement() {

        this.participantElement.setAttribute("id", this.id);
        this.participantElement.setAttribute("name", this.name);
        this.participantElement.setAttribute("processRef", this.processRef.getId());

    }

}