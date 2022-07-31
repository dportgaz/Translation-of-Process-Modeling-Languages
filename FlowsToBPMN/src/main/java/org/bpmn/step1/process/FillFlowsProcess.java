package org.bpmn.step1.process;

import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeCreateEntity;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.process.activity.Task;
import org.bpmn.step1.process.event.EndEvent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        // addEndEvent(doc, fp, process, objectMap, key);
        // addEndEventSequenceFlows(objectMap, key, fp, doc, process);
        // addDecision(doc, fp, objectMap, key, process);

    }

    public void addEndEventSequenceFlows(ObjectTypeMap objectMap, String key, FlowsProcess fp, Document doc, Element process) {

        EndEvent endEventTemp = new EndEvent();
        Element endEvent = doc.createElement("bpmn:endEvent");
        endEvent.setAttribute("id", endEventTemp.getId());
        process.appendChild(endEvent);

        for (int i = 0; i < fp.getEndTasks().size(); i++) {

            Task task = fp.getEndTasks().get(i);

            SequenceFlow sf = new SequenceFlow();
            sf.setSourceRef(task.getId());
            sf.setTargetRef(endEventTemp.getId());

            fp.addSequenceFlow(sf);

        }

    }

    public void addEndEvent(Document doc, FlowsProcess fp, Element process, ObjectTypeMap objectMap, String key) throws FileNotFoundException {
        for (Task task : fp.taskList) {
            boolean temp = false;
            for (SequenceFlow sf : fp.getSequenceFlowList()) {
                if (task.getId().equals(sf.getSourceRef())) {
                    temp = true;

                }

            }
            if (!temp) {
                fp.getEndTasks().add(task);
            }

        }

        // System.out.println(fp.getEndTasks());
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

                    // Fixes New State and double Edit/Submit/etc. problem
                    if (fp.containsTask(task)) {
                        fp.removeTaskFromList(task);
                    }

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

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {

            if (obj != null && obj.getMethodName().equals("AddTransitionType")) {
                Double source = (Double) obj.getParameters().get(0);
                Double target = (Double) obj.getParameters().get(1);

                // check whether source and/or target are predicates
                // in this case, change parameter of transition to his respective step

                AbstractObjectType sourceTemp = getPredicate(source, objectMap, key);
                AbstractObjectType targetTemp = getPredicate(target, objectMap, key);

                if (sourceTemp != null && sourceTemp.getMethodName().equals("AddPredicateStepType")) {
                    source = (Double) getPredicate(source, objectMap, key).getParameters().get(0);
                }

                if (targetTemp != null && targetTemp.getMethodName().equals("AddPredicateStepType")) {
                    target = (Double) getPredicate(target, objectMap, key).getParameters().get(0);
                }

                try {
                    Double sourceObjectId = (Double) findObjectById(source, objectMap, key).getParameters().get(0);
                    Double targetObjectId = (Double) findObjectById(target, objectMap, key).getParameters().get(0);
                    if (!sourceObjectId.equals(targetObjectId)) {
                        SequenceFlow sf = new SequenceFlow();
                        Task task1 = findTaskById(sourceObjectId, objectMap, key, fp);
                        Task task2 = findTaskById(targetObjectId, objectMap, key, fp);

                        //System.out.println(source + " ___ " + task1 + " ___ " + target + " ___ " + task2);

                        if (task1 != null && task2 != null) {
                            sf.setSourceRef(task1.getId());
                            sf.setTargetRef(task2.getId());
                            if (fp.containsFlow(sf)) {
                                fp.removeFlowFromList(sf);
                            }
                            fp.addSequenceFlow(sf);
                        }
                    }

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        addEndEvent(doc, fp, process, objectMap, key);
        addEndEventSequenceFlows(objectMap, key, fp, doc, process);

        addLoop(doc, fp, objectMap, key, process);
        addDecision(doc, fp, objectMap, key, process);

        for (SequenceFlow sequenceFlow : fp.getSequenceFlowList()) {
            Element flow = doc.createElement("bpmn:sequenceFlow");
            flow.setAttribute("id", sequenceFlow.getId());
            flow.setAttribute("sourceRef", sequenceFlow.getSourceRef());
            flow.setAttribute("targetRef", sequenceFlow.getTargetRef());
            process.appendChild(flow);
        }
    }

    private AbstractObjectType getPredicate(Double source, ObjectTypeMap objectMap, String key) throws FileNotFoundException {

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {

            if (obj != null && obj.getCreatedEntityId() != null && obj.getCreatedEntityId().equals(source)) {
                if (obj.getMethodName().equals("AddPredicateStepType")) {
                    return obj;
                }
            }

        }

        return null;
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

        Pattern pattern = Pattern.compile("Activity_*");
        LinkedHashSet<SequenceFlow> temp = new LinkedHashSet<>();


        for (int i = 0; i < fp.getSequenceFlowList().size() - 1; i++) {

            Matcher matcher = pattern.matcher(fp.getSequenceFlowList().get(i).getSourceRef());
            String outerSourceRef = fp.getSequenceFlowList().get(i).getSourceRef();
            boolean duplicate = false;

            ArrayList<SequenceFlow> flows = new ArrayList<>();
            flows.add(fp.getSequenceFlowList().get(i));

            if (matcher.find()) {

                for (int j = i + 1; j < fp.getSequenceFlowList().size(); j++) {

                    Matcher datcher = pattern.matcher(fp.getSequenceFlowList().get(j).getSourceRef());
                    String innerSourceRef = fp.getSequenceFlowList().get(j).getSourceRef();

                    if (datcher.find() && outerSourceRef.equals(innerSourceRef)) {

                        duplicate = true;
                        flows.add(fp.getSequenceFlowList().get(j));
                        fp.removeSequenzeFlow(fp.getSequenceFlowList().get(j));

                    }
                }

                if (duplicate) {
                    openDecisionFlows(flows, doc, fp, objectMap, key, process);
                    fp.removeSequenzeFlow(fp.getSequenceFlowList().get(i));
                }

            }

        }

    }

    public void openDecisionFlows(ArrayList<SequenceFlow> flows, Document doc, FlowsProcess fp, ObjectTypeMap objectMap, String key, Element process) {

        ExclusiveGateway gate = new ExclusiveGateway();

        Element gateway = doc.createElement("bpmn:exclusiveGateway");
        gateway.setAttribute("id", gate.getId());
        process.appendChild(gateway);

        SequenceFlow toGateway = new SequenceFlow();
        toGateway.setSourceRef(flows.get(0).getSourceRef());
        toGateway.setTargetRef(gate.getId());
        fp.addSequenceFlow(toGateway);

        for (int i = 0; i < flows.size(); i++) {

            SequenceFlow fromGateway = new SequenceFlow();
            fromGateway.setSourceRef(gate.getId());
            fromGateway.setTargetRef(flows.get(i).getTargetRef());
            fp.addSequenceFlow(fromGateway);

        }


    }

    public void closeDecisionFlows(ArrayList<SequenceFlow> flows, Document doc, FlowsProcess fp, ObjectTypeMap objectMap, String key, Element process) {

        ExclusiveGateway gate = new ExclusiveGateway();

        Element gateway = doc.createElement("bpmn:exclusiveGateway");
        gateway.setAttribute("id", gate.getId());
        process.appendChild(gateway);

        SequenceFlow toGateway = new SequenceFlow();
        toGateway.setSourceRef(flows.get(0).getSourceRef());
        toGateway.setTargetRef(gate.getId());
        fp.addSequenceFlow(toGateway);

        for (int i = 0; i < flows.size(); i++) {

            SequenceFlow fromGateway = new SequenceFlow();
            fromGateway.setSourceRef(gate.getId());
            fromGateway.setTargetRef(flows.get(i).getTargetRef());
            fp.addSequenceFlow(fromGateway);

        }


    }


}