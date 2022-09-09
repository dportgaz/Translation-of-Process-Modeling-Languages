
package org.bpmn.process;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.parse_json.Parser;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.participant.User;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.steps.Execution.doc;
import static org.bpmn.steps.StepOne.*;

public class FlowsProcessUser {

    static int countProcess = 0;

    static boolean hasLoop = false;

    String id;

    static String isExecutable = "true";

    Element elementFlowsProcess;

    User user;

    StartEvent startEvent;

    // ArrayList, da Reihenfolge der Tasks gewahrt werden soll
    ArrayList<Task> tasks = new ArrayList<>();

    EndEvent endEvent;

    HashSet<ExclusiveGateway> gateways = new HashSet<>();

    ArrayList<SequenceFlow> flows = new ArrayList<>();

    HashSet<DataObject> dataObjects = new HashSet<>();

    ArrayList<Predicate> predicates = new ArrayList<>();

    ArrayList<Task> subprocesses = new ArrayList<>();

    HashMap<String, SequenceFlow> decisionFlows = new HashMap<>();

    HashMap<String, ArrayList<String>> decisionTasks = new HashMap<>();

    public FlowsProcessUser(User participant, HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects) {

        this.id = "Process_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementFlowsProcess = doc.createElement("bpmn:process");
        this.user = participant;
        setFlowsProcess(userTypeObjects);

        setElementFlowsProcess();
        countProcess++;

    }

    private void setFlowsProcess(HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects) {

        Parser parser = new Parser();
        parser.parsePermissions(userTypeObjects);

        setTasks();
        setDataObjects();
        //resetInputAssociations();
        resetFlows();

        //TODO: noch incomings und outgoings von tasks entfernen

    }

    private void resetFlows() {

        for (Task task : tasks) {
            if (task.getIncoming() != null) {
                task.getElement().removeChild(task.getElementIncoming());
                task.setIncoming(null);
            }
            if (task.getOutgoing() != null) {
                task.getElement().removeChild(task.getElementOutgoing());
                task.setOutgoing(null);
            }
        }
    }

    private void sortTasks() {

        //TODO: Sort tasks according to coordination process

    }

    /*
    private void resetInputAssociations() {

        for (Task task : tasks) {
            if (task.getDataInputAssociations() != null) {
                task.getElement().removeChild(task.getDataInputAssociations().getElementDataInputAssociation());
                task.getElement().removeChild(task.getProperty().getElementProperty());
                task.setDataInputAssociation(null);
                task.setProperty(null);
            }
        }

    }

     */

    private void setElementFlowsProcess() {
        this.elementFlowsProcess.setAttribute("id", this.id);
        if (countProcess == 0) {
            this.elementFlowsProcess.setAttribute("isExecutable", this.isExecutable);
        } else {
            isExecutable = "false";
            this.elementFlowsProcess.setAttribute("isExecutable", this.isExecutable);
        }
    }

    private void setTasks() {

        for (Task task : allTasks) {
            if (task.getParticipant().getId().equals(this.user.getId())) {

                tasks.add(task);
                this.elementFlowsProcess.appendChild(task.getElement());
            }
        }
        sortTasks();
    }

    private void setDataObjects() {

        for (Task task : tasks) {

            DataObject dObj = task.getDataObject();
            Element tempObject = doc.createElement("bpmn:dataObject");
            tempObject.setAttribute("id", dObj.getId());

            dataObjects.add(dObj);

            this.elementFlowsProcess.appendChild(dObj.getElementDataObject());
            this.elementFlowsProcess.appendChild(tempObject);
            this.elementFlowsProcess.appendChild(task.getElement());

        }
    }

    public String getId() {
        return id;
    }

    public Element getElementFlowsProcess() {
        return elementFlowsProcess;
    }

}
