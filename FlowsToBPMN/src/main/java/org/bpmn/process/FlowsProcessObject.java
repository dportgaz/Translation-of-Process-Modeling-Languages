package org.bpmn.process;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
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
import java.util.stream.Collectors;

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
        this.flows = parser.parseFlows(this, objects);
        sortProcess();
        setEndTasks();
        addEndEventFlows();
        setAssociations();
        setSubProcesses();
        //parser.parseLoops(this, objects);
        setGateways();
        setFlows();

        /*
        addFlowsToActivities();
        addGateways();
         */

    }

    private void setGateways() {

        setSplitGateways();
        setJoinGateways();

    }

    private void setSplitGateways() {

        ArrayList<SequenceFlow> flowsToAdd = new ArrayList<>();
        ArrayList<SequenceFlow> flowsToRemove = new ArrayList<>();
        // add split gateways
        for(int i = 0 ; i < flows.size()-1; i++){

            boolean duplicate = false;
            ExclusiveGateway splitGateway = new ExclusiveGateway();
            BPMNElement outerElement = flows.get(i).getSourceRef();

            for(int j = i+1 ; j < flows.size(); j++){

                BPMNElement innerElement = flows.get(j).getSourceRef();

                if(outerElement.getId().equals(innerElement.getId())){
                    flowsToRemove.add(flows.get(j));
                    flowsToAdd.add(new SequenceFlow(splitGateway, flows.get(j).getTargetRef()));
                    duplicate = true;
                }

            }
            if(duplicate){
                flowsToRemove.add(flows.get(i));
                flowsToAdd.add(new SequenceFlow(splitGateway, flows.get(i).getTargetRef()));
                flowsToAdd.add(new SequenceFlow(flows.get(i).getSourceRef(), splitGateway));
                gateways.add(splitGateway);
            }

        }

        flows.addAll(flowsToAdd);
        flows.removeAll(flowsToRemove);

    }
    private void setJoinGateways() {

        ArrayList<SequenceFlow> flowsToAdd = new ArrayList<>();
        ArrayList<SequenceFlow> flowsToRemove = new ArrayList<>();
        // add split gateways
        for(int i = 0 ; i < flows.size()-1; i++){

            boolean duplicate = false;
            ExclusiveGateway joinGateway = new ExclusiveGateway();
            BPMNElement outerElement = flows.get(i).getTargetRef();

            for(int j = i+1 ; j < flows.size(); j++){

                BPMNElement innerElement = flows.get(j).getTargetRef();

                if(outerElement.getId().equals(innerElement.getId())){
                    flowsToRemove.add(flows.get(j));
                    flowsToAdd.add(new SequenceFlow(flows.get(j).getSourceRef(), joinGateway));
                    duplicate = true;
                }

            }
            if(duplicate){
                flowsToRemove.add(flows.get(i));
                flowsToAdd.add(new SequenceFlow(joinGateway, flows.get(i).getTargetRef()));
                flowsToAdd.add(new SequenceFlow(flows.get(i).getSourceRef(), joinGateway));
                gateways.add(joinGateway);

            }

        }

        flows.addAll(flowsToAdd);
        flows.removeAll(flowsToRemove);

    }
    private void setSubProcesses() {

        for (Task task : tasks) {
            if (task.getIsSubprocess()) {
                subprocesses.add(task);
                ArrayList<Step> steps = task.getSteps();
                SequenceFlow sfStart = new SequenceFlow(task.getStart(), steps.get(0));
                task.getElement().appendChild(sfStart.getElementSequenceFlow());
                task.getFlows().add(sfStart);
                task.getStart().setOutgoing(sfStart);
                for (int i = 0; i < task.getSteps().size() - 1; i++) {
                    SequenceFlow sf = new SequenceFlow(steps.get(i), steps.get(i+1));
                    task.getElement().appendChild(sf.getElementSequenceFlow());
                    task.getFlows().add(sf);
                }
                SequenceFlow sfEnd = new SequenceFlow(steps.get(task.getSteps().size() - 1), task.getEnd());
                task.getElement().appendChild(task.getEnd().getElement());
                task.getElement().appendChild(sfEnd.getElementSequenceFlow());
                task.getFlows().add(sfEnd);
                task.getEnd().setIncoming(sfEnd);
            }
        }

    }

    private void setEndTasks() {

        for(Task task : tasks){
            if(task.getAfter().size() == 0){
                task.setIsEndTask();
                task.getAfter().add(endEvent);
            }
        }

    }

    private void setAssociations() {

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            // add data input association
            if (i != 0) {
               for(BPMNElement element : task.getBefore()) {

                   Task tempTask = (Task) element;
                   DataInputAssociation tempInput = new DataInputAssociation();

                   tempInput.setInputAssociationSource(tempTask.getDataObject());
                   task.addDataInputAssociation(tempInput);
               }
            }

            // add data output association
            //task.setDataOutputAssociation(); erledigt im Konstruktor, maybe buggy
            task.getDataOutputAssociation().setOutputAssociationTarget(task.getDataObject());
        }

    }

    private void setTasks() {

        for (Task task : tasks) {
            this.elementFlowsProcess.appendChild(task.getElement());
        }

    }

    private void setFlows() {

        System.out.println("THIS: \n"  + flows);

        for(SequenceFlow flow : flows){
            elementFlowsProcess.appendChild(flow.getElementSequenceFlow());
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
            this.elementFlowsProcess.appendChild(task.getElement());

        }
    }

    private void setStartEvent() {

        StartEvent startEvent = new StartEvent();
        Element elementStartEvent = startEvent.getElement();

        this.startEvent = startEvent;
        this.elementFlowsProcess.appendChild(elementStartEvent);

    }

    private void setEndEvent() {

        EndEvent endEvent = new EndEvent();
        Element elementEndEvent = endEvent.getElement();

        this.endEvent = endEvent;
        this.elementFlowsProcess.appendChild(elementEndEvent);

    }

    private void addEndEventFlows() {

        for(Task task : tasks){

            if(task.getIsEndTask()){
                SequenceFlow endFlow = new SequenceFlow(task, endEvent);
                flows.add(endFlow);
            }

        }

    }

    public AbstractObjectType findObjectById(Double id, ArrayList<AbstractObjectType> objectTypeObjects) {

        return objectTypeObjects.stream().filter(obj -> obj != null && obj.getCreatedEntityId() != null && obj.getCreatedEntityId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public Task findTaskById(Double id) {

        for (Task task : this.tasks) {

            if (task.getCreatedEntityId().equals(id)) {
                return task;
            }

        }
        return null;
    }


    private void removeSequenceFlow(SequenceFlow sequenceFlow) {

        for (int i = 0; i < flows.size(); i++) {
            SequenceFlow sf = flows.get(i);
            if (sf.getId().equals(sequenceFlow.getId())) {
                flows.remove(i);
            }

        }

    }

    private void sortProcess(){

        for(SequenceFlow flow : flows){

            BPMNElement source = flow.getSourceRef();
            BPMNElement target = flow.getTargetRef();

            source.getAfter().add(target);
            target.getBefore().add(source);

        }

    }



/*
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

                loop.getFlows().add(new SequenceFlow(sourceTask, targetTask));

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

 */



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

    public HashSet<ExclusiveGateway> getGateways() {
        return gateways;
    }
}
