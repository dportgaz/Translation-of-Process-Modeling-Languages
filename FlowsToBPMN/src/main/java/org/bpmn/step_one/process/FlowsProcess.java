package org.bpmn.step_one.process;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Step;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Participant;
import org.w3c.dom.Element;

import java.lang.reflect.Array;
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

        addEndEvent();
        addEndEventSequenceFlows();
        addLoop(objects);
        addDecision();
        combineArtifcats();

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
                ArrayList<Step> steps = task.getSteps();
                SequenceFlow sfStart = new SequenceFlow();
                sfStart.setSourceRef(task.getStart().getId());
                sfStart.setTargetRef(steps.get(0).getId());
                task.getElementTask().appendChild(sfStart.getElementSequenceFlow());
                task.getFlows().add(sfStart);
                for (int i = 0; i < task.getSteps().size()-1; i++) {
                    SequenceFlow sf = new SequenceFlow();
                    sf.setSourceRef(steps.get(i).getId());
                    sf.setTargetRef(steps.get(i+1).getId());
                    task.getElementTask().appendChild(sf.getElementSequenceFlow());
                    task.getFlows().add(sf);
                }
                SequenceFlow sfEnd = new SequenceFlow();
                EndEvent end = new EndEvent();
                task.setEnd(end);
                sfEnd.setSourceRef(steps.get(task.getSteps().size()-1).getId());
                sfEnd.setTargetRef(end.getId());
                task.getElementTask().appendChild(end.getElementEndEvent());
                task.getElementTask().appendChild(sfEnd.getElementSequenceFlow());
                task.getFlows().add(sfEnd);
            }
        }
    }

    private void removeSequenzeFlow(SequenceFlow sequenceFlow) {

        for (int i = 0; i < flows.size(); i++) {
            SequenceFlow sf = flows.get(i);
            if (sf.getId().equals(sequenceFlow.getId())) {
                flows.remove(i);
            }

        }

    }

    private void combineArtifcats() {

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

    private void openDecisionFlows(ArrayList<SequenceFlow> flowsTemp) {

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

    private void addLoop(ArrayList<AbstractObjectType> objects) {

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

    /*
    public void addFlowsToActivities(ConcreteObjectType objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {


        for (Task task : tasks) {

            for (SequenceFlow sf : flows) {

                if (task.getId().equals(sf.getSourceRef())) {
                    task.setOutgoing(sf);
                }

                if (task.getId().equals(sf.getTargetRef())) {
                    task.setIncoming(sf);
                }

            }

        }

        for (Task task : tasks) {

            ArrayList<SequenceFlow> flowsTemp = new ArrayList<>();

            SequenceFlow sfStart = new SequenceFlow();
            flowsTemp.add(sfStart);

            sfStart.setSourceRef(startEvent.getId());
            sfStart.setTargetRef((task.getStepNames().get(0).getId()));

            Element startFlow = doc.createElement("bpmn:outgoing");
            startFlow.setTextContent(sfStart.getId());
            start.appendChild(startFlow);

            Element firstStepFlow = doc.createElement("bpmn:incoming");
            firstStepFlow.setTextContent(sfStart.getId());
            stepElements.get(0).appendChild(firstStepFlow);

            for (int k = 0; k < task.getStepNames().size() - 1; k++) {

                SequenceFlow sf = new SequenceFlow();

                flowsTemp.add(sf);

                Element tempStepFlowInc = doc.createElement("bpmn:incoming");
                tempStepFlowInc.setTextContent(sf.getId());
                stepElements.get(k + 1).appendChild(tempStepFlowInc);

                Element tempStepFlowOut = doc.createElement("bpmn:outgoing");
                tempStepFlowOut.setTextContent(sf.getId());
                stepElements.get(k).appendChild(tempStepFlowOut);

                sf.setSourceRef(task.getStepNames().get(k).getId());
                sf.setTargetRef(task.getStepNames().get(k + 1).getId());
            }

            SequenceFlow sfEnd = new SequenceFlow();
            flowsTemp.add(sfEnd);

            sfEnd.setSourceRef((task.getStepNames().get(task.getStepNames().size() - 1).getId()));
            sfEnd.setTargetRef(endEvent.getId());

            Element endFlow = doc.createElement("bpmn:outgoing");
            endFlow.setTextContent(sfEnd.getId());
            stepElements.get(stepElements.size() - 1).appendChild(endFlow);

            Element lastStepFlow = doc.createElement("bpmn:incoming");
            lastStepFlow.setTextContent(sfEnd.getId());
            end.appendChild(lastStepFlow);

            for (SequenceFlow sf : flowsTemp) {
                Element flow = doc.createElement("bpmn:sequenceFlow");
                flow.setAttribute("id", sf.getId());
                flow.setAttribute("sourceRef", sf.getSourceRef());
                flow.setAttribute("targetRef", sf.getTargetRef());
                activity.appendChild(flow);
            }

            activity.appendChild(start);
            activity.appendChild(end);
            for (Element e : stepElements) {
                activity.appendChild(e);
            }
            process.appendChild(activity);

        }

        // add flows
        Element inc = doc.createElement("bpmn:incoming");
        Element out = doc.createElement("bpmn:outgoing");
        inc.setTextContent(task.getIncoming().getId());
        out.setTextContent(task.getOutgoing().getId());

        activity.appendChild(inc);
        activity.appendChild(out);

        // add property
            /*
            Element prop = doc.createElement("bpmn:property");
            prop.setAttribute("id", task.getProperty());
            prop.setAttribute("name", "__targetRef_placeholder");
            activity.appendChild(prop);
             */

    // add data input association

    /*
        if (task.getDataInputAssociation() != null) {
            //System.out.println(task + " ___ " + task.getDataInputAssociation());
            Element dataObjectRef = doc.createElement("bpmn:dataInputAssociation");
            dataObjectRef.setAttribute("id", task.getDataInputAssociation());
            Element source = doc.createElement("bpmn:sourceRef");
            Element target = doc.createElement("bpmn:targetRef");
            //target.setTextContent(task.getProperty());
            task.setInputAssociationSource(task.getBefore().getDataObject().getRefId());
            source.setTextContent(task.getInputAssociationSource());
            dataObjectRef.appendChild(source);
            dataObjectRef.appendChild(target);
            activity.appendChild(dataObjectRef);
        }

        // add data output association
        Element dataObjectRef2 = doc.createElement("bpmn:dataOutputAssociation");
        dataObjectRef2.setAttribute("id", task.getDataOutputAssociation());
        Element target = doc.createElement("bpmn:targetRef");
        target.setTextContent(task.getDataObject().getRefId());
        dataObjectRef2.appendChild(target);
        activity.appendChild(dataObjectRef2);

    }

}

     */


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
