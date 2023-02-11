package org.bpmn.bpmn_elements.collaboration;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.transition.MessageFlow;
import org.bpmn.flows_entities.AbstractFlowsEntity;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.bpmn_elements.collaboration.participant.Pool;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.bpmn.transformation.FlowsToBpmn.doc;

public class Collaboration {

    String id;

    Element elementCollaboration;

    public static ArrayList<Pool> pools = new ArrayList<>();

    HashSet<MessageFlow> messageFlows = new HashSet<>();


    public Collaboration() {
        this.id = "Collaboration_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementCollaboration = doc.createElement("bpmn:collaboration");
        setElementCollaboration();
    }

    public HashSet<MessageFlow> getMessageFlows() {
        return messageFlows;
    }

    // fill attributes of collaboration Element
    private void setElementCollaboration() {
        this.elementCollaboration.setAttribute("id", this.id);
    }

    public Element getElementCollaboration() {
        return elementCollaboration;
    }

    public String getId() {
        return this.id;
    }

    public MessageFlow getMessageFlowByTarget(BPMNElement element){
        for(MessageFlow mf : messageFlows){
            if(element.getId().equals(mf.getTargetRef().getId())){
                return mf;
            }
        }
        return null;
    }

    public void setParticipants(HashMap<Double, ArrayList<AbstractFlowsEntity>> objectTypeObjects, boolean adHoc, boolean expandedSubprocess) {

        for (Double key : objectTypeObjects.keySet()) {
            objectTypeObjects.get(key).forEach(obj -> {

                if (obj != null && obj.getMethodName().equals("UpdateName")) {

                    String participantName = (String) obj.getParameters().get(0);

                    // work around for json double entry bug in flows json
                    if (!containsParticipant(participantName)) {

                        Pool pool = new Pool(this, key, participantName);
                        pools.add(pool);

                        // add participant to collaboration element
                        elementCollaboration.appendChild(pool.getParticipantElement());
                    }

                }

            });
        }
        for (Pool pool : pools) {
            pool.setProcessRef(objectTypeObjects, adHoc, expandedSubprocess);
        }
    }

    private boolean containsParticipant(String participantName) {

        for (Pool participant : pools) {
            if (participant.getName().equals(participantName)) {
                return true;
            }
        }
        return false;

    }
}
