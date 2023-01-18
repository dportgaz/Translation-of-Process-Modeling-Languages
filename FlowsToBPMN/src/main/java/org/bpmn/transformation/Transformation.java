package org.bpmn.transformation;

import org.w3c.dom.Element;

import javax.xml.transform.TransformerException;

public interface Transformation {

    void transform() throws TransformerException;
    void appendXMLElements(Element definitionsElement);

}
