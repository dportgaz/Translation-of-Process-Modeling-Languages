package org.bpmn.flows_process_model;

import org.bpmn.flows_entities.AbstractRelationship;
import org.bpmn.flows_entities.DeserializeFlowsEntity;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class DataModel {
    private ArrayList<AbstractRelationship> dataModel;
    private ObjectTypes objectTypes;
    private UserTypes userTypes;
    public DataModel(String flowsJSON) throws FileNotFoundException {

        DeserializeFlowsEntity objects = new DeserializeFlowsEntity(flowsJSON);
        this.dataModel = objects.getRelationships().getList();
        this.objectTypes = new ObjectTypes(flowsJSON);
        this.userTypes = new UserTypes(flowsJSON);

    }

    public ArrayList<AbstractRelationship> getEntities() {
        return dataModel;
    }

    public ObjectTypes getObjectTypes() {
        return objectTypes;
    }

    public UserTypes getUserTypes() {
        return userTypes;
    }
}
