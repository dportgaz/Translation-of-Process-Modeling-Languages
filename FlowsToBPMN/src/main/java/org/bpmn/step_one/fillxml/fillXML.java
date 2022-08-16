package org.bpmn.step_one.fillxml;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bpmn.flowsObjects.ConcreteObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.bpmndi.FillBPMNDI;
import org.bpmn.step_one.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step_one.process.FillFlowsProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class fillXML {

    static String collaborationID = "Collaboration_" + RandomIdGenerator.generateRandomUniqueId(6);
    static String bpmndiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

    public static void createBPMN(String jsonFlowsPath, String filename)
            throws ParserConfigurationException, FileNotFoundException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("bpmn:definitions");
        doc.appendChild(rootElement);

        FillFlowsParticipant fp = new FillFlowsParticipant(doc, jsonFlowsPath);
        ConcreteObjectType objectMap = new ConcreteObjectType(jsonFlowsPath);
        FillFlowsProcess ffp = new FillFlowsProcess();
        FillBPMNDI bpmndi = new FillBPMNDI();


        fillHeader(doc, rootElement);
        fp.fillCollaborationParticipants(doc, collaborationID, jsonFlowsPath, rootElement);
        ffp.fillProcesses(doc, rootElement, objectMap);
        bpmndi.fillBPMNDI(doc, bpmndiagramID, filename, rootElement);
        //System.out.println(objectMap.getObjectTypeObjects());

        createXml(doc, filename);
    }

    public static void fillHeader(Document doc, Element rootElement) {
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

    public static String getBpmndiagramID() {
        return bpmndiagramID;
    }

    public static String getCollaborationID() {
        return collaborationID;
    }
}
