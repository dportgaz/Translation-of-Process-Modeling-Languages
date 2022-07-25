package org.bpmn.fillxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
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

import org.bpmn.collaboration.flowsobject.AbstractFlowsObject;
import org.bpmn.collaboration.flowsobject.FlowsObjectJsonDeserializer;
import org.bpmn.collaboration.flowsobject.FlowsObjectList;
import org.bpmn.collaboration.flowsobjectname.AbstractFlowsObjectName;
import org.bpmn.collaboration.flowsobjectname.FlowsObjectNameJsonDeserializer;
import org.bpmn.collaboration.flowsobjectname.FlowsObjectNameList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class fillXML {

	static Element collaboration;
	static Element process;
	static Element bpmndiagram;

	public static Document createBPMN(String jsonFlowsPath)
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
		
		return doc;

	}

	public static void fillCollaborationParticipants(Document doc, Element collaboration, String filename)
			throws FileNotFoundException {

		Gson gsonFlowsObjectNameJsonDeserializer = new GsonBuilder()
				.registerTypeAdapter(AbstractFlowsObjectName.class, new FlowsObjectNameJsonDeserializer()).create();

		FlowsObjectNameList flowsObjects = gsonFlowsObjectNameJsonDeserializer
				.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectNameList.class);

		System.out.println(flowsObjects);

		for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {

			flowsObjects.ObjectTypeActionLogs.get(key).removeAll(Collections.singleton(null));

		}

		for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {

			System.out.println(flowsObjects.ObjectTypeActionLogs.get(key).get(0).getParameters());

		}

		Gson gsonFlowsObjectJsonDeserializer = new GsonBuilder()
				.registerTypeAdapter(AbstractFlowsObject.class, new FlowsObjectJsonDeserializer()).create();

		FlowsObjectList flowsObjects2 = gsonFlowsObjectJsonDeserializer
				.fromJson(new JsonReader(new FileReader(filename)), FlowsObjectList.class);

		System.out.println(flowsObjects2);

		ArrayList<String> names = new ArrayList<>();

		for (String key : flowsObjects.ObjectTypeActionLogs.keySet()) {
			for (AbstractFlowsObject obj : flowsObjects2.getList()) {
				if (obj != null && obj.getCreatedActorId().equals(key)) {
					names.addAll(flowsObjects.ObjectTypeActionLogs.get(key).get(0).getParameters());
				}
			}

		}
		System.out.println(names);

		for (int i = 0; i < names.size(); i++) {
			String participantID = UUID.randomUUID().toString();
			String processRef = UUID.randomUUID().toString();
			Element temp = doc.createElement("bpmn:participant");
			collaboration.appendChild(temp);
			temp.setAttribute("id", "Participant_" + participantID);
			temp.setAttribute("name", names.get(i));
			temp.setAttribute("processRef", "Process_" + processRef);
		}
	}

	public static void fillHeader(Document doc, Element rootElement) throws FileNotFoundException {
		rootElement.setAttribute("xmlns:bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL");
		rootElement.setAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
		rootElement.setAttribute("xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC");
		rootElement.setAttribute("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
		rootElement.setAttribute("xmlns:camunda", "http://camunda.org/schema/1.0/bpmn");
		rootElement.setAttribute("id", "Definitions_1");
		rootElement.setAttribute("targetNamespace", "http://bpmn.io/schema/bpmn");
		rootElement.setAttribute("camunda:diagramRelationId", "e9a61ae0-03e0-4936-9fa3-9d47de87bcfa");
	}

	public static void fillStructure(Document doc, Element rootElement) throws FileNotFoundException {

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
		StreamResult streamResult = new StreamResult(new File("src/resources/bpmn/PHoodle.xml"));
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
