package org.bpmn.step_one;

import org.bpmn.ExecStep;
import org.bpmn.bpmn_elements.Decision;
import org.bpmn.bpmn_elements.Loop;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.bpmndi.FillBPMNDI;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.Collaboration;
import org.bpmn.step_one.collaboration.participant.Object;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.fillxml.ExecSteps.*;
import static org.bpmn.step_one.collaboration.Collaboration.objects;

public class StepOne {
    ExecStep step;
    String file;
    Element definitionsElement;
    HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects;

    public static ArrayList<Task> allTasks = new ArrayList();

    public static ArrayList<SequenceFlow> allFlows = new ArrayList();

    public static ArrayList<ExclusiveGateway> allGateways = new ArrayList();

    public static HashSet<Loop> loops = new HashSet<>();

    public static ArrayList<Predicate> predicates = new ArrayList<>();

    static String bpmnDiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);


    public StepOne(String file, Element definitionsElement, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {
        this.file = file;
        this.definitionsElement = definitionsElement;
        this.objectTypeObjects = objectTypeObjects;
        this.step = ExecStep.ONE;
    }

    public void execute() throws TransformerException {

        Collaboration collaboration = new Collaboration();
        collaboration.setParticipantsOne(objectTypeObjects);
        Element collaborationElement = collaboration.getElementCollaboration();

        definitionsElement.appendChild(collaborationElement);
        setProcesses(definitionsElement);

        FillBPMNDI di = new FillBPMNDI();
        di.fillBPMNDI(bpmnDiagramID, definitionsElement, collaboration);

        createXml(file);

    }

    private void setProcesses(Element definitionsElement) {

        for (Object participant : objects) {

            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }

    }
}
