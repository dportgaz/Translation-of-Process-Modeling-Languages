package org.bpmn.transformation;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Pool;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.IntermediateCatchEvent;
import org.bpmn.bpmn_elements.event.IntermediateThrowEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.transition.MessageFlow;
import org.bpmn.bpmn_elements.transition.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.bpmndi.BPMNDiagramCoordinationAndUser;
import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.flows_entities.AbstractRelationship;
import org.bpmn.flows_process_model.Port;
import org.bpmn.flows_process_model.Relation;
import org.bpmn.flows_process_model.RelationType;
import org.bpmn.parse_json.Parser;
import org.bpmn.process.FlowsProcessObject;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.bpmn_elements.collaboration.Collaboration.pools;
import static org.bpmn.transformation.FlowsToBpmn.createXml;
import static org.bpmn.transformation.LifecycleTransformation.*;

public class CoordinationTransformation implements Transformation {
    String file;
    Element definitionsElement;
    HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects;
    ArrayList<AbstractRelationship> relationsDataModel;
    ArrayList<Task> coordinationProcess = new ArrayList<>();
    HashMap<Double, ArrayList<Participant>> relationships = new HashMap<>();
    Parser parser;
    LifecycleTransformation lifecycleTransformation;
    static String bpmnDiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

    private Collaboration collaboration;
    HashMap<Double, ArrayList<AbstractFlowsEntity>> userTypes;

    public CoordinationTransformation(LifecycleTransformation lifecycleTransformation, String file, Element definitionsElement, HashMap<Double, ArrayList<AbstractFlowsEntity>> userTypes, ArrayList<AbstractRelationship> relationsDataModel, HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects) {
        this.lifecycleTransformation = lifecycleTransformation;
        this.file = file;
        this.definitionsElement = definitionsElement;
        this.userTypes = userTypes;
        this.coordinationProcessObjects = coordinationProcessObjects;
        this.parser = new Parser();
        this.relationsDataModel = relationsDataModel;
        this.collaboration = lifecycleTransformation.getCollaboration();

    }

