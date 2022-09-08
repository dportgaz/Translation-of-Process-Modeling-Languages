package org.bpmn.step_one.collaboration.participant;

import org.bpmn.process.FlowsProcessUser;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.Collaboration;
import org.w3c.dom.Element;

import static org.bpmn.fillxml.ExecSteps.doc;

public abstract class Participant {

    String id;
    String key;
    String name;
    Element participantElement;

    Collaboration collaboration;

    public Participant(Collaboration collaboration, String key, String name) {

        this.collaboration = collaboration;
        this.key = key;
        this.name = name;
        this.id = "Participant_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.participantElement = doc.createElement("bpmn:participant");
        setParticipantElement();

    }

    private void setParticipantElement() {

        this.participantElement.setAttribute("id", this.id);
        this.participantElement.setAttribute("name", this.name);

    }

    public Element getParticipantElement() {
        return participantElement;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Collaboration getCollaboration() {
        return collaboration;
    }
}
