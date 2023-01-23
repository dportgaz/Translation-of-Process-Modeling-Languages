package org.bpmn.bpmndi;

import org.w3c.dom.Element;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class Shape {

    private String elementId;
    private String isPool;
    private boolean isSubprocess = false;
    boolean marked;
    private Bounds bounds;
    private Element bpmnElement;

    public Shape(String elementId, String isPool, Bounds bounds) {

        this.elementId = elementId;
        this.marked = false;
        this.isPool = isPool;
        this.bounds = bounds;
        this.bpmnElement = doc.createElement("bpmndi:BPMNShape");

    }

    public Shape(String elementId, boolean expandedSubprocess, Bounds bounds) {

        this.elementId = elementId;
        this.marked = false;
        this.bounds = bounds;
        this.bpmnElement = doc.createElement("bpmndi:BPMNShape");
        setElement(expandedSubprocess);

    }

    public Shape(String elementId, Bounds bounds) {

        this.elementId = elementId;
        this.marked = false;
        this.bounds = bounds;
        this.bpmnElement = doc.createElement("bpmndi:BPMNShape");

    }
    public void setMarked() {
        this.marked = true;
    }

    public boolean getMarked(){
        return this.marked;
    }

    private void setElement(boolean expandedSubprocess) {
        bpmnElement.setAttribute("bpmnElement", elementId);
        bpmnElement.setAttribute("id", elementId + "_di");
        if (expandedSubprocess) {
            bpmnElement.setAttribute("isExpanded", "true");
        } else {
            bpmnElement.setAttribute("isExpanded", "false");
        }
        bpmnElement.appendChild(bounds.getElementBounds());
    }

    public void setShapeParticipant() {

        bpmnElement.setAttribute("id", elementId + "_di");
        bpmnElement.setAttribute("bpmnElement", elementId);
        if (isSubprocess) {
            bpmnElement.setAttribute("isExpanded", "false");
        }

    }

    public void setShapePool() {

        bpmnElement.setAttribute("id", elementId + "_di");
        bpmnElement.setAttribute("bpmnElement", elementId);
        bpmnElement.setAttribute("isHorizontal", isPool);
        if (isSubprocess) {
            bpmnElement.setAttribute("isExpanded", "false");
        }

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

    public Bounds getBounds() {
        return bounds;
    }

    public String getElementId() {
        return elementId;
    }

    public Element getBpmnElement() {
        return bpmnElement;
    }

    @Override
    public String toString() {
        return elementId;
    }

    public String getIsPool() {
        return isPool;
    }
}