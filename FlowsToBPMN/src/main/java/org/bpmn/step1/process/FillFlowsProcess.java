package org.bpmn.step1.process;

import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.process.activity.Task;
import org.bpmn.step1.process.flow.SequenceFlow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

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
            objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
                if (obj != null) {
                    if (obj.getMethodName().equals("UpdateStateType")) {
                        Task task = new Task();
                        String activityName = obj.getParameters().get(1) + " " + participantName;
                        task.setCreatedEntityId((Double) obj.getParameters().get(0));
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

            // add SequenceFlows
            objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("AddTransitionType")) {
                    Double source = (Double) obj.getParameters().get(0);
                    Double target = (Double) obj.getParameters().get(1);
                    try {
                        Double sourceObjectId = (Double) findObjectById(source, objectMap, key).getParameters().get(0);
                        Double targetObjectId = (Double) findObjectById(target, objectMap, key).getParameters().get(0);
                        if (!sourceObjectId.equals(targetObjectId)) {
                            SequenceFlow sf = new SequenceFlow();

                            for (int k = 0; k < fp.getTaskList().size(); k++) {
                                Task task = fp.getTaskList().get(k);
                                Double createdEntityIdOfActivity = task.getCreatedEntityId();
                                if (sourceObjectId.equals(createdEntityIdOfActivity)) {
                                    sf.setSourceRef("Activity_" + task.getId());
                                }
                                if (targetObjectId.equals(createdEntityIdOfActivity)) {
                                    sf.setTargetRef("Activity_" + task.getId());
                                }
                            }
                            System.out.println(sf.getSourceRef());
                            System.out.println(sf.getTargetRef());
                            fp.addSequenceFlow(sf);
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

            for (SequenceFlow sequenceFlow : fp.getSequenceFlowList()) {
                Element flow = doc.createElement("bpmn:sequenceFlow");
                flow.setAttribute("id", "Flow_" + sequenceFlow.getId());
                flow.setAttribute("sourceRef", sequenceFlow.getSourceRef());
                flow.setAttribute("targetRef", sequenceFlow.getTargetRef());
                process.appendChild(flow);
            }

        }
    }

    public static AbstractObjectType findObjectById(Double id, ObjectTypeMap objectMap, String key) throws
            FileNotFoundException {

        return objectMap.getObjectTypeObjects().get(key).stream().filter(obj -> obj != null && obj.getCreatedEntityId() != null && obj.getCreatedEntityId().equals(id)).collect(Collectors.toList()).get(0);
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
