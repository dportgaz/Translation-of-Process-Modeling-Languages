package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.bpmn.step_one.fillxml.fillXMLStepOneRenew;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException, XPathExpressionException {


        String filePHoodle = "FlowsToBPMN/src/resources/flows/PhoodleDoodle.json";
        fillXMLStepOneRenew.createBPMN(filePHoodle, "PHoodle_RENEW.xml");
        // fillXMLStepTwo.createBPMN(filePHoodle, "PHoodle_Step2.xml");


/*
        String fileRecruitment = "FlowsToBPMN/src/resources/flows/recruitment.json";
        fillXMLStepOneRenew.createBPMN(fileRecruitment, "Recruitment_RENEW.xml");
*/

    }
}