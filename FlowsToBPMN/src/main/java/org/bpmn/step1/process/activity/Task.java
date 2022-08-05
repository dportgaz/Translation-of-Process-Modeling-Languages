package org.bpmn.step1.process.activity;

import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step1.process.FlowsProcess;
import org.bpmn.step1.process.dataobject.DataObject;
import org.bpmn.step1.process.event.EndEvent;
import org.bpmn.step1.process.event.StartEvent;
import org.bpmn.step1.process.flow.SequenceFlow;

import javax.lang.model.element.Element;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {

    String id;

    String name;

    Double CreatedEntityId;

    SequenceFlow incoming;

    SequenceFlow outgoing;

    String dataInputAssociation;

    String dataOutputAssociation;

    DataObject dataObject;

    Task before;

    Task after;

    StartEvent beforeEvent;

    EndEvent afterEvent;

    String property;

    HashMap<Double, AbstractObjectType> steps = new HashMap<>();

    ArrayList<Task> stepNamesByTask = new ArrayList<>();

    private ArrayList<String> stepNamesByName = new ArrayList<>();

    public Task() {
        this.id = "Activity_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public HashMap<Double, AbstractObjectType> getSteps() {
        return steps;
    }

    public ArrayList<Task> getStepNames() {
        return stepNamesByTask;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty() {
        this.property = "Property_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public String getDataOutputAssociation() {
        return dataOutputAssociation;
    }

    public Task getAfter() {
        return after;
    }

    public void setAfter(Task after) {
        this.after = after;
    }

    public Task getBefore() {
        return before;
    }

    public void setBefore(Task before) {
        this.before = before;
    }

    public EndEvent getAfterEvent() {
        return afterEvent;
    }

    public void setAfterEvent(EndEvent afterEvent) {
        this.afterEvent = afterEvent;
    }

    public StartEvent getBeforeEvent() {
        return beforeEvent;
    }

    public void setBeforeEvent(StartEvent beforeEvent) {
        this.beforeEvent = beforeEvent;
    }

    public void setDataOutputAssociation() {
        this.dataOutputAssociation = "DataOutputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public void setDataInputAssociation() {
        this.dataInputAssociation = "DataInputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public String getDataInputAssociation() {
        return dataInputAssociation;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
    }

    public void setOutgoing(SequenceFlow outcoming) {
        this.outgoing = outcoming;
    }

    public void setCreatedEntityId(Double createdEntityId) {
        CreatedEntityId = createdEntityId;
    }

    public Double getCreatedEntityId() {
        return CreatedEntityId;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public SequenceFlow getIncoming() {
        return this.incoming;
    }

    public SequenceFlow getOutgoing() {
        return this.outgoing;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void fillStepsToActivity(ObjectTypeMap objectMap, FlowsProcess fp, String key) throws FileNotFoundException {

        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            for (Double d : steps.keySet()) {
                if (obj != null) {
                    if (obj.getMethodName().equals("UpdateStepAttributeType") && obj.getParameters().get(0).equals(d)) {

                        Double temp = (Double) obj.getParameters().get(1);
                        try {
                            objectMap.getObjectTypeObjects().get(key).forEach(obj2 -> {
                                if (obj2 != null) {
                                    Pattern p = Pattern.compile("^Update.*AttributeType$");
                                    Matcher m = p.matcher(obj2.getMethodName());

                                    if (m.find() && obj2.getParameters().get(0).equals(temp)) {
                                        Task t = new Task();
                                        t.setName((String) obj2.getParameters().get(1));
                                        t.setCreatedEntityId(temp);
                                        if(!stepNamesByName.contains(t.getName())) {
                                            stepNamesByTask.add(t);
                                            stepNamesByName.add(t.getName());
                                        }
                                    }
                                }
                            });
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
    }
}

