package org.bpmn.flows_entities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bpmn.flows_entities.flows_entity.AbstractFlowsObject;
import org.bpmn.flows_entities.flows_entity.FlowsObjectJsonDeserializer;
import org.bpmn.flows_entities.flows_entity.DataModelActionLogs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DeserializeFlowsEntity extends AbstractFlowsEntity {

    private DeserializeFlowsEntity flowsEntity;
    private HashMap<Double, ArrayList<AbstractFlowsEntity>> CoordinationProcessTypeActionLogs;
    private HashMap<Double, ArrayList<AbstractFlowsEntity>> ObjectTypeActionLogs;
    private Relationships relationships;
    private HashMap<Double, ArrayList<AbstractFlowsEntity>> objectTypes = new HashMap<>();
    private HashMap<Double, ArrayList<AbstractFlowsEntity>> userTypes = new HashMap<>();

    public DeserializeFlowsEntity(String file) throws FileNotFoundException {

        deserializeObjectTypeActionLogs(file);
        deserializeCoordinationProcesses();
        deserializeDataModel(file);
    }

    private void deserializeObjectTypeActionLogs(String file) throws FileNotFoundException {
        // deserializes JSON objects where MethodName starts with "Add" or "Update"
        Gson gsonFlowsObjectTypeJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsEntity.class, new FlowsEntitiesDeserializer()).create();
        // initializes Java objects for each deserialized JSON object
        flowsEntity = gsonFlowsObjectTypeJsonDeserializer.fromJson(new JsonReader(new FileReader(file)), DeserializeFlowsEntity.class);
        // stores each Java object in data structure
        ObjectTypeActionLogs = flowsEntity.ObjectTypeActionLogs;
    }

    private void deserializeCoordinationProcesses() {
        CoordinationProcessTypeActionLogs = flowsEntity.CoordinationProcessTypeActionLogs;
    }

    private void deserializeDataModel(String file) throws FileNotFoundException {

        // deserializes object types in data model
        Gson gsonFlowsObjectJsonDeserializer = new GsonBuilder().
                registerTypeAdapter(AbstractFlowsObject.class, new FlowsObjectJsonDeserializer()).create();
        DataModelActionLogs dataModelActionLogs = gsonFlowsObjectJsonDeserializer.
                fromJson(new JsonReader(new FileReader(file)), DataModelActionLogs.class);
        dataModelActionLogs.getDataModel().removeAll(Collections.singleton(null));

        // deserializes only relationships in data model
        Gson gsonFlowsObjectJsonDeserializerRelation = new GsonBuilder().
                registerTypeAdapter(AbstractRelationship.class, new FlowsObjectJsonDeserializerRelation()).create();
        relationships = gsonFlowsObjectJsonDeserializerRelation.
                fromJson(new JsonReader(new FileReader(file)), Relationships.class);
        relationships.getList().removeAll(Collections.singleton(null));

        // distinguishes object and user types to store in separate data structures
        for (Double createdActorIdType : ObjectTypeActionLogs.keySet()) {
            for(AbstractFlowsObject abstractFlowsObject : dataModelActionLogs.getDataModel()) {
                Double createdActorIdObjectType = abstractFlowsObject.getCreatedActorId();

                if (createdActorIdType.equals(createdActorIdObjectType)) {
                    objectTypes.put(createdActorIdType, ObjectTypeActionLogs.get(createdActorIdType));
                } else {
                    userTypes.put(createdActorIdType, ObjectTypeActionLogs.get(createdActorIdType));
                }
            }
        }
    }

    public Relationships getRelationships() {
        return relationships;
    }

    public HashMap<Double, ArrayList<AbstractFlowsEntity>> getObjectTypes() {
        return objectTypes;
    }

    public HashMap<Double, ArrayList<AbstractFlowsEntity>> getCoordinationProcessTypeActionLogs() {
        return CoordinationProcessTypeActionLogs;
    }

    public HashMap<Double, ArrayList<AbstractFlowsEntity>> getUserTypes() {
        return userTypes;
    }

}