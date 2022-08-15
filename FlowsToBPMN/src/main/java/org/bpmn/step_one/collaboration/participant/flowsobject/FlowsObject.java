package org.bpmn.step_one.collaboration.participant.flowsobject;

public class FlowsObject extends AbstractFlowsObject {

	public FlowsObject(String CreatedActorId_) {
		CreatedActorId = CreatedActorId_;
	}

	@Override
	public String toString() {
		return "Object { " + CreatedActorId + " }";
	}
	
	@Override
	public String getCreatedActorId() {
		return this.CreatedActorId;
	}
	
}
