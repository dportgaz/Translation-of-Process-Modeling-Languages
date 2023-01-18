package org.bpmn.flows_process_model;

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

import org.bpmn.transformation.CoordinationTransformation;
import org.bpmn.transformation.LifecycleTransformation;
import org.bpmn.transformation.UserAssignmentTransformation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FlowsProcessModel {
    private String name;
    private CoordinationProcess coordinationProcess;
    private DataModel dataModel;
    public FlowsProcessModel(String flowsJSON) throws FileNotFoundException {

        this.name = flowsJSON;
        this.coordinationProcess = new CoordinationProcess(flowsJSON);
        this.dataModel = new DataModel(flowsJSON);

    }

    public CoordinationProcess getCoordinationProcess() {
        return coordinationProcess;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

}
