package org.bpmn.parse_json;

import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.step_one.collaboration.Collaboration;
import org.bpmn.step_one.collaboration.participant.Object;
import org.bpmn.step_one.collaboration.participant.Participant;
import org.bpmn.step_one.collaboration.participant.User;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.bpmn_elements.gateway.Predicate.parsePredicate;
import static org.bpmn.step_one.StepOne.allTasks;

public class Parser {

    public ArrayList<Task> parseTasks(Participant participant, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        ArrayList<AbstractObjectType> objects = objectTypeObjects.get(participant.getKey());
        ArrayList<Task> tasks = new ArrayList<>();
        boolean firstTask = true;

        for (AbstractObjectType obj : objects) {

            if (obj != null && obj.getMethodName().equals("UpdateStateType")) {

                String taskName = obj.getParameters().get(1) + " " + participant.getName();
                Double createdEntityId = (Double) obj.getParameters().get(0);

                Task task = new Task(createdEntityId, taskName, participant, objectTypeObjects);

                // fixes double entry json bug
                if (tasks.contains(task)) {
                    tasks.remove(task);
                    if (tasks.size() == 0) {
                        firstTask = true;
                    }
                }
                if (!firstTask) {
                    task.setDataInputAssociation();
                }
                task.setDataOutputAssociation();
                tasks.add(task);
                firstTask = false;

                if (!allTasks.contains(task)) {
                    allTasks.add(task);
                }
            }

        }
        return tasks;
    }

    public ArrayList<Predicate> parsePredicates(Object participant, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        ArrayList<AbstractObjectType> objects = objectTypeObjects.get(participant.getKey());
        ArrayList<Predicate> predicates = new ArrayList<>();

        for (AbstractObjectType obj : objects) {
            if (obj != null && obj.getMethodName().equals("AddPredicateStepType")) {

                Predicate predicate = parsePredicate(obj.getCreatedEntityId(), objectTypeObjects.get(participant.getKey()));
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
