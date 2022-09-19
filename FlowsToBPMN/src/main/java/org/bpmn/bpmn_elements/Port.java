package org.bpmn.bpmn_elements;

import org.bpmn.bpmn_elements.task.Task;

import java.util.ArrayList;

public class Port {

    Double id;
    Double taskId;
    ArrayList<Relation> incoming = new ArrayList<>();

    public Port(Double id, Double taskId){
        this.id = id;
        this.taskId = taskId;
    }

    public Double getId() {
        return id;
    }

    public void setTaskId(Double taskId) {
        this.taskId = taskId;
    }

    public Double getTaskId() {
        return taskId;
    }

    public ArrayList<Relation> getIncoming() {
        return incoming;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
