package org.bpmn.step1.process.dataobject;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.process.activity.Task;

public class DataObject {

    String id;

    String refId;

    Task associatedTask;

    String name;

    public DataObject() {
        this.id = "DataObject_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.refId = "DataObjectReference_" + RandomIdGenerator.generateRandomUniqueId(6);
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

}
