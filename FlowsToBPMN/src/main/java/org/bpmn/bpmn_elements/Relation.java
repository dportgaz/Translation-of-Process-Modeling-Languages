package org.bpmn.bpmn_elements;

import org.bpmn.bpmn_elements.task.Task;

public class Relation {

    Task task;
    RelationType relationType;

    public Relation(Task task, RelationType relationType){
        this.task = task;
        this.relationType = relationType;
    }

    public Relation(Task task){
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public RelationType getRelationType() {
        return relationType;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setRelationType(RelationType relationType) {
        this.relationType = relationType;
    }

    @Override
    public String toString() {
        return task + " " + relationType;
    }
}
