package org.bpmn.step1.process;

import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.collaboration.participant.flowsobject.AbstractFlowsObject;
import org.bpmn.step1.process.activity.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import static org.bpmn.step1.collaboration.participant.FillFlowsParticipant.getParticipants;

public class FillFlowsProcess {

    static ArrayList<FlowsProcess> processes = new ArrayList<FlowsProcess>();

    public FillFlowsProcess() throws FileNotFoundException {
        setProcessList();
    }

    public void fillProcesses(Document doc, Element rootElement, ObjectTypeMap objectMap) throws FileNotFoundException {
        for (int i = 0; i < processes.size(); i++) {

            // add Header
            FlowsProcess fp = processes.get(i);
            Element process = doc.createElement("bpmn:process");
            process.setAttribute("id", "Process_" + fp.getId());
            process.setAttribute("isExecutable", new Boolean(fp.getIsExecutable()).toString());
            rootElement.appendChild(process);

            // add StartEvent
            Element startEvent = doc.createElement("bpmn:startEvent");
            startEvent.setAttribute("id", "Event_" + fp.getStartEvent().getId());
            process.appendChild(startEvent);

            // add activities

            String key = objectMap.getObjectIdsList().get(i);
            String participantName = getParticipants().get(i).getName();
            objectMap.getObjectTypeObjects().get(key).
                    forEach(obj -> {
                        if (obj != null) {
                            if (obj.getMethodName().equals("UpdateStateType")) {
                                Task task = new Task();
                                String activityName = obj.getParameters().get(1) + " " + participantName;
                                task.setName(activityName);
                                fp.addTask(task);
                            }
                        }
                    });


            for (Task task : fp.getTaskList()) {
                Element activity = doc.createElement("bpmn:task");
                activity.setAttribute("id", "Activity_" + task.getId());
                activity.setAttribute("name", task.getName());
                process.appendChild(activity);
            }
        }
    }


    public void setProcessList() throws FileNotFoundException {

        boolean firstProcess = true;
        for (FlowsParticipant tempFlowsParticipant : getParticipants()) {

            if (firstProcess == true) {
                FlowsProcess fp = new FlowsProcess(tempFlowsParticipant.getParticipantID(), true);
                processes.add(fp);
                firstProcess = false;
            } else {
                FlowsProcess fp = new FlowsProcess(tempFlowsParticipant.getParticipantID(), false);
                processes.add(fp);
            }
        }
    }
}
