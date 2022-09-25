package org.bpmn.steps;

import org.bpmn.bpmn_elements.*;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Object;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.collaboration.participant.User;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.IntermediateCatchEvent;
import org.bpmn.bpmn_elements.flows.Association;
import org.bpmn.bpmn_elements.flows.MessageFlow;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Step;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.bpmndi.FillBPMNDI;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.flows_objects.AbstractRelation;
import org.bpmn.parse_json.Parser;
import org.bpmn.process.FlowsProcessObject;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

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
                        collaboration.getMessageFlows().add(new MessageFlow(coordinationTask, messageCatch));
                        DataObject d = new DataObject(coordinationTask);
                        fp.getDataObjects().add(d);
                        messageCatch.getDataObjects().add(d);
                    }

                    fp.getIntermediateCatchEvents().add(messageCatch);

                    SequenceFlow splitToCatch = new SequenceFlow(gateSplit, messageCatch);
                    SequenceFlow catchToJoin = new SequenceFlow(messageCatch, gateJoin);

                    for (DataObject d : messageCatch.getDataObjects()) {
                        String id = "FlowAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
                        catchToJoin.getAssociations().add(new Association(id, d));
                        Element associationFlow = doc.createElement("bpmn:association");
                        associationFlow.setAttribute("associationDirection", "One");
                        associationFlow.setAttribute("id", id);
                        associationFlow.setAttribute("sourceRef", d.getRefId());
                        associationFlow.setAttribute("targetRef", catchToJoin.getId());
                        fp.getAssociationFlows().add(associationFlow);
                    }

                    gateSplit.addOutgoing(splitToCatch);
                    gateJoin.addIncoming(catchToJoin);
                    messageCatch.setIncoming(splitToCatch);
                    messageCatch.setOutgoing(catchToJoin);

                    fp.getFlows().add(splitToCatch);
                    fp.getFlows().add(catchToJoin);


                }

                fp.setBeforeAndAfterElements();
            } else if (cntPorts == 1) {
                Port port = task.getPorts().get(0);
                if (port.getIncoming().size() >= 2) {
                    messageCatch = setParallelPort(port, fp, task);
                } else {
                    Relation relation = port.getIncoming().get(0);
                    if (relation.getRelationType() == RelationType.OTHER) {
                        Task coordinationTask = relation.getTask();
                        messageCatch = new IntermediateCatchEvent("Receive " + coordinationTask.getName(), task.getUser());
                        DataObject d = new DataObject(relation.getTask());
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

                    for (DataObject d : messageCatch.getDataObjects()) {
                        String id = "FlowAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
                        catchToTask.getAssociations().add(new Association(id, d));
                        Element associationFlow = doc.createElement("bpmn:association");
                        associationFlow.setAttribute("associationDirection", "One");
                        associationFlow.setAttribute("id", id);
                        associationFlow.setAttribute("sourceRef", d.getRefId());
                        associationFlow.setAttribute("targetRef", catchToTask.getId());
                        fp.getAssociationFlows().add(associationFlow);
                    }

                    fp.getFlows().add(beforeTaskToCatch);
                    fp.getFlows().add(catchToTask);
                    fp.getFlows().remove(flow);
                    fp.setBeforeAndAfterElements();
                }
            }

        }

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

                if (m.find() && m2.find()) {
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

        HashSet<Task> tasksToTransform = new HashSet<>();

        for (MessageFlow mf : collaboration.getMessageFlows()) {

            for (Task task : allTasks) {

                if (task.getId().equals(mf.getSourceRef().getId())) {
                    tasksToTransform.add(task);
                }

            }

        }

        // fill allFlows
        for (Participant object : allParticipants) {
            allFlows.addAll(object.getProcessRef().getFlows());
        }

        // transforms throwing message tasks to sendTasks; helping method
        for (Task task : tasksToTransform) {
            task.setSendTask();
            for (MessageFlow mf : collaboration.getMessageFlows()) {
                if (mf.getSourceRef().getId().equals(task.getId())) {
                    mf.getElementMessageFlow().setAttribute("sourceRef", task.getId());
                }
            }
            for (SequenceFlow sf : allFlows) {
                if (sf.getSourceRef().getId().equals(task.getId())) {
                    sf.getElementSequenceFlow().setAttribute("sourceRef", task.getId());
                }
                if (sf.getTargetRef().getId().equals(task.getId())) {
                    sf.getElementSequenceFlow().setAttribute("targetRef", task.getId());
                }
            }

        }

        setProcesses(definitionsElement);

        // set lanes for pools
        for (Participant object : allParticipants) {

            HashSet<User> user = new HashSet<>();
            HashSet<User> lanes = new HashSet<>();
            ArrayList<Task> tasks = object.getProcessRef().getTasks();
            HashSet<IntermediateCatchEvent> catchEvents = object.getProcessRef().getIntermediateCatchEvents();
            HashSet<ExclusiveGateway> gateways = object.getProcessRef().getGateways();
            ArrayList<SequenceFlow> flows = object.getProcessRef().getFlows();

            for (Task task : tasks) {
                user.add(task.getUser());
            }
            for (IntermediateCatchEvent event : catchEvents) {
                user.add(event.getUser());
            }

            object.getProcessRef().getStartEvent().setUser(tasks.get(0).getUser());
            tasks.get(0).getUser().getElements().add(object.getProcessRef().getStartEvent());
            object.getProcessRef().getEndEvent().setUser(tasks.get(tasks.size() - 1).getUser());
            tasks.get(tasks.size() - 1).getUser().getElements().add(object.getProcessRef().getEndEvent());

            for (ExclusiveGateway gateway : gateways) {
                for (SequenceFlow flow : flows) {
                    if (flow.getTargetRef().getId().equals(gateway.getId())) {
                        User temp = flow.getSourceRef().getUser();
                        if(temp != null) {
                            gateway.setUser(temp);
                            temp.getElements().add(gateway);
                        }
                    }
                }
            }

            Element laneSet = doc.createElement("bpmn:laneSet");
            laneSet.setAttribute("id", "LaneSet_" + RandomIdGenerator.generateRandomUniqueId(6));

            for (User u : user) {
                Element lane = doc.createElement("bpmn:lane");
                lane.setAttribute("id", "Lane_" + RandomIdGenerator.generateRandomUniqueId(6));
                lane.setAttribute("name", u.getName());
                for (BPMNElement element : u.getElements()) {
                    Element temp = doc.createElement("bpmn:flowNodeRef");
                    temp.setTextContent(element.getId());
                    lane.appendChild(temp);
                }
                laneSet.appendChild(lane);
            }

            object.getProcessRef().getElementFlowsProcess().appendChild(laneSet);

        }

        FillBPMNDI di = new FillBPMNDI();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration, false);

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
                    fp.getDataObjects().add(d);
                    messageCatch.getDataObjects().add(d);
                    collaboration.getMessageFlows().add(new MessageFlow(relation.getTask(), messageCatch));
                }

            }
        } else if (port.getCntOther() > 1) {

            messageCatch = new IntermediateCatchEvent(true, task.getUser());

            // find other relations for messageflows
            for (Relation relation : port.getIncoming()) {

                if (relation.getRelationType() == RelationType.OTHER) {
                    messageCatch.setName(messageCatch.getName() + " " + relation.getTask().getName() + ", ");
                    DataObject d = new DataObject(relation.getTask());
                    fp.getDataObjects().add(d);
                    messageCatch.getDataObjects().add(d);
                    collaboration.getMessageFlows().add(new MessageFlow(relation.getTask(), messageCatch));
                }
                messageCatch.setName(messageCatch.getName().substring(0, messageCatch.getName().length() - 1));

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
            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }

    }

    private Participant findParticipant(Double id) {
        for (Participant object : objects) {
            if (object.getKey().equals(id)) {
                return object;
            }
        }
        return null;
    }

}