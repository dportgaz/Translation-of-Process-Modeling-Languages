package org.bpmn.step_one.collaboration;

import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Participant;

import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class Collaboration {

    String id;

    Element elementCollaboration;

    public static ArrayList<Participant> participants = new ArrayList<>();

    public Collaboration() throws FileNotFoundException {
        this.id = "Collaboration_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementCollaboration = doc.createElement("bpmn:collaboration");
        setElementCollaboration();
    }

    // fill attributes of collaboration Element
    private void setElementCollaboration() {
        this.elementCollaboration.setAttribute("id", this.id);
    }

    public Element getElementCollaboration() {
        return elementCollaboration;
    }

    public String getId() {
        return this.id;
    }

    public void setParticipants(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        for (Map.Entry<String, ArrayList<AbstractObjectType>> objects : objectTypeObjects.entrySet()) {

            objects.getValue().forEach(obj -> {

                if (obj != null && obj.getMethodName().equals("UpdateName")) {

                    String participantName = (String) obj.getParameters().get(0);

                    // work around for json double entry bug in flows json
                    if (!containsParticipant(participantName)) {

                        Participant participant = new Participant(objects.getKey(), participantName, objectTypeObjects);
                        participants.add(participant);

                        // add participant to collaboration element
                        elementCollaboration.appendChild(participant.getParticipantElement());
                    }

                }

            });
        }
    }

    private boolean containsParticipant(String participantName) {

        for (Participant participant : participants) {
            if (participant.getName().equals(participantName)) {
                return true;
            }
        }
        return false;

    }

}
