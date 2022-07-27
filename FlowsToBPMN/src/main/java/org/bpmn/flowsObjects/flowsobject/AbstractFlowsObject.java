package org.bpmn.flowsObjects.flowsobject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public abstract class AbstractFlowsObject {

	protected String CreatedActorId;

	static FlowsObjectList flowsObjectIdList;

	public String getCreatedActorId() {
		return this.CreatedActorId;
	}

	/*
	public FlowsObjectList createFlowsObjectIdList(String filename) throws FileNotFoundException {

		Gson gsonFlowsObjectJsonDeserializer = new GsonBuilder().registerTypeAdapter(AbstractFlowsObject.class, new FlowsObjectJsonDeserializer()).create();

		flowsObjectIdList = gsonFlowsObjectJsonDeserializer.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectList.class);
	}
	*/

}
