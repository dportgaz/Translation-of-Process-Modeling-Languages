package org.bpmn.step1.collaboration.participant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bpmn.step1.collaboration.participant.flowsobject.AbstractFlowsObject;
import org.bpmn.step1.collaboration.participant.flowsobject.FlowsObjectJsonDeserializer;
import org.bpmn.step1.collaboration.participant.flowsobject.FlowsObjectList;
import org.bpmn.step1.collaboration.participant.flowsobjectname.AbstractFlowsObjectName;
import org.bpmn.step1.collaboration.participant.flowsobjectname.FlowsObjectNameJsonDeserializer;
import org.bpmn.step1.collaboration.participant.flowsobjectname.FlowsObjectNameList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class FillParticipant {

    public void fillCollaborationParticipants(Document doc, Element collaboration, String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectNameJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObjectName.class, new FlowsObjectNameJsonDeserializer()).create();

        FlowsObjectNameList flowsObjects = gsonFlowsObjectNameJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectNameList.class);

        for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {

            flowsObjects.ObjectTypeActionLogs.get(key).removeAll(Collections.singleton(null));

        }

        Gson gsonFlowsObjectJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObject.class, new FlowsObjectJsonDeserializer()).create();

        FlowsObjectList flowsObjects2 = gsonFlowsObjectJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectList.class);

        flowsObjects2.getList().removeAll(Collections.singleton(null));

        ArrayList<String> names = new ArrayList<>();

        for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {
            for (AbstractFlowsObject obj : flowsObjects2.getList()) {
                if (obj != null && obj.getCreatedActorId().equals(key)) {
                    names.addAll(flowsObjects.ObjectTypeActionLogs.get(key).get(0).getParameters());
                }
            }

        }

        for (String name : names) {
            Participant participant = new Participant(name);
            Element temp = doc.createElement("bpmn:participant");
            collaboration.appendChild(temp);
            temp.setAttribute("id", "Participant_" + participant.getParticipantID());
            temp.setAttribute("name", participant.getName());
            temp.setAttribute("processRef", "Process_" + participant.getProcessRef());
        }

    }


}
