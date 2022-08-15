package org.bpmn.step_one.collaboration.participant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bpmn.step_one.collaboration.participant.flowsobject.AbstractFlowsObject;
import org.bpmn.step_one.collaboration.participant.flowsobject.FlowsObjectJsonDeserializer;
import org.bpmn.step_one.collaboration.participant.flowsobject.FlowsObjectList;
import org.bpmn.step_one.collaboration.participant.flowsobjectname.AbstractFlowsObjectName;
import org.bpmn.step_one.collaboration.participant.flowsobjectname.FlowsObjectNameJsonDeserializer;
import org.bpmn.step_one.collaboration.participant.flowsobjectname.FlowsObjectNameList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class FillFlowsParticipant {

    public FillFlowsParticipant(Document doc, String filename) throws FileNotFoundException {
        setParticipants(test3(doc, filename));
    }

    static ArrayList<FlowsParticipant> flowsParticipants = new ArrayList<>();

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

    public void fillCollaborationParticipants(Document doc, String collaborationID, String filename, Element rootElement) throws FileNotFoundException {

        Element collaboration = doc.createElement("bpmn:collaboration");
        collaboration.setAttribute("id", collaborationID);
        rootElement.appendChild(collaboration);

        for (FlowsParticipant p : getParticipants()) {
            Element part = doc.createElement("bpmn:participant");
            collaboration.appendChild(part);
            part.setAttribute("id", p.getParticipantID());
            part.setAttribute("name", p.getName());
            part.setAttribute("processRef", p.getProcessRef());
        }

    }

    public static void setParticipants(ArrayList<String> names) {
        for (String name : names) {
            FlowsParticipant flowsParticipant = new FlowsParticipant(name);
            flowsParticipants.add(flowsParticipant);
        }
    }

    public static ArrayList<FlowsParticipant> getParticipants() {
        return flowsParticipants;
    }
}