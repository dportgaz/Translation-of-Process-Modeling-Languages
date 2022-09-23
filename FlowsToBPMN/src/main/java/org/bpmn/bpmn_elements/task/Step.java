package org.bpmn.bpmn_elements.task;

import org.bpmn.bpmn_elements.collaboration.participant.Participant;

public class Step extends Task {

    Task associatedTask;

    public Step(Double createdEntityId, String name, Participant participant, Task task, boolean computationStep) {

        super(createdEntityId, name, participant, computationStep);
        this.associatedTask = task;

    }

}
