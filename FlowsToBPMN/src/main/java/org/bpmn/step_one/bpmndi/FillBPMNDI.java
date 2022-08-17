package org.bpmn.step_one.bpmndi;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step_one.collaboration.participant.FlowsParticipant;
import org.bpmn.step_one.process.FillFlowsProcess;
import org.bpmn.step_one.process.FlowsProcess;
import org.bpmn.step_one.process.activity.Task;
import org.bpmn.step_one.process.flow.SequenceFlow;
import org.bpmn.step_one.process.gateway.ExclusiveGateway;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bpmn.step_one.fillxml.fillXML.getCollaborationID;
import static org.bpmn.step_one.process.FillFlowsProcess.getProcessMap;
import static org.bpmn.step_one.process.FillFlowsProcess.getProcesses;

public class FillBPMNDI {

    ArrayList<String> elements = new ArrayList<>();

    HashMap<String, ArrayList<String>> loopElement = new HashMap<>();

    HashMap<String, ArrayList<String>> decisionElement = new HashMap<>();
    final double participantX = 70.0;
    final double participantWidth = 1530.0;
    final double participantHeight = 500.0;
    final double participantYInc = 550.0;

    final double startEventYInc = 200.0;
    final double startEventX = 200.0;
    final double startEventWidth = 36.0;
    final double startEventHeight = 36.0;

    final double activityWidth = 100.0;

    final double activityHeight = 80.0;

    final double gatewayWidth = 50.0;

    final double gatewayHeight = 50.0;

    final double flowsLength = 160.0;

    public void parseFlows(String processId) {

        //bring elements of pool in order according to flows

        elements.clear();
        ArrayList<SequenceFlow> tempFlows = getProcessMap().get(processId).getSequenceFlowList();

        String endEventId = getProcessMap().get(processId).getEndEvent().getId();
        String element = getProcessMap().get(processId).getStartEvent().getId();

        while (element != null) {
            for (SequenceFlow sf : tempFlows) {
                if (sf.getSourceRef().equals(element)) {
                    elements.add(element);
                    element = sf.getTargetRef();
                }
            }
            if (endEventId.equals(element)) {
                elements.add(element);
                System.out.println("LIST: " + elements);
                return;
            }
        }

    }

    public boolean listContains(String element) {

        for (String source : elements) {
            if (source.equals(element)) {
                return true;
            }
        }
        return false;
    }

    public void fillBPMNDI(Document doc, String bpmndiagramID, String filename, Element rootElement) throws
            FileNotFoundException, XPathExpressionException {

        Element bpmndiagram = doc.createElement("bpmndi:BPMNDiagram");
        bpmndiagram.setAttribute("id", bpmndiagramID);
        rootElement.appendChild(bpmndiagram);

        Element bpmnlane = doc.createElement("bpmndi:BPMNPlane");
        bpmnlane.setAttribute("id", "BPMNlane_" + RandomIdGenerator.generateRandomUniqueId(6));
        bpmnlane.setAttribute("bpmnElement", getCollaborationID());
        bpmndiagram.appendChild(bpmnlane);

        double participantStartY = 100.0;
        double startEventStartY = participantHeight / 2 - 20 + participantStartY;
        for (FlowsParticipant participant : FillFlowsParticipant.getParticipants()) {

            // add participant shape
            addParticipantsShape(doc, bpmnlane, participant, participantStartY);

            // add StartEvent shape; every StartEvent starts at the same position of every pool
            //addPoolElements(doc, bpmnlane, participant, startEventStartY);
            parseFlows(participant.getProcessRef());

            // adapt positions for next participant/pool
            participantStartY += participantYInc;
            startEventStartY = participantHeight / 2 - 20 + participantStartY;

            /*
            // add flows edge
            addFlowsEdge(doc, bpmnlane, p);

            // add activities
            addActivitiesShape(doc, bpmnlane, p);

            // add gateway shapes
            addGatewaysShape(doc, bpmnlane, p);

            // add endevent shape
            addEndEventShape(doc, bpmnlane, p);

            // add labels
            // addLabels(doc, bpmnlane, p);
            */

        }

    }

