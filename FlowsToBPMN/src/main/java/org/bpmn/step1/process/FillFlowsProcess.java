package org.bpmn.step1.process;

import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.bpmn.step1.collaboration.participant.FillFlowsParticipant.getParticipants;

public class FillFlowsProcess {

    static ArrayList<FlowsProcess> processes = new ArrayList<FlowsProcess>();

    public FillFlowsProcess() throws FileNotFoundException {
        setProcessList();
    }

    public void fillProcesses(Document doc, Element rootElement) {
        for (int i = 0; i < processes.size(); i++) {
            FlowsProcess fp = processes.get(i);
            Element process = doc.createElement("bpmn:process");
            process.setAttribute("id", "Process_" + fp.getId());
            process.setAttribute("isExecutable", new Boolean(fp.getIsExecutable()).toString());
            rootElement.appendChild(process);

            Element startEvent = doc.createElement("bpmn:startEvent");
            startEvent.setAttribute("id", "Event_" + fp.getStartEvent().getId());
            process.appendChild(startEvent);
        }
    }

    public void setProcessList() throws FileNotFoundException {

        boolean firstProcess = true;
        for (FlowsParticipant tempFlowsParticipant : getParticipants()) {

            if (firstProcess == true) {
                FlowsProcess fp = new FlowsProcess(tempFlowsParticipant.getParticipantID(), true);
                processes.add(fp);
                firstProcess = false;
            } else {
                FlowsProcess fp = new FlowsProcess(tempFlowsParticipant.getParticipantID(), false);
                processes.add(fp);
            }
        }
    }
}
