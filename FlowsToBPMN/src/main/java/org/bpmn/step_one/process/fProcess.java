package org.bpmn.step_one.process;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.w3c.dom.Element;

import static org.bpmn.step_one.fillxml.fillXMLStepOneRenew.doc;

public class fProcess {

    static int countProcess = 0;

    String fProcessId;

    static String isExecutable = "true";

    Element elementfProcess;


    public fProcess(String fProcessId){
        this.fProcessId = "Process_" + fProcessId;
        this.elementfProcess = doc.createElement("bpmn:process");
        setElementfProcess();
        countProcess++;

    }

    private void setElementfProcess() {
        this.elementfProcess.setAttribute("id", this.fProcessId);
        if(countProcess == 0){
            this.elementfProcess.setAttribute("isExecutable", this.isExecutable);
        }
        else{
            isExecutable = "false";
            this.elementfProcess.setAttribute("isExecutable", this.isExecutable);
        }
    }

    public Element getElementfProcess() {
        return elementfProcess;
    }

    public String getfProcessId() {
        return fProcessId;
    }
}
