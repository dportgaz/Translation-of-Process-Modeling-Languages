package org.bpmn.bpmn_elements.dataobject;

import org.bpmn.bpmn_elements.collaboration.participant.Object;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;


import java.util.ArrayList;

import static org.bpmn.steps.BPMN.doc;

public class DataObject {

    String id;

    String refId;

    Task associatedTask;

    String name;

    String x;

    String y;

    ArrayList<String> states = new ArrayList<>();

    Element elementDataObject;

    Object object;

    public void setX(String x) {
        this.x = x;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public DataObject(Task task) {
        this.id = "DataObject_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.refId = "DataObjectReference_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.associatedTask = task;
        this.elementDataObject = doc.createElement("bpmn:dataObjectReference");
        setElementDataObject();
    }

    public DataObject(Participant participant) {
        this.id = "DataObject_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.refId = "DataObjectReference_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementDataObject = doc.createElement("bpmn:dataObjectReference");
        setElementDataObject(participant);
    }

    public ArrayList<String> getStates() {
        return states;
    }

    private void setElementDataObject(Participant participant) {

        this.elementDataObject.setAttribute("dataObjectRef", this.id);
        this.elementDataObject.setAttribute("id", this.refId);
        this.elementDataObject.setAttribute("name", participant.getName() + " [Finished]");

    }

    private void setElementDataObject() {

        this.elementDataObject.setAttribute("dataObjectRef", this.id);
        this.elementDataObject.setAttribute("id", this.refId);

        String obj = associatedTask.getParticipant().getName();
        String temp = associatedTask.getName().replaceAll(obj, "");
        String state = temp.substring(0, temp.length()-1);

        this.elementDataObject.setAttribute("name", obj + " " + "[" + state + "]");

    }

    public Element getElementDataObject() {
        return elementDataObject;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return this.id;
    }

    public String getRefId() {
        return refId;
    }

    public void setAssociatedTask(Task associatedTask) {
        this.associatedTask = associatedTask;
    }

    public Task getAssociatedTask() {
        return associatedTask;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
