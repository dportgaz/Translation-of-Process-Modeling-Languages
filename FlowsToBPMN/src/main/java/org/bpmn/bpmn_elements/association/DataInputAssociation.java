package org.bpmn.bpmn_elements.association;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.randomidgenerator.RandomIdGenerator;

import org.w3c.dom.Element;

import static org.bpmn.steps.BPMN.doc;

public class DataInputAssociation extends Association{

    String id;
    DataObject sourceRef;

    String inputAssociationSource;

    Element elementDataInputAssociation;

    Element elementSource;

    String associatedTaskId;

    public DataInputAssociation() {

        this.id = "DataInputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
        setElementDataInputAssociation();

    }

    private void setElementDataInputAssociation() {

        this.elementDataInputAssociation = doc.createElement("bpmn:dataInputAssociation");
        this.elementDataInputAssociation.setAttribute("id", this.id);

    }

    public void setAssociatedTaskId(String associatedTaskId) {
        this.associatedTaskId = associatedTaskId;
    }

    public String getAssociatedTaskId() {
        return associatedTaskId;
    }

    public void setInputAssociationSource(DataObject dataObject) {
        this.inputAssociationSource = dataObject.getRefId();
        setSourceRef(dataObject);
        if (this.inputAssociationSource != null) {
            Element source = doc.createElement("bpmn:sourceRef");
            source.setTextContent(inputAssociationSource);
            getElementDataInputAssociation().appendChild(source);
        }
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

    @Override
    public String toString() {
        return this.id;
    }
}
