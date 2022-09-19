package org.bpmn.process;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.Loop;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.task.Step;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.parse_json.Parser;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.participant.Object;
import org.bpmn.steps.BPMN;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.bpmn.steps.BPMN.doc;
import static org.bpmn.steps.StepOne.*;

public class FlowsProcessObject {

    static int countProcess = 0;

    String id;

    static String isExecutable = "true";

    Element elementFlowsProcess;

    Object participant;
    StartEvent startEvent;
    ArrayList<Task> tasks = new ArrayList<>();

    EndEvent endEvent;

    HashSet<ExclusiveGateway> gateways = new HashSet<>();

    ArrayList<SequenceFlow> flows = new ArrayList<>();

    HashSet<DataObject> dataObjects = new HashSet<>();

    ArrayList<Task> subprocesses = new ArrayList<>();

    ArrayList<AbstractObjectType> objects;

    ArrayList<Loop> loops = new ArrayList<>();

    DataObject finishedDataObject;


    public FlowsProcessObject(Object participant, HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects) {

        this.id = "Process_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementFlowsProcess = doc.createElement("bpmn:process");
        this.participant = participant;
        this.objects = objectTypeObjects.get(participant.getKey());
        setFlowsProcess();
        setElementFlowsProcess();
        countProcess++;

    }

    private void setFlowsProcess() {

        Parser parser = new Parser();

        this.tasks = parser.parseTasks(this.participant, objects);
        this.loops = parser.parseLoops(this, objects);
        predicates = parser.parsePredicates(objects);

        setStartEvent();
        setEndEvent();
        setDataObjects();
        this.flows = parser.parseFlows(this, objects);
        sortProcess();
        setEndTasks();
        addEndEventFlows();
        setAssociations();
        setSubProcesses();
        setBeforeAndAfterElements();
        setLoops();
        setGateways();
        setFlows();
        addFlowsToTasks();
        setTasks();
        addGateways();

    }

    private void setBeforeAndAfterElements() {

        for (SequenceFlow flow : flows) {

            BPMNElement source = flow.getSourceRef();
            BPMNElement target = flow.getTargetRef();

            source.setAfterElement(target);
            target.setBeforeElement(source);

        }

    }

    private void setLoops() {

        for (Loop loop : loops) {

            Task source = loop.getSource();
            Task target = loop.getTarget();
            BPMNElement beforeSource = source.getBeforeElement();
            BPMNElement afterTarget = target.getAfterElement();
            ExclusiveGateway beforeGateway = new ExclusiveGateway();
            ExclusiveGateway afterGateway = new ExclusiveGateway();

            gateways.add(beforeGateway);
            loop.setFirstGate(beforeGateway);
            gateways.add(afterGateway);
            loop.setSecondGate(afterGateway);

            flows.remove(getFlowBySourceAndTarget(beforeSource, source));
            flows.remove(getFlowBySourceAndTarget(target, afterTarget));

            // sourceBefore --> gateBefore
            beforeSource.setAfterElement(beforeGateway);
            flows.add(new SequenceFlow(beforeSource, beforeGateway));

            beforeGateway.addBeforeElement(beforeSource);
            beforeGateway.addAfterElement(source);
            flows.add(new SequenceFlow(beforeGateway, source));

            // target --> afterGate
            target.setAfterElement(afterGateway);
            flows.add(new SequenceFlow(target, afterGateway));
            afterGateway.addBeforeElement(target);

            afterGateway.addAfterElement(afterTarget);
            flows.add(new SequenceFlow(afterGateway, afterTarget));

            // afterGate --> beforeGate
            afterGateway.addAfterElement(beforeGateway);
            flows.add(new SequenceFlow(afterGateway, beforeGateway));

            afterTarget.setBeforeElement(afterGateway);

        }

    }

    private SequenceFlow getFlowBySourceAndTarget(BPMNElement source, BPMNElement target) {
        for (SequenceFlow flow : flows) {
            if (flow.getSourceRef().getId().equals(source.getId()) && flow.getTargetRef().getId().equals(target.getId())) {
                return flow;
            }
        }
        return null;
    }

    private SequenceFlow getFlowBySource(SequenceFlow flowIn) {
        for (SequenceFlow flow : flows) {
            if (flowIn.getSourceRef().equals(flow.getSourceRef())) {
                return flow;
            }
        }
        return null;
    }

    private SequenceFlow getFlowByTarget(SequenceFlow flowIn) {
        for (SequenceFlow flow : flows) {
            if (flowIn.getTargetRef().equals(flow.getTargetRef())) {
                return flow;
            }
        }
        return null;
    }

    private void setGateways() {

        setSplitGateways();
        setJoinGateways();

    }

