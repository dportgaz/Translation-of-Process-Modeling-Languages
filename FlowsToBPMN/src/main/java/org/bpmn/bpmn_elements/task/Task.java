package org.bpmn.bpmn_elements.task;

import com.google.gson.internal.LinkedTreeMap;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.association.DataOutputAssociation;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Participant;
import org.bpmn.step_one.process.Subprocess;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class Task {

    String id;

    String name;

    Double createdEntityId;

    Participant participant;

    // SequenceFlow incoming;

    // SequenceFlow outgoing;

    DataInputAssociation dataInputAssociation;

    DataOutputAssociation dataOutputAssociation;

    private DataObject dataObject;

    Task before;

    Task after;

    Task step;

    StartEvent beforeEvent;

    // EndEvent afterEvent;

    String property;

    private HashSet<Step> steps = new HashSet<>();

    ArrayList<Task> stepNamesByTask = new ArrayList<>();

    boolean isSubprocess;

    String inputAssociationSource;

    Element elementTask;

    Element elementDataOutputAssociation;

    Element elementDataInputAssociation;

    public Task(Double createdEntityId, String name, Participant participant) {
        this.id = "Activity_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.createdEntityId = createdEntityId;
        this.name = "Provide " + name;
        this.participant = participant;
        this.elementTask = doc.createElement("bpmn:task");
        this.elementTask.setAttribute("id", this.id);
        this.elementTask.setAttribute("name", this.name);
    }

    public Task(Double createdEntityId, String name, Participant participant, HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {
        this.id = "Activity_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.createdEntityId = createdEntityId;
        this.name = name;
        this.participant = participant;
        this.dataObject = new DataObject(this);
        this.steps = setSteps(objectTypeObjects);
        setTaskElement();
    }

    private void setTaskElement() {
        if (this.steps.size() > 0) {
            this.isSubprocess = true;
            this.elementTask = doc.createElement("bpmn:subProcess");
            setSubProcess();
        } else {
            this.isSubprocess = false;
            this.elementTask = doc.createElement("bpmn:task");
        }
        this.elementTask.setAttribute("id", this.id);
        this.elementTask.setAttribute("name", this.name);
    }

    private void setSubProcess() {

        StartEvent startEvent = new StartEvent();
        this.elementTask.appendChild(startEvent.getElementStartEvent());

        for(Step step : steps){
            this.elementTask.appendChild(step.getElementTask());
        }

    }

    /*
    private addStepsToTask() {
        for (Step step : steps) {
            this.elementTask.appendChild(step.getElementTask());
        }
    }


     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((createdEntityId == null) ? 0 : createdEntityId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Task other = (Task) obj;
        if (createdEntityId == null) {
            if (other.createdEntityId != null)
                return false;
        } else if (!createdEntityId.equals(other.createdEntityId))
            return false;
        return true;
    }

    public void setInputAssociationSource(String inputAssoSource) {
        this.inputAssociationSource = inputAssoSource;
    }

    public String getInputAssociationSource() {
        return inputAssociationSource;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }

    public Element getElementTask() {
        return elementTask;
    }

    public void setIsSubprocess() {
        this.isSubprocess = true;
    }

    public boolean getIsSubprocess() {
        return this.isSubprocess;
    }

    private ArrayList<String> stepNamesByName = new ArrayList<>();

    public HashSet<Step> getSteps() {
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

    public DataOutputAssociation getDataOutputAssociation() {
        return this.dataOutputAssociation;
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

    /*
    public EndEvent getAfterEvent() {
        return afterEvent;
    }

     */

    /*
    public void setAfterEvent(EndEvent afterEvent) {
        this.afterEvent = afterEvent;
    }

     */

    public StartEvent getBeforeEvent() {
        return beforeEvent;
    }

    public void setBeforeEvent(StartEvent beforeEvent) {
        this.beforeEvent = beforeEvent;
    }

    public void setDataOutputAssociation() {

        this.dataOutputAssociation = new DataOutputAssociation();
        this.elementTask.appendChild(this.dataOutputAssociation.getElementDataOutputAssociation());

    }

    public void setDataInputAssociation() {

        this.dataInputAssociation = new DataInputAssociation();
        this.elementTask.appendChild(this.dataInputAssociation.getElementDataInputAssociation());

    }

    public DataInputAssociation getDataInputAssociation() {
        return this.dataInputAssociation;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public DataObject getDataObject() {
        return this.dataObject;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
    }

    public void setOutgoing(SequenceFlow outcoming) {
        this.outgoing = outcoming;
    }
     */

    public void setCreatedEntityId(Double createdEntityId) {
        this.createdEntityId = createdEntityId;
    }

    public Double getCreatedEntityId() {
        return createdEntityId;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    /*
    public SequenceFlow getIncoming() {
        return this.incoming;
    }

    public SequenceFlow getOutgoing() {
        return this.outgoing;
    }

     */

    @Override
    public String toString() {
        return this.name;
    }

    public HashSet<Step> setSteps(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        String participantKey = this.participant.getKey();
        objectTypeObjects.get(participantKey).forEach(obj -> {

            if (obj != null && obj.getMethodName().equals("AddStepType")) {
                Double tempId = (Double) obj.getParameters().get(0);
                if (this.getCreatedEntityId().equals(tempId)) {
                    // trim steps by removing default steps
                    objectTypeObjects.get(participantKey).forEach(obj2 -> {
                        if (obj2 != null
                                && obj2.getMethodName().equals("UpdateStepAttributeType")
                                && obj2.getParameters().get(0).equals(obj.getCreatedEntityId())
                                && !stepIsPredicate(objectTypeObjects, (Double) obj2.getParameters().get(1))) {
                            this.steps.add(this.getStep(objectTypeObjects, obj));
                        }
                    });
                }
            }
        });
        return steps;
    }

    private boolean stepIsPredicate(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects, Double id) {
        for (AbstractObjectType obj : objectTypeObjects.get(this.participant.getKey())) {
            if (obj != null && obj.getMethodName().equals("UpdatePredicateStepTypeExpression")) {
                LinkedTreeMap link = (LinkedTreeMap) obj.getParameters().get(1);
                LinkedTreeMap innerLink = (LinkedTreeMap) link.get("Left");
                if (innerLink.get("AttributeTypeId").equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Step getStep(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects, AbstractObjectType absObj) {

        String participantKey = this.participant.getKey();
        Double id = absObj.getCreatedEntityId();

        for (AbstractObjectType obj : objectTypeObjects.get(participantKey)) {
            if (obj != null && obj.getMethodName().equals("UpdateStepAttributeType") && obj.getParameters().get(0).equals(id)) {
                Double tempId = (Double) obj.getParameters().get(1);
                for (AbstractObjectType obj2 : objectTypeObjects.get(participantKey)) {
                    if (obj2 != null) {

                        Pattern p = Pattern.compile("^Update.*AttributeType$");
                        Matcher m = p.matcher(obj2.getMethodName());

                        if (m.find() && obj2.getParameters().get(0).equals(tempId)) {
                            String name = (String) obj2.getParameters().get(1);
                            return new Step(tempId, name, this.participant, this);
                        }
                    }
                }
            }
        }
        return null;
    }
}
