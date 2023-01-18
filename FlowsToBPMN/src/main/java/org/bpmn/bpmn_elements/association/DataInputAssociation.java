package org.bpmn.bpmn_elements.association;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.randomidgenerator.RandomIdGenerator;

import org.w3c.dom.Element;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class DataInputAssociation extends Association{

    String id;
    DataObject sourceRef;
    Element elementDataInputAssociation;
    String associatedTaskId;

    public DataInputAssociation() {

        this.id = "DataInputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
        setElementDataInputAssociation();

    }

    private void setElementDataInputAssociation() {

        this.elementDataInputAssociation = doc.createElement("bpmn:dataInputAssociation");
        this.elementDataInputAssociation.setAttribute("id", this.id);

    }

    public String getAssociatedTaskId() {
        return associatedTaskId;
    }

    public DataObject getSource() {
        return sourceRef;
    }

    public Element getElementDataInputAssociation() {
        return elementDataInputAssociation;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
