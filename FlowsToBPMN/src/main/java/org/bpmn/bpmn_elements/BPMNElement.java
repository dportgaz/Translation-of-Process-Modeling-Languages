package org.bpmn.bpmn_elements;


import org.w3c.dom.Element;

import java.util.ArrayList;

public interface BPMNElement {

    String getId();

    ArrayList<BPMNElement> getBefore();

    ArrayList<BPMNElement> getAfter();

    Element getElement();

    void setElement();

}
