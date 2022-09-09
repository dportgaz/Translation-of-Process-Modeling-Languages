package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.bpmn.steps.Execution;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException {


        Execution execution = new Execution();
        String filePHoodle = "FlowsToBPMN/src/resources/flows/PhoodleDoodle.json";
        execution.createBPMN(filePHoodle, "PHoodle_RENEW.xml");

/*
        String fileRecruitment = "FlowsToBPMN/src/resources/flows/recruitment.json";
        fillXMLStepOneRenew.createBPMN(fileRecruitment, "Recruitment_RENEW.xml");
*/

    }
}