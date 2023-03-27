package org.bpmn.parse_json;

import org.bpmn.bpmn_elements.transition.Loop;
import org.bpmn.bpmn_elements.task.Step;
import org.bpmn.flows_process_model.Port;
import org.bpmn.flows_process_model.Relation;
import org.bpmn.flows_process_model.RelationType;
import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.transition.SequenceFlow;
import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.process.FlowsProcessObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.bpmn_elements.transition.SequenceFlow.getFlowBySource;
import static org.bpmn.bpmn_elements.transition.SequenceFlow.getFlowByTarget;
import static org.bpmn.bpmn_elements.gateway.Predicate.getPredicate;
import static org.bpmn.bpmn_elements.gateway.Predicate.createPredicate;
import static org.bpmn.transformation.LifecycleTransformation.allTasks;

public class Parser {

    public ArrayList<Task> coordinationTasks = new ArrayList<>();

    public ArrayList<Port> coordinationPorts = new ArrayList<>();


    public ArrayList<Task> parseTasks(Participant participant, ArrayList<AbstractFlowsEntity> objects, boolean adHoc) {

        ArrayList<Task> tasks = new ArrayList<>();

        for (AbstractFlowsEntity obj : objects) {

            if (obj != null && obj.getMethodName().equals("UpdateStateType")) {

                String stateName = (String) obj.getParameters().get(1);
                Double updatedEntityId = (Double) obj.getParameters().get(0);

                Task task = new Task(updatedEntityId, stateName, participant, objects, adHoc);
                ArrayList<Step> subTasks = new ArrayList<>();

                for (AbstractFlowsEntity step : objects) {

                    if (step != null
                            && (step.getMethodName().equals("AddStepType") || step.getMethodName().equals("AddPredicateStepType"))
                            && (step.getParameters().get(0).equals(updatedEntityId))) {

                        // get name of step
                        String stepName = "";
                        for (AbstractFlowsEntity attribute : objects) {

                            if (attribute != null
                                    && (attribute.getMethodName().equals("UpdateStepAttributeType"))
                                    && (attribute.getParameters().get(0).equals(step.getCreatedEntityId()))) {

                                for (AbstractFlowsEntity updateAttribute : objects) {

                                    if (updateAttribute != null) {
                                        Pattern p = Pattern.compile("^Update.*AttributeType$");
                                        Matcher m = p.matcher(updateAttribute.getMethodName());
                                        if (m.find() && updateAttribute.getParameters().get(0).equals(attribute.getParameters().get(1))) {
                                            stepName = (String) updateAttribute.getParameters().get(1);
                                            subTasks.add(new Step(step.getCreatedEntityId(), stepName, task.getParticipant(), task, false));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                task.setStepsTemp(subTasks);
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

        // parse state transitions
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

                Double sourceObjectId = (Double) object.getObjectById(source, objects).getParameters().get(0);
                Double targetObjectId = (Double) object.getObjectById(target, objects).getParameters().get(0);
                if (!sourceObjectId.equals(targetObjectId)) {

                    Task taskSource = object.getTaskById(sourceObjectId);
                    Task taskTarget = object.getTaskById(targetObjectId);

                    if (taskSource != null && taskTarget != null) {
                        SequenceFlow sf = new SequenceFlow(taskSource, taskTarget);
                        flows.add(sf);
                    }
                }

            }
        }

        // parse step transitions
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

                Task taskSource = object.getTaskById(source);
                Task taskTarget = object.getTaskById(target);

                if (taskSource != null && taskTarget != null && sourceTemp != null) {
                    SequenceFlow sf = new SequenceFlow(taskSource, taskTarget);
                    flows.add(sf);
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

                Double sourceObjectId = object.getObjectById(source, objects).getCreatedEntityId();
                Double targetObjectId = object.getObjectById(target, objects).getCreatedEntityId();

                Task sourceTask = object.getTaskById(sourceObjectId);
                Task targetTask = object.getTaskById(targetObjectId);

                loops.add(new Loop(sourceTask, targetTask));

            }
        });
        return loops;
    }


    public ArrayList<Predicate> parsePredicates(ArrayList<AbstractFlowsEntity> objects) {

        ArrayList<Predicate> predicates = new ArrayList<>();

        for (AbstractFlowsEntity obj : objects) {
            if (obj != null && obj.getMethodName().equals("AddPredicateStepType")) {

                Predicate predicate = createPredicate(obj.getCreatedEntityId(), objects);
                predicate.setCreatedEntityId(obj.getCreatedEntityId());
                predicates.add(predicate);

            }
        }
        return predicates;
    }

    public HashSet<Lane> parsePermissions(HashMap<Double, ArrayList<AbstractFlowsEntity>> users) {
        HashSet<Lane> lane = new HashSet<>();
        for (Double key : users.keySet()) {
            users.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("UpdateGlobalRoleName")) {

                    Double userId = (Double) obj.getParameters().get(0);
                    String name = (String) obj.getParameters().get(1);
                    lane.add(new Lane(userId, name));
                }
            });
        }
        Double d = 71238172381763d;
        lane.add(new Lane(d, "System"));
        return lane;
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
                        if (!relation.getTask().getParticipant().equals(task.getParticipant())) {
                            relation.setRelationType(RelationType.OTHER);
                            port.incCntOther();
                            task.intCntOtherRelations();
                        }
                    }
                    if(port.getCntOther() > 0){
                        task.getPorts().add(port);
                    }
                }
            }
        }
        return coordinationTasks;
    }

    public ArrayList<Task> getCoordinationTasks(HashMap<Double, ArrayList<AbstractFlowsEntity>> coordinationProcessObjects) {
        return appendPortsToTasks(coordinationProcessObjects);
    }

}
