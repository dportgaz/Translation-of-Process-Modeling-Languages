package org.bpmn.step_one.bpmndi;

import org.bpmn.step_one.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step_one.process.activity.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

public class BPMNShape {

    Document doc;
    private String elementId;
    private String elementIdDi;
    private String isHorizontal;
    private Bounds bounds;
    private Element bpmnElement;
    private boolean isSubprocess = false;


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

    public BPMNShape(Document doc, String elementId, Bounds bounds, ArrayList<Task> tasks) {

        this.doc = doc;
        this.elementId = elementId;
        this.elementIdDi = elementId + "_di";
        this.bounds = bounds;
        this.bpmnElement = doc.createElement("bpmndi:BPMNShape");

        for(Task task : tasks){
            if(task.getId().equals(elementId) && task.getIsSubprocess()){
                this.isSubprocess = true;
            }
        }
    }

    public void setShapeParticipant() {

        bpmnElement.setAttribute("id", elementIdDi);
        bpmnElement.setAttribute("bpmnElement", elementId);
        //bpmnElement.setAttribute("isHorizontal", isHorizontal);
        if(isSubprocess) {
            bpmnElement.setAttribute("isExpanded", "false");
        }

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
}
