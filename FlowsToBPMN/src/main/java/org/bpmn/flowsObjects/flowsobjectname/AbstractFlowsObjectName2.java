package org.bpmn.flowsObjects.flowsobjectname;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public abstract class AbstractFlowsObjectName2 {

    // protected String __type;
    // protected String Id;
    protected String MethodName;
    protected ArrayList<Object> Parameters;
    protected String CreatedEntityId;

    protected HashMap<String, ArrayList<AbstractFlowsObjectName>> objects;

    public String getMethodName() {
        return this.MethodName;
    }

    public ArrayList<Object> getParameters() {
        return this.Parameters;
    }

    public Object getObjectName() {
        return this.Parameters.get(0);
    }

    public String getCreatedEntityId() {
        return this.CreatedEntityId;
    }

   /* public HashMap<String, ArrayList<AbstractFlowsObjectName>> getObjects(String filename) throws FileNotFoundException {

        Gson gsonFlowsObjectNameJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObjectName.class, new FlowsObjectNameJsonDeserializer()).create();

        this.objects = gsonFlowsObjectNameJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectNameList.class);

        return this.objects;
    }*/

}
