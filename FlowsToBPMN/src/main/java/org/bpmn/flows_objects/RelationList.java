package org.bpmn.flows_objects;

import org.bpmn.flows_objects.flowsobject.AbstractFlowsObject;

import java.util.ArrayList;

public class RelationList extends AbstractRelation {

    public ArrayList<AbstractRelation> DataModelActionLogs;

    @Override
    public String toString() {

        String retString = "";
        retString += DataModelActionLogs;

        return retString;
    }

    public ArrayList<AbstractRelation> getList() {
        return this.DataModelActionLogs;
    }
}
