package org.bpmn.step_one.process;


import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.flowsObjects.ConcreteObjectType;
import org.bpmn.step_one.process.activity.Task;
import org.bpmn.step_one.process.dataobject.DataObject;
import org.bpmn.step_one.process.event.EndEvent;
import org.bpmn.step_one.process.event.StartEvent;
import org.bpmn.step_one.process.flow.SequenceFlow;
import org.bpmn.step_one.process.gateway.ExclusiveGateway;
import org.bpmn.step_one.process.gateway.Predicate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FlowsProcess {

    String id;
    boolean isExecutable;
    StartEvent startEvent;

    ArrayList<EndEvent> endEvents = new ArrayList<>();

    ArrayList<Task> taskList = new ArrayList<>();

    ArrayList<SequenceFlow> sequenceFlowList = new ArrayList<>();

    ArrayList<Predicate> predicateList = new ArrayList<>();

    ArrayList<AbstractObjectType> predicateStepTypes = new ArrayList<>();

    ArrayList<Task> endTasks = new ArrayList<>();

    HashSet<ExclusiveGateway> gateways = new HashSet<>();

    HashMap<String, DataObject> dataObjects = new HashMap();

    EndEvent endEvent;

    HashMap<String, ArrayList<String>> decisionTasks = new HashMap<>();

    HashMap<String, SequenceFlow> decisionFlows = new HashMap<>();

    public FlowsProcess(String id, boolean isExecutable) throws FileNotFoundException {
        this.id = id;
        this.isExecutable = isExecutable;
    }

    public HashMap<String, SequenceFlow> getDecisionFlows() {
        return decisionFlows;
    }

    public HashMap<String, ArrayList<String>> getDecisionTasks() {
        return decisionTasks;
    }

    public HashMap<String, DataObject> getDataObjects() {
        return dataObjects;
    }

    public HashSet<ExclusiveGateway> getGateways() {
        return gateways;
    }

    public void setGateways(HashSet<ExclusiveGateway> gateways) {
        this.gateways = gateways;
    }

    public EndEvent getEndEvent() {
        return endEvent;
    }

    public void setEndEvent(EndEvent endEvent) {
        this.endEvent = endEvent;
    }

    public ArrayList<Task> getEndTasks() {
        return endTasks;
    }

    public void addTask(Task task) {

        this.taskList.add(task);

    }

    public void addEndEvent(EndEvent endEvent) {

        this.endEvents.add(endEvent);

    }

    public void addPredicate(Predicate predicate) {
        predicateList.add(predicate);
    }

    public void addPredicateStepType(AbstractObjectType o) {
        if (!predicateStepTypes.contains(o)) {
            this.predicateStepTypes.add(o);
        }
    }

    public boolean containsTask(Task task) {
        for (Task t : taskList) {
            if (t.getCreatedEntityId().equals(task.getCreatedEntityId())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsFlow(SequenceFlow flow) {
        for (SequenceFlow sf : sequenceFlowList) {
            if (sf.getSourceRef().equals(flow.getSourceRef()) && sf.getTargetRef().equals(flow.getTargetRef())) {
                return true;
            }
        }
        return false;
    }

    public void removeTaskFromList(Task task) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getCreatedEntityId().equals(task.getCreatedEntityId())) {
                taskList.remove(i);
            }
        }
    }

    public void removeFlowFromList(SequenceFlow flow) {
        for (int i = 0; i < sequenceFlowList.size(); i++) {
            if (sequenceFlowList.get(i).getSourceRef().equals(flow.getSourceRef()) && sequenceFlowList.get(i).getTargetRef().equals(flow.getTargetRef())) {
                sequenceFlowList.remove(i);
            }
        }
    }

    public void setPredicateStepTypes(ArrayList<AbstractObjectType> predicateStepTypes) {
        this.predicateStepTypes = predicateStepTypes;
    }

    public ArrayList<AbstractObjectType> getPredicateStepTypes() {
        return this.predicateStepTypes;
    }

    public ArrayList<Predicate> getPredicateList() {
        return this.predicateList;
    }

    public void addSequenceFlow(SequenceFlow sequenceFlow) {

        this.sequenceFlowList.add(sequenceFlow);

    }

    public void removeSequenzeFlow(SequenceFlow sequenceFlow) {

        for (int i = 0; i < getSequenceFlowList().size(); i++) {
            SequenceFlow sf = getSequenceFlowList().get(i);
            if (sf.getId().equals(sequenceFlow.getId())) {
                this.sequenceFlowList.remove(i);
            }

        }

    }

    public void removeSequenzeFlowBySource(SequenceFlow sequenceFlow) {

        for (int i = 0; i < getSequenceFlowList().size(); i++) {
            SequenceFlow sf = getSequenceFlowList().get(i);
            if (sf.getSourceRef().equals(sequenceFlow.getSourceRef())) {
                this.sequenceFlowList.remove(i);
            }

        }

    }

    public void setStartEvent(StartEvent startEvent) {
        this.startEvent = startEvent;
    }

    public StartEvent getStartEvent() {
        return this.startEvent;
    }

    public String getId() {
        return this.id;
    }

    public boolean getIsExecutable() {
        return this.isExecutable;
    }

    public ArrayList<Task> getTaskList() {
        return this.taskList;
    }

    public ArrayList<SequenceFlow> getSequenceFlowList() {
        return sequenceFlowList;
    }

    public boolean containsLoop(ConcreteObjectType objectMap, String key) throws FileNotFoundException {

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {
            if (obj != null && obj.getMethodName().equals("AddBackwardsTransitionType")) {
                return true;
            }
        }

        return false;
    }

    public SequenceFlow getFlowBySource(String source) {

        for (SequenceFlow sf : getSequenceFlowList()) {

            if (sf.getTargetRef().equals(source)) {
                return sf;
            }

        }

        return null;

    }

    public SequenceFlow getFlowBySource(Task source) {

        for (SequenceFlow sf : getSequenceFlowList()) {

            if (sf.getTargetRef().equals(source.getId())) {
                return sf;
            }

        }

        return null;

    }

    public SequenceFlow getFlowByTarget(String target) {

        for (SequenceFlow sf : getSequenceFlowList()) {

            if (sf.getSourceRef().equals(target)) {
                return sf;
            }

        }

        return null;

    }

    public SequenceFlow getFlowByTarget(Task target) {

        for (SequenceFlow sf : getSequenceFlowList()) {

            if (sf.getSourceRef().equals(target.getId())) {
                return sf;
            }

        }

        return null;

    }

    @Override
    public String toString() {
        return "Process: " + "Id= " + id + " isExecutable= " + isExecutable;
    }

}