package org.bpmn.steps;

import org.bpmn.bpmn_elements.*;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Object;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.collaboration.participant.User;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.IntermediateCatchEvent;
import org.bpmn.bpmn_elements.flows.MessageFlow;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Step;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.bpmndi.FillBPMNDI;
import org.bpmn.bpmndi.FillBPMNDI_StepThree_lazy;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.flows_objects.AbstractRelation;
import org.bpmn.parse_json.Parser;
import org.bpmn.process.FlowsProcessObject;
import org.bpmn.process.Lane;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import javax.xml.crypto.Data;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.bpmn_elements.collaboration.Collaboration.objects;
import static org.bpmn.steps.BPMN.createXml;
import static org.bpmn.steps.BPMN.doc;
import static org.bpmn.steps.StepOne.*;

public class StepThree {

    ExecStep step;
    String file;
    Element definitionsElement;
    HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects;

    HashMap<Double, ArrayList<AbstractObjectType>> coordinationProcessObjects;

    ArrayList<AbstractRelation> relationsDataModel;

    ArrayList<Task> coordinationProcess = new ArrayList<>();

    HashMap<Double, ArrayList<Participant>> relations = new HashMap<>();
    Parser parser;

    StepOne stepOne;
    static String bpmnDiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

    private Collaboration collaboration;

    HashMap<Double, ArrayList<AbstractObjectType>> userTypeObjects = new HashMap<>();

    public StepThree(StepOne stepOne, String file, Element definitionsElement, HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects, HashMap<Double, ArrayList<AbstractObjectType>> userTypeObjects,
                     HashMap<Double, ArrayList<AbstractObjectType>> coordinationProcessObjects, ArrayList<AbstractRelation> relationsDataModel) {
        this.stepOne = stepOne;
        this.file = file;
        this.definitionsElement = definitionsElement;
        this.objectTypeObjects = objectTypeObjects;
        this.userTypeObjects = userTypeObjects;
        this.step = ExecStep.THREE;
        this.coordinationProcessObjects = coordinationProcessObjects;
        this.parser = new Parser();
        this.relationsDataModel = relationsDataModel;
        this.collaboration = stepOne.getCollaboration();

    }


