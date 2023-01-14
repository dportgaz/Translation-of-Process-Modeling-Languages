package org.bpmn.flows_entities;

import java.util.ArrayList;

public class Relationships extends AbstractRelationship {

    public ArrayList<AbstractRelationship> DataModelActionLogs;

    @Override
    public String toString() {

        String retString = "";
        retString += DataModelActionLogs;

        return retString;
    }

    public ArrayList<AbstractRelationship> getList() {
        return this.DataModelActionLogs;
    }
}
