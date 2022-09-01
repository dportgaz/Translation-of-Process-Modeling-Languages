package org.bpmn.bpmn_elements.gateway;

import com.google.gson.internal.LinkedTreeMap;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Predicate {

    String Id;
    String condition;
    Double createdEntityId;

    public Predicate() {
        this.Id = "Predicate_" + RandomIdGenerator.generateRandomUniqueId(6);
    }


    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Double getCreatedEntityId() {
        return createdEntityId;
    }

    public void setCreatedEntityId(Double createdEntityId) {
        this.createdEntityId = createdEntityId;
    }

    public String getId() {
        return Id;
    }

    @Override
    public String toString() {
        return "Id= " + this.createdEntityId + "Value= " + this.getCondition();
    }

    public static Predicate parsePredicate(Double id, ArrayList<AbstractObjectType> objectTypeObjects) {

        for (AbstractObjectType obj : objectTypeObjects) {
            //System.out.println("3");
            if (obj != null) {
                //System.out.println("4");
                if (obj.getMethodName().equals("UpdatePredicateStepTypeExpression")) {
                    Double stepId = (Double) obj.getParameters().get(0);
                    LinkedTreeMap link = (LinkedTreeMap) obj.getParameters().get(1);
                    LinkedTreeMap innerLeft = (LinkedTreeMap) link.get("Left");
                    LinkedTreeMap innerRight = (LinkedTreeMap) link.get("Right");
                    Double expressionFunction = (Double) link.get("ExpressionFunction");
                    if (stepId.equals(id)) {

                        String att = findAttributeOfPredicate((Double) innerLeft.get("AttributeTypeId"), objectTypeObjects);
                        String expr = "";

                        //TODO restlichen Opearationen noch ergänzen
                        if (expressionFunction == 26.0) {
                            expr = ">=";
                        } else if (expressionFunction == 27.0) {
                            expr = "=";
                        } else if (expressionFunction == 30.0) {
                            expr = "<";
                        }

                        Predicate predicate = new Predicate();
                        predicate.setCondition("[" + att + "]" + " " + expr + " " + innerRight.get("Value"));
                        return predicate;
                    }
                }
            }
        }
        return null;
    }

    public static AbstractObjectType getPredicate(Double source, ArrayList<AbstractObjectType> objectTypeObjects) {

        for (AbstractObjectType obj : objectTypeObjects) {

            if (obj != null && obj.getCreatedEntityId() != null && obj.getCreatedEntityId().equals(source)) {
                if (obj.getMethodName().equals("AddPredicateStepType")) {
                    return obj;
                }
            }
        }
        return null;
    }

    private static String findAttributeOfPredicate(Double id, ArrayList<AbstractObjectType> objectTypeObjects) {

        for (AbstractObjectType obj : objectTypeObjects) {
            //System.out.println("3");
            if (obj != null) {

                Pattern p = Pattern.compile("^Update.*AttributeType$");
                Matcher m = p.matcher(obj.getMethodName());

                if (m.find() && obj.getParameters().get(0).equals(id)) {
                    return (String) obj.getParameters().get(1);
                }
            }

        }
        return null;
    }
}