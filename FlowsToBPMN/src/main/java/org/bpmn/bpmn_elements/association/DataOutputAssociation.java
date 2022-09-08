package org.bpmn.bpmn_elements.association;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.randomidgenerator.RandomIdGenerator;

import org.w3c.dom.Element;

import static org.bpmn.fillxml.ExecSteps.doc;

public class DataOutputAssociation extends Association{

    String id;
    DataObject targetRef;
    Element elementDataOutputAssociation;

    Element elementTarget;

    public DataOutputAssociation(){

        this.id = "DataOutputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
        setElementDataOutputAssociation();

    }

    private void setElementDataOutputAssociation() {

        this.elementDataOutputAssociation = doc.createElement("bpmn:dataOutputAssociation");
        this.elementDataOutputAssociation.setAttribute("id", this.id);

    }

    public void setTargetRef(DataObject targetRef) {
        this.targetRef = targetRef;
    }

    public DataObject getTargetRef() {
        return targetRef;
    }

    public void setElementTarget(Element elementTarget) {
        this.elementTarget = elementTarget;
    }

    public Element getElementTarget() {
        return elementTarget;
    }

    public Element getElementDataOutputAssociation() {
        return elementDataOutputAssociation;
    }

    public String getId() {
        return id;
    }
}
