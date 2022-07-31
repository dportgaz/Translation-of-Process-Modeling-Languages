package org.bpmn.step1.process.event;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.process.flow.SequenceFlow;

import java.util.ArrayList;

public class EndEvent {

    String id;
    ArrayList<SequenceFlow> incomingFlows;
    public EndEvent() {
        this.id = "Event_" + RandomIdGenerator.generateRandomUniqueId(6);

    }
    public String getId(){
        return this.id;
    }
}
