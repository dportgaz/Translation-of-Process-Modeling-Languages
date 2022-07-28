package org.bpmn.step1.fillxml;

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

import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step1.process.FillFlowsProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class fillXML {

    static Element collaboration;
    static Element bpmndiagram;

    public static void createBPMN(String jsonFlowsPath)
            throws ParserConfigurationException, FileNotFoundException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("bpmn:definitions");
        doc.appendChild(rootElement);

        FillFlowsParticipant fp = new FillFlowsParticipant(doc, collaboration, jsonFlowsPath);
        ObjectTypeMap objectMap = new ObjectTypeMap(jsonFlowsPath);
        FillFlowsProcess ffp = new FillFlowsProcess();


        fillHeader(doc, rootElement);
        fillStructure(doc, rootElement);
        fp.fillCollaborationParticipants(doc, collaboration, jsonFlowsPath);
        ffp.fillProcesses(doc, rootElement, objectMap);
        System.out.println(objectMap.getObjectTypeObjects());

        createXml(doc);
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

    public static void fillStructure(Document doc, Element rootElement) {

        String collaborationID = RandomIdGenerator.generateRandomUniqueId(6);
        String bpmndiagramID = RandomIdGenerator.generateRandomUniqueId(6);

        bpmndiagram = doc.createElement("bpmndi:BPMNDiagram");
        bpmndiagram.setAttribute("id", "BPMNDiagram_" + bpmndiagramID);
        rootElement.appendChild(bpmndiagram);

        collaboration = doc.createElement("bpmn:collaboration");
        collaboration.setAttribute("id", "Collaboration_" + collaborationID);
        rootElement.appendChild(collaboration);

    }

    private static void createXml(Document doc) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new File("FlowsToBPMN/src/resources/bpmn/PHoodle.xml"));
        transformer.transform(domSource, streamResult);

    }

    public static Element getCollaboration() {
        return collaboration;
    }

    public static Element getBPMNDiagram() {
        return bpmndiagram;
    }

}
