package org.bpmn.bpmn_elements;

import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.task.Task;

public class Relation {

    Task task;

    Participant source;

    Participant target;
    RelationType relationType;

    public Relation(Participant source, Participant target) {
        this.source = source;
        this.target = target;
    }

    public Relation(Task task, RelationType relationType) {
        this.task = task;
        this.relationType = relationType;
    }

    public Relation(Task task) {
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
        if (task != null && relationType != null) {
            return "(" + task + " , " + relationType + ")";
        }else if(source != null && target != null){
            return "(" + source.getName() + " , " + target.getName() + ")";
        }
        return null;
    }
}
