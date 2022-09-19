package org.bpmn.flows_objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bpmn.flows_objects.flowsobject.AbstractFlowsObject;
import org.bpmn.flows_objects.flowsobject.FlowsObjectJsonDeserializer;
import org.bpmn.flows_objects.flowsobject.FlowsObjectList;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ConcreteObjectType extends AbstractObjectType {

    HashMap<Double, ArrayList<AbstractObjectType>> ObjectTypeActionLogs;
    HashMap<Double, ArrayList<AbstractObjectType>> CoordinationProcessTypeActionLogs;
    ConcreteObjectType allObjectTypes;
    HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects = new HashMap<>();
    HashMap<Double, ArrayList<AbstractObjectType>> userTypeObjects = new HashMap<>();

    @Override
    public String toString() {

        String retString = "";

        for (Double name : ObjectTypeActionLogs.keySet()) {
            String value = ObjectTypeActionLogs.get(name).toString();
            retString += name + "= {" + value + "}" + "\n";
        }

        return retString;
    }

    public ConcreteObjectType(String filename) throws FileNotFoundException {
        setAllObjects(filename);
        setObjectAndUserTypeObjectsSeparately(filename);
        setCoorinationProcess();
    }

    private void setCoorinationProcess() {
        this.CoordinationProcessTypeActionLogs = allObjectTypes.CoordinationProcessTypeActionLogs;
    }

    public void setAllObjects(String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectTypeJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractObjectType.class, new FlowsObjectTypeJsonDeserializer()).create();
        allObjectTypes = gsonFlowsObjectTypeJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), ConcreteObjectType.class);

    }

    public void setObjectAndUserTypeObjectsSeparately(String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObject.class, new FlowsObjectJsonDeserializer()).create();

        FlowsObjectList objectTypeObjectsIdList = gsonFlowsObjectJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectList.class);

        objectTypeObjectsIdList.getList().removeAll(Collections.singleton(null));

        ArrayList<Double> nameIdList = new ArrayList<>();

        for (int i = 0; i < objectTypeObjectsIdList.getList().size(); i++) {
            nameIdList.add(objectTypeObjectsIdList.getList().get(i).getCreatedActorId());
        }

        for (Double key : this.getAllObjects(filename).keySet()) {
            if (nameIdList.contains(key)) {
                objectTypeObjects.put(key, this.getAllObjects(filename).get(key));
            } else {
                userTypeObjects.put(key, this.getAllObjects(filename).get(key));
            }
        }
    }

    public HashMap<Double, ArrayList<AbstractObjectType>> getAllObjects(String filename) throws FileNotFoundException {
        return allObjectTypes.ObjectTypeActionLogs;
    }

    public HashMap<Double, ArrayList<AbstractObjectType>> getObjectTypeObjects() throws FileNotFoundException {
        return objectTypeObjects;
    }

    public HashMap<Double, ArrayList<AbstractObjectType>> getCoordinationProcessTypeActionLogs() {
        return CoordinationProcessTypeActionLogs;
    }

    public HashMap<Double, ArrayList<AbstractObjectType>> getUserTypeObjects() throws FileNotFoundException {
        return userTypeObjects;
    }

}