package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.bpmn.step_one.fillxml.fillXMLStepOne;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException, XPathExpressionException {


        String filePHoodle = "FlowsToBPMN/src/resources/flows/phoodle.json";
        fillXMLStepOne.createBPMN(filePHoodle, "PHoodle_Step1.xml");


/*
        String fileRecruitment = "FlowsToBPMN/src/resources/flows/recruitment.json";
        fillXML.createBPMN(fileRecruitment, "Recruitment.xml");
*/


    }
}
