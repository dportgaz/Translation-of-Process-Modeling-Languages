package org.bpmn.step1.collaboration.participant;

import org.bpmn.randomidgenerator.RandomIdGenerator;

public class FlowsParticipant {

    String participantID;
    String processRef;
    String name;

    public void setParticipantID() {
        this.participantID = RandomIdGenerator.generateRandomUniqueId(6);
    }

    public void setProcessRef() {
        this.processRef = RandomIdGenerator.generateRandomUniqueId(6);
    }

    public void setProcessRef(String name) {

        this.name = name;
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
        this.participantID = RandomIdGenerator.generateRandomUniqueId(6);
        this.processRef = RandomIdGenerator.generateRandomUniqueId(6);
        this.name = name;
    }
}