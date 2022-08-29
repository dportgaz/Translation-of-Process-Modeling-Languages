package org.bpmn.step_two;


import com.google.gson.internal.LinkedTreeMap;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.flowsObjects.ConcreteObjectType;
import org.bpmn.step_one.collaboration.participant.FlowsParticipant;
import org.bpmn.step_one.process.FillFlowsProcess;
import org.bpmn.step_one.process.FlowsProcess;
import org.bpmn.step_one.process.activity.Task;
import org.bpmn.step_one.process.event.StartEvent;
import org.bpmn.step_one.process.gateway.Predicate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.step_one.collaboration.participant.FillFlowsParticipant.getParticipants;
import static org.bpmn.step_two.User.permissionParticipants;

public class FillFlowsProcess2 {

    public void executeFLowsProcess2(Document doc, Element rootElement, ConcreteObjectType objectMap) throws FileNotFoundException {

        setPermissionsforTasks(objectMap);

        //fill Processes
        boolean first = true;
        for (FlowsParticipant p : permissionParticipants) {
            if (first) {
                setProcessRoot(doc, rootElement, p, "true");
            } else {
                setProcessRoot(doc, rootElement, p, "false");
            }
            setStartEvent(doc, p);
        }

    }

    public void setPermissionsforTasks(ConcreteObjectType objectMap) throws FileNotFoundException {

        HashMap<String, ArrayList<AbstractObjectType>> users = objectMap.getUserTypeObjects();

        for (String userId : users.keySet()) {
            users.get(userId).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("AddStateExecutionPermissionToGlobalRole")) {

                    Double participantId = (Double) obj.getParameters().get(0);
                    Double taskId = (Double) obj.getParameters().get(1);

                    Task task = FillFlowsProcess.getTask(taskId);
                    task.setParticipant(User.getUser(participantId));

                }
            });
        }

    }

    public void setProcessRoot(Document doc, Element rootElement, FlowsParticipant p, String isExecutable) {

        Element processElement = doc.createElement("bpmn:process");
        p.setFlowsProcessElement(processElement);

        processElement.setAttribute("id", p.getProcessRef());
        processElement.setAttribute("isExecutable", isExecutable);

        rootElement.appendChild(processElement);

    }

    public void setStartEvent(Document doc, FlowsParticipant p) {

        StartEvent startEvent = new StartEvent(doc);
        Element startEventElement = startEvent.getElementStartEvent();
        Element processElement = p.getFlowsProcessElement();

        startEventElement.setAttribute("id", startEvent.getId());

        p.setStartEvent(startEvent);
        processElement.appendChild(startEventElement);

    }
}
