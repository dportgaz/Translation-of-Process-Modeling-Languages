package org.bpmn.bpmndi;

import org.w3c.dom.Element;

import static org.bpmn.steps.BPMN.doc;

public class Edge {

    private Element bpmnElement;

    public Edge(String elementId) {

        this.bpmnElement = doc.createElement("bpmndi:BPMNEdge");
        this.bpmnElement.setAttribute("id", elementId + "_di");
        this.bpmnElement.setAttribute("bpmnElement", elementId);

    }
    public Element getBpmnElement() {
        return bpmnElement;
    }

}