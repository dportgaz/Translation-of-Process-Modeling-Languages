package org.bpmn.bpmn_elements;

import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.task.Task;

import java.util.ArrayList;

public class Loop {

    Task first;

    Task second;

    ExclusiveGateway firstGate;

    ExclusiveGateway secondGate;

    ArrayList<ExclusiveGateway> gateways = new ArrayList<>();
    ArrayList<Task> tasks = new ArrayList<>();
    ArrayList<SequenceFlow> flows = new ArrayList<>();

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

    public void setFirst(Task first) {
        this.first = first;
    }

    public void setFirstGate(ExclusiveGateway firstGate) {
        this.firstGate = firstGate;
    }

    public void setSecond(Task second) {
        this.second = second;
    }

    public void setSecondGate(ExclusiveGateway secondGate) {
        this.secondGate = secondGate;
    }

    public Task getFirst() {
        return first;
    }

    public Task getSecond() {
        return second;
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
        return "Tasks: " + tasks + "\nGateways: " + gateways + "\nFlows: " + flows;
    }
}
