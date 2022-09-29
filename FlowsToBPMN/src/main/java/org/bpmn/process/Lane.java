package org.bpmn.process;

import org.bpmn.bpmn_elements.BPMNElement;
import org.bpmn.bpmn_elements.collaboration.participant.Participant;
import org.bpmn.bpmn_elements.collaboration.participant.User;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.steps.BPMN;
import org.w3c.dom.Element;

import java.util.HashSet;

import static org.bpmn.steps.BPMN.doc;

public class Lane {

    Double x;

    Double y;

    Double height;

    Double width;
    String id;

    User user;

    Double middleX;

    Double middleY;

    Element laneElement;

    HashSet<BPMNElement> elements = new HashSet<>();

    Double participantMiddleY;

    public Lane(User user){
        this.user = user;
        this.id = "Lane_" + RandomIdGenerator.generateRandomUniqueId(6);
        setElement();
    }

    private void setElement() {
        this.laneElement = doc.createElement("bpmn:lane");
        laneElement.setAttribute("id", id);
        laneElement.setAttribute("name", user.getName());
    }

    public void addBPMNElement(BPMNElement element){
        elements.add(element);
        Element temp = doc.createElement("bpmn:flowNodeRef");
        temp.setTextContent(element.getId());
        laneElement.appendChild(temp);
    }

    public void setParticipantMiddleY(Double participantMiddleY) {
        this.participantMiddleY = participantMiddleY;
    }

    public Double getParticipantMiddleY() {
        return participantMiddleY;
    }

    public HashSet<BPMNElement> getElements() {
        return elements;
    }

    public Element getLaneElement() {
        return laneElement;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getHeight() {
        return height;
    }

    public Double getWidth() {
        return width;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Double getMiddleX() {
        return middleX;
    }

    public void setMiddleX(Double middleX) {
        this.middleX = middleX;
    }

    public void setMiddleY(Double middleY) {
        this.middleY = middleY;
    }

    public Double getMiddleY() {
        return middleY;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
