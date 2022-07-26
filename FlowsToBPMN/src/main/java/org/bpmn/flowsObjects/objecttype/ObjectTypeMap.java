package org.bpmn.flowsObjects.objecttype;

import java.util.ArrayList;
import java.util.HashMap;

public class ObjectTypeMap extends AbstractObjectType {

    public HashMap<String, ArrayList<AbstractObjectType>> ObjectTypeActionLogs;

    @Override
    public String toString() {

        String retString = "";

        for (String name : ObjectTypeActionLogs.keySet()) {
            String key = name.toString();
            String value = ObjectTypeActionLogs.get(name).toString();
            retString += key + "= {" + value + "}" + "\n";
        }

        return retString;
    }

}