    private void setSplitGateways() {

        ArrayList<SequenceFlow> flowsToAdd = new ArrayList<>();
        ArrayList<SequenceFlow> flowsToRemove = new ArrayList<>();
        // add split gateways
        for (int i = 0; i < flows.size() - 1; i++) {

            boolean duplicate = false;
            ExclusiveGateway splitGateway = new ExclusiveGateway();
            BPMNElement outerElement = flows.get(i).getSourceRef();

            for (int j = i + 1; j < flows.size(); j++) {

                Pattern p = Pattern.compile("(Activity*|EndEvent*)");
                BPMNElement innerElement = flows.get(j).getSourceRef();
                Matcher m = p.matcher(innerElement.getId());

                if (m.find() && outerElement.getId().equals(innerElement.getId())) {
                    flowsToRemove.add(flows.get(j));
                    flowsToAdd.add(new SequenceFlow(splitGateway, flows.get(j).getTargetRef()));
                    duplicate = true;
                }

            }
            if (duplicate) {
                flowsToRemove.add(flows.get(i));
                flowsToAdd.add(new SequenceFlow(splitGateway, flows.get(i).getTargetRef()));
                flowsToAdd.add(new SequenceFlow(flows.get(i).getSourceRef(), splitGateway));
                gateways.add(splitGateway);
            }

        }

        flows.addAll(flowsToAdd);
        flows.removeAll(flowsToRemove);

    }

    private void setJoinGateways() {

        ArrayList<SequenceFlow> flowsToAdd = new ArrayList<>();
        ArrayList<SequenceFlow> flowsToRemove = new ArrayList<>();
        // add split gateways
        for (int i = 0; i < flows.size() - 1; i++) {

            boolean duplicate = false;
            ExclusiveGateway joinGateway = new ExclusiveGateway();
            BPMNElement outerElement = flows.get(i).getTargetRef();

            for (int j = i + 1; j < flows.size(); j++) {

                Pattern p = Pattern.compile("(Activity*|EndEvent*)");
                BPMNElement innerElement = flows.get(j).getTargetRef();
                Matcher m = p.matcher(innerElement.getId());

                if (m.find() && outerElement.getId().equals(innerElement.getId())) {
                    flowsToRemove.add(flows.get(j));
                    flowsToAdd.add(new SequenceFlow(flows.get(j).getSourceRef(), joinGateway));
                    duplicate = true;
                }

            }
            if (duplicate) {
                flowsToRemove.add(flows.get(i));
                flowsToAdd.add(new SequenceFlow(joinGateway, flows.get(i).getTargetRef()));
                flowsToAdd.add(new SequenceFlow(flows.get(i).getSourceRef(), joinGateway));
                gateways.add(joinGateway);

            }

        }

        flows.addAll(flowsToAdd);
        flows.removeAll(flowsToRemove);

    }

    private void setSubProcesses() {

        for (Task task : tasks) {
            if (task.getIsSubprocess()) {
                subprocesses.add(task);
                ArrayList<Step> steps = task.getSteps();
                SequenceFlow sfStart = new SequenceFlow(task.getStart(), steps.get(0));
                task.getElement().appendChild(sfStart.getElementSequenceFlow());
                task.getFlows().add(sfStart);
                task.getStart().setOutgoing(sfStart);
                for (int i = 0; i < task.getSteps().size() - 1; i++) {
                    SequenceFlow sf = new SequenceFlow(steps.get(i), steps.get(i + 1));
                    task.getElement().appendChild(sf.getElementSequenceFlow());
                    task.getFlows().add(sf);
                }
                SequenceFlow sfEnd = new SequenceFlow(steps.get(task.getSteps().size() - 1), task.getEnd());
                task.getElement().appendChild(task.getEnd().getElement());
                task.getElement().appendChild(sfEnd.getElementSequenceFlow());
                task.getFlows().add(sfEnd);
                task.getEnd().setIncoming(sfEnd);
            }
        }

    }

    private void setEndTasks() {

        for (Task task : tasks) {
            if (task.getAfter().size() == 0) {
                task.setIsEndTask();
                task.getAfter().add(endEvent);
            }
        }

    }

    private void setAssociations() {

            startEvent.setDataOutputAssociation();
            startEvent.getDataOutputAssociation().setOutputAssociationTarget(tasks.get(0).getDataObject());

            for(int i = 0; i < tasks.size()-1; i++) {
                // add data output association
                // task.setDataOutputAssociation(); erledigt im Konstruktor, maybe buggy
                tasks.get(i).getDataOutputAssociation().setOutputAssociationTarget(tasks.get(i+1).getDataObject());
            }
            tasks.get(tasks.size()-1).getDataOutputAssociation().setOutputAssociationTarget(finishedDataObject);

    }


    private void setTasks() {

        for (Task task : tasks) {
            this.elementFlowsProcess.appendChild(task.getElement());
        }

    }

