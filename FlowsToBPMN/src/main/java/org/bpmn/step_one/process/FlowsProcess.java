package org.bpmn.step_one.process;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Participant;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.bpmn.bpmn_elements.flows.SequenceFlow.*;
import static org.bpmn.bpmn_elements.gateway.Predicate.getPredicate;
import static org.bpmn.bpmn_elements.gateway.Predicate.parsePredicate;
import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class FlowsProcess {

    static int countProcess = 0;

    String id;

    static String isExecutable = "true";

    Element elementFlowsProcess;

    Participant participant;
    StartEvent startEvent;

    // ArrayList, da Reihenfolge der Tasks gewahrt werden soll
    ArrayList<Task> tasks = new ArrayList<>();

    EndEvent endEvent;

    ArrayList<ExclusiveGateway> gateways = new ArrayList<>();

    ArrayList<SequenceFlow> flows = new ArrayList<>();

    HashSet<DataObject> dataObjects = new HashSet<>();

    ArrayList<Predicate> predicates = new ArrayList<>();

    HashMap<String, SequenceFlow> decisionFlows = new HashMap<>();

    HashMap<String, ArrayList<String>> decisionTasks = new HashMap<>();


    public FlowsProcess(Participant participant, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        this.id = "Process_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementFlowsProcess = doc.createElement("bpmn:process");
        this.participant = participant;
        setFlowsProcess(objectTypeObjects);
        setElementFlowsProcess();
        countProcess++;

    }

    private void setFlowsProcess(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        setStartEvent();
        setTasks(objectTypeObjects);
        addPredicates(objectTypeObjects);
        addSequenceFlows(objectTypeObjects);

    }

    private void setStartEvent() {

        StartEvent startEvent = new StartEvent();
        Element elementStartEvent = startEvent.getElementStartEvent();

        this.startEvent = startEvent;
        this.elementFlowsProcess.appendChild(elementStartEvent);

    }

    public void setTasks(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        objectTypeObjects.get(this.participant.getKey()).forEach(obj -> {

            if (obj != null && obj.getMethodName().equals("UpdateStateType")) {

                String taskName = obj.getParameters().get(1) + " " + this.participant.getName();
                Double createdEntityId = (Double) obj.getParameters().get(0);
                Participant participant = this.participant;

                Task task = new Task(createdEntityId, taskName, participant, objectTypeObjects);

                if (this.tasks.size() > 0) {
                    task.setDataInputAssociation();
                }
                task.setDataOutputAssociation();
                if (this.tasks.contains(task)) {
                    tasks.remove(task);
                }
                tasks.add(task);
                // allTasks.add(task);

            }

        });

        // add task elements and dataobject elements to process
        for (Task task : tasks) {

            DataObject dObj = task.getDataObject();
            // TODO: SEITENEFFEKT ENTFERNEN/LOESEN
            dataObjects.add(dObj);

            this.elementFlowsProcess.appendChild(dObj.getElementDataObject());

            Element tempObj = doc.createElement("bpmn:dataObject");
            tempObj.setAttribute("id", dObj.getId());
            this.elementFlowsProcess.appendChild(tempObj);

            this.elementFlowsProcess.appendChild(task.getElementTask());

        }

    }

    public void addPredicates(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        objectTypeObjects.get(this.participant.getKey()).forEach(obj -> {
            if (obj != null && obj.getMethodName().equals("AddPredicateStepType")) {

                Predicate predicate = parsePredicate(obj.getCreatedEntityId(), objectTypeObjects.get(this.participant.getKey()));

                predicate.setCreatedEntityId(obj.getCreatedEntityId());
                this.predicates.add(predicate);

            }
        });
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

    public void addSequenceFlows(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        SequenceFlow startFlow = new SequenceFlow();
        startFlow.setSourceRef(this.startEvent.getId());
        startFlow.setTargetRef(this.tasks.get(0).getId());
        flows.add(startFlow);

        ArrayList<AbstractObjectType> objects = objectTypeObjects.get(this.participant.getKey());
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

        System.out.println("12");
        addEndEvent();
        System.out.println("22");
        addEndEventSequenceFlows();
        System.out.println("32");
        addLoop(objects);
        System.out.println("42");
        addDecision();
        System.out.println("52");
        combineArtifcats();
        System.out.println("62");

        int decisionFlowsCnt = 0;
        for (SequenceFlow sequenceFlow : flows) {

            Element flow = doc.createElement("bpmn:sequenceFlow");
            flow.setAttribute("id", sequenceFlow.getId());
            flow.setAttribute("sourceRef", sequenceFlow.getSourceRef());
            flow.setAttribute("targetRef", sequenceFlow.getTargetRef());

            if (decisionFlows.containsKey(sequenceFlow.getId()) && predicates.size() > 0) {
                //System.out.println("://////////////// : " + fp.getPredicateList());
                // TODO: MAYBE BUGGY BECAUSE CHANGED TO LIST FROM SET
                sequenceFlow.setName(predicates.get(decisionFlowsCnt).getCondition());
                decisionFlowsCnt++;
                flow.setAttribute("name", sequenceFlow.getName());
            }
            this.elementFlowsProcess.appendChild(flow);
            System.out.println("1");
        }
        System.out.println("2");
        // System.out.println(fp.getTaskList());

        for (int k = 0; k < tasks.size(); k++) {

            Task task = tasks.get(k);

            if (k == 0) {
                task.setBeforeEvent(startEvent);
                task.setAfter(tasks.get(k + 1));
                //System.out.println("1: " + task.getBeforeEvent() + " --> " + task + " --> " + task.getAfter());
            } else if (k == tasks.size() - 1) {
                task.setAfterEvent(endEvent);
                task.setBefore(tasks.get(k - 1));
                //System.out.println("2: " + task.getBefore() + " --> " + task + " --> " + task.getAfterEvent());
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
                                    //System.out.println("4: " + tempTask.getBefore() + " --> " + tempTask + " --> " + tempTask.getAfter());

                                } else {
                                    tempTask.setAfterEvent(endEvent);
                                    //System.out.println("4: " + tempTask.getBefore() + " --> " + tempTask + " --> " + tempTask.getAfterEvent());
                                }
                            }
                        }
                    }
                    k += decisionTasks.size() + 1;
                } else {
                    task.setBefore(tasks.get(k - 1));
                    task.setAfter(tasks.get(k + 1));
                    //System.out.println("3: " + task.getBefore() + " --> " + task + " --> " + task.getAfter());
                }
            }
        }

    }

    public void removeSequenzeFlow(SequenceFlow sequenceFlow) {

        for (int i = 0; i < flows.size(); i++) {
            SequenceFlow sf = flows.get(i);
            if (sf.getId().equals(sequenceFlow.getId())) {
                flows.remove(i);
            }

        }

    }

    public void combineArtifcats() {

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

    public void addDecision() {

        Pattern pattern = Pattern.compile("Activity_*");

        System.out.println("1000");

        for (int i = 0; i < flows.size() - 1; i++) {
            System.out.println("1001");
            Matcher matcher = pattern.matcher(flows.get(i).getSourceRef());
            String outerSourceRef = flows.get(i).getSourceRef();
            boolean duplicate = false;

            ArrayList<SequenceFlow> flowsTemp = new ArrayList<>();
            flowsTemp.add(flows.get(i));
            System.out.println("1002");
            if (matcher.find()) {
                System.out.println("1003");
                for (int j = i + 1; j < flows.size(); j++) {

                    Matcher datcher = pattern.matcher(flows.get(j).getSourceRef());
                    String innerSourceRef = flows.get(j).getSourceRef();
                    System.out.println(flows);
                    System.out.println("1004");
                    if (datcher.find() && outerSourceRef.equals(innerSourceRef)) {
                        System.out.println("1005");
                        duplicate = true;

                        //TODO: MAYBE BUGGY
                        flowsTemp.add(flows.get(j));
                        removeSequenzeFlow(flows.get(j));
                        System.out.println("1006");
                    }
                }

                if (duplicate) {
                    openDecisionFlows(flowsTemp);
                    removeSequenzeFlow(flows.get(i));
                    System.out.println("1007");
                }

            }
            System.out.println("1008");
        }

    }

    public void openDecisionFlows(ArrayList<SequenceFlow> flowsTemp) {

        ExclusiveGateway gate = new ExclusiveGateway();
        gateways.add(gate);
        ArrayList<String> tempTasks = new ArrayList<>();

        SequenceFlow toGateway = new SequenceFlow();
        toGateway.setSourceRef(flowsTemp.get(0).getSourceRef());
        toGateway.setTargetRef(gate.getId());
        flows.add(toGateway);

        // System.out.println(toGateway);


        for (int i = 0; i < flowsTemp.size(); i++) {

            SequenceFlow fromGateway = new SequenceFlow();
            fromGateway.setSourceRef(gate.getId());
            fromGateway.setTargetRef(flowsTemp.get(i).getTargetRef());
            // System.out.println(fromGateway);
            flows.add(fromGateway);
            gateways.add(gate);
            tempTasks.add(fromGateway.getTargetRef());
            decisionFlows.put(fromGateway.getId(), fromGateway);
        }

        decisionTasks.put(toGateway.getSourceRef(), tempTasks);

    }

    public void addLoop(ArrayList<AbstractObjectType> objects) {

        // gateways in case of loop
        objects.forEach(obj -> {
            if (obj != null && obj.getMethodName().equals("AddBackwardsTransitionType")) {
                Double source = (Double) obj.getParameters().get(1);
                Double target = (Double) obj.getParameters().get(0);

                Double sourceObjectId = findObjectById(source, objects).getCreatedEntityId();
                Double targetObjectId = findObjectById(target, objects).getCreatedEntityId();

                SequenceFlow sf = new SequenceFlow();
                Task sourceTask = findTaskById(sourceObjectId);
                Task targetTask = findTaskById(targetObjectId);

                SequenceFlow flowBeforeStart = getFlowBySource(sourceTask, flows);
                SequenceFlow flowAfterEnd = getFlowByTarget(targetTask, flows);

                ExclusiveGateway startGate = new ExclusiveGateway();
                ExclusiveGateway endGate = new ExclusiveGateway();

                gateways.add(startGate);
                gateways.add(endGate);

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


            }


        });

    }

    public void addEndEvent() {
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

    public void addEndEventSequenceFlows() {

        EndEvent endEventTemp = new EndEvent();
        endEvent = endEventTemp;

        for (int i = 0; i < tasks.size(); i++) {

            Task task = tasks.get(i);
            if (task.getIsEndTask()) {

                SequenceFlow sf = new SequenceFlow();
                sf.setSourceRef(task.getId());
                sf.setTargetRef(endEvent.getId());
                flows.add(sf);
            }
        }

    }

    public boolean containsFlow(SequenceFlow flow) {
        for (SequenceFlow sf : this.flows) {
            if (sf.getSourceRef().equals(flow.getSourceRef()) && sf.getTargetRef().equals(flow.getTargetRef())) {
                return true;
            }
        }
        return false;
    }

    public void removeFlowFromList(SequenceFlow flow) {
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
}
