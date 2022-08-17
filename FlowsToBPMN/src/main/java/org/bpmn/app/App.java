package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.bpmn.step_one.fillxml.fillXML;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException, XPathExpressionException {


        String filePHoodle = "FlowsToBPMN/src/resources/flows/phoodle.json";
        fillXML.createBPMN(filePHoodle, "PHoodle.xml");


/*
        String fileRecruitment = "FlowsToBPMN/src/resources/flows/recruitment.json";
        fillXML.createBPMN(fileRecruitment, "Recruitment.xml");

 */

    }
}