    public void execute() throws TransformerException {


        // fill coordination Process
        coordinationProcess = parser.getCoordinationTasks(coordinationProcessObjects);

        // fill Data Model
        for (AbstractRelation relation : relationsDataModel) {

            Double sourceId = (Double) relation.getParameters().get(0);
            Double targetId = (Double) relation.getParameters().get(1);
            ArrayList<Participant> p = new ArrayList<>();

            if (relations.containsKey(sourceId)) {
                ArrayList<Participant> temp = relations.get(sourceId);
                for (Participant target : allParticipants) {
                    if (target.getKey().equals(targetId)) {
                        temp.add(target);
                    }
                }
            } else {
                for (Participant source : allParticipants) {
                    if (source.getKey().equals(sourceId)) {
                        for (Participant target : allParticipants) {
                            if (target.getKey().equals(targetId)) {
                                p.add(target);
                            }
                        }
                    }
                    relations.put(sourceId, p);
                }
            }
        }

        // set tasks to user
        for (Participant object : allParticipants) {
            HashSet<User> user = parser.parsePermissions(userTypeObjects);
            for (Double key : userTypeObjects.keySet()) {
                userTypeObjects.get(key).forEach(obj -> {
                    if (obj != null && obj.getMethodName().equals("AddStateExecutionPermissionToGlobalRole")) {

                        Double participantId = (Double) obj.getParameters().get(0);
                        Double taskId = (Double) obj.getParameters().get(1);

                        for (Task task : object.getProcessRef().getTasks()) {
                            if (task.getCreatedEntityId().equals(taskId)) {
                                for (User u : user) {
                                    if (u.getId().equals(participantId)) {
                                        task.setUser(u);
                                        u.getElements().add(task);
                                        break;
                                    }
                                }
                            }
                        }

                    }
                });
            }

            User systemUser = null;
            for (User k : user) {
                if (k.getName().equals("System")) {
                    systemUser = k;
                }
            }
            for (Task task : allTasks) {
                if (task.getUser() == null) {
                    task.setUser(systemUser);
                    systemUser.getElements().add(task);
                }
            }
        }

        // TODO: complement coordination process with data model relation


        // _______________________________

        for (Task task : coordinationProcess) {

            int cntPorts = task.getPorts().size();
            if (cntPorts == 0) {
                continue;
            }

            FlowsProcessObject fp = task.getParticipant().getProcessRef();
            IntermediateCatchEvent messageCatch = null;

            if (cntPorts >= 2 && task.getCntOtherRelations() >= 1) {

                SequenceFlow flow = fp.getFlowByTarget(task);
                ExclusiveGateway gateSplit = new ExclusiveGateway(true);
                ExclusiveGateway gateJoin = new ExclusiveGateway();

                fp.getGateways().add(gateSplit);
                fp.getGateways().add(gateJoin);

                SequenceFlow joinToTask = new SequenceFlow(gateJoin, task);
                gateJoin.addOutgoing(joinToTask);
                SequenceFlow taskBeforeToSplit = new SequenceFlow(task.getBeforeElement(), gateSplit);
                gateSplit.addIncoming(taskBeforeToSplit);

                fp.getFlows().add(joinToTask);
                fp.getFlows().add(taskBeforeToSplit);

                fp.getFlows().remove(flow);

                for (Port port : task.getPorts()) {

                    // case: port has more than one relation --> parallel multiple
                    if (port.getIncoming().size() > 1) {
                        messageCatch = setParallelPort(port, fp, task);
                    } else {
                        Task coordinationTask = port.getIncoming().get(0).getTask();
                        messageCatch = new IntermediateCatchEvent("Receive " + coordinationTask.getName(), task.getUser());
                        DataObject d = new DataObject(coordinationTask);
                        fp.getDataObjects().add(d);
                        messageCatch.getDataObjects().add(d);
                        collaboration.getMessageFlows().add(new MessageFlow(coordinationTask, messageCatch));
                    }

                    fp.getIntermediateCatchEvents().add(messageCatch);

                    SequenceFlow splitToCatch = new SequenceFlow(gateSplit, messageCatch);
                    SequenceFlow catchToJoin = new SequenceFlow(messageCatch, gateJoin);

                    gateSplit.addOutgoing(splitToCatch);
                    gateJoin.addIncoming(catchToJoin);
                    messageCatch.setIncoming(splitToCatch);
                    messageCatch.setOutgoing(catchToJoin);

                    fp.getFlows().add(splitToCatch);
                    fp.getFlows().add(catchToJoin);


                }

            } else if (cntPorts == 1) {
                Port port = task.getPorts().get(0);
                if (port.getIncoming().size() >= 2) {
                    messageCatch = setParallelPort(port, fp, task);
                } else {
                    Relation relation = port.getIncoming().get(0);
                    if (relation.getRelationType() == RelationType.OTHER) {
                        Task coordinationTask = relation.getTask();
                        messageCatch = new IntermediateCatchEvent("Receive " + coordinationTask.getName(), task.getUser());
                        DataObject d = new DataObject(coordinationTask);
                        fp.getDataObjects().add(d);
                        messageCatch.getDataObjects().add(d);
                        collaboration.getMessageFlows().add(new MessageFlow(relation.getTask(), messageCatch));

                    }
                }
                if (messageCatch != null) {
                    fp.getIntermediateCatchEvents().add(messageCatch);

                    SequenceFlow flow = fp.getFlowByTarget(task);
                    SequenceFlow beforeTaskToCatch = new SequenceFlow(task.getBeforeElement(), messageCatch);
                    SequenceFlow catchToTask = new SequenceFlow(messageCatch, task);

                    fp.getFlows().add(beforeTaskToCatch);
                    fp.getFlows().add(catchToTask);
                    fp.getFlows().remove(flow);
                    fp.setBeforeAndAfterElements();
                }
            }
            fp.setBeforeAndAfterElements();
        }

        for (Participant object : allParticipants) {

            HashSet<IntermediateCatchEvent> events = object.getProcessRef().getIntermediateCatchEvents();
            HashSet<IntermediateCatchEvent> eventsToRemove = new HashSet<>();
            HashSet<DataObject> dataObjectsToRemove = new HashSet<>();
            FlowsProcessObject fp = object.getProcessRef();

            for (IntermediateCatchEvent event : events) {
                if (event.getParallelMultiple()) {

                    eventsToRemove.add(event);

                    ExclusiveGateway parallelGatewaySplit = new ExclusiveGateway(true, true);
                    ExclusiveGateway parallelGatewayJoin = new ExclusiveGateway(true, true);

                    fp.getGateways().add(parallelGatewaySplit);
                    fp.getGateways().add(parallelGatewayJoin);

                    fp.getGateways().add(parallelGatewayJoin);
                    fp.getGateways().add(parallelGatewayJoin);

                    SequenceFlow flowIn = fp.getFlowByTarget(event);
                    SequenceFlow flowOut = fp.getFlowBySource(event);

                    SequenceFlow flowInToParallel = new SequenceFlow(flowIn.getSourceRef(), parallelGatewaySplit);
                    SequenceFlow parallelToFlowOut = new SequenceFlow(parallelGatewayJoin, flowOut.getTargetRef());

                    parallelGatewaySplit.addIncoming(flowInToParallel);
                    parallelGatewayJoin.addOutgoing(parallelToFlowOut);

                    fp.getFlows().add(flowInToParallel);
                    fp.getFlows().add(parallelToFlowOut);
                    fp.getFlows().remove(flowIn);
                    fp.getFlows().remove(flowOut);

                    //TODO: write parallel Gateway class
                    for (Map.Entry<Task, DataObject> entry : event.getAssociatedTasks().entrySet()) {
                        Task task = entry.getKey();
                        DataObject dataObject = entry.getValue();
                        IntermediateCatchEvent messageCatch = new IntermediateCatchEvent("Receive " + task.getName(), task.getUser());
                        fp.getDataObjects().add(dataObject);
                        fp.getIntermediateCatchEvents().add(messageCatch);
                        messageCatch.getDataObjects().add(dataObject);

                        SequenceFlow parallelToEvent = new SequenceFlow(parallelGatewaySplit, messageCatch);
                        SequenceFlow eventToParallel = new SequenceFlow(messageCatch, parallelGatewayJoin);

                        parallelGatewaySplit.addOutgoing(parallelToEvent);
                        parallelGatewayJoin.addIncoming(eventToParallel);
                        messageCatch.setIncoming(parallelToEvent);
                        messageCatch.setOutgoing(eventToParallel);

                        fp.getFlows().add(parallelToEvent);
                        fp.getFlows().add(eventToParallel);

                        collaboration.getMessageFlows().remove(collaboration.getMessageFlowByTarget(event));
                        collaboration.getMessageFlows().add(new MessageFlow(task, messageCatch));
                    }
                    fp.setBeforeAndAfterElements();
                }
            }

            fp.getIntermediateCatchEvents().removeAll(eventsToRemove);
            fp.getDataObjects().removeAll(dataObjectsToRemove);

            fp.setBeforeAndAfterElements();
        }

        /*
        // prints coordination process and data relations
        System.out.println(relations + "\n");
        for (Task task : coordinationProcess) {
            System.out.print(task + " ");
            if (task.getPorts().size() > 0) {
                for (Port port : task.getPorts()) {
                    System.out.print(port + " ");
                    System.out.print(port.getIncoming() + " ");
                }
            }
            System.out.println();
        }
        t
         */

        // TODO: Very ugly, needs refactor; replaces XOR to event when appropriate
        for (Participant object : allParticipants) {

            ArrayList<SequenceFlow> flows = object.getProcessRef().getFlows();
            for (int i = 0; i < flows.size(); i++) {

                boolean needsChange = false;
                String id = null;
                SequenceFlow flow = flows.get(i);
                Pattern p = Pattern.compile("^Gateway_+");
                Matcher m = p.matcher(flow.getSourceRef().getId());
                Pattern p2 = Pattern.compile("(^ReceiveActivity_+|^EventGateway_+)");
                Matcher m2 = p2.matcher(flow.getTargetRef().getId());

                if (m.find() && m2.find() && !((ExclusiveGateway) flow.getSourceRef()).isParallelGate()) {
                    for (int j = i + 1; j < flows.size(); j++) {

                        SequenceFlow flowInner = flows.get(j);
                        Matcher m3 = p2.matcher(flowInner.getTargetRef().getId());
                        if (flow.getSourceRef().getId().equals(flowInner.getSourceRef().getId()) && m3.find()) {
                            ((ExclusiveGateway) flowInner.getSourceRef()).setEventBased();
                            id = flowInner.getSourceRef().getId();
                            flowInner.setSourceRef(flowInner.getSourceRef());
                            needsChange = true;
                        }

                    }
                }

                if (needsChange) {
                    for (SequenceFlow flowOuter : flows) {
                        if (flowOuter.getTargetRef().getId().equals(flow.getSourceRef().getId())) {
                            flowOuter.setTargetRef(flow.getSourceRef());
                        }
                    }
                    ((ExclusiveGateway) flow.getSourceRef()).setEventBased(id);
                    flow.setSourceRef(flow.getSourceRef());

                }

            }

        }

        // trim eventgate --> eventgate
        for (Participant object : allParticipants) {

            ArrayList<SequenceFlow> flows = object.getProcessRef().getFlows();
            HashSet<SequenceFlow> flowsToRemove = new HashSet<>();
            HashSet<ExclusiveGateway> gatewaysToRemove = new HashSet<>();
            HashSet<SequenceFlow> flowsToAdd = new HashSet<>();

            for (SequenceFlow flow : flows) {
                Pattern p = Pattern.compile("^EventGateway_+");
                Matcher matchSource = p.matcher(flow.getSourceRef().getId());
                Matcher matchTarget = p.matcher(flow.getTargetRef().getId());

                if (matchSource.find() && matchTarget.find()) {
                    flowsToRemove.add(flow);
                    gatewaysToRemove.add((ExclusiveGateway) flow.getTargetRef());
                    ArrayList<SequenceFlow> outgoingOuterEventBased = new ArrayList<>();
                    for (SequenceFlow outerFlow : flows) {
                        if (outerFlow.getSourceRef().getId().equals(flow.getTargetRef().getId())) {
                            outgoingOuterEventBased.add(outerFlow);
                            flowsToRemove.add(outerFlow);
                        }
                    }
                    for (SequenceFlow temp : outgoingOuterEventBased) {
                        flowsToAdd.add(new SequenceFlow(flow.getSourceRef(), temp.getTargetRef()));
                    }
                }
            }

            flows.removeAll(flowsToRemove);
            flows.addAll(flowsToAdd);
            object.getProcessRef().getGateways().removeAll(gatewaysToRemove);

        }

        // transforms throwing message tasks to sendTasks

        HashSet<Task> throwingMessageTasks = new HashSet<>();

        for (MessageFlow mf : collaboration.getMessageFlows()) {

            for (Task task : allTasks) {

                if (task.getId().equals(mf.getSourceRef().getId())) {
                    throwingMessageTasks.add(task);
                }

            }

        }

        // fill allFlows
        for (Participant object : allParticipants) {
            allFlows.addAll(object.getProcessRef().getFlows());
        }

        // transforms throwing message tasks to sendTasks; helping method; needs to be after "fill allFlows"
        HashSet<Task> transformedMessages = new HashSet<>();
        for (Task task : throwingMessageTasks) {
            if (!task.getIsSubprocess()) {
                task.setSendTask();
                transformedMessages.add(task);
                for (SequenceFlow sf : allFlows) {
                    if (sf.getSourceRef().getId().equals(task.getId())) {
                        sf.setSourceRef(task);
                    }
                    if (sf.getTargetRef().getId().equals(task.getId())) {
                        sf.setTargetRef(task);
                    }
                }
                for (MessageFlow mf : collaboration.getMessageFlows()) {
                    if (mf.getSourceRef().getId().equals(task.getId())) {
                        mf.setSourceRef(task);
                    }
                }
            }
        }
        throwingMessageTasks.removeAll(transformedMessages);

        // set data input associations for receiving tasks

        for (Participant object : objects) {
            HashSet<IntermediateCatchEvent> events = object.getProcessRef().getIntermediateCatchEvents();
            for (IntermediateCatchEvent event : events) {
                for (DataObject d : event.getDataObjects()) {
                    DataInputAssociation in = new DataInputAssociation();
                    d.getDataInputAssociations().add(in);
                    in.setAssociatedTaskId(event.getId());
                    event.getDataInputAssociations().add(in);
                    Element tempSource = doc.createElement("bpmn:sourceRef");
                    Element tempTarget = doc.createElement("bpmn:targetRef");
                    tempSource.setTextContent(d.getRefId());
                    tempTarget.setTextContent("_property_placeholder");
                    in.getElementDataInputAssociation().appendChild(tempSource);
                    in.getElementDataInputAssociation().appendChild(tempTarget);
                    event.getElement().appendChild(in.getElementDataInputAssociation());
                }
            }

        }

        setProcesses(definitionsElement);

        FillBPMNDI_StepThree_lazy di = new FillBPMNDI_StepThree_lazy();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration, true, true);

