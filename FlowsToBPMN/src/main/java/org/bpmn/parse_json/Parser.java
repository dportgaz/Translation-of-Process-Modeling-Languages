package org.bpmn.parse_json;

import org.bpmn.bpmn_elements.Loop;
import org.bpmn.bpmn_elements.Port;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.ExclusiveGateway;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.collaboration.participant.User;
import org.bpmn.process.FlowsProcessObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.bpmn_elements.flows.SequenceFlow.getFlowBySource;
import static org.bpmn.bpmn_elements.flows.SequenceFlow.getFlowByTarget;
import static org.bpmn.bpmn_elements.gateway.Predicate.getPredicate;
import static org.bpmn.bpmn_elements.gateway.Predicate.parsePredicate;
import static org.bpmn.steps.StepOne.allTasks;
import static org.bpmn.steps.StepOne.loops;

public class Parser {

    public ArrayList<Task> parseTasks(Participant participant, ArrayList<AbstractObjectType> objects) {

        ArrayList<Task> tasks = new ArrayList<>();

        for (AbstractObjectType obj : objects) {

            if (obj != null && obj.getMethodName().equals("UpdateStateType")) {

                String taskName = obj.getParameters().get(1) + " " + participant.getName();
                Double createdEntityId = (Double) obj.getParameters().get(0);

                Task task = new Task(createdEntityId, taskName, participant, objects);
                tasks.add(task);
                allTasks.add(task);

            }

        }
        return tasks;
    }

    public ArrayList<SequenceFlow> parseFlows(FlowsProcessObject object, ArrayList<AbstractObjectType> objects) {

        ArrayList<Task> tasks = object.getTasks();
        ArrayList<SequenceFlow> flows = new ArrayList<>();

        StartEvent start = object.getStartEvent();

        SequenceFlow startFlow = new SequenceFlow(start, tasks.get(0));

        flows.add(startFlow);
        start.setOutgoing(startFlow);

        for (AbstractObjectType obj : objects) {

            if (obj != null && obj.getMethodName().equals("AddTransitionType")) {

                Double source = (Double) obj.getParameters().get(0);
                Double target = (Double) obj.getParameters().get(1);

                AbstractObjectType sourceTemp = getPredicate(source, objects);
                AbstractObjectType targetTemp = getPredicate(target, objects);

                if (sourceTemp != null) {
                    source = (Double) sourceTemp.getParameters().get(0);
                }

                if (targetTemp != null) {
                    target = (Double) targetTemp.getParameters().get(0);
                }

                Double sourceObjectId = (Double) object.findObjectById(source, objects).getParameters().get(0);
                Double targetObjectId = (Double) object.findObjectById(target, objects).getParameters().get(0);
                if (!sourceObjectId.equals(targetObjectId)) {

                    Task taskSource = object.findTaskById(sourceObjectId);
                    Task taskTarget = object.findTaskById(targetObjectId);

                    if (taskSource != null && taskTarget != null) {
                        SequenceFlow sf = new SequenceFlow(taskSource, taskTarget);
                        flows.add(sf);
                    }
                }

            }
        }
        return flows;
    }

    public ArrayList<Loop> parseLoops(FlowsProcessObject object, ArrayList<AbstractObjectType> objects) {

        ArrayList<Loop> loops = new ArrayList<>();
        // gateways in case of loop
        objects.forEach(obj -> {
            if (obj != null && obj.getMethodName().equals("AddBackwardsTransitionType")) {

                Double source = (Double) obj.getParameters().get(1);
                Double target = (Double) obj.getParameters().get(0);

                Double sourceObjectId = object.findObjectById(source, objects).getCreatedEntityId();
                Double targetObjectId = object.findObjectById(target, objects).getCreatedEntityId();

                Task sourceTask = object.findTaskById(sourceObjectId);
                Task targetTask = object.findTaskById(targetObjectId);

                loops.add(new Loop(sourceTask,targetTask));

            }
        });
        return loops;
    }


    public ArrayList<Predicate> parsePredicates(ArrayList<AbstractObjectType> objects) {

        ArrayList<Predicate> predicates = new ArrayList<>();

        for (AbstractObjectType obj : objects) {
            if (obj != null && obj.getMethodName().equals("AddPredicateStepType")) {

                Predicate predicate = parsePredicate(obj.getCreatedEntityId(), objects);
                predicate.setCreatedEntityId(obj.getCreatedEntityId());
                predicates.add(predicate);

            }
        }

        return predicates;
    }

    public void parsePermissions(HashMap<Double, ArrayList<AbstractObjectType>> users) {
        for (Double key : users.keySet()) {
            users.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("AddStateExecutionPermissionToGlobalRole")) {

                    Double participantId = (Double) obj.getParameters().get(0);
                    Double taskId = (Double) obj.getParameters().get(1);
                    User user = Collaboration.getUser(participantId);

                    for (Task task : allTasks) {
                        if (task.getCreatedEntityId().equals(taskId)) {
                            task.setParticipant(user);
                        }
                    }
                }
            });
        }
    }

    public static ArrayList<Task> parseCoordinationSteps(HashMap<Double, ArrayList<AbstractObjectType>> coordinationProcessObjects){

        ArrayList<Task> coordinationTasks = new ArrayList<>();
        for (Double key : coordinationProcessObjects.keySet()) {
            coordinationProcessObjects.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("UpdateCoordinationStepType")) {

                    Double coordinationId = (Double) obj.getParameters().get(0);
                    Double taskId = (Double) obj.getParameters().get(2);

                    for (Task task : allTasks) {
                        if (task.getCreatedEntityId().equals(taskId)) {
                            task.setCoordinationStepTypeId(coordinationId);
                            coordinationTasks.add(task);
                        }
                    }
                }
            });
        }
        return coordinationTasks;
    }

    public static void parseCoordinationPorts(HashMap<Double, ArrayList<AbstractObjectType>> coordinationProcessObjects){

        ArrayList<Task> coordinationTasks = parseCoordinationSteps(coordinationProcessObjects);
        for (Double key : coordinationProcessObjects.keySet()) {
            coordinationProcessObjects.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("AddPortType")) {

                    Port port = new Port(obj.getCreatedEntityId(), (Double) obj.getParameters().get(0));

                    for (Task task : coordinationTasks) {
                        if (task.getCoordinationStepTypeId().equals(port.getTaskId())) {
                            task.getPorts().add(port);
                        }
                    }
                }
            });
        }
        System.out.println("PARSER + parseCoordinationPorts()");
        for(Task task : coordinationTasks){
            System.out.print(task);
            if(task.getPorts().size() > 0){
                System.out.print(" , Ports: ");
                for(Port port : task.getPorts()){
                    System.out.print(port + " , ");
                }
            }
            System.out.println();
        }
    }

}
