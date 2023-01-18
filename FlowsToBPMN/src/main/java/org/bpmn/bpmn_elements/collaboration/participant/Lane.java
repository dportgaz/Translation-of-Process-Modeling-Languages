package org.bpmn.bpmn_elements.collaboration.participant;

import org.bpmn.bpmn_elements.BPMNElement;

import java.util.HashSet;

public class Lane {

    Double id;

    String name;

    HashSet<BPMNElement> elements = new HashSet<>();

    public Lane(Double id){
        this.id = id;
    }

    public Lane(String name){
        this.name = name;
    }

    public Lane(Double id, String name){
        this.id = id;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Double id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Double getId() {
        return id;
    }

    public HashSet<BPMNElement> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
