package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bpmn.steps.BPMN;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException {


        BPMN bpmn = new BPMN();
        String flowsFile = "FlowsToBPMN/src/resources/flows/333.json";
        bpmn.create(flowsFile, "PHoodle_RENEW.xml");

    }
} 