package org.bpmn.flows_entities;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlowsEntitiesDeserializer implements JsonDeserializer<AbstractFlowsEntity> {
    @Override
    public AbstractFlowsEntity deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {

        String methodName = json.getAsJsonObject().get("MethodName").getAsString();

        Pattern addPattern = Pattern.compile("Add*");
        Matcher addMatcher = addPattern.matcher(methodName);

        Pattern updatePattern = Pattern.compile("Update*");
        Matcher updateMatcher = updatePattern.matcher(methodName);

        if (addMatcher.find()) {
            return context.deserialize(json, CreatedEntity.class);
        } else if(updateMatcher.find()) {
            return context.deserialize(json, UpdatedEntity.class);
        }
        else {
            return null;
        }
    }
}
