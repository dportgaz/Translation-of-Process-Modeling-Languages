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

import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.flows_entities.AbstractRelationship;
import org.bpmn.flows_entities.DeserializeFlowsEntity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BPMN {

    public static Document doc;

    public void create(String flowsFile, String targetFile)
            throws ParserConfigurationException, FileNotFoundException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        doc = docBuilder.newDocument();
        Element definitionsElement1 = doc.createElement("bpmn:definitions");
        doc.appendChild(definitionsElement1);
        setHeader(definitionsElement1);

        DeserializeFlowsEntity objects = new DeserializeFlowsEntity(flowsFile);
        HashMap<Double, ArrayList<AbstractFlowsEntity>> objectTypeObjects = objects.getObjectTypes();
        HashMap<Double, ArrayList<AbstractFlowsEntity>> userTypeObjects = objects.getUserTypes();

        HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects = objects.getCoordinationProcessTypeActionLogs();
        ArrayList<AbstractRelationship> relationsDataModel = objects.getRelationships().getList();

        String fileTempOne = targetFile + "_Step1.xml";
        LifecycleTransformation s1 = new LifecycleTransformation(fileTempOne, definitionsElement1, objectTypeObjects);
        s1.execute();

        // ____________________________________________________________________________________________________________


        doc.removeChild(doc.getFirstChild());
        Element definitionsElement2 = doc.createElement("bpmn:definitions");
        doc.appendChild(definitionsElement2);
        setHeader(definitionsElement2);


        String fileTempThree = targetFile + "_Step3.xml";
        CoordinationTransformation s3 = new CoordinationTransformation(s1, fileTempThree, definitionsElement2, objectTypeObjects, userTypeObjects, coordinationProcessObjects, relationsDataModel);
        s3.execute();


        // _______________________________
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
        File writeFile = new File("FlowsToBPMN/src/resources/bpmn/" + file);
        StreamResult streamResult = new StreamResult(writeFile);
        transformer.transform(domSource, streamResult);


    }
}
