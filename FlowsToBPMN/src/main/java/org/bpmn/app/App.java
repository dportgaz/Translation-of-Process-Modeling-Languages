package org.bpmn.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bpmn.steps.BPMN;


public class App {

    public static void main(String[] args)
            throws ParserConfigurationException, TransformerException, FileNotFoundException {


        String[] files = {"Recruitment", "PHoodle", "Insurance"};

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please choose one of the following PHILharmonicFlows Process Model samples:");
        System.out.print("\t[");
        for (int i = 0; i < files.length; i++) {
            if(i == files.length-1) {
                System.out.print(files[i]);
            }else{
                System.out.print(files[i] + ", ");
            }
        }
        System.out.println("]");
        String processModel = scanner.nextLine();
        System.out.println("Creating BPMN...");

        BPMN bpmn = new BPMN();
        String flowsFile = "FlowsToBPMN/src/resources/flows/" + processModel + ".json";
        bpmn.create(flowsFile, processModel);

        System.out.println("Created.");


    }
} 