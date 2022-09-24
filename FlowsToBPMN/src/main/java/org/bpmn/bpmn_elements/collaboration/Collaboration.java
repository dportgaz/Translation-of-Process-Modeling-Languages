package org.bpmn.bpmn_elements.collaboration;

import org.bpmn.bpmn_elements.flows.MessageFlow;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.participant.Object;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.steps.BPMN.doc;

public class Collaboration {

    String id;

    Element elementCollaboration;

    public static ArrayList<Object> objects = new ArrayList<>();

    HashSet<MessageFlow> messageFlows = new HashSet<>();


    public Collaboration() {
        this.id = "Collaboration_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementCollaboration = doc.createElement("bpmn:collaboration");
        setElementCollaboration();
    }

    public HashSet<MessageFlow> getMessageFlows() {
        return messageFlows;
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

    public void setParticipants(HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects) {

        for (Double key : objectTypeObjects.keySet()) {
            objectTypeObjects.get(key).forEach(obj -> {

                if (obj != null && obj.getMethodName().equals("UpdateName")) {

                    String participantName = (String) obj.getParameters().get(0);

                    // work around for json double entry bug in flows json
                    // TODO: JSON BUG
                    if (!containsParticipant(participantName)) {

                        Object object = new Object(this, key, participantName);
                        objects.add(object);

                        // add participant to collaboration element
                        elementCollaboration.appendChild(object.getParticipantElement());
                    }

                }

            });
        }
        for (Object object : objects) {
            object.setProcessRef(objectTypeObjects, true);
        }
    }

    private boolean containsParticipant(String participantName) {

        for (Object participant : objects) {
            if (participant.getName().equals(participantName)) {
                return true;
            }
        }
        return false;

    }
}