    /*
    public void addPoolElements(Document doc, Element rootElement, FlowsParticipant participant, double startEventStartY) {

        String processId = participant.getProcessRef();
        String startId = getProcessMap().get(processId).getStartEvent().getId();

        addStartEventShape(doc, rootElement, participant, startEventStartY);
        parseFlows(processId);
        String element = flows.get(startId);

        Pattern activityPattern = Pattern.compile("Activity*");
        Pattern gatewayPattern = Pattern.compile("Gateway*");

        int cnt = 0;


        while (element != null) {

            Matcher activityMatcher = activityPattern.matcher(element);
            Matcher gatewayMatcher = gatewayPattern.matcher(element);

            if (activityMatcher.find()) {

                cnt++;
                Bounds bounds = new Bounds(doc, this.startEventX + this.flowsLength*cnt, startEventStartY - 20, this.activityWidth, this.activityHeight);
                BPMNShape shape = new BPMNShape(doc, element, bounds);
                shape.setShape();
                shape.setBounds();
                rootElement.appendChild(shape.getBpmnElement());


            } else if (gatewayMatcher.find()) {

                cnt++;
                Bounds bounds = new Bounds(doc, this.startEventX + this.flowsLength*cnt, startEventStartY - 5, this.gatewayWidth, this.gatewayHeight);
                BPMNShape shape = new BPMNShape(doc, element, bounds);
                shape.setShape();
                shape.setBounds();
                rootElement.appendChild(shape.getBpmnElement());

            }

            String temp = element;
            element = flows.get(element);
            flows.remove(temp);
            System.out.println(element);


        }

    }
    */


    public void addActivitiesShape(Document doc, Element bpmnlane, FlowsParticipant p) {

        FlowsProcess fp = FillFlowsProcess.getProcessById(p.getProcessRef());

        for (Task task : fp.getTaskList()) {

            Element taskTemp = doc.createElement("bpmndi:BPMNShape");
            taskTemp.setAttribute("id", task.getId() + "_di");
            taskTemp.setAttribute("bpmnElement", task.getId());
            bpmnlane.appendChild(taskTemp);

        }

    }

    public void addEndEventShape(Document doc, Element bpmnlane, FlowsParticipant p) {

        Element end = doc.createElement("bpmndi:BPMNShape");
        end.setAttribute("id", FillFlowsProcess.getProcessById(p.getProcessRef()).getEndEvent().getId() + "_di");
        end.setAttribute("bpmnElement", FillFlowsProcess.getProcessById(p.getProcessRef()).getEndEvent().getId());
        bpmnlane.appendChild(end);

    }

    public void addGatewaysShape(Document doc, Element bpmnlane, FlowsParticipant p) {

        FlowsProcess fp = FillFlowsProcess.getProcessById(p.getProcessRef());

        for (ExclusiveGateway gate : fp.getGateways()) {

            Element flow = doc.createElement("bpmndi:BPMNShape");
            flow.setAttribute("id", gate.getId() + "_di");
            flow.setAttribute("bpmnElement", gate.getId());
            bpmnlane.appendChild(flow);

        }

    }

    public void addLabels(Document doc, Element bpmnlane, FlowsParticipant p) {

        Element start = doc.createElement("bpmndi:BPMNLabel");
        bpmnlane.appendChild(start);

    }

    public void addStartEventShape(Document doc, Element rootElement, FlowsParticipant p, double startEventStartY) {

        String startEventId = FillFlowsProcess.getProcessById(p.getProcessRef()).getStartEvent().getId();
        Bounds bounds = new Bounds(doc, this.startEventX, startEventStartY, this.startEventWidth, this.startEventHeight);
        BPMNShape shape = new BPMNShape(doc, startEventId, bounds);
        shape.setShape();
        shape.setBounds();
        rootElement.appendChild(shape.getBpmnElement());

    }

    public void addParticipantsShape(Document doc, Element rootElement, FlowsParticipant p, double participantY) {

        Bounds bounds = new Bounds(doc, this.participantX, participantY, this.participantWidth, this.participantHeight);
        BPMNShape shape = new BPMNShape(doc, p.getParticipantID(), "true", bounds);
        shape.setShapeParticipant();
        shape.setBounds();
        rootElement.appendChild(shape.getBpmnElement());

    }

    public void addFlowsEdge(Document doc, Element rootElement, FlowsParticipant p) {

        FlowsProcess fp = FillFlowsProcess.getProcessById(p.getProcessRef());

        for (SequenceFlow sf : fp.getSequenceFlowList()) {

            Element flow = doc.createElement("bpmndi:BPMNEdge");
            flow.setAttribute("id", sf.getId() + "_di");
            flow.setAttribute("bpmnElement", sf.getId());
            rootElement.appendChild(flow);

        }


    }
}
