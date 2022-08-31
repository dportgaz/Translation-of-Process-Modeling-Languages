package org.bpmn.step_one.process;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Participant;
import org.w3c.dom.Element;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.bpmn_elements.gateway.Predicate.getPredicate;
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

    HashSet<Predicate> predicates = new HashSet<>();


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
            dataobjects.add(dObj);

            System.out.println("HERE: " + dObj);
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

                Predicate predicate = getPredicate(obj.getCreatedEntityId(), objectTypeObjects.get(this.participant.getKey()));

                predicate.setCreatedEntityId(obj.getCreatedEntityId());
                this.predicates.add(predicate);

            }
        });
        System.out.println("PREDICATELIST: " + this.predicates);
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
