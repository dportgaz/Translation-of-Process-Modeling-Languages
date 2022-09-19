package org.bpmn.flows_objects.flowsobject;

public class FlowsObject extends AbstractFlowsObject {

	public FlowsObject(Double CreatedActorId_) {
		CreatedActorId = CreatedActorId_;
	}

	@Override
	public String toString() {
		return "Object { " + CreatedActorId + " }";
	}
	
	@Override
	public Double getCreatedActorId() {
		return this.CreatedActorId;
	}
	
}
