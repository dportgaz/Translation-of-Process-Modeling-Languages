package org.bpmn.bpmn_elements;


import org.bpmn.bpmn_elements.collaboration.participant.User;
import org.w3c.dom.Element;

import java.util.ArrayList;

public interface BPMNElement {

    String getId();

    ArrayList<BPMNElement> getBefore();

    ArrayList<BPMNElement> getAfter();

    BPMNElement getBeforeElement();

    BPMNElement getAfterElement();

    void setBeforeElement(BPMNElement element);

    void setAfterElement(BPMNElement element);

    Element getElement();

    void setElement();

    User getUser();

    String getName();

    Double getCreateId();

}
