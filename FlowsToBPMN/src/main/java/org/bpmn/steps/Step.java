package org.bpmn.steps;

import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;

public interface Step {

    void execute() throws TransformerException;

    void setProcesses(Element definitionsElement);

}
