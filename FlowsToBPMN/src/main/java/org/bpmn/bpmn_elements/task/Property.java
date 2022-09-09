package org.bpmn.bpmn_elements.task;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import static org.bpmn.steps.Execution.doc;

public class Property {

    String id;

    final String name = "__targetRef_placeholder";

    Element elementProperty;

    public Property(){
        this.id = "Property_" + RandomIdGenerator.generateRandomUniqueId(6);
        setElement();
    }

    private void setElement() {

        this.elementProperty = doc.createElement("bpmn:property");
        this.elementProperty.setAttribute("id", id);
        this.elementProperty.setAttribute("name", name);

    }

    public Element getElementProperty() {
        return elementProperty;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
