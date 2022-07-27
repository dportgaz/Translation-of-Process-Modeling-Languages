package org.bpmn.step1.collaboration.participant.flowsobjectname;

import java.util.ArrayList;

public class FlowsObjectName extends AbstractFlowsObjectName {

    public FlowsObjectName(ArrayList<String> Parameters_) {
        Parameters = Parameters_;
    }

    @Override
    public String toString() {
        return "Object { " + Parameters + " }";
    }

}