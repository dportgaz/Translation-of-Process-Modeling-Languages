package org.bpmn.parse_json;

import org.bpmn.bpmn_elements.Loop;
import org.bpmn.bpmn_elements.Port;
import org.bpmn.bpmn_elements.Relation;
import org.bpmn.bpmn_elements.RelationType;
import org.bpmn.bpmn_elements.collaboration.participant.User;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.process.FlowsProcessObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.bpmn_elements.flows.SequenceFlow.getFlowBySource;
import static org.bpmn.bpmn_elements.flows.SequenceFlow.getFlowByTarget;
import static org.bpmn.bpmn_elements.gateway.Predicate.getPredicate;
import static org.bpmn.bpmn_elements.gateway.Predicate.parsePredicate;
import static org.bpmn.steps.LifecycleTransformation.allTasks;

public class Parser {

    public ArrayList<Task> coordinationTasks = new ArrayList<>();

    public ArrayList<Port> coordinationPorts = new ArrayList<>();


    public ArrayList<Task> parseTasks(Participant participant, ArrayList<AbstractFlowsEntity> objects, boolean adHoc, boolean expandedSubprocess) {

        ArrayList<Task> tasks = new ArrayList<>();

        for (AbstractFlowsEntity obj : objects) {

            if (obj != null && obj.getMethodName().equals("UpdateStateType")) {

                String taskName = obj.getParameters().get(1) + " " + participant.getName();
                Double updateEntityId = (Double) obj.getParameters().get(0);
                Double stepEntityId = null;

                for (AbstractFlowsEntity stateObj : objects) {

                    if (stateObj != null
                            && stateObj.getMethodName().equals("AddStepType")
                            && stateObj.getParameters().get(0).equals(updateEntityId)) {

                        stepEntityId = stateObj.getCreatedEntityId();

                    }

                }

                Task task = new Task(stepEntityId, updateEntityId, taskName, participant, objects, adHoc, expandedSubprocess);
                tasks.add(task);
                allTasks.add(task);

            }

        }
        return tasks;
    }

    public ArrayList<SequenceFlow> parseFlows(FlowsProcessObject object, ArrayList<AbstractFlowsEntity> objects) {

        ArrayList<Task> tasks = object.getTasks();
        ArrayList<SequenceFlow> flows = new ArrayList<>();

        StartEvent start = object.getStartEvent();

        SequenceFlow startFlow = new SequenceFlow(start, tasks.get(0));

        flows.add(startFlow);
        start.setOutgoing(startFlow);

        for (AbstractFlowsEntity obj : objects) {

            if (obj != null && obj.getMethodName().equals("AddTransitionType")) {

                Double source = (Double) obj.getParameters().get(0);
                Double target = (Double) obj.getParameters().get(1);

                AbstractFlowsEntity sourceTemp = getPredicate(source, objects);
                AbstractFlowsEntity targetTemp = getPredicate(target, objects);

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

    public ArrayList<Loop> parseLoops(FlowsProcessObject object, ArrayList<AbstractFlowsEntity> objects) {

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

                loops.add(new Loop(sourceTask, targetTask));

            }
        });
        return loops;
    }


    public ArrayList<Predicate> parsePredicates(ArrayList<AbstractFlowsEntity> objects) {

        ArrayList<Predicate> predicates = new ArrayList<>();

        for (AbstractFlowsEntity obj : objects) {
            if (obj != null && obj.getMethodName().equals("AddPredicateStepType")) {

                Predicate predicate = parsePredicate(obj.getCreatedEntityId(), objects);
                predicate.setCreatedEntityId(obj.getCreatedEntityId());
                predicates.add(predicate);

            }
        }

        return predicates;
    }

    public HashSet<User> parsePermissions(HashMap<Double, ArrayList<AbstractFlowsEntity>> users) {
        HashSet<User> user = new HashSet<>();
        for (Double key : users.keySet()) {
            users.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("UpdateGlobalRoleName")) {

                    Double userId = (Double) obj.getParameters().get(0);
                    String name = (String) obj.getParameters().get(1);
                    user.add(new User(userId, name));
                }
            });
        }
        Double d = 71238172381763d;
        user.add(new User(d, "System"));
        return user;
    }


    public void parseCoordinationSteps(HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects) {

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
    }

    public void parseCoordinationPorts(HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects) {

        for (Double key : coordinationProcessObjects.keySet()) {
            coordinationProcessObjects.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("AddPortType")) {
                    Port port = new Port(obj.getCreatedEntityId(), (Double) obj.getParameters().get(0));
                    coordinationPorts.add(port);
                }
            });
        }
    }

    public void parseCoordinationTransitions(HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects) {

        for (Double key : coordinationProcessObjects.keySet()) {
            coordinationProcessObjects.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("AddCoordinationTransitionType")) {

                    Double taskId = (Double) obj.getParameters().get(0);
                    Double portId = (Double) obj.getParameters().get(1);

                    for (Port port : coordinationPorts) {
                        if (port.getId().equals(portId)) {
                            for (Task task : coordinationTasks) {
                                if (task.getCoordinationStepTypeId().equals(taskId)) {
                                    port.getIncoming().add(new Relation(task));
                                }
                            }
                        }
                    }

                }
            });
        }
    }

    public ArrayList<Task> appendPortsToTasks(HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects) {

        parseCoordinationSteps(coordinationProcessObjects);
        parseCoordinationPorts(coordinationProcessObjects);
        parseCoordinationTransitions(coordinationProcessObjects);

        for (Task task : coordinationTasks) {
            for (Port port : coordinationPorts) {
                if (task.getCoordinationStepTypeId().equals(port.getTaskId())) {
                    for (Relation relation : port.getIncoming()) {
                        if (relation.getTask().getParticipant().equals(task.getParticipant())) {
                            relation.setRelationType(RelationType.SELF);
                        } else {
                            relation.setRelationType(RelationType.OTHER);
                            port.incCntOther();
                            task.intCntOtherRelations();
                        }
                    }
                    task.getPorts().add(port);
                }
            }
        }
        return coordinationTasks;
    }

    public ArrayList<Task> getCoordinationTasks(HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects) {
        return appendPortsToTasks(coordinationProcessObjects);
    }

}
