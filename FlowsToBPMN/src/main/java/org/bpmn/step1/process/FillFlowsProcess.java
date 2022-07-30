package org.bpmn.step1.process;

import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.process.activity.Task;
import org.bpmn.step1.process.event.StartEvent;
import org.bpmn.step1.process.flow.SequenceFlow;
import org.bpmn.step1.process.gateway.ExclusiveGateway;
import org.bpmn.step1.process.gateway.Gateway;
import org.bpmn.step1.process.gateway.Predicate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bpmn.step1.collaboration.participant.FillFlowsParticipant.getParticipants;

public class FillFlowsProcess {

    static ArrayList<FlowsProcess> processes = new ArrayList<FlowsProcess>();

    public void fillProcesses(Document doc, Element rootElement, ObjectTypeMap objectMap) throws FileNotFoundException {

        for (int i = 0; i < processes.size(); i++) {

            String key = objectMap.getObjectIdsList().get(i);
            FlowsProcess fp = processes.get(i);
            Element process = doc.createElement("bpmn:process");

            fillProcess(doc, rootElement, process, objectMap, fp, key, i);


        }

    }

    public FillFlowsProcess() throws FileNotFoundException {
        setProcessList();
    }

    public Task findTaskById(Double id, ObjectTypeMap objectMap, String key, FlowsProcess f) throws FileNotFoundException {

        for (Task task : f.getTaskList()) {

            if (task.getCreatedEntityId().equals(id)) {
                return task;
            }

        }
        return null;
    }

    public AbstractObjectType findObjectById(Double id, ObjectTypeMap objectMap, String key) throws FileNotFoundException {

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

    public void fillProcess(Document doc, Element rootElement, Element process, ObjectTypeMap objectMap, FlowsProcess fp, String key, int i) throws FileNotFoundException {

        addProcessHeader(rootElement, fp, process, i);
        addStartEvent(doc, fp, process);
        addPredicates(objectMap, key, i, fp, doc, process);
        addPredicateSteps(objectMap, key, i, fp, doc, process);
        // System.out.println(fp.getPredicateStepTypes());
        // System.out.println(fp.getPredicateList());
        addActivities(objectMap, key, i, fp, doc, process);
        addSequenceFlows(objectMap, key, i, fp, doc, process);

    }

    public void addProcessHeader(Element rootElement, FlowsProcess fp, Element process, int i) {

        process.setAttribute("id", "Process_" + getParticipants().get(i).getProcessRef());
        process.setAttribute("isExecutable", new Boolean(fp.getIsExecutable()).toString());
        rootElement.appendChild(process);

    }

    public void addStartEvent(Document doc, FlowsProcess fp, Element process) {

        StartEvent startEventTemp = new StartEvent();
        fp.setStartEvent(startEventTemp);
        Element startEvent = doc.createElement("bpmn:startEvent");
        startEvent.setAttribute("id", fp.getStartEvent().getId());
        process.appendChild(startEvent);

    }

    public void addActivities(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        // add activities
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
            activity.setAttribute("id", task.getId());
            activity.setAttribute("name", task.getName());
            process.appendChild(activity);
        }

    }

