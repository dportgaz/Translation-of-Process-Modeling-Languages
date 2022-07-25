package org.bpmn.flowsObjects.flowsobjectname;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class FlowsObjectNameJsonDeserializer2 implements JsonDeserializer<AbstractFlowsObjectName> {

    @Override
    public AbstractFlowsObjectName deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                               JsonDeserializationContext context) throws JsonParseException {

        String methodName = json.getAsJsonObject().get("MethodName").getAsString();

        switch (methodName) {
            case "UpdateName":
                return context.deserialize(json, FlowsObjectName.class);
            default:
                return null;
        }
    }

}
