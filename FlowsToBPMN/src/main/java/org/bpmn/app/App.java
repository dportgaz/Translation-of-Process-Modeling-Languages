package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bpmn.steps.BPMN;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException {


        BPMN bpmn = new BPMN();
        String filePHoodle = "FlowsToBPMN/src/resources/flows/Recruitment_letsgo.json";
        bpmn.create(filePHoodle, "PHoodle_RENEW.xml");

/*
        String fileRecruitment = "FlowsToBPMN/src/resources/flows/recruitment.json";
        fillXMLStepOneRenew.createBPMN(fileRecruitment, "Recruitment_RENEW.xml");
*/

    }
} 