    public void addPredicates(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        // add predicates
        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            if (obj != null) {
                if (obj.getMethodName().equals("AddPredicateStepType")) {

                    Predicate predicate = new Predicate();
                    predicate.setCreatedEntityId((Double) obj.getParameters().get(0));
                    fp.addPredicate(predicate);
                }
            }
        });

    }

    public void addPredicateSteps(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        // add predicate steptypes
        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            if (obj != null) {
                if (obj.getMethodName().equals("AddStepType")) {

                    for (Predicate p : fp.getPredicateList()) {

                        if (p.getCreatedEntityId().equals(obj.getCreatedEntityId())) {
                            fp.addPredicateStepType(obj);
                        }

                    }
                }
            }
        });

        //System.out.println(fp.getPredicateStepTypes());
    }

    public void addSequenceFlows(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        // add SequenceFlows
        SequenceFlow startFlow = new SequenceFlow();
        startFlow.setSourceRef(fp.getStartEvent().getId());
        startFlow.setTargetRef(fp.getTaskList().get(0).getId());
        fp.addSequenceFlow(startFlow);

        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            if (obj != null && obj.getMethodName().equals("AddTransitionType")) {
                Double source = (Double) obj.getParameters().get(0);
                Double target = (Double) obj.getParameters().get(1);

                try {
                    Double sourceObjectId = (Double) findObjectById(source, objectMap, key).getParameters().get(0);
                    Double targetObjectId = (Double) findObjectById(target, objectMap, key).getParameters().get(0);
                    if (!sourceObjectId.equals(targetObjectId)) {
                        SequenceFlow sf = new SequenceFlow();
                        Task task1 = findTaskById(sourceObjectId, objectMap, key, fp);
                        Task task2 = findTaskById(targetObjectId, objectMap, key, fp);

                        //find task by Id but predicate

                        // TODO: !!!!! does not work with predicates in multiple different states/steps; needs fix
                        if (task1 == null) {
                            task1 = findTaskById((Double) fp.getPredicateStepTypes().get(0).getParameters().get(0), objectMap, key, fp);
                        }

                        if (task2 == null) {
                            task2 = findTaskById((Double) fp.getPredicateStepTypes().get(0).getParameters().get(0), objectMap, key, fp);
                        }

                        // System.out.println(source + " ___ " + task1 + " ___ " + target + " ___ " + task2);

                        sf.setSourceRef(task1.getId());
                        sf.setTargetRef(task2.getId());
                        fp.addSequenceFlow(sf);
                    }

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        addLoop(doc, fp, objectMap, key, process);

        for (SequenceFlow sequenceFlow : fp.getSequenceFlowList()) {
            Element flow = doc.createElement("bpmn:sequenceFlow");
            flow.setAttribute("id", sequenceFlow.getId());
            flow.setAttribute("sourceRef", sequenceFlow.getSourceRef());
            flow.setAttribute("targetRef", sequenceFlow.getTargetRef());
            process.appendChild(flow);
        }
    }

    public void addLoop(Document doc, FlowsProcess fp, ObjectTypeMap objectMap, String key, Element process) throws FileNotFoundException {

        // gateways in case of loop
        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            if (obj != null && obj.getMethodName().equals("AddBackwardsTransitionType")) {
                Double source = (Double) obj.getParameters().get(1);
                Double target = (Double) obj.getParameters().get(0);

                try {
                    Double sourceObjectId = findObjectById(source, objectMap, key).getCreatedEntityId();
                    Double targetObjectId = findObjectById(target, objectMap, key).getCreatedEntityId();

                    SequenceFlow sf = new SequenceFlow();
                    Task sourceTask = findTaskById(sourceObjectId, objectMap, key, fp);
                    Task targetTask = findTaskById(targetObjectId, objectMap, key, fp);

                    SequenceFlow flowBeforeStart = fp.getFlowBySource(sourceTask);
                    SequenceFlow flowAfterEnd = fp.getFlowByTarget(targetTask);

                    ExclusiveGateway startGate = new ExclusiveGateway();
                    ExclusiveGateway endGate = new ExclusiveGateway();

                    SequenceFlow sf1 = new SequenceFlow();
                    sf1.setSourceRef(flowBeforeStart.getSourceRef());
                    sf1.setTargetRef(startGate.getId());
                    flowBeforeStart.setSourceRef(startGate.getId());

                    SequenceFlow sf2 = new SequenceFlow();
                    sf2.setSourceRef(endGate.getId());
                    sf2.setTargetRef(flowAfterEnd.getTargetRef());
                    flowAfterEnd.setTargetRef(endGate.getId());

                    SequenceFlow sf3 = new SequenceFlow();
                    sf3.setSourceRef(endGate.getId());
                    sf3.setTargetRef(startGate.getId());

                    fp.addSequenceFlow(sf1);
                    fp.addSequenceFlow(sf2);
                    fp.addSequenceFlow(sf3);

                    Element gateway1 = doc.createElement("bpmn:exclusiveGateway");
                    Element gateway2 = doc.createElement("bpmn:exclusiveGateway");
                    gateway1.setAttribute("id", startGate.getId());
                    gateway2.setAttribute("id", endGate.getId());
                    process.appendChild(gateway1);
                    process.appendChild(gateway2);


                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }

        });

    }

    public void addDecision(Document doc, FlowsProcess fp, ObjectTypeMap objectMap, String key, Element process) throws FileNotFoundException {

        // check which flows have the same sourceRef
        for (int i = 0; i < fp.getSequenceFlowList().size(); i++) {

            for (int j = 0; j < fp.getSequenceFlowList().size(); j++) {


            }

        }
    }


}