package org.bpmn.flowsObjects;

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

}
