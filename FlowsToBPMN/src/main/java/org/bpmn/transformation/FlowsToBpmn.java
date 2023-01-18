package org.bpmn.transformation;

import org.bpmn.flows_process_model.FlowsProcessModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
public class FlowsToBpmn {

    public static Document doc;
    private FlowsProcessModel flowsProcessModel;
    public void transform(FlowsProcessModel flowsProcessModel) throws ParserConfigurationException, TransformerException {

        this.flowsProcessModel = flowsProcessModel;
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();

        LifecycleTransformation lifecycleTransformation = transformLifecycle();
        CoordinationTransformation coordinationTransformation = transformCoordination(lifecycleTransformation);
        transformUserAssignment(coordinationTransformation);

    }

    // Lifecycle Process Transformation; implements TR1-TR5
    private LifecycleTransformation transformLifecycle() throws TransformerException {
        Element definitionsElementLifecycle = doc.createElement("bpmn:definitions");
        doc.appendChild(definitionsElementLifecycle);
        setHeader(definitionsElementLifecycle);
        String lifecycleXML = "_LifecycleTransformation.xml";
        LifecycleTransformation lifecycleTransformation = new LifecycleTransformation(lifecycleXML, definitionsElementLifecycle, flowsProcessModel.getDataModel().getObjectTypes().getEntities());
        lifecycleTransformation.transform();
        return lifecycleTransformation;
    }

    // Coordination Process Transformation; implements TR6
    private CoordinationTransformation transformCoordination(LifecycleTransformation lifecycleTransformation) throws TransformerException {
        doc.removeChild(doc.getFirstChild());
        Element definitionsElementCoordination = doc.createElement("bpmn:definitions");
        doc.appendChild(definitionsElementCoordination);
        setHeader(definitionsElementCoordination);
        String coordinationXML = "_CoordinationTransformation.xml";
        CoordinationTransformation coordinationTransformation = new CoordinationTransformation(lifecycleTransformation, coordinationXML, definitionsElementCoordination, flowsProcessModel.getDataModel().getUserTypes().getEntities(), flowsProcessModel.getDataModel().getEntities(), flowsProcessModel.getCoordinationProcess().getEntities());
        coordinationTransformation.transform();
        return coordinationTransformation;
    }

    // User Assignment Transformation; implements TR7
    private void transformUserAssignment(CoordinationTransformation coordinationTransformation) throws TransformerException {
        doc.removeChild(doc.getFirstChild());
        Element definitionsElementUser = doc.createElement("bpmn:definitions");
        doc.appendChild(definitionsElementUser);
        setHeader(definitionsElementUser);
        String userXML = "_UserTransformation.xml";
        UserAssignmentTransformation userAssignmentTransformation = new UserAssignmentTransformation(coordinationTransformation, userXML, definitionsElementUser);
        userAssignmentTransformation.transform();
    }

    private void setHeader(Element rootElement) {
        rootElement.setAttribute("xmlns:bpmn", "http://www.omg.org/spec/BPMN/20100524/MODEL");
        rootElement.setAttribute("xmlns:bpmndi", "http://www.omg.org/spec/BPMN/20100524/DI");
        rootElement.setAttribute("xmlns:dc", "http://www.omg.org/spec/DD/20100524/DC");
        rootElement.setAttribute("xmlns:di", "http://www.omg.org/spec/DD/20100524/DI");
        rootElement.setAttribute("xmlns:camunda", "http://camunda.org/schema/1.0/bpmn");
        rootElement.setAttribute("id", "Definitions_1");
        rootElement.setAttribute("targetNamespace", "http://bpmn.io/schema/bpmn");
        rootElement.setAttribute("camunda:diagramRelationId", "e9a61ae0-03e0-4936-9fa3-9d47de87bcfa");
    }

    public static void createXml(String xmlFile) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource domSource = new DOMSource(doc);
        File writeFile = new File("FlowsToBPMN/src/resources/bpmn/" + xmlFile);
        StreamResult streamResult = new StreamResult(writeFile);
        transformer.transform(domSource, streamResult);


    }

}
