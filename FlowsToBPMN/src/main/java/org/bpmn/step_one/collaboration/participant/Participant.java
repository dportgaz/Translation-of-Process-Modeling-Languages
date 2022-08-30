package org.bpmn.step_one.collaboration.participant;

import org.bpmn.randomidgenerator.RandomIdGenerator;

import org.bpmn.step_one.process.fProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class Participant {

    String participantID;
    String name;
    fProcess processRef;
    Double updatedEntityId;

    Element participantElement;
    Element processElement;

    // StartEvent startEvent;
    // ArrayList<Task> tasks = new ArrayList<Task>();

    /*
    public ArrayList<Task> getTasks() {
        return tasks;
    }

     */

    public Element getFlowsProcessElement() {
        return processElement;
    }

    public void setProcessElement(Element processElement) {
        this.processElement = processElement;
    }

    /*
    public void setStartEvent(StartEvent startEvent) {
        this.startEvent = startEvent;
    }

    public StartEvent getStartEvent() {
        return startEvent;
    }

     */

    public Double getUpdatedEntityId() {
        return updatedEntityId;
    }

    public String getParticipantID() {
        return this.participantID;
    }

    public fProcess getProcessRef() {
        return this.processRef;
    }

    public String getName() {
        return this.name;
    }

    public Participant(String name) {
        this.participantID = "Participant_" + RandomIdGenerator.generateRandomUniqueId(6);
        String fProcessId = "Process_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.processRef = new fProcess(fProcessId);
        this.name = name;
        this.participantElement = doc.createElement("bpmn:participant");
        setParticipantElement();
    }

    private void setParticipantElement() {

        this.participantElement.setAttribute("id", this.participantID);
        this.participantElement.setAttribute("name", this.name);
        this.participantElement.setAttribute("processRef", this.processRef.getfProcessId());

    }

    public Element getParticipantElement() {
        return participantElement;
    }
}