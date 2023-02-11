package org.bpmn.transformation;

import org.bpmn.bpmn_elements.*;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Pool;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.event.IntermediateCatchEvent;
import org.bpmn.bpmn_elements.transition.MessageFlow;
import org.bpmn.bpmn_elements.transition.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.bpmndi.BPMNDiagramCoordinationAndUser;
import org.bpmn.parse_json.Parser;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.bpmn_elements.collaboration.Collaboration.pools;
import static org.bpmn.transformation.FlowsToBpmn.createXml;
import static org.bpmn.transformation.FlowsToBpmn.doc;

public class UserAssignmentTransformation implements Transformation {

    String file;
    Element definitionsElement;
    Parser parser;
    CoordinationTransformation coordinationTransformation;
    static String bpmnDiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

    private Collaboration collaboration;

    public UserAssignmentTransformation(CoordinationTransformation coordinationTransformation, String file, Element definitionsElement) {
        this.coordinationTransformation = coordinationTransformation;
        this.file = file;
        this.definitionsElement = definitionsElement;
        this.parser = new Parser();
        this.collaboration = coordinationTransformation.getCollaboration();
    }

    public void transform() throws TransformerException {

        appendXMLElements(definitionsElement);

        BPMNDiagramCoordinationAndUser di = new BPMNDiagramCoordinationAndUser();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration, true, false, true);

        createXml(file);

    }

    public void appendXMLElements(Element definitionsElement) {

        definitionsElement.appendChild(collaboration.getElementCollaboration());

        for (MessageFlow mf : collaboration.getMessageFlows()) {
            collaboration.getElementCollaboration().appendChild(mf.getElement());
        }

        for (Pool participant : pools) {

            participant.getProcessRef().setElementFlowsProcess();
            setLanes(participant);
            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }

    }

    private void setLanes(Participant object) {

        HashSet<Lane> users = new HashSet<>();
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
                    Lane temp = target.getUser();
                    while (temp == null) {
                        SequenceFlow tempFlow = object.getProcessRef().getFlowBySource(target);
                        if(tempFlow == null){
                            break;
                        }
                        target = tempFlow.getTargetRef();
                        temp = target.getUser();
                    }
                    if (temp != null && !gateway.getMarked()) {
                        gateway.setUser(temp);
                        temp.getElements().add(gateway);
                        gateway.setMarked();
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

        HashMap<Lane, org.bpmn.process.Lane> lanes = new HashMap<>();
        for (Lane u : users) {
            org.bpmn.process.Lane lane = new org.bpmn.process.Lane(u);
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

