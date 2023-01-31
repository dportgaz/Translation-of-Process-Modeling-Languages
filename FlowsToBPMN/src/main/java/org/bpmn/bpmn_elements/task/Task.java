package org.bpmn.bpmn_elements.task;

import com.google.gson.internal.LinkedTreeMap;
import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.event.IntermediateThrowEvent;
import org.bpmn.flows_process_model.Port;
import org.bpmn.bpmn_elements.association.DataInputAssociation;
import org.bpmn.bpmn_elements.association.DataOutputAssociation;
import org.bpmn.bpmn_elements.collaboration.participant.Pool;
import org.bpmn.bpmn_elements.collaboration.participant.Lane;
import org.bpmn.bpmn_elements.dataobject.DataObject;
import org.bpmn.bpmn_elements.event.EndEvent;
import org.bpmn.bpmn_elements.event.StartEvent;
import org.bpmn.bpmn_elements.flows.SequenceFlow;
import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.bpmn_elements.collaboration.Collaboration.pools;
import static org.bpmn.transformation.FlowsToBpmn.doc;

public class Task implements BPMNElement {

    private Double createId;
    String id;

    String name;

    Double createdEntityId;

    Participant participant;

    SequenceFlow incoming;

    Element elementIncoming;

    SequenceFlow outgoing;

    Element elementOutgoing;

    BPMNElement beforeElement;

    BPMNElement afterElement;

    ArrayList<DataInputAssociation> dataInputAssociations = new ArrayList<>();

    DataOutputAssociation dataOutputAssociation;

    private DataObject dataObject;

    ArrayList<BPMNElement> before = new ArrayList<>();

    ArrayList<BPMNElement> after = new ArrayList<>();

    ArrayList<Task> beforeTask = new ArrayList<>();

    ArrayList<Task> afterTask = new ArrayList<>();

    Task step;

    StartEvent beforeEvent;

    EndEvent afterEvent;

    Property property;

    private ArrayList<Step> steps = new ArrayList<>();

    ArrayList<Task> stepNamesByTask = new ArrayList<>();

    boolean isSubprocess;

    boolean isEndTask = false;

    Element elementTask;

    Element elementDataOutputAssociation;

    Element elementDataInputAssociation;

    StartEvent start;

    EndEvent end;

    ArrayList<SequenceFlow> flows = new ArrayList<>();

    Double coordinationStepTypeId;

    ArrayList<Port> ports = new ArrayList<>();

    String participantName;

    int cntOtherRelations;

    boolean adHoc;

    Permission permission;

    Participant stepParticipant;

    boolean computationStep;

    boolean isSendTask;

    Lane lane;

    IntermediateThrowEvent sendingMessage;

    public Task(Double createdEntityId, String name, Participant participant, boolean computationStep) {
        this.id = "Activity_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.createdEntityId = createdEntityId;
        this.permission = permissionForStep(name);
        String permission = stepName();
        this.name = permission + " " + name;
        this.dataObject = new DataObject(this, name);
        this.participant = participant;
        this.participantName = name;
        this.computationStep = computationStep;
        if (computationStep) {
            this.elementTask = doc.createElement("bpmn:serviceTask");
        } else {
            this.elementTask = doc.createElement("bpmn:task");
        }
        this.elementTask.setAttribute("id", this.id);
        this.elementTask.setAttribute("name", this.name);
        this.elementIncoming = doc.createElement("bpmn:incoming");
        this.elementTask.appendChild(this.elementIncoming);
        this.elementOutgoing = doc.createElement("bpmn:outgoing");
        this.elementTask.appendChild(this.elementOutgoing);
    }

    public void setSendTask() {
        Node temp = this.elementTask.getFirstChild();
        this.isSendTask = true;
        this.id = "SendActivity_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementTask = doc.createElement("bpmn:sendTask");
        this.elementTask.setAttribute("id", this.id);
        this.elementTask.setAttribute("name", this.name);
        this.elementTask.appendChild(temp);
    }

    private Permission permissionForStep(String name) {
        for (Pool pool : pools) {
            if (name.equals(pool.getName())) {
                this.stepParticipant = pool;
                return Permission.READ;
            }
        }
        return Permission.WRITE;
    }

    private String stepName() {
        if (this.permission == Permission.WRITE) {
            return "Write";
        } else {
            return "Read";
        }
    }

    public void setSendingMessage(IntermediateThrowEvent sendingMessage) {
        this.sendingMessage = sendingMessage;
    }

    public IntermediateThrowEvent getSendingMessage() {
        return sendingMessage;
    }

    public Double getCreateId() {
        return createId;
    }

    public Permission getPermission() {
        return permission;
    }

    public String getParticipantName() {
        return participantName;
    }

    public int getCntOtherRelations() {
        return cntOtherRelations;
    }

    public void intCntOtherRelations() {
        cntOtherRelations++;
    }

    public void setUser(Lane lane) {
        this.lane = lane;
    }

    public Lane getUser() {
        return lane;
    }

    public Task(Double createdEntityId, String name, Participant participant, ArrayList<AbstractFlowsEntity> objects, boolean adHoc) {
        this.id = "Activity_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.createdEntityId = createdEntityId;
        this.name = name;
        this.participant = participant;
        this.dataObject = new DataObject(this);
        this.adHoc = adHoc;
        //this.steps = setSteps(objects);
        //setElement();
    }

    public ArrayList<Port> getPorts() {
        return ports;
    }

    public void setCoordinationStepTypeId(Double coordinationStepTypeId) {
        this.coordinationStepTypeId = coordinationStepTypeId;
    }

    public Double getCoordinationStepTypeId() {
        return coordinationStepTypeId;
    }

