package org.bpmn.step_one.fillxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.flowsObjects.ConcreteObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.Collaboration;

import org.bpmn.step_one.collaboration.participant.Participant;
import org.bpmn.step_one.process.FlowsProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.bpmn.step_one.collaboration.Collaboration.participants;

public class fillXMLStepOneRenew {

    public static Document doc;

    static String bpmnDiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

    public static void createBPMN(String jsonFlowsPath, String filename)
            throws ParserConfigurationException, FileNotFoundException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        doc = docBuilder.newDocument();
        Element definitionsElement = doc.createElement("bpmn:definitions");
        doc.appendChild(definitionsElement);
        setHeader(definitionsElement);

        ConcreteObjectType objects = new ConcreteObjectType(jsonFlowsPath);
        HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects = objects.getObjectTypeObjects();

        Collaboration collaboration = new Collaboration();
        collaboration.setParticipants(objectTypeObjects);
        Element collaborationElement = collaboration.getElementCollaboration();

        definitionsElement.appendChild(collaborationElement);
        setProcesses(definitionsElement);

        for (Participant participant : participants) {

            System.out.print(participant.getName() + ": ");
            System.out.println(participant.getProcessRef().getTasks()
                    + " \n\tdObj's: " + participant.getProcessRef().getDataobjects());
            for(Task task : participant.getProcessRef().getTasks()){
                System.out.println("\tSteps: " + task.getSteps());
            }

        }

        createXml(doc, filename);
    }

    private static void setProcesses(Element definitionsElement) {

        for (Participant participant : participants) {

            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }

    }

    private static void setHeader(Element rootElement) {
        rootElement.setAttribute("xmlns:bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL");
        rootElement.setAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
        rootElement.setAttribute("xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC");
        rootElement.setAttribute("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
        rootElement.setAttribute("xmlns:camunda", "http://camunda.org/schema/1.0/bpmn");
        rootElement.setAttribute("id", "Definitions_1");
        rootElement.setAttribute("targetNamespace", "http://bpmn.io/schema/bpmn");
        rootElement.setAttribute("camunda:diagramRelationId", "e9a61ae0-03e0-4936-9fa3-9d47de87bcfa");
    }

    private static void createXml(Document doc, String filename) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File("FlowsToBPMN/src/resources/bpmn/" + filename));
        transformer.transform(domSource, streamResult);

    }
}
