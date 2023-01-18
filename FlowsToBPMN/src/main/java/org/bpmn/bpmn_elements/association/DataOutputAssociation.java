package org.bpmn.bpmn_elements.association;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.randomidgenerator.RandomIdGenerator;

import org.w3c.dom.Element;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class DataOutputAssociation extends Association{

    String id;
    DataObject targetRef;
    String outputAssociationTarget;
    Element elementDataOutputAssociation;


    public DataOutputAssociation(){

        this.id = "DataOutputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
        setElementDataOutputAssociation();

    }

    private void setElementDataOutputAssociation() {

        this.elementDataOutputAssociation = doc.createElement("bpmn:dataOutputAssociation");
        this.elementDataOutputAssociation.setAttribute("id", this.id);

    }

    public void setOutputAssociationTarget(DataObject dataObject) {
        this.outputAssociationTarget = dataObject.getRefId();
        setTargetRef(dataObject);
        Element target = doc.createElement("bpmn:targetRef");
        target.setTextContent(outputAssociationTarget);
        getElementDataOutputAssociation().appendChild(target);
    }

    public void setTargetRef(DataObject targetRef) {
        this.targetRef = targetRef;
    }

    public Element getElementDataOutputAssociation() {
        return elementDataOutputAssociation;
    }

    public String getId() {
        return id;
    }
}