        createXml(file);

    }

    private IntermediateCatchEvent setParallelPort(Port port, FlowsProcessObject fp, Task task) {

        IntermediateCatchEvent messageCatch = null;
        if (port.getCntOther() == 1) {

            // find other relation for messageflow
            for (Relation relation : port.getIncoming()) {

                if (relation.getRelationType() == RelationType.OTHER) {
                    messageCatch = new IntermediateCatchEvent("Receive " + relation.getTask().getName(), task.getUser());
                    DataObject d = new DataObject(relation.getTask());
                    messageCatch.getDataObjects().add(d);
                    fp.getDataObjects().add(d);
                    collaboration.getMessageFlows().add(new MessageFlow(relation.getTask(), messageCatch));
                }

            }
        } else if (port.getCntOther() > 1) {

            messageCatch = new IntermediateCatchEvent(true, task.getUser());

            // find other relations for messageflows
            for (Relation relation : port.getIncoming()) {

                if (relation.getRelationType() == RelationType.OTHER) {
                    DataObject d = new DataObject(relation.getTask());
                    messageCatch.getAssociatedTasks().put(relation.getTask(), d);
                    fp.getDataObjects().add(d);
                    messageCatch.getDataObjects().add(d);
                    collaboration.getMessageFlows().add(new MessageFlow(relation.getTask(), messageCatch));
                }

            }
        }
        return messageCatch;
    }

    public void setProcesses(Element definitionsElement) {

        definitionsElement.appendChild(collaboration.getElementCollaboration());

        for (MessageFlow mf : collaboration.getMessageFlows()) {
            collaboration.getElementCollaboration().appendChild(mf.getElement());
        }

        for (Object participant : objects) {

            participant.getProcessRef().setElementFlowsProcess();
            setLane(participant);
            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }

    }

    private void setLane(Participant object) {

        HashSet<User> users = new HashSet<>();
        ArrayList<Task> tasks = object.getProcessRef().getTasks();
        HashSet<IntermediateCatchEvent> catchEvents = object.getProcessRef().getIntermediateCatchEvents();
        HashSet<ExclusiveGateway> gateways = object.getProcessRef().getGateways();
        ArrayList<SequenceFlow> flows = object.getProcessRef().getFlows();

        for (Task task : tasks) {
            users.add(task.getUser());
        }
        for (IntermediateCatchEvent event : catchEvents) {
            users.add(event.getUser());
        }

        object.getProcessRef().getStartEvent().setUser(tasks.get(0).getUser());
        tasks.get(0).getUser().getElements().add(object.getProcessRef().getStartEvent());
        object.getProcessRef().getEndEvent().setUser(tasks.get(tasks.size() - 1).getUser());
        tasks.get(tasks.size() - 1).getUser().getElements().add(object.getProcessRef().getEndEvent());

        for (ExclusiveGateway gateway : gateways) {

            for (SequenceFlow flow : flows) {
                if (flow.getSourceRef().getId().equals(gateway.getId())) {
                    BPMNElement target = flow.getTargetRef();
                    User temp = target.getUser();
                    BPMNElement targetFromTarget = target;
                    while(temp == null){
                        SequenceFlow tempFlow = object.getProcessRef().getFlowBySource(targetFromTarget);
                        targetFromTarget = tempFlow.getTargetRef();
                        temp = targetFromTarget.getUser();
                    }
                    if (temp != null) {
                        gateway.setUser(temp);
                        temp.getElements().add(gateway);
                    }
                }
            }
        }

        for (SequenceFlow flow : flows) {
            if (flow.getTargetRef().getId().equals(object.getProcessRef().getEndEvent().getId())) {
                object.getProcessRef().getEndEvent().setUser(flow.getSourceRef().getUser());
            }
        }

        Element laneSet = doc.createElement("bpmn:laneSet");
        laneSet.setAttribute("id", "LaneSet_" + RandomIdGenerator.generateRandomUniqueId(6));

        HashMap<User, Lane> lanes = new HashMap<>();
        for (User u : users) {
            Lane lane = new Lane(u);
            lanes.put(u, lane);

            for (BPMNElement element : u.getElements()) {
                lane.addBPMNElement(element);
            }
            laneSet.appendChild(lane.getLaneElement());
        }

        object.getProcessRef().getElementFlowsProcess().appendChild(laneSet);
        object.setLanes(lanes);

    }
}

