package org.bpmn.step1.process;


import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.process.event.StartEvent;
import org.bpmn.step1.process.flow.SequenceFlow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlowsProcess {

    String id;
    boolean isExecutable;
    StartEvent startEvent;

    public FlowsProcess(String id, boolean isExecutable) throws FileNotFoundException {
        this.id = id;
        this.isExecutable = isExecutable;
        this.startEvent = new StartEvent();
    }

    public StartEvent getStartEvent() {
        return this.startEvent;
    }

    public String getId(){
        return this.id;
    }

    public boolean getIsExecutable(){
        return this.isExecutable;
    }

    @Override
    public String toString(){
        return "Process: " + "Id= " + id + " isExecutable= " + isExecutable;
    }

}