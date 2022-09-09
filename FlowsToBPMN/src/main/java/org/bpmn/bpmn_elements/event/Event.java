package org.bpmn.bpmn_elements.event;

import org.bpmn.bpmn_elements.BPMNElement;

import org.w3c.dom.Element;
import java.util.ArrayList;

public class Event implements BPMNElement {

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();

    String id;

    public String getId() {
        return id;
    }

    public ArrayList<BPMNElement> getAfter() {
        return after;
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
