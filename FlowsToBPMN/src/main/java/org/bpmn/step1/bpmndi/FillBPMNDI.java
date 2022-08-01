package org.bpmn.step1.bpmndi;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.process.FillFlowsProcess;
import org.bpmn.step1.process.FlowsProcess;
import org.bpmn.step1.process.activity.Task;
import org.bpmn.step1.process.flow.SequenceFlow;
import org.bpmn.step1.process.gateway.ExclusiveGateway;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;

import static org.bpmn.step1.fillxml.fillXML.getCollaborationID;

public class FillBPMNDI {

    public void fillBPMNDI(Document doc, String bpmndiagramID, String filename, Element rootElement) throws FileNotFoundException {

        Element bpmndiagram = doc.createElement("bpmndi:BPMNDiagram");
        bpmndiagram.setAttribute("id", bpmndiagramID);
        rootElement.appendChild(bpmndiagram);

        Element bpmnlane = doc.createElement("bpmndi:BPMNPlane");
        bpmnlane.setAttribute("id", "BPMNlane_" + RandomIdGenerator.generateRandomUniqueId(6));
        bpmnlane.setAttribute("bpmnElement", getCollaborationID());
        bpmndiagram.appendChild(bpmnlane);


        for (FlowsParticipant p : FillFlowsParticipant.getParticipants()) {

            // add participant shape
            addParticipantsShape(doc, bpmnlane, p);

            // add flows edge
            addFlowsEdge(doc, bpmnlane, p);

            // add startevent shape
            addStartEventShape(doc, bpmnlane, p);

            // add activities
            addActivitiesShape(doc, bpmnlane, p);

            // add gateway shapes
            addGatewaysShape(doc, bpmnlane, p);

            // add endevent shape
            addEndEventShape(doc, bpmnlane, p);

            // add labels
            addLabels(doc, bpmnlane, p);

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

    public void addStartEventShape(Document doc, Element rootElement, FlowsParticipant p) {

        Element start = doc.createElement("bpmndi:BPMNShape");
        start.setAttribute("id", FillFlowsProcess.getProcessById(p.getProcessRef()).getStartEvent().getId() + "_di");
        start.setAttribute("bpmnElement", FillFlowsProcess.getProcessById(p.getProcessRef()).getStartEvent().getId());
        rootElement.appendChild(start);

    }

    public void addParticipantsShape(Document doc, Element rootElement, FlowsParticipant p) {

        Element participant = doc.createElement("bpmndi:BPMNShape");
        participant.setAttribute("id", p.getParticipantID() + "_di");
        participant.setAttribute("bpmnElement", p.getParticipantID());
        participant.setAttribute("isHorizontal", "true");
        rootElement.appendChild(participant);

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
