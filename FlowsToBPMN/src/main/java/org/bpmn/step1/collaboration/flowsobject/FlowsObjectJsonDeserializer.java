package org.bpmn.step1.collaboration.flowsobject;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class FlowsObjectJsonDeserializer implements JsonDeserializer<AbstractFlowsObject> {

	@Override
	public AbstractFlowsObject deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {

		String methodName = json.getAsJsonObject().get("MethodName").getAsString();

		switch (methodName) {
		case "CreateObjectType":
			return context.deserialize(json, FlowsObject.class);
		default:
			return null;
		}

	}

}
