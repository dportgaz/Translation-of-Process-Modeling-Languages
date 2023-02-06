package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bpmn.flows_process_model.FlowsProcessModel;
import org.bpmn.transformation.FlowsToBpmn;


public class Prototype {
    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException {

        // Absolute path must be adjusted by user. E.g., user wants to transform the process 'Recruitment'.
        String flowsJSON = "FlowsToBPMN/src/resources/philharmonicflows/Recruitment.json";

        FlowsToBpmn flowsToBpmn = new FlowsToBpmn();
        flowsToBpmn.transform(new FlowsProcessModel(flowsJSON));

        System.out.println("Transforming Flows process model...");
        System.out.println("Transformation complete.");
    }
} 