    public void transform() throws TransformerException {

        // fill coordination Process
        coordinationProcess = parser.getCoordinationTasks(coordinationProcessObjects);
        // fill Data Model
        for (AbstractRelationship relation : relationsDataModel) {

            Double sourceId = (Double) relation.getParameters().get(0);
            Double targetId = (Double) relation.getParameters().get(1);
            ArrayList<Participant> p = new ArrayList<>();

            if (relationships.containsKey(sourceId)) {
                ArrayList<Participant> temp = relationships.get(sourceId);
                for (Participant target : Participants) {
                    if (target.getKey().equals(targetId)) {
                        temp.add(target);
                    }
                }
            } else {
                for (Participant source : Participants) {
                    if (source.getKey().equals(sourceId)) {
                        for (Participant target : Participants) {
                            if (target.getKey().equals(targetId)) {
                                p.add(target);
                            }
                        }
                    }
                    relationships.put(sourceId, p);
                }
            }
        }
        // set tasks to user
        for (Participant object : Participants) {
            HashSet<Lane> lane = parser.parsePermissions(userTypes);
            for (Double key : userTypes.keySet()) {
                userTypes.get(key).forEach(obj -> {
                    if (obj != null && obj.getMethodName().equals("AddStateExecutionPermissionToGlobalRole")) {

                        Double participantId = (Double) obj.getParameters().get(0);
                        Double taskId = (Double) obj.getParameters().get(1);

                        for (Task task : object.getProcessRef().getTasks()) {
                            if (task.getCreatedEntityId().equals(taskId)) {
                                for (Lane u : lane) {
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

            Lane systemLane = null;
            for (Lane k : lane) {
                if (k.getName().equals("System")) {
                    systemLane = k;
                }
            }
            for (Task task : allTasks) {
                if (task.getUser() == null) {
                    task.setUser(systemLane);
                    systemLane.getElements().add(task);
                }
            }
        }

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
                        messageCatch = new IntermediateCatchEvent(task.getUser());
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

                    fp.getGateways().add(gateSplit);
                    fp.getGateways().add(gateJoin);

                }

            } else if (cntPorts == 1) {
                Port port = task.getPorts().get(0);
                if (port.getIncoming().size() >= 2) {
                    messageCatch = setParallelPort(port, fp, task);
                } else {
                    Relation relation = port.getIncoming().get(0);
                    if (relation.getRelationType() == RelationType.OTHER) {
                        messageCatch = new IntermediateCatchEvent(task.getUser());
                        collaboration.getMessageFlows().add(new MessageFlow(relation.getTask(), messageCatch));
                    }
                }
                if (messageCatch != null) {
                    fp.getIntermediateCatchEvents().add(messageCatch);

                    SequenceFlow flow = fp.getFlowByTarget(task);
                    SequenceFlow beforeTaskToCatch = new SequenceFlow(task.getBeforeElement(), messageCatch);
                    task.getBeforeElement().setOutgoing(beforeTaskToCatch);
                    SequenceFlow catchToTask = new SequenceFlow(messageCatch, task);
                    task.setIncoming(catchToTask);

                    fp.getFlows().add(beforeTaskToCatch);
                    fp.getFlows().add(catchToTask);
                    fp.getFlows().remove(flow);
                    fp.setBeforeAndAfterElements();
                }

            }
            fp.setBeforeAndAfterElements();
        }

        for (Participant object : Participants) {

            HashSet<IntermediateCatchEvent> events = object.getProcessRef().getIntermediateCatchEvents();
            FlowsProcessObject fp = object.getProcessRef();

            for (IntermediateCatchEvent event : events) {
                if (event.getParallelMultiple()) {

                    SequenceFlow flowIn = fp.getFlowByTarget(event);
                    SequenceFlow flowOut = fp.getFlowBySource(event);

                    SequenceFlow flowInToParallel = new SequenceFlow(flowIn.getSourceRef(), event);
                    SequenceFlow parallelToFlowOut = new SequenceFlow(event, flowOut.getTargetRef());

                    fp.getFlows().add(flowInToParallel);
                    fp.getFlows().add(parallelToFlowOut);
                    fp.getFlows().remove(flowIn);
                    fp.getFlows().remove(flowOut);

                    fp.setBeforeAndAfterElements();
                }
            }

            fp.setBeforeAndAfterElements();
        }

        // Replaces XOR to event when appropriate; braucht man vielleicht/wahrscheinlich für backwards transitions mit predicates
        for (Participant object : Participants) {

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

        for (Participant object : Participants) {

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

        HashSet<Task> marked = new HashSet<>();

        for (MessageFlow mf : collaboration.getMessageFlows()) {
            Task task = (Task) mf.getSourceRef();
            IntermediateThrowEvent messageThrow = null;
            if (!marked.contains(task)) {
                FlowsProcessObject fp = task.getParticipant().getProcessRef();
                messageThrow = new IntermediateThrowEvent(task);
                task.setSendingMessage(messageThrow);
                fp.getIntermediateThrowEvents().add(messageThrow);
                fp.getFlows().add(new SequenceFlow(messageThrow, task.getAfterElement()));
                fp.setBeforeAndAfterElements();
                fp.getFlows().remove(task.getOutgoing());
                fp.getFlows().add(new SequenceFlow(task, messageThrow));
                marked.add(task);
            }
            if(messageThrow != null) {
                mf.setSourceRef(messageThrow);
            }
        }

        // subsequent step for start and end messages
        for(Participant object: Participants){

            // start message
            FlowsProcessObject fp = object.getProcessRef();
            fp.setBeforeAndAfterElements();
            StartEvent startEvent = fp.getStartEvent();

            Pattern p = Pattern.compile("^Event_+");
            Matcher m = p.matcher(startEvent.getOutgoing().getTargetRef().getId());

            if(m.find()){
                IntermediateCatchEvent messageCatch = (IntermediateCatchEvent) startEvent.getAfterElement();
                for(MessageFlow mf : collaboration.getMessageFlows()){
                    if(mf.getTargetRef().getId().equals(messageCatch.getId())){
                        mf.setTargetRef(startEvent);
                    }
                }
                startEvent.setMessage();
                startEvent.getOutgoing().setTargetRef(messageCatch.getOutgoing().getTargetRef());
                fp.getFlows().remove(messageCatch.getOutgoing());
                fp.getIntermediateCatchEvents().remove(messageCatch);
            }

            // end message
            EndEvent endEvent = fp.getEndEvent();

            Pattern p2 = Pattern.compile("^Event_+");
            Matcher m2 = p2.matcher(endEvent.getIncoming().getSourceRef().getId());

            if(m2.find()){
                IntermediateThrowEvent messageThrow = (IntermediateThrowEvent) endEvent.getIncoming().getSourceRef();
                for(MessageFlow mf : collaboration.getMessageFlows()){
                    if(mf.getSourceRef().getId().equals(messageThrow.getId())){
                        mf.setSourceRef(endEvent);
                    }
                }
                endEvent.setMessage();
                endEvent.getIncoming().setSourceRef(messageThrow.getIncoming().getSourceRef());
                fp.getFlows().remove(messageThrow.getIncoming());
                fp.getIntermediateCatchEvents().remove(messageThrow);
            }

            fp.setBeforeAndAfterElements();
      }

        // subsequent: exclusive to event-based where needed
        for(Participant object: Participants){

            FlowsProcessObject fp = object.getProcessRef();
            fp.setBeforeAndAfterElements();
            Pattern messageEvent = Pattern.compile("^Event_+");
            Pattern exclusiveGateway = Pattern.compile("^Gateway_+");
            int messageTargetCount = 1;

            for(int i = 0; i < fp.getFlows().size()-1; i++){
                SequenceFlow firstFlow = fp.getFlows().get(i);
                Matcher gateWayMatcher = exclusiveGateway.matcher(firstFlow.getSourceRef().getId());
                Matcher messageEventMatcher = messageEvent.matcher(firstFlow.getTargetRef().getId());

                if(gateWayMatcher.find() && messageEventMatcher.find()) {
                    ExclusiveGateway gateway = (ExclusiveGateway) firstFlow.getSourceRef();
                    for (int j = i + 1; j < fp.getFlows().size(); j++) {
                        SequenceFlow secondFlow = fp.getFlows().get(j);
                        if(secondFlow.getSourceRef().getId().equals(firstFlow.getSourceRef().getId())) {
                            messageEventMatcher = messageEvent.matcher(secondFlow.getTargetRef().getId());
                            if (messageEventMatcher.find()) {
                                messageTargetCount++;
                            } else {
                                messageTargetCount--;
                            }
                        }
                    }
                    if(messageTargetCount >= 2){
                        gateway.setEventBased();
                        System.out.println(gateway.getId());
                        messageTargetCount = 1;
                    }
                }
            }

            fp.setBeforeAndAfterElements();
        }

        // subsequent for sendTask
        Pattern p = Pattern.compile("^Activity+");
        Matcher m;
        for(MessageFlow mf : collaboration.getMessageFlows()){
            m = p.matcher(mf.getSourceRef().getId());
            if(m.find()){
                BPMNElement sendTask = mf.getSourceRef();
                for(Participant object : Participants){
                    FlowsProcessObject fp = object.getProcessRef();
                    for(Task task : fp.getTasks()){
                        if(task.getId().equals(sendTask.getId())){
                            mf.setSourceRef(task.getSendingMessage());
                        }
                    }
                }
            }
        }

        setParallelMultipleMessageStartEvent();

        appendXMLElements(definitionsElement);

        BPMNDiagramCoordinationAndUser di = new BPMNDiagramCoordinationAndUser();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration, true, false, false);

        createXml(file);

    }

    private void setParallelMultipleMessageStartEvent(){
        Pattern p = Pattern.compile("^StartEvent+");
        Matcher m;
        for(MessageFlow mf : collaboration.getMessageFlows()){
            m = p.matcher(mf.getTargetRef().getId());
            if(m.find()){
                for(MessageFlow mf2 : collaboration.getMessageFlows()){
                    m = p.matcher(mf2.getTargetRef().getId());
                    if(m.find() && !mf2.getId().equals(mf.getId()) && mf2.getTargetRef().getId().equals(mf.getTargetRef().getId())){
                        ((StartEvent) mf.getTargetRef()).setParallelMessage();
                        return;
                    }
                }
            }
        }
    }

    private IntermediateCatchEvent setParallelPort(Port port, FlowsProcessObject fp, Task task) {

        IntermediateCatchEvent messageCatch = null;
        if (port.getCntOther() == 1) {

            // find other relation for message flows
            for (Relation relation : port.getIncoming()) {

                if (relation.getRelationType() == RelationType.OTHER) {
                    messageCatch = new IntermediateCatchEvent(task.getUser());
                    collaboration.getMessageFlows().add(new MessageFlow(relation.getTask(), messageCatch));
                }

            }
        } else if (port.getCntOther() > 1) {

            messageCatch = new IntermediateCatchEvent(true, task.getUser());

            // find other relations for message flows
            for (Relation relation : port.getIncoming()) {

                if (relation.getRelationType() == RelationType.OTHER) {
                    collaboration.getMessageFlows().add(new MessageFlow(relation.getTask(), messageCatch));
                }

            }
        }
        fp.getIntermediateCatchEvents().add(messageCatch);
        return messageCatch;
    }

    public void appendXMLElements(Element definitionsElement) {

        definitionsElement.appendChild(collaboration.getElementCollaboration());

        for (MessageFlow mf : collaboration.getMessageFlows()) {
            collaboration.getElementCollaboration().appendChild(mf.getElement());
        }

        for (Pool participant : pools) {

            participant.getProcessRef().setElementFlowsProcess();
            //setLane(participant);
            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }

    }
    public Collaboration getCollaboration() {
        return collaboration;
    }

}

