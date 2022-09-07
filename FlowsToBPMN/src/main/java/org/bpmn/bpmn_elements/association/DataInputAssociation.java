package org.bpmn.bpmn_elements.association;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.randomidgenerator.RandomIdGenerator;

import org.w3c.dom.Element;

import static org.bpmn.fillxml.ExecSteps.doc;

public class DataInputAssociation {

    String id;
    DataObject sourceRef;
    Element elementDataInputAssociation;

    Element elementSource;

    public DataInputAssociation() {

        this.id = "DataInputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
        setElementDataInputAssociation();

    }

    private void setElementDataInputAssociation() {

        this.elementDataInputAssociation = doc.createElement("bpmn:dataInputAssociation");
        this.elementDataInputAssociation.setAttribute("id", this.id);

    }

    public void setSourceRef(DataObject source) {
        this.sourceRef = source;
    }

    public DataObject getSource() {
        return sourceRef;
    }

    public void setElementSource(Element elementSource) {
        this.elementSource = elementSource;
    }

    public Element getElementSource() {
        return elementSource;
    }

    public Element getElementDataInputAssociation() {
        return elementDataInputAssociation;
    }

    public String getId() {
        return id;
    }
}
