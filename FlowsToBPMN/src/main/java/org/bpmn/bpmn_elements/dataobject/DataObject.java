package org.bpmn.bpmn_elements.dataobject;

import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.collaboration.participant.Pool;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;


import java.util.ArrayList;
import java.util.HashSet;

import static org.bpmn.transformation.FlowsToBpmn.doc;
import static org.bpmn.transformation.LifecycleTransformation.allDataObjects;

public class DataObject {

    String id;

    String refId;

    Task associatedTask;

    String name;

    Double x;

    Double y;

    ArrayList<String> states = new ArrayList<>();

    Element elementDataObject;

    Element elementDataObjectSingle;

    Pool pool;

    HashSet<DataInputAssociation> dataInputAssociations = new HashSet<>();

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public DataObject(Task task) {
        this.id = "DataObject_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.refId = "DataObjectReference_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.associatedTask = task;
        this.elementDataObject = doc.createElement("bpmn:dataObjectReference");
        allDataObjects.add(this);
        setElementDataObject();
        setElementDataObjectSingle();
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

    public HashSet<DataInputAssociation> getDataInputAssociations() {
        return dataInputAssociations;
    }

    private void setElementDataObject(Participant participant) {

        this.elementDataObject.setAttribute("dataObjectRef", this.id);
        this.elementDataObject.setAttribute("id", this.refId);
        this.elementDataObject.setAttribute("name", participant.getName() + " [Finished]");

    }

    public DataObject(Task task, String name) {
        this.id = "DataObject_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.refId = "DataObjectReference_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementDataObject = doc.createElement("bpmn:dataObjectReference");
        this.associatedTask = task;
        allDataObjects.add(this);
        setElementDataObjectForStep(name);
        setElementDataObjectForStepSingle();
    }

    private void setElementDataObject() {

        this.elementDataObject.setAttribute("dataObjectRef", this.id);
        this.elementDataObject.setAttribute("id", this.refId);

        String obj = associatedTask.getParticipant().getName();
        this.elementDataObject.setAttribute("name", obj + " " + "[" + associatedTask.getStateName() + "]");
    }

    private void setElementDataObjectSingle() {

        this.elementDataObjectSingle = doc.createElement("bpmn:dataObject");
        this.elementDataObjectSingle.setAttribute("id", this.id);
        this.elementDataObjectSingle.setAttribute("isCollection", "true");

    }

    private void setElementDataObjectForStepSingle() {

        this.elementDataObjectSingle = doc.createElement("bpmn:dataObject");
        this.elementDataObjectSingle.setAttribute("id", this.id);

    }

    public Element getElementDataObjectSingle() {
        return elementDataObjectSingle;
    }

    private void setElementDataObjectForStep(String name) {

        this.elementDataObject.setAttribute("dataObjectRef", this.id);
        this.elementDataObject.setAttribute("id", this.refId);
        this.elementDataObject.setAttribute("name", associatedTask.getParticipant().getName() + ".\n" + name);

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

    public Task getAssociatedTask() {
        return associatedTask;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
