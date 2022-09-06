
package org.bpmn.step_two;

import org.bpmn.ExecStep;
import org.bpmn.bpmndi.FillBPMNDI;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.Collaboration;
import org.bpmn.step_one.collaboration.participant.ParticipantObject;
import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.fillxml.ExecSteps.*;
import static org.bpmn.fillxml.ExecSteps.doc;
import static org.bpmn.step_one.collaboration.Collaboration.participants;

public class StepTwo {

    ExecStep step;

    String file;

    Element definitionsElement;
    HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects;

    static String bpmnDiagramID = "BPMNDiagram_" + RandomIdGenerator.generateRandomUniqueId(6);

    public StepTwo(String file, Element definitionsElement, HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects) {
        this.file = file;
        this.definitionsElement = definitionsElement;
        this.userTypeObjects = userTypeObjects;
        this.step = ExecStep.TWO;
    }

    public void execute(ArrayList<ParticipantObject> participants) throws TransformerException {

        Collaboration collaboration = new Collaboration();
        collaboration.setParticipantsTwo(step, userTypeObjects);
        Element collaborationElement = collaboration.getElementCollaboration();

        definitionsElement.appendChild(collaborationElement);
        setProcesses(definitionsElement);

        FillBPMNDI di = new FillBPMNDI();
        di.fillBPMNDI(doc, bpmnDiagramID, definitionsElement, collaboration);
        createXml(doc, file);


    }

    private void setProcesses(Element definitionsElement) {

        for (ParticipantObject participant : participants) {

            definitionsElement.appendChild(participant.getProcessRef().getElementFlowsProcess());

        }

    }

}

