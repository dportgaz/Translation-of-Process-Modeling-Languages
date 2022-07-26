package org.bpmn.flowsObjects.objecttype;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlowsObjectTypeJsonDeserializer implements JsonDeserializer<AbstractObjectType> {

    @Override
    public AbstractObjectType deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                          JsonDeserializationContext context) throws JsonParseException {

        String methodName = json.getAsJsonObject().get("MethodName").getAsString();

        Pattern addPattern = Pattern.compile("Add*");
        Matcher addMatcher = addPattern.matcher(methodName);

        Pattern updatePattern = Pattern.compile("Update*");
        Matcher updateMatcher = updatePattern.matcher(methodName);

        if (addMatcher.find()) {
            return context.deserialize(json, ObjectTypeCreateEntity.class);
        } else if(updateMatcher.find()) {
            return context.deserialize(json, ObjectTypeUpdateEntity.class);
        }
        else {
            return null;
        }
    }
}
