package org.bpmn.step_one.collaboration.participant;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.process.event.StartEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FlowsParticipant {

    String participantID;
    String processRef;
    String name;
    Double updatedEntityId;

    Element flowsProcessElement;
    StartEvent startEvent;

    public Element getFlowsProcessElement() {
        return flowsProcessElement;
    }

    public void setFlowsProcessElement(Element flowsParticipantElement) {
        this.flowsProcessElement = flowsParticipantElement;
    }

    public void setStartEvent(StartEvent startEvent) {
        this.startEvent = startEvent;
    }

    public StartEvent getStartEvent() {
        return startEvent;
    }

    public Double getUpdatedEntityId() {
        return updatedEntityId;
    }

    public String getParticipantID() {
        return this.participantID;
    }

    public String getProcessRef() {
        return this.processRef;
    }

    public String getName() {
        return this.name;
    }

    public FlowsParticipant(String name) {
        this.participantID = "Participant_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.processRef = "Process_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.name = name;
    }

    public FlowsParticipant(String name, Double updatedEntityId) {
        this.updatedEntityId = updatedEntityId;
        this.name = name;
        this.participantID = "Participant_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.processRef = "Process_" + RandomIdGenerator.generateRandomUniqueId(6);
    }
}