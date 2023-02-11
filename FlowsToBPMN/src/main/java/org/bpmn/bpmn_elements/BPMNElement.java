package org.bpmn.bpmn_elements;


import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.transition.SequenceFlow;
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

    Lane getUser();

    String getName();

    Double getCreateId();

    void setOutgoing(SequenceFlow sf);

    void setIncoming(SequenceFlow sf);

    SequenceFlow getOutgoing();

    SequenceFlow getIncoming();

}
