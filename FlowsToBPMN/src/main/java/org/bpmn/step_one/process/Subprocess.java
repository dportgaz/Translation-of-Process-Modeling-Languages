package org.bpmn.step_one.process;

import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.task.Step;
import org.bpmn.bpmn_elements.task.Task;

import javax.swing.text.Element;
import java.util.HashSet;

public class Subprocess {

    StartEvent startEvent;

    EndEvent endEvent;

    HashSet<Step> steps;


    public Subprocess(HashSet<Step> steps, Element elementSubprocess) {

        this.steps = steps;
        this.startEvent = new StartEvent();
        this.endEvent = new EndEvent();

    }

}
