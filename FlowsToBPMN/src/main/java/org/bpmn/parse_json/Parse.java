package org.bpmn.parse_json;

import org.bpmn.bpmn_elements.gateway.Predicate;
import org.bpmn.bpmn_elements.task.Task;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.step_one.collaboration.participant.ParticipantObject;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.bpmn_elements.gateway.Predicate.parsePredicate;

public class Parse {

    public static ArrayList<Task> allTasks = new ArrayList<>();

    public ArrayList<Task> parseTasks(ParticipantObject participant, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

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

    public ArrayList<Predicate> parsePredicates(ParticipantObject participant, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

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

    /*
    public ArrayList<Participant> parseUsers(HashMap<String, ArrayList<AbstractObjectType>> users) {

        ArrayList<Participant> permissionParticipants = new ArrayList<>();
        for (String userId : users.keySet()) {
            users.get(userId).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("UpdateGlobalRoleName")) {

                    String name = (String) obj.getParameters().get(1);
                    Double updatedEntityId = obj.getUpdatedEntityId();
                    Participant participant = new Participant("two", userId, name, updatedEntityId);

                    if (!permissionParticipants.contains(participant)) {
                        permissionParticipants.add(participant);
                    }

                }
            });
        }

    }

     */
            }
