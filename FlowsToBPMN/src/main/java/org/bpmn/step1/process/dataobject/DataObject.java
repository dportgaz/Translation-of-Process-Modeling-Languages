package org.bpmn.step1.process.dataobject;

import org.bpmn.randomidgenerator.RandomIdGenerator;

public class DataObject {

    String id;

    public DataObject() {
        this.id = "DataObject_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public String getId() {
        return this.id;
    }
}
