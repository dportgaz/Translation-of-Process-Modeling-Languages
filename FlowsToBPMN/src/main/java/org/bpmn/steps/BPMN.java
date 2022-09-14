package org.bpmn.steps;

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

import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.flows_objects.ConcreteObjectType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BPMN {

    public static Document doc;
    
    public void create(String jsonFlowsPath, String file)
            throws ParserConfigurationException, FileNotFoundException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        doc = docBuilder.newDocument();
        Element definitionsElement1 = doc.createElement("bpmn:definitions");
        doc.appendChild(definitionsElement1);
        setHeader(definitionsElement1);

        ConcreteObjectType objects = new ConcreteObjectType(jsonFlowsPath);
        HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects = objects.getObjectTypeObjects();
        HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects = objects.getUserTypeObjects();

        String fileTempOne = "PHOODLE_STEP_ONE_RENEW.xml";
        StepOne s1 = new StepOne(fileTempOne, definitionsElement1, objectTypeObjects);
        s1.execute();

        // ____________________________________________________________________________________________________________

        doc.removeChild(doc.getFirstChild());
        Element definitionsElement2 = doc.createElement("bpmn:definitions");
        doc.appendChild(definitionsElement2);
        setHeader(definitionsElement2);

        String fileTempTwo = "PHOODLE_STEP_TWO_RENEW.xml";
        StepTwo s2 = new StepTwo(fileTempTwo, definitionsElement2, userTypeObjects, objectTypeObjects);
        s2.execute();

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

    public static void createXml(String file) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File("FlowsToBPMN/src/resources/bpmn/" + file));
        transformer.transform(domSource, streamResult);

    }
}
