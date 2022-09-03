package org.bpmn.bpmndi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Bounds {

    private final Element elementBounds;
    Document doc;
    private double x;
    private double y;
    private double width;
    private double height;

    public Bounds(Document doc, double x, double y, double width, double height) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.elementBounds = doc.createElement("dc:Bounds");

    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Element getElementBounds() {
        return elementBounds;
    }

}