package org.bpmn.bpmn_elements.transition;

import org.bpmn.bpmn_elements.dataobject.DataObject;

public class Association {

    String associationId;

    DataObject dataObject;

    public Association(String associationId,DataObject dataObject){

        this.associationId = associationId;
        this.dataObject = dataObject;

    }

    public String getAssociationId() {
        return associationId;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setAssociationId(String associationId) {
        this.associationId = associationId;
    }

    public void setDataObjectAssociation(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public String toString() {
        return "(" + associationId + " , " + dataObject + ")";
    }
}
