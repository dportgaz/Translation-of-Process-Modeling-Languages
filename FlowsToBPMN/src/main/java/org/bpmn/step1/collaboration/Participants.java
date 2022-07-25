package org.bpmn.step1.collaboration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bpmn.flowsObjects.flowsobject.AbstractFlowsObject;
import org.bpmn.flowsObjects.flowsobject.FlowsObjectJsonDeserializer;
import org.bpmn.flowsObjects.flowsobject.FlowsObjectList;
import org.bpmn.flowsObjects.flowsobjectname.AbstractFlowsObjectName;
import org.bpmn.flowsObjects.flowsobjectname.FlowsObjectNameJsonDeserializer;
import org.bpmn.flowsObjects.flowsobjectname.FlowsObjectNameList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;


import java.util.UUID;

public class Participants {

    public static void fillCollaborationParticipants(Document doc, Element collaboration, String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectNameJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObjectName.class, new FlowsObjectNameJsonDeserializer()).create();

        FlowsObjectNameList flowsObjects = gsonFlowsObjectNameJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectNameList.class);

        System.out.println(flowsObjects);

        for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {

            flowsObjects.ObjectTypeActionLogs.get(key).removeAll(Collections.singleton(null));

        }

        for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {

            System.out.println(flowsObjects.ObjectTypeActionLogs.get(key).get(0).getParameters());

        }

        Gson gsonFlowsObjectJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObject.class, new FlowsObjectJsonDeserializer()).create();

        FlowsObjectList flowsObjects2 = gsonFlowsObjectJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectList.class);

        System.out.println(flowsObjects2);

        ArrayList<String> names = new ArrayList<>();

        for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {
            for (AbstractFlowsObject obj : flowsObjects2.getList()) {
                if (obj != null && obj.getCreatedActorId().equals(key)) {
                    names.addAll(flowsObjects.ObjectTypeActionLogs.get(key).get(0).getParameters());
                }
            }

        }
        System.out.println(names);

        for (int i = 0; i < names.size(); i++) {
            String participantID = UUID.randomUUID().toString();
            String processRef = UUID.randomUUID().toString();
            Element temp = doc.createElement("bpmn:participant");
            collaboration.appendChild(temp);
            temp.setAttribute("id", "Participant_" + participantID);
            temp.setAttribute("name", names.get(i));
            temp.setAttribute("processRef", "Process_" + processRef);
        }
    }
}
