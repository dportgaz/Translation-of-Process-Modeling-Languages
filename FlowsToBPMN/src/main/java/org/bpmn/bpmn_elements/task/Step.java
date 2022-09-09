package org.bpmn.bpmn_elements.task;

import org.bpmn.bpmn_elements.collaboration.participant.Participant;

public class Step extends Task {

    Task associatedTask;

    public Step(Double createdEntityId, String name, Participant participant, Task task) {

        super(createdEntityId, name, participant);
        this.associatedTask = task;

    }

}
