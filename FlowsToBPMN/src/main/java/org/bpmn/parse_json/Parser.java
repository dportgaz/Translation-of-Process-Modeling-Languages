package org.bpmn.parse_json;

import org.bpmn.bpmn_elements.Loop;
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

    public void parseLoops(FlowsProcessObject object, ArrayList<AbstractObjectType> objects) {

        // gateways in case of loop
        objects.forEach(obj -> {
            if (obj != null && obj.getMethodName().equals("AddBackwardsTransitionType")) {

                Loop loop = new Loop();

                Double source = (Double) obj.getParameters().get(1);
                Double target = (Double) obj.getParameters().get(0);

                Double sourceObjectId = object.findObjectById(source, objects).getCreatedEntityId();
                Double targetObjectId = object.findObjectById(target, objects).getCreatedEntityId();

                Task sourceTask = object.findTaskById(sourceObjectId);
                Task targetTask = object.findTaskById(targetObjectId);

                System.out.println(sourceTask + " " + targetTask);

            }
        });
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

    public void parsePermissions(HashMap<String, ArrayList<AbstractObjectType>> users) {
        for (String key : users.keySet()) {
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
}
