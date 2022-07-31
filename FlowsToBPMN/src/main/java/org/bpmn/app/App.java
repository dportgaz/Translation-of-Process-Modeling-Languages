package org.bpmn.app;

import java.io.FileNotFoundException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bpmn.step1.fillxml.fillXML;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException {

        //String filePHoodle = "FlowsToBPMN/src/resources/flows/PHoodle.json";
        String fileRecruitment = "FlowsToBPMN/src/resources/flows/recruitment_json.json";
        //fillXML.createBPMN(filePHoodle, "PHoodle.xml");
        fillXML.createBPMN(fileRecruitment, "Recruitment.xml");

    }
}
