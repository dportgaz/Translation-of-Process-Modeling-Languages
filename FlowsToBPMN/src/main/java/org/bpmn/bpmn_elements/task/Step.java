package org.bpmn.bpmn_elements.task;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Participant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class Step extends Task {

    Task associatedTask;

    public Step(Double createdEntityId, String name, Participant participant, Task task) {

        super(createdEntityId, name, participant);
        this.associatedTask = task;

    }

}
