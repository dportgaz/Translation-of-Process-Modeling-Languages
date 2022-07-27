package org.bpmn.step1.fillxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.bpmn.step1.collaboration.participant.Participant.fillCollaborationParticipants;

public class fillXML {

    static Element collaboration;
    static Element process;
    static Element bpmndiagram;

    public static void createBPMN(String jsonFlowsPath)
            throws ParserConfigurationException, FileNotFoundException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("bpmn:definitions");
        doc.appendChild(rootElement);

        fillHeader(doc, rootElement);
        fillStructure(doc, rootElement);
        fillCollaborationParticipants(doc, collaboration, jsonFlowsPath);

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

        String collaborationID = UUID.randomUUID().toString();
        String processID = UUID.randomUUID().toString();
        String bpmndiagramID = UUID.randomUUID().toString();

        process = doc.createElement("bpmn:process");
        process.setAttribute("id", "Process_" + processID);
        rootElement.appendChild(process);

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

    public static Element getProcess() {
        return process;
    }

    public static Element getBPMNDiagram() {
        return bpmndiagram;
    }

}
