package org.bpmn.bpmn_elements.task;

import org.bpmn.step_one.collaboration.participant.ParticipantObject;

public class Step extends Task {

    Task associatedTask;

    public Step(Double createdEntityId, String name, ParticipantObject participant, Task task) {

        super(createdEntityId, name, participant);
        this.associatedTask = task;

    }

}