    public void setElement() {
        if (this.steps.size() > 0) {
            this.isSubprocess = true;
            if (adHoc) {
                this.elementTask = doc.createElement("bpmn:adHocSubProcess");
            } else {
                this.elementTask = doc.createElement("bpmn:subProcess");
            }
            setSubProcess();
        } else {
            this.isSubprocess = false;
            this.elementTask = doc.createElement("bpmn:task");
        }
        this.elementTask.setAttribute("id", this.id);
        this.elementTask.setAttribute("name", this.name);

        setDataOutputAssociation();
        this.elementIncoming = doc.createElement("bpmn:incoming");
        this.elementTask.appendChild(this.elementIncoming);
        this.elementOutgoing = doc.createElement("bpmn:outgoing");
        this.elementTask.appendChild(this.elementOutgoing);
    }

    public void setProperty(Property property) {
        this.property = property;
        if (this.property != null) {
            this.elementTask.appendChild(this.property.getElementProperty());
        }
    }

    public ArrayList<Task> getBeforeTask() {
        return beforeTask;
    }

    public ArrayList<Task> getAfterTask() {
        return afterTask;
    }

    public void setStart(StartEvent start) {
        this.start = start;
    }

    public StartEvent getStart() {
        return start;
    }

    @Override
    public void setBeforeElement(BPMNElement element) {
        this.beforeElement = element;
    }

    @Override
    public void setAfterElement(BPMNElement element) {
        this.afterElement = element;
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

        for (Step step : steps) {
            this.elementTask.appendChild(step.getElement());
        }

    }

    public void setStartEvent() {
        this.start = new StartEvent();
        this.elementTask.appendChild(this.start.getElement());
    }

    public void setEndEvent() {
        this.end = new EndEvent();
        this.elementTask.appendChild(this.end.getElement());
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

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }

    public Element getElement() {
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

    public Property getProperty() {
        return property;
    }

    public DataOutputAssociation getDataOutputAssociation() {
        return this.dataOutputAssociation;
    }

    public ArrayList<BPMNElement> getAfter() {
        return after;
    }

    @Override
    public BPMNElement getBeforeElement() {
        return beforeElement;
    }

    @Override
    public BPMNElement getAfterElement() {
        return afterElement;
    }

    public ArrayList<BPMNElement> getBefore() {
        return before;
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

    public void addDataInputAssociation(DataInputAssociation dataInputAssociation) {

        this.dataInputAssociations.add(dataInputAssociation);
        this.elementTask.appendChild(dataInputAssociation.getElementDataInputAssociation());
        Element target = doc.createElement("bpmn:targetRef");
        setProperty(new Property());
        target.setTextContent(property.getId());
        dataInputAssociation.getElementDataInputAssociation().appendChild(target);

    }

    public ArrayList<DataInputAssociation> getDataInputAssociations() {
        return this.dataInputAssociations;
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
            this.elementIncoming.setTextContent(incoming.getId());
        }
    }

    public void setOutgoing(SequenceFlow outgoing) {
        this.outgoing = outgoing;
        if (outgoing != null) {
            this.elementOutgoing.setTextContent(outgoing.getId());
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

    private boolean stepIsPredicate(ArrayList<AbstractFlowsEntity> objects, Double id) {
        for (AbstractFlowsEntity obj : objects) {
            if (obj != null && obj.getMethodName().equals("UpdatePredicateStepTypeExpression")) {
                LinkedTreeMap link = (LinkedTreeMap) obj.getParameters().get(1);
                LinkedTreeMap innerLink = (LinkedTreeMap) link.get("Left");
                if (innerLink.get("AttributeTypeId") != null && innerLink.get("AttributeTypeId").equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setStepsTemp(ArrayList<Step> subTasks) {
        this.steps = subTasks;
        setElement();
    }

    // TODO: GEHT BESSER SETSTEPS()
    public ArrayList<Step> setSteps(ArrayList<AbstractFlowsEntity> objects) {

        objects.forEach(obj -> {

            if (obj != null && (obj.getMethodName().equals("AddStepType") || obj.getMethodName().equals("AddComputationStepType"))) {
                boolean computationStep = obj.getMethodName().equals("AddComputationStepType");
                Double tempId = (Double) obj.getParameters().get(0);
                if (this.getCreatedEntityId().equals(tempId)) {
                    // trim steps by removing default steps
                    objects.forEach(obj2 -> {
                        if (obj2 != null
                                && obj2.getMethodName().equals("UpdateStepAttributeType")
                                && obj2.getParameters().get(0).equals(obj.getCreatedEntityId())
                                && !stepIsPredicate(objects, (Double) obj2.getParameters().get(1))) {
                            this.steps.add(this.getStep(objects, obj, computationStep));
                        }
                    });
                }
            }
        });

        return steps;
    }

    private Step getStep(ArrayList<AbstractFlowsEntity> objects, AbstractFlowsEntity absObj, boolean computationStep) {

        Double id = absObj.getCreatedEntityId();

        for (AbstractFlowsEntity obj : objects) {
            if (obj != null && obj.getMethodName().equals("UpdateStepAttributeType") && obj.getParameters().get(0).equals(id)) {
                Double tempId = (Double) obj.getParameters().get(1);
                for (AbstractFlowsEntity obj2 : objects) {
                    if (obj2 != null) {

                        Pattern p = Pattern.compile("^Update.*AttributeType$");
                        Matcher m = p.matcher(obj2.getMethodName());

                        if (m.find() && obj2.getParameters().get(0).equals(tempId)) {
                            String name = (String) obj2.getParameters().get(1);
                            return new Step(tempId, name, this.participant, this, computationStep);
                        }
                    }
                }
            }
        }
        return null;
    }
}
