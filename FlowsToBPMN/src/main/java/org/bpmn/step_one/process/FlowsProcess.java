package org.bpmn.step_one.process;

import com.google.gson.internal.LinkedTreeMap;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Participant;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class FlowsProcess {

    static int countProcess = 0;

    String id;

    static String isExecutable = "true";

    Element elementFlowsProcess;

    Participant participant;
    StartEvent startEvent;

    HashSet<Task> tasks = new HashSet<>();

    HashSet<DataObject> dataobjects = new HashSet<>();


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

                Task task = new Task(createdEntityId, taskName, participant);

                if (this.tasks.size() > 0) {
                    task.setDataInputAssociation();
                }
                task.setDataOutputAssociation();
                if (this.tasks.contains(task)) {
                    tasks.remove(task);
                }
                tasks.add(task);
                // allTasks.add(task);

                this.dataobjects.add(task.getDataObject());
            }

        });

        addSteps(objectTypeObjects);

    }

    private void addSteps(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        String participantKey = this.participant.getKey();
        objectTypeObjects.get(participantKey).forEach(obj -> {

            for (Task task : this.tasks) {
                if (obj != null && obj.getMethodName().equals("AddStepType")) {
                    Double tempId = (Double) obj.getParameters().get(0);
                    if (task.getCreatedEntityId().equals(tempId)) {
                        // trim steps by removing default steps
                        objectTypeObjects.get(participantKey).forEach(obj2 -> {
                            if (obj2 != null
                                    && obj2.getMethodName().equals("UpdateStepAttributeType")
                                    && obj2.getParameters().get(0).equals(obj.getCreatedEntityId())
                                    && !stepIsPredicate(objectTypeObjects)) {
                                task.getSteps().add(obj);

                            }
                        });
                    }
                }
            }
        });
    }

    private boolean stepIsPredicate(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        for (AbstractObjectType obj : objectTypeObjects.get(this.participant.getKey())) {
            if (obj != null && obj.getMethodName().equals("UpdatePredicateStepTypeExpression")) {
                LinkedTreeMap link = (LinkedTreeMap) obj.getParameters().get(1);
                LinkedTreeMap innerLink = (LinkedTreeMap) link.get("Left");
                if (innerLink.get("AttributeTypeId").equals(id)) {
                    return true;
                }
            }
        }
        return false;
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

    public HashSet<DataObject> getDataobjects() {
        return dataobjects;
    }

    public HashSet<Task> getTasks() {
        return tasks;
    }

    public String getId() {
        return this.id;
    }
}
