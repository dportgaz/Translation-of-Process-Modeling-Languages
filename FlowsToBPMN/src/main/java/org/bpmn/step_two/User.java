package org.bpmn.step_two;

import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.flowsObjects.ConcreteObjectType;
import org.bpmn.step_one.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step_one.collaboration.participant.FlowsParticipant;
import org.bpmn.step_one.process.FillFlowsProcess;
import org.bpmn.step_one.process.FlowsProcess;
import org.bpmn.step_one.process.activity.Task;
import org.bpmn.step_one.process.dataobject.DataObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.step_one.collaboration.participant.FillFlowsParticipant.getParticipants;

public class User {

    static ArrayList<FlowsParticipant> permissionParticipants = new ArrayList<>();

    public void testStepTwoParticipants(ConcreteObjectType objectMap) throws FileNotFoundException {

        HashMap<String, ArrayList<AbstractObjectType>> users = objectMap.getUserTypeObjects();

        for (String userId : users.keySet()) {
            users.get(userId).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("UpdateGlobalRoleName")) {

                    String name = (String) obj.getParameters().get(1);
                    Double updatedEntityId = obj.getUpdatedEntityId();
                    FlowsParticipant fp = new FlowsParticipant(name, updatedEntityId);

                    if (!permissionParticipants.contains(fp)) {
                        permissionParticipants.add(fp);
                    }

                }
            });
        }

    }

    public void fillCollaborationParticipantsPermission(Document doc, String collaborationID, Element rootElement) {

        Element collaboration = doc.createElement("bpmn:collaboration");
        collaboration.setAttribute("id", collaborationID);
        rootElement.appendChild(collaboration);

        for (FlowsParticipant p : permissionParticipants) {

            Element participantElement = doc.createElement("bpmn:participant");
            participantElement.setAttribute("id", p.getParticipantID());
            participantElement.setAttribute("name", p.getName());
            participantElement.setAttribute("processRef", p.getProcessRef());
            collaboration.appendChild(participantElement);

        }

    }

    public static FlowsParticipant getUser(Double id) {

        for (FlowsParticipant p : permissionParticipants) {
            if (p.getUpdatedEntityId().equals(id)) {
                return p;
            }
        }
        return null;
    }

}
