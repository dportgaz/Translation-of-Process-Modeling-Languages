package org.bpmn.process;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.ParticipantUser;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.fillxml.ExecSteps.doc;

public class FlowsProcessUser {

    static int countProcess = 0;

    String id;

    static String isExecutable = "true";

    Element elementFlowsProcess;

    ParticipantUser user;

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

    public FlowsProcessUser(ParticipantUser participant) {

        this.id = "Process_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementFlowsProcess = doc.createElement("bpmn:process");
        this.user = participant;
        /*
        setFlowsProcess(objectTypeObjects);
         */
        setElementFlowsProcess();
        countProcess++;

    }

    private void setFlowsProcess(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        /*
        setStartEvent();
        setEndEvent();
        setTasks();
        setDataObjects(objectTypeObjects);
        addPredicates(objectTypeObjects);

        addSequenceFlows(objectTypeObjects);
        setAssociations();
        addFlowsToActivities();
        addFlowsToEvents();
        addGateways();

         */

    }

    private void setElementFlowsProcess() {
        this.elementFlowsProcess.setAttribute("id", this.id);
        if (countProcess == 0) {
            this.elementFlowsProcess.setAttribute("isExecutable", this.isExecutable);
        } else {
            isExecutable = "false";
            this.elementFlowsProcess.setAttribute("isExecutable", this.isExecutable);
        }
    }

    public String getId() {
        return id;
    }
}
