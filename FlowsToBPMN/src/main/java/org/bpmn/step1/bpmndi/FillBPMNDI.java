package org.bpmn.step1.bpmndi;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;

public class FillBPMNDI {

    public void fillBPMNDI(Document doc, Element bpmndiagram, String filename, Element rootElement) throws FileNotFoundException {

        String bpmndiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

        bpmndiagram = doc.createElement("bpmndi:BPMNDiagram");
        bpmndiagram.setAttribute("id", bpmndiagramID);
        rootElement.appendChild(bpmndiagram);

    }
}
