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
import java.util.HashSet;
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

    final double eventWidth = 36.0;

    final double eventHeight = 36.0;

    final double activityWidth = 100.0;

    final double activityHeight = 80.0;

    final double gatewayWidth = 50.0;

    final double gatewayHeight = 50.0;

    final double flowsLength = 160.0;

    final double flowsHeight = 150.0;

    ArrayList<BPMNShape> temp = new ArrayList<>();

    HashSet<String> printMark = new HashSet<>();

    HashSet<String> targetMark = new HashSet<>();

    ArrayList<BPMNShape> shapes = new ArrayList<>();

    public void f(Document doc, Element rootElement, double x, double y, String e, String pId, ArrayList<SequenceFlow> flows) {

        ArrayList<String> list = new ArrayList<>();

        for (SequenceFlow sf : flows) {

            String source = sf.getSourceRef();
            String target = sf.getTargetRef();
            //System.out.println("SOURCE: " + source + " e: " + e + " target: " + target);

            if (e.equals(source)) {
                Bounds tempBounds = null;
                if (!printMark.contains(e)) {
                    System.out.println("x=" + x + " y=" + y + " " + e);


                    // extract in method to recognize if activity, event or gateway
                    Pattern activityPattern = Pattern.compile("Activity*");
                    Pattern eventPattern = Pattern.compile("Event*");
                    Pattern gatewayPattern = Pattern.compile("Gateway*");
                    Matcher activityMatcher = activityPattern.matcher(e);
                    Matcher eventMatcher = eventPattern.matcher(e);
                    Matcher gatewayMatcher = gatewayPattern.matcher(e);

                    if(activityMatcher.find()){
                        tempBounds = new Bounds(doc,x,y,activityWidth, activityHeight);
                    }else if(eventMatcher.find()){
                        tempBounds = new Bounds(doc,x,y,eventWidth, eventHeight);
                    }else if(gatewayMatcher.find()){
                        tempBounds = new Bounds(doc,x,y,gatewayWidth, gatewayHeight);
                    }

                    BPMNShape tempShape = new BPMNShape(doc, e, tempBounds);

                    tempShape.setShapeParticipant();
                    tempShape.setBounds();
                    rootElement.appendChild(tempShape.getBpmnElement());

                    shapes.add(tempShape);
                    printMark.add(e);
                }

                if (!targetMark.contains(target)) {
                    list.add(target);
                    //System.out.println("LIST: " + list);
                    targetMark.add(target);
                }

            }


        }
        x += flowsLength;
        int cntElements = list.size();
        int offSet = 0;
        if (cntElements > 1) {
            if(cntElements % 2 == 0){

            }else{

            }
            for (int t = cntElements - 1; t >= 0; t--) {
                y -= flowsHeight;
                f(doc, rootElement, x, y, list.get(t),pId, flows);
            }
        } else if (cntElements == 1) {
            f(doc, rootElement, x, y, list.get(0),pId, flows);
        } else if (!printMark.contains(e)) {
            double tempX = x - flowsLength;
            String end = getProcessMap().get(pId).getEndEvent().getId();
            printMark.add(end);
            System.out.println("x=" + tempX + " y=" + y + " " + end);
            Bounds tempBounds = new Bounds(doc,tempX,y,eventWidth, eventHeight);
            BPMNShape tempShape = new BPMNShape(doc, e, tempBounds);
            tempShape.setShapeParticipant();
            tempShape.setBounds();
            rootElement.appendChild(tempShape.getBpmnElement());
        }
    }


    public void parseFlows(Document doc, Element rootElement, String processId, double x, double y) {

        //bring elements of pool in order according to flows

        String start = getProcessMap().get(processId).getStartEvent().getId();
        ArrayList<SequenceFlow> flows = getProcessMap().get(processId).getSequenceFlowList();
        printMark.clear();
        targetMark.clear();

        f(doc, rootElement, x, y, start, processId, flows);


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
        double startEventY = participantHeight / 2 - 20 + participantStartY;
        for (FlowsParticipant participant : FillFlowsParticipant.getParticipants()) {

            // add pools
            addParticipantsShape(doc, bpmnlane, participant, participantStartY);

            parseFlows(doc, bpmnlane, participant.getProcessRef(), startEventX, startEventY);

            // adapt positions for next participant/pool
            participantStartY += participantYInc;
            startEventY = participantHeight / 2 - 20 + participantStartY;

            System.out.println();

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
        Bounds bounds = new Bounds(doc, this.startEventX, startEventStartY, this.eventWidth, this.eventHeight);
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
