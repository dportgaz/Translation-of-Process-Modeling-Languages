package org.bpmn.bpmn_elements.collaboration.participant;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.flows_objects.AbstractObjectType;

import org.bpmn.process.FlowsProcessObject;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.steps.StepOne.allParticipants;

public class Object extends Participant {

    String id;
    Double key;
    String name;
    DataObject dataObject;
    FlowsProcessObject processRef;
    Element participantElement;

    // ArrayList<Task> tasks = new ArrayList<Task>();

    /*
    public ArrayList<Task> getTasks() {
        return tasks;
    }

     */


    public Object(Collaboration collaboration, Double key, String name) {

        super(collaboration, key, name);
        this.collaboration = collaboration;
        this.id = super.getId();
        this.key = key;
        this.name = name;
        this.participantElement = super.getParticipantElement();
        allParticipants.add(this);

    }

    public void setProcessRef(HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects, boolean adHoc) {
        this.processRef = new FlowsProcessObject(this, objectTypeObjects, adHoc);
        this.participantElement.setAttribute("processRef", this.processRef.getId());
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public String getId() {
        return this.id;
    }

    public Double getKey() {
        return key;
    }

    public FlowsProcessObject getProcessRef() {
        return this.processRef;
    }

    public String getName() {
        return this.name;
    }

    public Element getParticipantElement() {
        return this.participantElement;
    }

}