package org.bpmn.step1.bpmndi;

import org.bpmn.randomidgenerator.RandomIdGenerator;
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

    }
}
