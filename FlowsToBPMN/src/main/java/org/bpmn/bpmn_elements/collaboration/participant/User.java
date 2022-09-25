package org.bpmn.bpmn_elements.collaboration.participant;

import org.bpmn.bpmn_elements.BPMNElement;

import java.util.HashSet;

public class User {

    Double id;

    String name;

    HashSet<BPMNElement> elements = new HashSet<>();

    public User(Double id){
        this.id = id;
    }

    public User(String name){
        this.name = name;
    }

    public User(Double id, String name){
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
