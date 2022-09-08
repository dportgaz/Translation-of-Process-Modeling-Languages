package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.bpmn.fillxml.ExecSteps;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException, XPathExpressionException {


        ExecSteps execution = new ExecSteps();
        String filePHoodle = "FlowsToBPMN/src/resources/flows/PhoodleDoodle.json";
        execution.createBPMN(filePHoodle, "PHoodle_RENEW.xml");
        //FillXMLStepTwoRenew.createBPMN(filePHoodle, "PHoodle_Step2_RENEW.xml");


/*
        String fileRecruitment = "FlowsToBPMN/src/resources/flows/recruitment.json";
        fillXMLStepOneRenew.createBPMN(fileRecruitment, "Recruitment_RENEW.xml");
*/

    }
}