package org.bpmn.step_one.collaboration;

import org.bpmn.ExecStep;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.ParticipantObject;

import org.bpmn.step_one.collaboration.participant.ParticipantUser;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.fillxml.ExecSteps.doc;

public class Collaboration {

    String id;

    Element elementCollaboration;

    public static ArrayList<ParticipantObject> participants = new ArrayList<>();

    public static ArrayList<ParticipantUser> userParticipants = new ArrayList<>();


    public Collaboration() {
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

    public void setParticipantsOne(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        for (String key : objectTypeObjects.keySet()) {
            objectTypeObjects.get(key).forEach(obj -> {

                if (obj != null && obj.getMethodName().equals("UpdateName")) {

                    String participantName = (String) obj.getParameters().get(0);

                    // work around for json double entry bug in flows json
                    if (!containsParticipant(participantName)) {

                        ParticipantObject participant = new ParticipantObject(key, participantName, objectTypeObjects);
                        participants.add(participant);

                        // add participant to collaboration element
                        elementCollaboration.appendChild(participant.getParticipantElement());
                    }

                }

            });
        }
    }

    //TODO: USER TYPES FINDEN
    public void setParticipantsTwo(ExecStep step, HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects) {

        for (String key : userTypeObjects.keySet()) {
            userTypeObjects.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("UpdateGlobalRoleName")) {

                    String name = (String) obj.getParameters().get(1);
                    Double updatedEntityId = obj.getUpdatedEntityId();
                    ParticipantUser user = new ParticipantUser(key, name, updatedEntityId);

                    if (!userParticipants.contains(user)) {
                        userParticipants.add(user);
                    }

                }
            });
        }

    }

    private boolean containsParticipant(String participantName) {

        for (ParticipantObject participant : participants) {
            if (participant.getName().equals(participantName)) {
                return true;
            }
        }
        return false;

    }

}
