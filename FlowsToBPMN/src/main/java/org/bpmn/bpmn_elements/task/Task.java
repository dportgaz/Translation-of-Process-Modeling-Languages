package org.bpmn.bpmn_elements.task;

import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Participant;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Task {

    String id;

    String name;

    Double createdEntityId;

    Participant participant;

    // SequenceFlow incoming;

    // SequenceFlow outgoing;

    String dataInputAssociation;

    String dataOutputAssociation;

    DataObject dataObject;

    Task before;

    Task after;

    Task step;

    StartEvent beforeEvent;

    // EndEvent afterEvent;

    String property;

    HashSet<AbstractObjectType> steps = new HashSet<>();

    ArrayList<Task> stepNamesByTask = new ArrayList<>();

    boolean isSubprocess;

    String inputAssociationSource;

    Element elementTask;

    public Task(Double createdEntityId, String name, Participant participant) {
        this.id = "Activity_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.createdEntityId = createdEntityId;
        this.name = name;
        this.participant = participant;
        this.dataObject = new DataObject(this);
    }

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

    public void setIsSubprocess() {
        this.isSubprocess = true;
    }

    public boolean getIsSubprocess() {
        return this.isSubprocess;
    }

    private ArrayList<String> stepNamesByName = new ArrayList<>();

    public HashSet<AbstractObjectType> getSteps() {
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
        this.dataOutputAssociation = "DataOutputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public void setDataInputAssociation() {
        this.dataInputAssociation = "DataInputAssociation_" + RandomIdGenerator.generateRandomUniqueId(6);
    }

    public String getDataInputAssociation() {
        return this.dataInputAssociation;
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

    /*
    public void fillStepsToActivity(ConcreteObjectType objectMap, FlowsProcess fp, String key) throws FileNotFoundException {

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
                                        if (!stepNamesByName.contains(t.getName())) {
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

     */
}
