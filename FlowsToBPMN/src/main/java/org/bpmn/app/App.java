package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.fillxml.fillXML;

public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException {

        String file = "FlowsToBPMN/src/resources/flows/PHoodle.json";
        ObjectTypeMap t = new ObjectTypeMap(file);
        //fillXML.createBPMN(file);
        //System.out.println(t.getObjects(file));
        //System.out.println(t.getObjectTypeObjects(file));

        String retString = "";

        for (String name : t.getAllObjects(file).keySet()) {
            String key = name;
            String value = t.getAllObjects(file).get(name).toString();
            retString += key + "= {" + value + "}" + "\n";
        }

        System.out.println(retString);

        String retString2 = "";

        for (String name : t.getObjectTypeObjects(file).keySet()) {
            String key = name;
            String value = t.getObjectTypeObjects(file).get(name).toString();
            retString2 += key + "= {" + value + "}" + "\n";
        }

        System.out.println(retString2);

        String retString3 = "";

        for (String name : t.getUserTypeObjects(file).keySet()) {
            String key = name;
            String value = t.getUserTypeObjects(file).get(name).toString();
            retString3 += key + "= {" + value + "}" + "\n";
        }

        System.out.println(retString3);

        /* Tests:
        Double temp1 = (Double) flowsObjects3.ObjectTypeActionLogs.get("8908346732141720536").get(7).getCreatedEntityId();
        Double temp2 = (Double) flowsObjects3.ObjectTypeActionLogs.get("8908346732141720536").get(8).getParameters().get(0);
        Double temp3 = (Double) flowsObjects3.ObjectTypeActionLogs.get("8908346732141720536").get(8).getUpdatedEntityId();

        System.out.println(temp1 + " " + temp2 + " " + temp3 + " " + (Double.compare(temp1, temp2)) + " " + (Double.compare(temp1, temp3)) + " " + (Double.compare(temp2, temp3)));

        System.out.println(flowsObjects3);
        */
    }
}
