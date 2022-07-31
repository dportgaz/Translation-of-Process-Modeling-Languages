package org.bpmn.step1.process;


import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.process.activity.Task;
import org.bpmn.step1.process.event.EndEvent;
import org.bpmn.step1.process.event.StartEvent;
import org.bpmn.step1.process.flow.SequenceFlow;
import org.bpmn.step1.process.gateway.Predicate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.bpmn.step1.collaboration.participant.FillFlowsParticipant.getParticipants;

public class FlowsProcess {

    String id;
    boolean isExecutable;
    StartEvent startEvent;

    ArrayList<EndEvent> endEvents = new ArrayList<>();

    ArrayList<Task> taskList = new ArrayList<>();

    ArrayList<SequenceFlow> sequenceFlowList = new ArrayList<>();

    ArrayList<Predicate> predicateList = new ArrayList<>();

    ArrayList<AbstractObjectType> predicateStepTypes = new ArrayList<>();


    public FlowsProcess(String id, boolean isExecutable) throws FileNotFoundException {
        this.id = "Process_" + id;
        this.isExecutable = isExecutable;
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

    public void removeTaskFromList(Task task) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getCreatedEntityId().equals(task.getCreatedEntityId())) {
                taskList.remove(i);
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
                System.out.println(sf);
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

    public boolean containsLoop(ObjectTypeMap objectMap, String key) throws FileNotFoundException {

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {
            if (obj != null && obj.getMethodName().equals("AddBackwardsTransitionType")) {
                return true;
            }
        }

        return false;
    }

    public SequenceFlow getFlowBySource(Task source) {

        for (SequenceFlow sf : getSequenceFlowList()) {

            if (sf.getTargetRef().equals(source.getId())) {
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