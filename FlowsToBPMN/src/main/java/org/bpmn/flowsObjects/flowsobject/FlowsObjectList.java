package org.bpmn.flowsObjects.flowsobject;

import java.util.ArrayList;

public class FlowsObjectList extends AbstractFlowsObject {

	public ArrayList<AbstractFlowsObject> DataModelActionLogs;

	@Override
	public String toString() {

		String retString = "";
		retString += DataModelActionLogs;

		return retString;
	}

	public ArrayList<AbstractFlowsObject> getList() {
		return this.DataModelActionLogs;
	}

}
