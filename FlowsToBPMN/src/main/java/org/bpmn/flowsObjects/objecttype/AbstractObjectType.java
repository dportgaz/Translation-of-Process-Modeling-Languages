package org.bpmn.flowsObjects.objecttype;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractObjectType {

    // protected String __type;
    // protected String Id;
    protected String MethodName;
    protected ArrayList<Object> Parameters;
    protected Double CreatedEntityId;
    protected Double UpdatedEntityId;

    protected HashMap<String, ArrayList<AbstractObjectType>> objects;

    public String getMethodName() {
        return this.MethodName;
    }

    public ArrayList<Object> getParameters() {
        return this.Parameters;
    }

    public Object getObjectName() {
        return this.Parameters.get(0);
    }

    public Double getCreatedEntityId() {
        return this.CreatedEntityId;
    }

    public Double getUpdatedEntityId() {
        return this.UpdatedEntityId;
    }

    public static ObjectTypeMap getObjects(String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectTypeJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractObjectType.class, new FlowsObjectTypeJsonDeserializer()).create();

        ObjectTypeMap flowsObjects3 = gsonFlowsObjectTypeJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), ObjectTypeMap.class);

        return flowsObjects3;
    }

}
