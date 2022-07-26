package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bpmn.step1.fillxml.fillXML;

public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException {

        String file = "FlowsToBPMN/src/resources/flows/PHoodle.json";
        fillXML.createBPMN(file);

        /* Tests:
        Double temp1 = (Double) flowsObjects3.ObjectTypeActionLogs.get("8908346732141720536").get(7).getCreatedEntityId();
        Double temp2 = (Double) flowsObjects3.ObjectTypeActionLogs.get("8908346732141720536").get(8).getParameters().get(0);
        Double temp3 = (Double) flowsObjects3.ObjectTypeActionLogs.get("8908346732141720536").get(8).getUpdatedEntityId();

        System.out.println(temp1 + " " + temp2 + " " + temp3 + " " + (Double.compare(temp1, temp2)) + " " + (Double.compare(temp1, temp3)) + " " + (Double.compare(temp2, temp3)));

        System.out.println(flowsObjects3);
        */
    }
}
