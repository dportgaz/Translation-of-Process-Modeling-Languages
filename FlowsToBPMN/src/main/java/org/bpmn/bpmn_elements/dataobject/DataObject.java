package org.bpmn.bpmn_elements.dataobject;

import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.randomidgenerator.RandomIdGenerator;

public class DataObject {

    String id;

    String refId;

    Task associatedTask;

    String name;

    String x;

    String y;

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
