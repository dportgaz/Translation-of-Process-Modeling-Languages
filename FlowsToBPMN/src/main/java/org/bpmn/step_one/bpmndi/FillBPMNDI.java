package org.bpmn.step_one.bpmndi;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step_one.collaboration.participant.FlowsParticipant;
import org.bpmn.step_one.process.FillFlowsProcess;
import org.bpmn.step_one.process.FlowsProcess;
import org.bpmn.step_one.process.activity.Task;
import org.bpmn.step_one.process.flow.SequenceFlow;
import org.bpmn.step_one.process.gateway.ExclusiveGateway;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;

import static org.bpmn.step_one.fillxml.fillXML.getCollaborationID;

public class FillBPMNDI {

    final double participantX = 70.0;
    final double participantWidth = 1530.0;
    final double participantHeight = 500.0;
    final double participantYInc = 550.0;
    final double startEventYInc = 200.0;
    final double startEventX = 200.0;
    final double startEventWidth = 36.0;
    final double startEventHeight = 36.0;

    public void fillBPMNDI(Document doc, String bpmndiagramID, String filename, Element rootElement) throws FileNotFoundException {

        Element bpmndiagram = doc.createElement("bpmndi:BPMNDiagram");
        bpmndiagram.setAttribute("id", bpmndiagramID);
        rootElement.appendChild(bpmndiagram);

        Element bpmnlane = doc.createElement("bpmndi:BPMNPlane");
        bpmnlane.setAttribute("id", "BPMNlane_" + RandomIdGenerator.generateRandomUniqueId(6));
        bpmnlane.setAttribute("bpmnElement", getCollaborationID());
        bpmndiagram.appendChild(bpmnlane);

        double participantStartY = 100.0;
        double startEventStartY = participantHeight / 5;
        for (FlowsParticipant participant : FillFlowsParticipant.getParticipants()) {

            // add participant shape
            addParticipantsShape(doc, bpmnlane, participant, participantStartY);
            participantStartY += participantYInc;


            // add StartEvent shape; every StartEvent starts at the same position of every pool
            addStartEventShape(doc, bpmnlane, participant, startEventStartY);
            startEventStartY += startEventYInc;

            /*
            // add flows edge
            addFlowsEdge(doc, bpmnlane, p);

            // add activities
            addActivitiesShape(doc, bpmnlane, p);

            // add gateway shapes
            addGatewaysShape(doc, bpmnlane, p);

            // add endevent shape
            addEndEventShape(doc, bpmnlane, p);

            // add labels
            // addLabels(doc, bpmnlane, p);
            */

        }

    }

    public void addActivitiesShape(Document doc, Element bpmnlane, FlowsParticipant p) {

        FlowsProcess fp = FillFlowsProcess.getProcessById(p.getProcessRef());

        for (Task task : fp.getTaskList()) {

            Element taskTemp = doc.createElement("bpmndi:BPMNShape");
            taskTemp.setAttribute("id", task.getId() + "_di");
            taskTemp.setAttribute("bpmnElement", task.getId());
            bpmnlane.appendChild(taskTemp);

        }

    }

    public void addEndEventShape(Document doc, Element bpmnlane, FlowsParticipant p) {

        Element end = doc.createElement("bpmndi:BPMNShape");
        end.setAttribute("id", FillFlowsProcess.getProcessById(p.getProcessRef()).getEndEvent().getId() + "_di");
        end.setAttribute("bpmnElement", FillFlowsProcess.getProcessById(p.getProcessRef()).getEndEvent().getId());
        bpmnlane.appendChild(end);

    }

    public void addGatewaysShape(Document doc, Element bpmnlane, FlowsParticipant p) {

        FlowsProcess fp = FillFlowsProcess.getProcessById(p.getProcessRef());

        for (ExclusiveGateway gate : fp.getGateways()) {

            Element flow = doc.createElement("bpmndi:BPMNShape");
            flow.setAttribute("id", gate.getId() + "_di");
            flow.setAttribute("bpmnElement", gate.getId());
            bpmnlane.appendChild(flow);

        }

    }

    public void addLabels(Document doc, Element bpmnlane, FlowsParticipant p) {

        Element start = doc.createElement("bpmndi:BPMNLabel");
        bpmnlane.appendChild(start);

    }

    public void addStartEventShape(Document doc, Element rootElement, FlowsParticipant p, double starteventStartY) {

        String startEventId = FillFlowsProcess.getProcessById(p.getProcessRef()).getStartEvent().getId();
        Bounds bounds = new Bounds(doc, this.startEventX, starteventStartY, this.startEventWidth, this.startEventHeight);
        BPMNShape shape = new BPMNShape(doc, startEventId, bounds);
        shape.setShape();
        shape.setBounds();
        rootElement.appendChild(shape.getBpmnElement());

    }

    public void addParticipantsShape(Document doc, Element rootElement, FlowsParticipant p, double participantY) {

        Bounds bounds = new Bounds(doc, this.participantX, participantY, this.participantWidth, this.participantHeight);
        BPMNShape shape = new BPMNShape(doc, p.getParticipantID(), "true", bounds);
        shape.setShapeParticipant();
        shape.setBounds();
        rootElement.appendChild(shape.getBpmnElement());

    }

    public void addFlowsEdge(Document doc, Element rootElement, FlowsParticipant p) {

        FlowsProcess fp = FillFlowsProcess.getProcessById(p.getProcessRef());

        for (SequenceFlow sf : fp.getSequenceFlowList()) {

            Element flow = doc.createElement("bpmndi:BPMNEdge");
            flow.setAttribute("id", sf.getId() + "_di");
            flow.setAttribute("bpmnElement", sf.getId());
            rootElement.appendChild(flow);

        }


    }
}
