package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;

import org.w3c.dom.Element;
import java.util.ArrayList;

public class Event implements BPMNElement {

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();

    String id;

    BPMNElement beforeElement;

    BPMNElement afterElement;

    public String getId() {
        return id;
    }

    public ArrayList<BPMNElement> getAfter() {
        return after;
    }

    @Override
    public BPMNElement getBeforeElement() {
        return beforeElement;
    }

    @Override
    public BPMNElement getAfterElement() {
        return afterElement;
    }

    @Override
    public void setBeforeElement(BPMNElement element) {
        this.beforeElement = element;
    }

    @Override
    public void setAfterElement(BPMNElement element) {
        this.afterElement = element;
    }

    @Override
    public Element getElement() {
        return null;
    }

    @Override
    public void setElement() {
    }
    public ArrayList<BPMNElement> getBefore() {
        return before;
    }

}
