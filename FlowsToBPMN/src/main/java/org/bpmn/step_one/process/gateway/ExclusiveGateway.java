package org.bpmn.step_one.process.gateway;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.process.flow.SequenceFlow;

import java.util.ArrayList;

public class ExclusiveGateway {

    String id;

    ArrayList<SequenceFlow> incomings = new ArrayList<>();

    ArrayList<SequenceFlow> outgoings = new ArrayList<>();

    public ExclusiveGateway() {
        this.id = "Gateway_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public String getId() {
        return id;
    }

    public void addIncoming(SequenceFlow incoming) {
        incomings.add(incoming);
    }

    public void addOutgoing(SequenceFlow outgoing) {
        outgoings.add(outgoing);
    }
}
