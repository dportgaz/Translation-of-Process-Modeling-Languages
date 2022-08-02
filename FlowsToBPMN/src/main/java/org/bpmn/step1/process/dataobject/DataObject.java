package org.bpmn.step1.process.dataobject;

import org.bpmn.randomidgenerator.RandomIdGenerator;

public class DataObject {

    String id;

    String refId;

    public DataObject() {
        this.id = "DataObject_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.refId = "DataObjectReference_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public String getId() {
        return this.id;
    }

    public String getRefId() {
        return refId;
    }
}
