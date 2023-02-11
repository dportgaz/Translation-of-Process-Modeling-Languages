package org.bpmn.bpmn_elements.transition;

import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.task.Task;

import java.util.ArrayList;

public class Loop {

    Task source;

    Task target;

    ExclusiveGateway firstGate;

    ExclusiveGateway secondGate;

    ArrayList<ExclusiveGateway> gateways = new ArrayList<>();
    ArrayList<Task> tasks = new ArrayList<>();
    ArrayList<SequenceFlow> flows = new ArrayList<>();

    public Loop(Task source, Task target){
        this.source = source;
        this.target = target;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<SequenceFlow> getFlows() {
        return flows;
    }

    public ArrayList<ExclusiveGateway> getGateways() {
        return gateways;
    }

    public ExclusiveGateway getFirstGate() {
        return firstGate;
    }

    public ExclusiveGateway getSecondGate() {
        return secondGate;
    }

    public void setSource(Task source) {
        this.source = source;
    }

    public void setFirstGate(ExclusiveGateway firstGate) {
        this.firstGate = firstGate;
        this.gateways.add(firstGate);
    }

    public void setTarget(Task target) {
        this.target = target;
    }

    public void setSecondGate(ExclusiveGateway secondGate) {
        this.secondGate = secondGate;
        this.gateways.add(secondGate);
    }

    public Task getSource() {
        return source;
    }

    public Task getTarget() {
        return target;
    }

    public void addGate(ExclusiveGateway gateway) {
        gateways.add(gateway);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addFlow(SequenceFlow flow) {
        flows.add(flow);
    }

    @Override
    public String toString() {
        return "Loop: " + source + " --> " + target;
    }
}
