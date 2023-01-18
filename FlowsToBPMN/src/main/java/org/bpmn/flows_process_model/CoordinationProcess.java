package org.bpmn.flows_process_model;

import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.flows_entities.AbstractRelationship;
import org.bpmn.flows_entities.DeserializeFlowsEntity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class CoordinationProcess {
    private HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcess;
    public CoordinationProcess(String flowsJSON) throws FileNotFoundException {

        DeserializeFlowsEntity objects = new DeserializeFlowsEntity(flowsJSON);
        this.coordinationProcess = objects.getCoordinationProcessTypeActionLogs();

    }

    public HashMap<Double, ArrayList<AbstractFlowsEntity>> getEntities() {
        return coordinationProcess;
    }
}
