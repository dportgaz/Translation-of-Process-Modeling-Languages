package org.bpmn.step_one.bpmndi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BPMNShape {

    Document doc;
    private String elementId;
    private String elementIdDi;
    private String isHorizontal;
    private Bounds bounds;
    private Element bpmnElement;


    public BPMNShape(Document doc, String elementId, String isHorizontal, Bounds bounds) {

        this.doc = doc;
        this.elementId = elementId;
        this.elementIdDi = elementId + "_di";
        this.isHorizontal = isHorizontal;
        this.bounds = bounds;
        this.bpmnElement = doc.createElement("bpmndi:BPMNShape");

    }

    public BPMNShape(Document doc, String elementId, Bounds bounds) {

        this.doc = doc;
        this.elementId = elementId;
        this.elementIdDi = elementId + "_di";
        this.bounds = bounds;
        this.bpmnElement = doc.createElement("bpmndi:BPMNShape");

    }

    public void setShapeParticipant() {

        bpmnElement.setAttribute("id", elementIdDi);
        bpmnElement.setAttribute("bpmnElement", elementId);
        bpmnElement.setAttribute("isHorizontal", isHorizontal);

    }

    public void setShape() {

        bpmnElement.setAttribute("id", elementIdDi);
        bpmnElement.setAttribute("bpmnElement", elementId);

    }

    public void setBounds() {

        Element elementBounds = bounds.getElementBounds();
        elementBounds.setAttribute("height", String.valueOf(bounds.getHeight()));
        // x, width, height are default values set as attributes of the class FillBPMNDI
        elementBounds.setAttribute("width", String.valueOf(bounds.getWidth()));
        elementBounds.setAttribute("x", String.valueOf(bounds.getX()));
        elementBounds.setAttribute("y", String.valueOf(bounds.getY()));
        bpmnElement.appendChild(elementBounds);

    }

    public Element getBpmnElement() {
        return bpmnElement;
    }
}
