package org.bpmn.process;

import org.bpmn.bpmn_elements.Loop;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.task.Step;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.parse_json.Parser;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.participant.Object;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.bpmn.bpmn_elements.flows.SequenceFlow.*;
import static org.bpmn.bpmn_elements.gateway.Predicate.getPredicate;
import static org.bpmn.steps.Execution.doc;
import static org.bpmn.steps.StepOne.*;

public class FlowsProcessObject {

    static int countProcess = 0;

    String id;

    static String isExecutable = "true";

    Element elementFlowsProcess;

    Object participant;
    StartEvent startEvent;

    // ArrayList, da Reihenfolge der Tasks gewahrt werden soll
    ArrayList<Task> tasks = new ArrayList<>();

    EndEvent endEvent;

    HashSet<ExclusiveGateway> gateways = new HashSet<>();

    ArrayList<SequenceFlow> flows = new ArrayList<>();

    HashSet<DataObject> dataObjects = new HashSet<>();

    ArrayList<Task> subprocesses = new ArrayList<>();

    HashMap<String, SequenceFlow> decisionFlows = new HashMap<>();

    HashMap<String, ArrayList<String>> decisionTasks = new HashMap<>();

    ArrayList<AbstractObjectType> objects;

    public FlowsProcessObject(Object participant, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        this.id = "Process_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementFlowsProcess = doc.createElement("bpmn:process");
        this.participant = participant;
        this.objects = objectTypeObjects.get(participant.getKey());
        setFlowsProcess();
        setElementFlowsProcess();
        countProcess++;

    }

    private void setFlowsProcess() {

        Parser parser = new Parser();

        this.tasks = parser.parseTasks(this.participant, objects);
        predicates = parser.parsePredicates(objects);

        setStartEvent();
        setEndEvent();
        setTasks();
        setDataObjects();
        addSequenceFlows();

        setAssociations();
        addFlowsToActivities();
        addGateways();

    }

