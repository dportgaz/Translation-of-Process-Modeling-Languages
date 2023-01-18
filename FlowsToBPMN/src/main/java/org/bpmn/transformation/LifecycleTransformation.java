package org.bpmn.transformation;

import org.bpmn.bpmn_elements.flows.Loop;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
//import org.bpmn.bpmndi.FillBPMNDI;
import org.bpmn.bpmndi.BPMNDiagram;
import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.process.FlowsProcessObject;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Pool;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.bpmn_elements.collaboration.Collaboration.pools;
import static org.bpmn.transformation.FlowsToBpmn.createXml;

public class LifecycleTransformation implements Transformation {
    String file;
    Element definitionsElement;
    HashMap<Double, ArrayList<AbstractFlowsEntity>> objectTypeObjects;
    public static ArrayList<Pool> Participants = new ArrayList();
    public static ArrayList<Task> allTasks = new ArrayList();
    public static ArrayList<DataObject> allDataObjects = new ArrayList();
    public static ArrayList<SequenceFlow> allFlows = new ArrayList();
    public static ArrayList<ExclusiveGateway> allGateways = new ArrayList();
    public static HashSet<Loop> allLoops = new HashSet<>();
    public static ArrayList<Predicate> predicates = new ArrayList<>();
    static String bpmnDiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

    private Collaboration collaboration;

    public LifecycleTransformation(String file, Element definitionsElement, HashMap<Double, ArrayList<AbstractFlowsEntity>> objectTypeObjects) {
        this.file = file;
        this.definitionsElement = definitionsElement;
        this.objectTypeObjects = objectTypeObjects;
    }

    public void transform() throws TransformerException {

        boolean adHoc = true;
        boolean expandedSubprocess = true;
        this.collaboration = new Collaboration();
        collaboration.setParticipants(objectTypeObjects, adHoc, expandedSubprocess);
        Element collaborationElement = collaboration.getElementCollaboration();

        definitionsElement.appendChild(collaborationElement);
        appendXMLElements(definitionsElement);

        BPMNDiagram di = new BPMNDiagram();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration, false, true, expandedSubprocess);

        createXml(file);

    }

     public void appendXMLElements(Element definitionsElement) {

        for (Pool participant : pools) {

            participant.getProcessRef().setElementFlowsProcess();
            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }
         FlowsProcessObject.resetCountProcess();
    }

    public Collaboration getCollaboration() {
        return collaboration;
    }
}
