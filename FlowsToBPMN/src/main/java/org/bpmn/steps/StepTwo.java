
package org.bpmn.steps;

//import org.bpmn.bpmndi.FillBPMNDI;
import org.bpmn.bpmndi.FillBPMNDI;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.User;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.steps.BPMN.*;
import static org.bpmn.bpmn_elements.collaboration.Collaboration.users;

public class StepTwo implements Step{

    ExecStep step;

    String file;

    Element definitionsElement;
    HashMap<Double, ArrayList<AbstractObjectType>> userTypeObjects;
    HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects;

    static String bpmnDiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

    public StepTwo(String file, Element definitionsElement, HashMap<Double, ArrayList<AbstractObjectType>> userTypeObjects,
                   HashMap<Double, ArrayList<AbstractObjectType>> objectTypeObjects) {
        this.file = file;
        this.definitionsElement = definitionsElement;
        this.userTypeObjects = userTypeObjects;
        this.objectTypeObjects = objectTypeObjects;
        this.step = ExecStep.TWO;
    }

    public void execute() throws TransformerException {

        Collaboration collaboration = new Collaboration();
        collaboration.setParticipants(objectTypeObjects, userTypeObjects);
        Element collaborationElement = collaboration.getElementCollaboration();

        definitionsElement.appendChild(collaborationElement);
        setProcesses(definitionsElement);

        FillBPMNDI di = new FillBPMNDI();
        di.fillBPMNDITwo(bpmnDiagramID, definitionsElement, collaboration);

        createXml(file);

    }

    public void setProcesses(Element definitionsElement) {

        for (User user : users) {

            definitionsElement.appendChild(user.getProcessRef().getElementFlowsProcess());

        }

    }

}

