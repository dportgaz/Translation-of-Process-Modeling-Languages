package org.bpmn.step1.bpmndi;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.process.FillFlowsProcess;
import org.bpmn.step1.process.FlowsProcess;
import org.bpmn.step1.process.flow.SequenceFlow;
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
            addParticipantShape(doc, bpmnlane, p);
        }

        // add flows edge
        addFlowsEdge(doc, bpmnlane);
    }

    public void addParticipantShape(Document doc, Element rootElement, FlowsParticipant p) {

        Element participant = doc.createElement("bpmndi:BPMNShape");
        participant.setAttribute("id", p.getParticipantID() + "_di");
        participant.setAttribute("bpmnElement", p.getParticipantID());
        participant.setAttribute("isHorizontal", "true");
        rootElement.appendChild(participant);

    }

    public void addFlowsEdge(Document doc, Element rootElement) {

        for (FlowsProcess fp : FillFlowsProcess.getProcesses()) {
            for (SequenceFlow sf : fp.getSequenceFlowList()) {

                Element flow = doc.createElement("bpmndi:BPMNEdge");
                flow.setAttribute("id", sf.getId() + "_di");
                flow.setAttribute("bpmnElement", sf.getId());
                flow.setAttribute("isHorizontal", "true");
                rootElement.appendChild(flow);

            }
        }

    }
}
