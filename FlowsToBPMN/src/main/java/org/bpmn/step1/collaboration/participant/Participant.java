package org.bpmn.step1.collaboration.participant;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.collaboration.participant.flowsobject.AbstractFlowsObject;
import org.bpmn.step1.collaboration.participant.flowsobject.FlowsObjectJsonDeserializer;
import org.bpmn.step1.collaboration.participant.flowsobject.FlowsObjectList;
import org.bpmn.step1.collaboration.participant.flowsobjectname.AbstractFlowsObjectName;
import org.bpmn.step1.collaboration.participant.flowsobjectname.FlowsObjectNameJsonDeserializer;
import org.bpmn.step1.collaboration.participant.flowsobjectname.FlowsObjectNameList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;


import java.util.UUID;

public class Participant {

    String participantID;
    String processRef;
    String name;

    public void setParticipantID() {
        this.participantID = RandomIdGenerator.generateRandomUniqueId(6);
    }

    public void setProcessRef() {
        this.processRef = RandomIdGenerator.generateRandomUniqueId(6);
    }

    public void setProcessRef(String name) {

        this.name = name;
    }

    public String getParticipantID() {
        return this.participantID;
    }

    public String getProcessRef() {
        return this.processRef;
    }

    public String getName() {
        return this.name;
    }

    public Participant(String name) {
        this.participantID = RandomIdGenerator.generateRandomUniqueId(6);
        this.processRef = RandomIdGenerator.generateRandomUniqueId(6);
        this.name = name;
    }
}