package org.bpmn.steps;

import org.bpmn.bpmn_elements.*;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Object;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.event.IntermediateCatchEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
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
    }

    public void execute() throws TransformerException {


        Collaboration collaboration = new Collaboration();
        collaboration.setParticipants(objectTypeObjects);
        Element collaborationElement = collaboration.getElementCollaboration();

        definitionsElement.appendChild(collaborationElement);
        setProcesses(definitionsElement);

        coordinationProcess = parser.getCoordinationTasks(coordinationProcessObjects);

        FillBPMNDI di = new FillBPMNDI();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration);

        /*

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

        // _______________________________
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

                fp.getFlows().add(new SequenceFlow(gateJoin, task));
                fp.getFlows().add(new SequenceFlow(task.getBeforeElement(), gateSplit));

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
                                fp.getFlows().add(new SequenceFlow(gateSplit, messageCatch));
                                fp.getFlows().add(new SequenceFlow(messageCatch, gateJoin));
                            }else{
                                fp.getFlows().add(new SequenceFlow(gateSplit, gateJoin));
                            }

                        }

                    }

                }
            }
            for (SequenceFlow flow : fp.getFlows()) {
                fp.getElementFlowsProcess().appendChild(flow.getElementSequenceFlow());
            }
        }
        */

        createXml(file);

    }

    public void setProcesses(Element definitionsElement) {

        for (Object participant : objects) {

            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }

    }

}
