package org.bpmn.bpmn_elements.collaboration.participant;

import org.bpmn.process.FlowsProcessObject;
import org.bpmn.process.Lane;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.steps.BPMN.doc;

public abstract class Participant {

    String id;
    Double key;
    String name;
    Element participantElement;
    Collaboration collaboration;
    FlowsProcessObject processRef;
    HashMap<User, Lane> lanes = new HashMap<>();


    public Participant(Collaboration collaboration, Double key, String name) {

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

        //<participantMultiplicity id="sid-5e17d400-2dc5-44fa-802c-cc4742eddbb9" maximum="2" minimum="2"/>
        Element multiInstance = doc.createElement("bpmn:participantMultiplicity");
        multiInstance.setAttribute("id", "MultiInstanceParticipant_" + RandomIdGenerator.generateRandomUniqueId(6));
        multiInstance.setAttribute("maximum", "2");
        multiInstance.setAttribute("minimum","2");
        this.participantElement.appendChild(multiInstance);

    }

    public HashMap<User, Lane> getLanes() {
        return lanes;
    }

    public void setLanes(HashMap<User, Lane> lanes) {
        this.lanes = lanes;
    }

    public Element getParticipantElement() {
        return participantElement;
    }

    public String getId() {
        return id;
    }

    public Double getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Collaboration getCollaboration() {
        return collaboration;
    }

    public FlowsProcessObject getProcessRef() {
        return processRef;
    }

    @Override
    public String toString() {
        return name;
    }
}