    private void setAssociations() {

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            // add data input association
            if (i != 0) {
                task.setDataInputAssociation();
                task.setInputAssociationSource(task.getBefore().getDataObject());
            }

            // add data output association
            task.setDataOutputAssociation();
            task.setOutputAssociationTarget(task.getDataObject());
        }

    }

    private void setTasks() {

        for (Task task : tasks) {
            this.elementFlowsProcess.appendChild(task.getElementTask());
        }

    }

    private void setDataObjects() {

        for (Task task : tasks) {

            DataObject dObj = task.getDataObject();
            dataObjects.add(dObj);

            this.elementFlowsProcess.appendChild(dObj.getElementDataObject());

            Element tempObj = doc.createElement("bpmn:dataObject");
            tempObj.setAttribute("id", dObj.getId());
            this.elementFlowsProcess.appendChild(tempObj);
            this.elementFlowsProcess.appendChild(task.getElementTask());

        }
    }

    private void setStartEvent() {

        StartEvent startEvent = new StartEvent();
        Element elementStartEvent = startEvent.getElementStartEvent();

        this.startEvent = startEvent;
        this.elementFlowsProcess.appendChild(elementStartEvent);

    }

    private void setEndEvent() {

        EndEvent endEvent = new EndEvent();
        Element elementEndEvent = endEvent.getElementEndEvent();

        this.endEvent = endEvent;
        this.elementFlowsProcess.appendChild(elementEndEvent);

    }

    private AbstractObjectType findObjectById(Double id, ArrayList<AbstractObjectType> objectTypeObjects) {

        return objectTypeObjects.stream().filter(obj -> obj != null && obj.getCreatedEntityId() != null && obj.getCreatedEntityId().equals(id)).collect(Collectors.toList()).get(0);
    }

    private Task findTaskById(Double id) {

        for (Task task : this.tasks) {

            if (task.getCreatedEntityId().equals(id)) {
                return task;
            }

        }
        return null;
    }

    private void
    addSequenceFlows() {

        SequenceFlow startFlow = new SequenceFlow();
        startFlow.setSourceRef(this.startEvent.getId());
        startFlow.setTargetRef(this.tasks.get(0).getId());
        flows.add(startFlow);
        startEvent.setOutgoing(startFlow);

        for (AbstractObjectType obj : objects) {

            if (obj != null && obj.getMethodName().equals("AddTransitionType")) {
                Double source = (Double) obj.getParameters().get(0);
                Double target = (Double) obj.getParameters().get(1);

                // check whether source and/or target are predicates
                // in this case, change parameter of transition to his respective step

                AbstractObjectType sourceTemp = getPredicate(source, objects);
                AbstractObjectType targetTemp = getPredicate(target, objects);

                if (sourceTemp != null && sourceTemp.getMethodName().equals("AddPredicateStepType")) {
                    source = (Double) getPredicate(source, objects).getParameters().get(0);
                }

                if (targetTemp != null && targetTemp.getMethodName().equals("AddPredicateStepType")) {
                    target = (Double) getPredicate(target, objects).getParameters().get(0);
                }

                Double sourceObjectId = (Double) findObjectById(source, objects).getParameters().get(0);
                Double targetObjectId = (Double) findObjectById(target, objects).getParameters().get(0);
                if (!sourceObjectId.equals(targetObjectId)) {
                    SequenceFlow sf = new SequenceFlow();
                    Task task1 = findTaskById(sourceObjectId);
                    Task task2 = findTaskById(targetObjectId);

                    if (task1 != null && task2 != null) {
                        sf.setSourceRef(task1.getId());
                        sf.setTargetRef(task2.getId());
                        if (containsFlow(sf)) {
                            removeFlowFromList(sf);
                        }
                        flows.add(sf);
                    }
                }
            }
        }

        addEndEvent();
        addEndEventSequenceFlows();
        addLoop(objects);

        // addDecision();
        // combine();

        int decisionFlowsCnt = 0;
        for (SequenceFlow sequenceFlow : flows) {

            Element flow = doc.createElement("bpmn:sequenceFlow");
            flow.setAttribute("id", sequenceFlow.getId());
            flow.setAttribute("sourceRef", sequenceFlow.getSourceRef());
            flow.setAttribute("targetRef", sequenceFlow.getTargetRef());

            if (decisionFlows.containsKey(sequenceFlow.getId()) && predicates.size() > 0) {
                // TODO: MAYBE BUGGY BECAUSE CHANGED TO LIST FROM SET
                sequenceFlow.setName(predicates.get(decisionFlowsCnt).getCondition());
                decisionFlowsCnt++;
                flow.setAttribute("name", sequenceFlow.getName());
            }
            this.elementFlowsProcess.appendChild(flow);
        }

        for (int k = 0; k < tasks.size(); k++) {

            Task task = tasks.get(k);

            if (k == 0) {
                task.setBeforeEvent(startEvent);
                task.setAfter(tasks.get(k + 1));
            } else if (k == tasks.size() - 1) {
                task.setAfterEvent(endEvent);
                task.setBefore(tasks.get(k - 1));
            } else {
                if (decisionTasks.containsKey(task.getId())) {
                    task.setBefore(tasks.get(k - 1));
                    int indexAfter = k + decisionTasks.get(task.getId()).size() + 1;

                    for (String id : decisionTasks.get(task.getId())) {
                        for (int m = 0; m < tasks.size(); m++) {
                            Task tempTask = tasks.get(m);
                            if (tempTask.getId().equals(id)) {
                                tempTask.setBefore(task);
                                if (indexAfter < tasks.size()) {
                                    tempTask.setAfter(tasks.get(indexAfter));

                                } else {
                                    tempTask.setAfterEvent(endEvent);
                                }
                            }
                        }
                    }
                    k += decisionTasks.size() + 1;
                } else {
                    task.setBefore(tasks.get(k - 1));
                    task.setAfter(tasks.get(k + 1));
                }
            }
        }

        for (Task task : tasks) {
            if (task.getIsSubprocess()) {
                subprocesses.add(task);
                ArrayList<Step> steps = task.getSteps();
                SequenceFlow sfStart = new SequenceFlow();
                sfStart.setSourceRef(task.getStart().getId());
                sfStart.setTargetRef(steps.get(0).getId());
                task.getElementTask().appendChild(sfStart.getElementSequenceFlow());
                task.getFlows().add(sfStart);
                task.getStart().setOutgoing(sfStart);
                for (int i = 0; i < task.getSteps().size() - 1; i++) {
                    SequenceFlow sf = new SequenceFlow();
                    sf.setSourceRef(steps.get(i).getId());
                    sf.setTargetRef(steps.get(i + 1).getId());
                    task.getElementTask().appendChild(sf.getElementSequenceFlow());
                    task.getFlows().add(sf);
                }
                SequenceFlow sfEnd = new SequenceFlow();
                sfEnd.setSourceRef(steps.get(task.getSteps().size() - 1).getId());
                sfEnd.setTargetRef(task.getEnd().getId());
                task.getElementTask().appendChild(task.getEnd().getElementEndEvent());
                task.getElementTask().appendChild(sfEnd.getElementSequenceFlow());
                task.getFlows().add(sfEnd);
                task.getEnd().setIncoming(sfEnd);
            }
        }
        for (SequenceFlow sf : flows) {
            allFlows.add(sf);
        }
    }

    private void removeSequenceFlow(SequenceFlow sequenceFlow) {

        for (int i = 0; i < flows.size(); i++) {
            SequenceFlow sf = flows.get(i);
            if (sf.getId().equals(sequenceFlow.getId())) {
                flows.remove(i);
            }

        }

    }

    private void combine() {

        HashSet<SequenceFlow> temp = new HashSet<>();
        Pattern pattern = Pattern.compile("Gateway_*");

        for (int i = 0; i < flows.size() - 1; i++) {
            Matcher matcher = pattern.matcher(flows.get(i).getTargetRef());
            boolean duplicate = false;
            if (!matcher.find()) {

                SequenceFlow outerFlow = flows.get(i);
                ExclusiveGateway gate = new ExclusiveGateway();

                for (int j = i + 1; j < flows.size(); j++) {
                    Matcher datcher = pattern.matcher(flows.get(j).getTargetRef());
                    if (!datcher.find()) {
                        SequenceFlow innerFlow = flows.get(j);

                        if (outerFlow.getTargetRef().equals(innerFlow.getTargetRef()) && !temp.contains(outerFlow)) {

                            duplicate = true;
                            temp.add(outerFlow);
                            innerFlow.setTargetRef(gate.getId());
                            gateways.add(gate);

                        }

                    }

                    if (duplicate) {
                        SequenceFlow sf = new SequenceFlow();
                        sf.setSourceRef(gate.getId());
                        sf.setTargetRef(outerFlow.getTargetRef());
                        flows.add(sf);
                        outerFlow.setTargetRef(gate.getId());
                        duplicate = false;
                    }
                }
            }
        }
    }

    private void addDecision() {

        Pattern pattern = Pattern.compile("Activity_*");

        for (int i = 0; i < flows.size() - 1; i++) {
            Matcher matcher = pattern.matcher(flows.get(i).getSourceRef());
            String outerSourceRef = flows.get(i).getSourceRef();
            boolean duplicate = false;

            ArrayList<SequenceFlow> flowsTemp = new ArrayList<>();
            flowsTemp.add(flows.get(i));
            if (matcher.find()) {
                for (int j = i + 1; j < flows.size(); j++) {

                    Matcher datcher = pattern.matcher(flows.get(j).getSourceRef());
                    String innerSourceRef = flows.get(j).getSourceRef();
                    if (datcher.find() && outerSourceRef.equals(innerSourceRef)) {
                        duplicate = true;

                        //TODO: MAYBE BUGGY
                        flowsTemp.add(flows.get(j));
                        removeSequenceFlow(flows.get(j));
                    }
                }

                if (duplicate) {
                    openDecisionFlows(flowsTemp);
                    removeSequenceFlow(flows.get(i));
                }

            }
        }
    }

    private void openDecisionFlows(ArrayList<SequenceFlow> flowsTemp) {

        ExclusiveGateway gate = new ExclusiveGateway();
        gateways.add(gate);
        ArrayList<String> tempTasks = new ArrayList<>();

        SequenceFlow toGateway = new SequenceFlow();
        toGateway.setSourceRef(flowsTemp.get(0).getSourceRef());
        toGateway.setTargetRef(gate.getId());
        flows.add(toGateway);


        for (int i = 0; i < flowsTemp.size(); i++) {

            SequenceFlow fromGateway = new SequenceFlow();
            fromGateway.setSourceRef(gate.getId());
            fromGateway.setTargetRef(flowsTemp.get(i).getTargetRef());
            flows.add(fromGateway);
            gateways.add(gate);
            tempTasks.add(fromGateway.getTargetRef());
            decisionFlows.put(fromGateway.getId(), fromGateway);
        }

        decisionTasks.put(toGateway.getSourceRef(), tempTasks);

    }

    private void addLoop(ArrayList<AbstractObjectType> objects) {

        // gateways in case of loop
        objects.forEach(obj -> {
            if (obj != null && obj.getMethodName().equals("AddBackwardsTransitionType")) {

                Loop loop = new Loop();

                Double source = (Double) obj.getParameters().get(1);
                Double target = (Double) obj.getParameters().get(0);

                Double sourceObjectId = findObjectById(source, objects).getCreatedEntityId();
                Double targetObjectId = findObjectById(target, objects).getCreatedEntityId();

                Task sourceTask = findTaskById(sourceObjectId);
                Task targetTask = findTaskById(targetObjectId);

                loop.getFlows().add(new SequenceFlow(sourceTask.getId(), targetTask.getId()));

                loop.addTask(sourceTask);
                loop.setFirst(sourceTask);
                loop.addTask(targetTask);
                loop.setSecond(targetTask);

                SequenceFlow flowBeforeStart = getFlowBySource(sourceTask, flows);
                SequenceFlow flowAfterEnd = getFlowByTarget(targetTask, flows);

                ExclusiveGateway startGate = new ExclusiveGateway();
                ExclusiveGateway endGate = new ExclusiveGateway();

                gateways.add(startGate);
                gateways.add(endGate);

                loop.getFlows().add(new SequenceFlow(startGate.getId(), sourceTask.getId()));
                loop.getFlows().add(new SequenceFlow(targetTask.getId(), endGate.getId()));

                loop.addGate(startGate);
                loop.setFirstGate(startGate);
                loop.addGate(endGate);
                loop.setSecondGate(endGate);

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

                flows.add(sf1);
                flows.add(sf2);
                flows.add(sf3);

                loop.addFlow(sf3);
                loops.add(loop);
            }


        });

    }

    private void addEndEvent() {
        for (Task task : tasks) {
            boolean temp = false;
            for (SequenceFlow sf : flows) {
                if (task.getId().equals(sf.getSourceRef())) {
                    temp = true;
                }
            }
            if (!temp) {
                task.setIsEndTask();
            }
        }
    }

    private void addEndEventSequenceFlows() {

        for (int i = 0; i < tasks.size(); i++) {

            Task task = tasks.get(i);
            if (task.getIsEndTask()) {
                SequenceFlow sf = new SequenceFlow();
                sf.setSourceRef(task.getId());
                sf.setTargetRef(endEvent.getId());
                flows.add(sf);
                endEvent.setIncoming(sf);
            }
        }

    }

    private boolean containsFlow(SequenceFlow flow) {
        for (SequenceFlow sf : this.flows) {
            if (sf.getSourceRef().equals(flow.getSourceRef()) && sf.getTargetRef().equals(flow.getTargetRef())) {
                return true;
            }
        }
        return false;
    }

    private void removeFlowFromList(SequenceFlow flow) {
        for (int i = 0; i < flows.size(); i++) {
            if (flows.get(i).getSourceRef().equals(flow.getSourceRef()) && flows.get(i).getTargetRef().equals(flow.getTargetRef())) {
                flows.remove(i);
            }
        }
    }

    private void setElementFlowsProcess() {
        this.elementFlowsProcess.setAttribute("id", this.id);
        if (countProcess == 0) {
            this.elementFlowsProcess.setAttribute("isExecutable", this.isExecutable);
        } else {
            isExecutable = "false";
            this.elementFlowsProcess.setAttribute("isExecutable", this.isExecutable);
        }
    }

    private void addFlowsToActivities() {

        for (Task task : tasks) {
            if (task.getIsSubprocess()) {
                for (Step step : task.getSteps()) {
                    for (SequenceFlow sf : task.getFlows()) {
                        if (step.getId().equals(sf.getSourceRef())) {
                            step.setOutgoing(sf);
                        }
                        if (step.getId().equals(sf.getTargetRef())) {
                            step.setIncoming(sf);
                        }
                    }
                }
            }
            for (SequenceFlow sf : flows) {
                if (task.getId().equals(sf.getSourceRef())) {
                    task.setOutgoing(sf);
                }
                if (task.getId().equals(sf.getTargetRef())) {
                    task.setIncoming(sf);
                }
            }
        }

    }

    public void addGateways() {

        for (ExclusiveGateway gate : gateways) {
            allGateways.add(gate);
            this.elementFlowsProcess.appendChild(gate.getElementExclusiveGateway());
            for (SequenceFlow sf : flows) {
                if (sf.getSourceRef().equals(gate.getId())) {
                    Element out = doc.createElement("bpmn:outgoing");
                    out.setTextContent(sf.getId());
                    gate.getElementExclusiveGateway().appendChild(out);
                }
                if (sf.getTargetRef().equals(gate.getId())) {
                    Element inc = doc.createElement("bpmn:incoming");
                    inc.setTextContent(sf.getId());
                    gate.getElementExclusiveGateway().appendChild(inc);
                }
            }
        }
    }

    public Element getElementFlowsProcess() {
        return this.elementFlowsProcess;
    }

    public HashSet<DataObject> getDataObjects() {
        return dataObjects;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public String getId() {
        return this.id;
    }

    public EndEvent getEndEvent() {
        return endEvent;
    }

    public StartEvent getStartEvent() {
        return startEvent;
    }

    public ArrayList<SequenceFlow> getFlows() {
        return flows;
    }
}
