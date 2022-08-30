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

    String collaborationID;

    Element elementCollaboration;

    public static ArrayList<Participant> participants = new ArrayList<>();

    public Collaboration() throws FileNotFoundException {
        this.collaborationID = "Collaboration_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementCollaboration = doc.createElement("bpmn:collaboration");
        setElementCollaboration();
        //setParticipants(test3(doc, filename));
    }

    // fill attributes of collaboration Element
    private void setElementCollaboration() {
        this.elementCollaboration.setAttribute("id", this.collaborationID);
    }

    public Element getElementCollaboration() {
        return elementCollaboration;
    }

    public String getCollaborationID() {
        return collaborationID;
    }

    public void setParticipants(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        for (Map.Entry<String, ArrayList<AbstractObjectType>> objects : objectTypeObjects.entrySet()) {

            objects.getValue().forEach(obj -> {

                if (obj != null && obj.getMethodName().equals("UpdateName")) {

                    String participantName = (String) obj.getParameters().get(0);

                    // work around for json double entry bug in flows json
                    if (!containsParticipant(participantName)) {

                        Participant participant = new Participant(participantName);
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

    /*
    public FlowsObjectNameList fillFlowsObjectNameList(Document doc, String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectNameJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObjectName.class, new FlowsObjectNameJsonDeserializer()).create();

        FlowsObjectNameList flowsObjects = gsonFlowsObjectNameJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectNameList.class);

        for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {

            flowsObjects.ObjectTypeActionLogs.get(key).removeAll(Collections.singleton(null));

        }

        return flowsObjects;
    }

    public ArrayList<AbstractFlowsObject> fillFlowsObjectList(Document doc, String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObject.class, new FlowsObjectJsonDeserializer()).create();

        FlowsObjectList flowsObjects2 = gsonFlowsObjectJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectList.class);

        flowsObjects2.getList().removeAll(Collections.singleton(null));

        return flowsObjects2.getList();
    }

    public ArrayList<String> test3(Document doc, String filename) throws FileNotFoundException {

        ArrayList<String> names = new ArrayList<>();
        ArrayList<AbstractFlowsObject> temp = fillFlowsObjectList(doc, filename);
        FlowsObjectNameList temp2 = fillFlowsObjectNameList(doc, filename);

        for (String key : temp2.ObjectTypeActionLogs.keySet()) {

            for (AbstractFlowsObject obj : temp) {
                if (obj != null && obj.getCreatedActorId().equals(key)) {
                    names.addAll(temp2.ObjectTypeActionLogs.get(key).get(0).getParameters());
                }
            }

        }

        return names;

    }

    public static void setParticipants(ArrayList<String> names) {
        for (String name : names) {
            FlowsParticipant flowsParticipant = new FlowsParticipant(name);
            participants.add(flowsParticipant);
        }
    }

    public ArrayList<Participant> getParticipants() {
        return participants;
    }

     */


}
