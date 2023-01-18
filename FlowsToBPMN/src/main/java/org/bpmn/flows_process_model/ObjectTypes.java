package org.bpmn.flows_process_model;

import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.flows_entities.AbstractRelationship;
import org.bpmn.flows_entities.DeserializeFlowsEntity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectTypes {

    private HashMap<Double, ArrayList<AbstractFlowsEntity>> objectTypes;
    public ObjectTypes(String flowsJSON) throws FileNotFoundException {

        DeserializeFlowsEntity objects = new DeserializeFlowsEntity(flowsJSON);
        this.objectTypes = objects.getObjectTypes();

    }

    public HashMap<Double, ArrayList<AbstractFlowsEntity>> getEntities() {
        return objectTypes;
    }
}
