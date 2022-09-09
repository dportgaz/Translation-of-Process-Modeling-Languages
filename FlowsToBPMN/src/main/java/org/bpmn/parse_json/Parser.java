package org.bpmn.parse_json;

import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flows_objects.AbstractObjectType;
import org.bpmn.bpmn_elements.collaboration.Collaboration;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.collaboration.participant.User;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.bpmn_elements.gateway.Predicate.parsePredicate;
import static org.bpmn.steps.StepOne.allTasks;

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
