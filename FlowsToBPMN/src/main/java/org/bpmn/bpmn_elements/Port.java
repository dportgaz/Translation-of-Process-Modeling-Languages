package org.bpmn.bpmn_elements;

import org.bpmn.bpmn_elements.task.Task;

public class Port {

    Double id;

    Double taskId;

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

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
