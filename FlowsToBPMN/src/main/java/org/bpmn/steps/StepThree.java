package org.bpmn.steps;

import org.bpmn.bpmn_elements.*;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Object;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.event.IntermediateCatchEvent;
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
import static org.bpmn.steps.StepOne.allGateways;
import static org.bpmn.steps.StepOne.allParticipants;

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

    public StepThree(StepOne stepOne, String file, Element definitionsElement, HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects,
                     HashMap<Double, ArrayList<AbstractObjectType>> coordinationProcessObjects, ArrayList<AbstractRelation> relationsDataModel) {
        this.stepOne = stepOne;
        this.file = file;
        this.definitionsElement = definitionsElement;
        this.objectTypeObjects = objectTypeObjects;
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

        // complement coordination process with data model relation


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
                        messageCatch = setParallelPort(port);
                    } else {
                        messageCatch = new IntermediateCatchEvent();
                        MessageFlow messageFlow = new MessageFlow(port.getIncoming().get(0).getTask(), messageCatch);
                        collaboration.getMessageFlows().add(messageFlow);
                        collaboration.getElementCollaboration().appendChild(messageFlow.getElement());
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

                fp.setBeforeAndAfterElements();
            }
            else if (cntPorts == 1) {
                Port port = task.getPorts().get(0);
                if (port.getIncoming().size() >= 2) {
                    messageCatch = setParallelPort(port);
                } else {
                    Relation relation = port.getIncoming().get(0);
                    if (relation.getRelationType() == RelationType.OTHER) {
                        messageCatch = new IntermediateCatchEvent();

                        MessageFlow messageFlow = new MessageFlow(relation.getTask(), messageCatch);
                        collaboration.getMessageFlows().add(messageFlow);
                        collaboration.getElementCollaboration().appendChild(messageFlow.getElement());

                    }
                }
                if(messageCatch != null) {
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

        }

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


        // TODO: Very ugly, needs refactor; transforms XOR to event
        for(Participant object : allParticipants){

            ArrayList<SequenceFlow> flows = object.getProcessRef().getFlows();
            for(int i = 0; i < flows.size(); i++){

                boolean needsChange = false;
                String id = null;
                SequenceFlow flow = flows.get(i);
                Pattern p = Pattern.compile("^Gateway_");
                Matcher m = p.matcher(flow.getSourceRef().getId());
                Pattern p2 = Pattern.compile("^Event_");
                Matcher m2 = p2.matcher(flow.getTargetRef().getId());

                if(m.find() && m2.find()){
                    for(int j = i+1; j < flows.size(); j++){

                        SequenceFlow flowInner = flows.get(j);
                        Matcher m3 = p2.matcher(flowInner.getTargetRef().getId());
                        if(flow.getSourceRef().getId().equals(flowInner.getSourceRef().getId()) && m3.find()){
                            ((ExclusiveGateway)flowInner.getSourceRef()).setEventBased();
                            id = flowInner.getSourceRef().getId();
                            flowInner.setSourceRef(flowInner.getSourceRef());
                            needsChange = true;
                        }

                    }
                }

                if(needsChange){
                    for(SequenceFlow flowOuter : flows){
                        if(flowOuter.getTargetRef().getId().equals(flow.getSourceRef().getId())){
                            flowOuter.setTargetRef(flow.getSourceRef());
                        }
                    }
                    ((ExclusiveGateway)flow.getSourceRef()).setEventBased(id);
                    flow.setSourceRef(flow.getSourceRef());

                }

            }

        }




        setProcesses(definitionsElement);

        FillBPMNDI di = new FillBPMNDI();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration);

        createXml(file);

    }


    private IntermediateCatchEvent setParallelPort(Port port) {

        IntermediateCatchEvent messageCatch = new IntermediateCatchEvent();
        if (port.getCntOther() == 1) {

            // find other relation for messageflow
            for (Relation relation : port.getIncoming()) {

                if (relation.getRelationType() == RelationType.OTHER) {
                    MessageFlow messageFlow = new MessageFlow(relation.getTask(), messageCatch);
                    collaboration.getMessageFlows().add(messageFlow);
                    collaboration.getElementCollaboration().appendChild(messageFlow.getElement());
                }

            }
        } else if (port.getCntOther() > 1) {

            messageCatch = new IntermediateCatchEvent(true);

            // find other relations for messageflows
            for (Relation relation : port.getIncoming()) {

                if (relation.getRelationType() == RelationType.OTHER) {
                    MessageFlow messageFlow = new MessageFlow(relation.getTask(), messageCatch);
                    collaboration.getMessageFlows().add(messageFlow);
                    collaboration.getElementCollaboration().appendChild(messageFlow.getElement());
                }

            }
        }
        return messageCatch;
    }


    public void setProcesses(Element definitionsElement) {

        definitionsElement.appendChild(collaboration.getElementCollaboration());

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
