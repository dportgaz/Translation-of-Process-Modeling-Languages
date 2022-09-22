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

import static org.bpmn.bpmn_elements.collaboration.Collaboration.objects;
import static org.bpmn.steps.BPMN.createXml;
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
        /*
        for (Task task : coordinationProcess) {

            int cntPorts = task.getPorts().size();
            if (cntPorts == 0) {
                continue;
            }

            FlowsProcessObject fp = task.getParticipant().getProcessRef();
            ArrayList<SequenceFlow> flows = fp.getFlows();

            if (cntPorts >= 2 && task.getCntOtherRelations() >= 1) {

                SequenceFlow flow = fp.getFlowByTarget(task);
                ExclusiveGateway gateSplit = new ExclusiveGateway();
                ExclusiveGateway gateJoin = new ExclusiveGateway();

                fp.getGateways().add(gateSplit);
                fp.getGateways().add(gateJoin);

                SequenceFlow joinToTask = new SequenceFlow(gateJoin, task);
                SequenceFlow taskBeforeToSplit = new SequenceFlow(task.getBeforeElement(), gateSplit);
                fp.getFlows().add(joinToTask);
                fp.getFlows().add(taskBeforeToSplit);

                fp.getFlows().remove(flow);

                if (task.getIsSubprocess()) {
                    //TODO
                } else {

                    for (Port port : task.getPorts()) {

                        if(port.getIncoming().size() > 1){

                        }

                        for(Relation relation : port.getIncoming()){
                            if(relation.getRelationType() == RelationType.OTHER){
                                IntermediateCatchEvent messageCatch = new IntermediateCatchEvent();
                                fp.getIntermediateCatchEvents().add(messageCatch);
                                MessageFlow messageFlow = new MessageFlow(relation.getTask(), messageCatch);
                                collaboration.getMessageFlows().add(messageFlow);
                                collaboration.getElementCollaboration().appendChild(messageFlow.getElement());
                                fp.getFlows().add(new SequenceFlow(gateSplit, messageCatch));
                                fp.getFlows().add(new SequenceFlow(messageCatch, gateJoin));
                            }else{
                                fp.getFlows().add(new SequenceFlow(gateSplit, gateJoin));
                            }

                        }

                    }

                }
            }
        }

         */

        for (Participant object : objects) {

            for (Task task : object.getProcessRef().getTasks()) {

                if (task.getIsSubprocess()) {
                    //System.out.println(object + " ; " + task + " : " + task.getSteps());
                    for (Step step : task.getSteps()) {

                        Participant stepParticipant = step.getStepParticipant();
                        if (stepParticipant != null) {
                            //System.out.println("\t" + task + " , " + step + " , " + stepParticipant);

                        }

                    }
                }

            }

        }

        setProcesses(definitionsElement);

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

        /*
        FillBPMNDI di = new FillBPMNDI();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration);

         */

        createXml(file);

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
