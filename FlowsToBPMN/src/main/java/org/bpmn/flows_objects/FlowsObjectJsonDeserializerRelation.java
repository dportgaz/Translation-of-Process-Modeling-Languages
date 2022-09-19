package org.bpmn.flows_objects;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.bpmn.bpmn_elements.RelationType;
import org.bpmn.flows_objects.flowsobject.AbstractFlowsObject;
import org.bpmn.flows_objects.flowsobject.FlowsObject;

public class FlowsObjectJsonDeserializerRelation implements JsonDeserializer<AbstractRelation> {

    @Override
    public AbstractRelation deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {

        String methodName = json.getAsJsonObject().get("MethodName").getAsString();

        switch (methodName) {
            case "CreateRelationType":
                return context.deserialize(json, RelationTypeData.class);
            default:
                return null;
        }

    }

}
