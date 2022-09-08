package org.bpmn.bpmn_elements.task;

import com.google.gson.internal.LinkedTreeMap;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.association.DataOutputAssociation;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Object;
import org.bpmn.step_one.collaboration.participant.Participant;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.fillxml.ExecSteps.doc;

public class Task {

    String id;

    String name;

    Double createdEntityId;

    Participant participant;

    SequenceFlow incoming;

    Element elementIncoming;

    SequenceFlow outgoing;

    Element elementOutgoing;

    DataInputAssociation dataInputAssociation;

    DataOutputAssociation dataOutputAssociation;

    private DataObject dataObject;

    Task before;

    Task after;

    Task step;

    StartEvent beforeEvent;

    EndEvent afterEvent;

    String property;

    private ArrayList<Step> steps = new ArrayList<>();

    ArrayList<Task> stepNamesByTask = new ArrayList<>();

    boolean isSubprocess;

    boolean isEndTask = false;

    String inputAssociationSource;

    String outputAssociationTarget;

    Element elementTask;

    Element elementDataOutputAssociation;

    Element elementDataInputAssociation;

    StartEvent start;

    EndEvent end;

    ArrayList<SequenceFlow> flows = new ArrayList<>();

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

    public void setStart(StartEvent start) {
        this.start = start;
    }

    public StartEvent getStart() {
        return start;
    }

    public void setEnd(EndEvent end) {
        this.end = end;
    }

    public EndEvent getEnd() {
        return end;
    }

    public ArrayList<SequenceFlow> getFlows() {
        return flows;
    }

    private void setSubProcess() {

        StartEvent startEvent = new StartEvent();
        EndEvent endEvent = new EndEvent();
        start = startEvent;
        end = endEvent;
        this.elementTask.appendChild(startEvent.getElementStartEvent());
        this.elementTask.appendChild(endEvent.getElementEndEvent());

        for (Step step : steps) {
            this.elementTask.appendChild(step.getElementTask());
        }

    }

    public boolean getIsEndTask() {
        return this.isEndTask;
    }

    public void setIsEndTask() {
        this.isEndTask = true;
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
    public boolean equals(final java.lang.Object obj) {
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

    public void setInputAssociationSource(DataObject dataObject) {
        this.inputAssociationSource = dataObject.getRefId();
        this.dataInputAssociation.setSourceRef(dataObject);
        if (this.inputAssociationSource != null) {
            Element source = doc.createElement("bpmn:sourceRef");
            source.setTextContent(inputAssociationSource);
            this.dataInputAssociation.getElementDataInputAssociation().appendChild(source);
        }
    }

    public void setOutputAssociationTarget(DataObject dataObject) {
        this.outputAssociationTarget = dataObject.getRefId();
        this.dataOutputAssociation.setTargetRef(dataObject);
        Element target = doc.createElement("bpmn:targetRef");
        target.setTextContent(outputAssociationTarget);
        this.dataOutputAssociation.getElementDataOutputAssociation().appendChild(target);
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

    public ArrayList<Step> getSteps() {
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

        this.dataOutputAssociation = new DataOutputAssociation();
        this.elementTask.appendChild(this.dataOutputAssociation.getElementDataOutputAssociation());

    }

    public void setDataInputAssociation() {

        this.dataInputAssociation = new DataInputAssociation();
        this.elementTask.appendChild(this.dataInputAssociation.getElementDataInputAssociation());

    }

    public void setDataInputAssociation(DataInputAssociation dataInputAssociation) {

        this.dataInputAssociation = dataInputAssociation;

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

    public void setIncoming(SequenceFlow incoming) {
        this.incoming = incoming;
        if (incoming != null) {
            this.elementIncoming = doc.createElement("bpmn:incoming");
            this.elementIncoming.setTextContent(incoming.getId());
            this.elementTask.appendChild(this.elementIncoming);
        }
    }

    public void setOutgoing(SequenceFlow outgoing) {
        this.outgoing = outgoing;
        if (outgoing != null) {
            this.elementOutgoing = doc.createElement("bpmn:outgoing");
            this.elementOutgoing.setTextContent(outgoing.getId());
            this.elementTask.appendChild(this.elementOutgoing);
        }
    }

    public Element getElementIncoming() {
        return elementIncoming;
    }

    public Element getElementOutgoing() {
        return elementOutgoing;
    }

    public SequenceFlow getOutgoing() {
        return outgoing;
    }

    public SequenceFlow getIncoming() {
        return incoming;
    }

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

    public ArrayList<Step> setSteps(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

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
