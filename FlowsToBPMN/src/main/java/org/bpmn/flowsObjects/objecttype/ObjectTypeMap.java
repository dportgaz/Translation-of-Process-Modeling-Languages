package org.bpmn.flowsObjects.objecttype;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bpmn.step1.collaboration.participants.flowsobject.AbstractFlowsObject;
import org.bpmn.step1.collaboration.participants.flowsobject.FlowsObjectJsonDeserializer;
import org.bpmn.step1.collaboration.participants.flowsobject.FlowsObjectList;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ObjectTypeMap extends AbstractObjectType {

    public HashMap<String, ArrayList<AbstractObjectType>> ObjectTypeActionLogs;
    static ObjectTypeMap allObjectTypesMap;
    static HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjectsMap = new HashMap<>();

    static HashMap<String, ArrayList<AbstractObjectType>> userTypeObjectsMap = new HashMap<>();

    @Override
    public String toString() {

        String retString = "";

        for (String name : ObjectTypeActionLogs.keySet()) {
            String key = name;
            String value = ObjectTypeActionLogs.get(name).toString();
            retString += key + "= {" + value + "}" + "\n";
        }

        return retString;
    }

    public ObjectTypeMap(String filename) throws FileNotFoundException {
        setAllObjects(filename);
        setObjectAndUserTypeObjects(filename);
    }

    public void setAllObjects(String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectTypeJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractObjectType.class, new FlowsObjectTypeJsonDeserializer()).create();

        allObjectTypesMap = gsonFlowsObjectTypeJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), ObjectTypeMap.class);

    }

    public void setObjectAndUserTypeObjects(String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObject.class, new FlowsObjectJsonDeserializer()).create();

        FlowsObjectList objectTypeObjectsIdList = gsonFlowsObjectJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectList.class);

        objectTypeObjectsIdList.getList().removeAll(Collections.singleton(null));

        ArrayList<String> nameIdList = new ArrayList<>();

        for(int i = 0; i < objectTypeObjectsIdList.getList().size(); i++) {
            nameIdList.add(objectTypeObjectsIdList.getList().get(i).getCreatedActorId());
        }

        for(String key : this.getAllObjects(filename).keySet()){
                if (nameIdList.contains(key)) {
                    objectTypeObjectsMap.put(key, this.getAllObjects(filename).get(key));
                }else {
                    userTypeObjectsMap.put(key, this.getAllObjects(filename).get(key));
                }
            }
        }

    public HashMap<String, ArrayList<AbstractObjectType>> getAllObjects(String filename) throws FileNotFoundException {
        return allObjectTypesMap.ObjectTypeActionLogs;
    }

    public HashMap<String, ArrayList<AbstractObjectType>> getObjectTypeObjects(String filename) throws FileNotFoundException {
        return objectTypeObjectsMap;
    }

    public HashMap<String, ArrayList<AbstractObjectType>> getUserTypeObjects(String filename) throws FileNotFoundException {
        return userTypeObjectsMap;
    }



}