    private void setFlows() {

        for (SequenceFlow flow : flows) {
            elementFlowsProcess.appendChild(flow.getElementSequenceFlow());
        }

    }

    public DataObject getFinishedDataObject() {
        return finishedDataObject;
    }

    private void setDataObjects() {

        for (Task task : tasks) {

            DataObject dObj = task.getDataObject();
            dataObjects.add(dObj);

            this.elementFlowsProcess.appendChild(dObj.getElementDataObject());

            Element tempObj = doc.createElement("bpmn:dataObject");
            tempObj.setAttribute("id", dObj.getId());
            this.elementFlowsProcess.appendChild(tempObj);
            this.elementFlowsProcess.appendChild(task.getElement());

        }

        finishedDataObject = new DataObject(participant);
        dataObjects.add(finishedDataObject);
        this.elementFlowsProcess.appendChild(finishedDataObject.getElementDataObject());
        Element tempObj = doc.createElement("bpmn:dataObject");
        tempObj.setAttribute("id", finishedDataObject.getId());
        this.elementFlowsProcess.appendChild(tempObj);

    }

    private void setStartEvent() {

        StartEvent startEvent = new StartEvent();
        Element elementStartEvent = startEvent.getElement();

        this.startEvent = startEvent;
        this.elementFlowsProcess.appendChild(elementStartEvent);

    }

    private void setEndEvent() {

        EndEvent endEvent = new EndEvent();
        Element elementEndEvent = endEvent.getElement();

        this.endEvent = endEvent;
        this.elementFlowsProcess.appendChild(elementEndEvent);

    }

    private void addEndEventFlows() {

        for (Task task : tasks) {

            if (task.getIsEndTask()) {
                SequenceFlow endFlow = new SequenceFlow(task, endEvent);
                flows.add(endFlow);
            }

        }

    }

    public AbstractObjectType findObjectById(Double id, ArrayList<AbstractObjectType> objectTypeObjects) {

        return objectTypeObjects.stream().filter(obj -> obj != null && obj.getCreatedEntityId() != null && obj.getCreatedEntityId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public Task findTaskById(Double id) {

        for (Task task : this.tasks) {

            if (task.getCreatedEntityId().equals(id)) {
                return task;
            }

        }
        return null;
    }

    private void sortProcess() {

        for (SequenceFlow flow : flows) {

            BPMNElement source = flow.getSourceRef();
            BPMNElement target = flow.getTargetRef();

            source.getAfter().add(target);
            target.getBefore().add(source);

        }

    }

    private void setElementFlowsProcess() {
        this.elementFlowsProcess.setAttribute("id", this.id);
        if (countProcess == 0) {
            this.elementFlowsProcess.setAttribute("isExecutable", this.isExecutable);
        } else {
            isExecutable = "false";
            this.elementFlowsProcess.setAttribute("isExecutable", this.isExecutable);
        }
    }

    private void addFlowsToTasks() {

        for (Task task : tasks) {
            if (task.getIsSubprocess()) {
                for (Step step : task.getSteps()) {
                    for (SequenceFlow sf : task.getFlows()) {
                        if (step.getId().equals(sf.getSourceRef().getId())) {
                            step.setOutgoing(sf);
                        }
                        if (step.getId().equals(sf.getTargetRef().getId())) {
                            step.setIncoming(sf);
                        }
                    }
                }
            }
            for (SequenceFlow sf : flows) {
                if (task.getId().equals(sf.getSourceRef().getId())) {
                    task.setOutgoing(sf);
                }
                if (task.getId().equals(sf.getTargetRef().getId())) {
                    task.setIncoming(sf);
                }
            }
        }

    }

    private void addGateways() {

        for (ExclusiveGateway gate : gateways) {
            allGateways.add(gate);
            this.elementFlowsProcess.appendChild(gate.getElementExclusiveGateway());
            for (SequenceFlow sf : flows) {
                if (sf.getSourceRef().getId().equals(gate.getId())) {
                    Element out = doc.createElement("bpmn:outgoing");
                    out.setTextContent(sf.getId());
                    gate.getElementExclusiveGateway().appendChild(out);
                }
                if (sf.getTargetRef().getId().equals(gate.getId())) {
                    Element inc = doc.createElement("bpmn:incoming");
                    inc.setTextContent(sf.getId());
                    gate.getElementExclusiveGateway().appendChild(inc);
                }
            }
        }
    }

    public Element getElementFlowsProcess() {
        return this.elementFlowsProcess;
    }

    public HashSet<DataObject> getDataObjects() {
        return dataObjects;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public String getId() {
        return this.id;
    }

    public EndEvent getEndEvent() {
        return endEvent;
    }

    public StartEvent getStartEvent() {
        return startEvent;
    }

    public ArrayList<SequenceFlow> getFlows() {
        return flows;
    }

    public HashSet<ExclusiveGateway> getGateways() {
        return gateways;
    }
}
