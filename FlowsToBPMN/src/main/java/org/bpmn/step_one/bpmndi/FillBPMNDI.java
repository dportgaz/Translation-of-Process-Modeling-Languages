package org.bpmn.step_one.bpmndi;

import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.FillFlowsParticipant;
import org.bpmn.step_one.collaboration.participant.FlowsParticipant;
import org.bpmn.step_one.process.FillFlowsProcess;
import org.bpmn.step_one.process.FlowsProcess;
import org.bpmn.step_one.process.activity.Task;
import org.bpmn.step_one.process.dataobject.DataObject;
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

    final double flowsLength = 52.0;

    final double flowsHeight = 150.0;

    final double dataObjectWidth = 50.0;

    final double dataObjectHeight = 64.0;

    double poolHeight;

    final double poolHeightOffset = 20;

    final double xTaskOffset = 160;

    double poolWidth;

    ArrayList<BPMNShape> temp = new ArrayList<>();

    HashSet<String> printMark = new HashSet<>();

    HashSet<String> targetMark = new HashSet<>();

    ArrayList<BPMNShape> shapes = new ArrayList<>();

    public void f(Document doc, Element rootElement, double x, double y, String e, String pId, ArrayList<Task> tasks, ArrayList<SequenceFlow> flows, String previous) {

        ArrayList<String> list = new ArrayList<>();

        // extract in method to recognize if activity, event or gateway
        Pattern activityPattern = Pattern.compile("Activity*");
        Pattern eventPattern = Pattern.compile("Event*");
        Pattern gatewayPattern = Pattern.compile("Gateway*");
        Matcher activityMatcher = activityPattern.matcher(e);
        Matcher eventMatcher = eventPattern.matcher(e);
        Matcher gatewayMatcher = gatewayPattern.matcher(e);
        Matcher activityMatcherPrev = null;
        Matcher eventMatcherPrev = null;
        Matcher gatewayMatcherPrev = null;
        Bounds tempBounds = null;

        if (previous != null) {

            activityMatcherPrev = activityPattern.matcher(previous);
            eventMatcherPrev = eventPattern.matcher(previous);
            gatewayMatcherPrev = gatewayPattern.matcher(previous);
        }

        for (SequenceFlow sf : flows) {

            String source = sf.getSourceRef();
            String target = sf.getTargetRef();
            //System.out.println("SOURCE: " + source + " e: " + e + " target: " + target);

            if (e.equals(source)) {
                if (!printMark.contains(e)) {

                    if (previous != null) {

                        if (activityMatcher.find()) {
                            x += 145;
                            if (eventMatcherPrev.find()) {
                                x -= 67;
                                y -= 22;
                            } else if (gatewayMatcherPrev.find()) {
                                x -= 55;
                                y -= 15;
                            }
                            tempBounds = new Bounds(doc, x, y, activityWidth, activityHeight);
                        } else if (eventMatcher.find()) {
                            tempBounds = new Bounds(doc, x, y, eventWidth, eventHeight);
                        } else if (gatewayMatcher.find()) {
                            x += 160;
                            if (activityMatcherPrev.find()) {
                                x -= 20;
                                y += 15;
                            } else if (eventMatcherPrev.find()) {
                                x -= 87;
                                y -= 7;
                            }
                            tempBounds = new Bounds(doc, x, y, gatewayWidth, gatewayHeight);
                        }
                    } else {
                        if (activityMatcher.find()) {
                            tempBounds = new Bounds(doc, x, y, activityWidth, activityHeight);
                        } else if (eventMatcher.find()) {
                            tempBounds = new Bounds(doc, x, y, eventWidth, eventHeight);
                        } else if (gatewayMatcher.find()) {
                            tempBounds = new Bounds(doc, x, y, gatewayWidth, gatewayHeight);
                        }
                    }

                    BPMNShape tempShape = new BPMNShape(doc, e, tempBounds, tasks);

                    tempShape.setShapeParticipant();
                    tempShape.setBounds();
                    rootElement.appendChild(tempShape.getBpmnElement());

                    shapes.add(tempShape);
                    printMark.add(e);
                    System.out.println("x=" + x + " y=" + y + " " + e);
                }

                if (!targetMark.contains(target)) {
                    list.add(target);
                    //System.out.println("LIST: " + list);
                    targetMark.add(target);
                }

            }


        }
        //x += flowsLength;
        int cntElements = list.size();
        if (cntElements > 1) {
            for (int t = cntElements - 1; t >= 0; t--) {
                f(doc, rootElement, x, y, list.get(t), pId, tasks, flows, e);
                y -= 100;
            }
        } else if (cntElements == 1) {
            f(doc, rootElement, x, y, list.get(0), pId,tasks, flows, e);
        } else if (!printMark.contains(e)) {
            //double tempX = x - flowsLength;
            String end = getProcessMap().get(pId).getEndEvent().getId();
            printMark.add(end);
            System.out.println("x=" + x + " y=" + y + " " + end);

            if (gatewayMatcherPrev.find()) {
                x += 86;
                y += 7;
            } else if (activityMatcherPrev.find()) {
                x += 141;
                y += 22;
            }
            tempBounds = new Bounds(doc, x, y, eventWidth, eventHeight);
            BPMNShape tempShape = new BPMNShape(doc, e, tempBounds);
            shapes.add(tempShape);
            tempShape.setShapeParticipant();
            tempShape.setBounds();
            rootElement.appendChild(tempShape.getBpmnElement());
        }

    }


    public void parseFlows(Document doc, Element rootElement, String processId, double x, double y) {

        //bring elements of pool in order according to flows

        String start = getProcessMap().get(processId).getStartEvent().getId();
        ArrayList<SequenceFlow> flows = getProcessMap().get(processId).getSequenceFlowList();
        ArrayList<Task> tasks = getProcessMap().get(processId).getTaskList();
        printMark.clear();
        targetMark.clear();

        f(doc, rootElement, x, y, start, processId,tasks, flows, null);
        addFlowsEdge(doc, rootElement, flows);
        addDataObjects(doc, rootElement, tasks);
        System.out.println(shapes);
        shapes.clear();


    }

    public void fillBPMNDI(Document doc, String bpmndiagramID, Element rootElement) {

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

    public void addParticipantsShape(Document doc, Element rootElement, FlowsParticipant p, double participantY) {

        Bounds bounds = new Bounds(doc, this.participantX, participantY, this.participantWidth, this.participantHeight);
        BPMNShape shape = new BPMNShape(doc, p.getParticipantID(), "true", bounds);
        shape.setShapeParticipant();
        shape.setBounds();
        poolHeight = shape.getBounds().getY();
        rootElement.appendChild(shape.getBpmnElement());

    }

    private BPMNShape getBPMNShapeByFlow(ArrayList<SequenceFlow> flows, String sfId) {

        for (BPMNShape bs : shapes) {

            if (bs.getElementId().equals(sfId)) {
                return bs;
            }

        }

        return null;

    }

    private BPMNShape getBPMNShapeByTask(String taskId) {

        for (BPMNShape bs : shapes) {

            if (bs.getElementId().equals(taskId)) {
                return bs;
            }

        }

        return null;

    }

    private BPMNShape getFirstTaskShape() {

        for (BPMNShape bs : shapes) {
            Pattern activityPattern = Pattern.compile("Activity*");
            Matcher activityMatcher = activityPattern.matcher(bs.getElementId());
            if (activityMatcher.find()) {
                return bs;
            }
        }

        return null;

    }

    public void addDataObjects(Document doc, Element rootElement, ArrayList<Task> tasks) {
        BPMNShape firstBsTask = getBPMNShapeByTask(tasks.get(0).getId());
        double xTask = firstBsTask.getBounds().getX();
        for (Task task : tasks) {
            DataObject d = task.getDataObject();
            BPMNShape bs = getBPMNShapeByTask(task.getId());
            if (d != null) {

                Element dataObject = doc.createElement("bpmndi:BPMNShape");
                dataObject.setAttribute("id", d.getRefId() + "_di");
                dataObject.setAttribute("bpmnElement", d.getRefId());

                Element dataObjectBounds = doc.createElement("dc:Bounds");
                dataObjectBounds.setAttribute("x", String.valueOf(xTask));
                dataObjectBounds.setAttribute("y", String.valueOf(poolHeight + poolHeightOffset));
                dataObjectBounds.setAttribute("width", String.valueOf(dataObjectWidth));
                dataObjectBounds.setAttribute("height", String.valueOf(dataObjectHeight));
                dataObject.appendChild(dataObjectBounds);

                Element dataObjectFlowOutput = doc.createElement("bpmndi:BPMNEdge");
                dataObjectFlowOutput.setAttribute("id", task.getDataOutputAssociation() + "_di");
                dataObjectFlowOutput.setAttribute("bpmnElement", task.getDataOutputAssociation());
                System.out.println("WEIRD: " + task.getDataOutputAssociation());

                Element waypointOutStart = doc.createElement("di:waypoint");
                Element waypointOutEnd = doc.createElement("di:waypoint");
                waypointOutStart.setAttribute("x", String.valueOf(bs.getBounds().getX() + bs.getBounds().getWidth() / 2));
                waypointOutStart.setAttribute("y", String.valueOf(bs.getBounds().getY()));
                waypointOutEnd.setAttribute("x", String.valueOf(xTask + dataObjectWidth / 2));
                waypointOutEnd.setAttribute("y", String.valueOf(poolHeight + poolHeightOffset - dataObjectHeight + 300));

                dataObjectFlowOutput.appendChild(waypointOutStart);
                dataObjectFlowOutput.appendChild(waypointOutEnd);
                rootElement.appendChild(dataObjectFlowOutput);

                if (task.getDataInputAssociation() != null) {


                    Element dataObjectFlowInput = doc.createElement("bpmndi:BPMNEdge");
                    dataObjectFlowInput.setAttribute("id", task.getDataInputAssociation() + "_di");
                    dataObjectFlowInput.setAttribute("bpmnElement", task.getDataInputAssociation());

                    Element waypointInStart = doc.createElement("di:waypoint");
                    Element waypointInEnd = doc.createElement("di:waypoint");
                    waypointInStart.setAttribute("x", String.valueOf(xTask + dataObjectWidth / 2));
                    waypointInStart.setAttribute("y", String.valueOf(bs.getBounds().getY()));
                    waypointInEnd.setAttribute("x", String.valueOf(bs.getBounds().getX() + bs.getBounds().getWidth() / 2));
                    waypointInEnd.setAttribute("y", String.valueOf(poolHeight + poolHeightOffset - dataObjectHeight + 300));

                    dataObjectFlowInput.appendChild(waypointInStart);
                    dataObjectFlowInput.appendChild(waypointInEnd);

                    rootElement.appendChild(dataObjectFlowInput);
                }

                rootElement.appendChild(dataObject);

                xTask += xTaskOffset;

            }

        }
    }

    public void addFlowsEdge(Document doc, Element rootElement, ArrayList<SequenceFlow> flows) {

        for (SequenceFlow sf : flows) {

            BPMNShape bsSource = getBPMNShapeByFlow(flows, sf.getSourceRef());
            BPMNShape bsTarget = getBPMNShapeByFlow(flows, sf.getTargetRef());

            Element flow = doc.createElement("bpmndi:BPMNEdge");
            flow.setAttribute("id", sf.getId() + "_di");
            flow.setAttribute("bpmnElement", sf.getId());

            double xStart = bsSource.getBounds().getX() + bsSource.getBounds().getWidth();
            double yStart = bsSource.getBounds().getY() + bsSource.getBounds().getHeight() / 2;

            double xEnd = bsTarget.getBounds().getX();
            double yEnd = bsTarget.getBounds().getY() + bsTarget.getBounds().getHeight() / 2;

            Element waypointStart = doc.createElement("di:waypoint");
            waypointStart.setAttribute("x", String.valueOf(xStart));
            waypointStart.setAttribute("y", String.valueOf(yStart));

            Element waypointEnd = doc.createElement("di:waypoint");
            waypointEnd.setAttribute("x", String.valueOf(xEnd));
            waypointEnd.setAttribute("y", String.valueOf(yEnd));

            flow.appendChild(waypointStart);
            flow.appendChild(waypointEnd);
            rootElement.appendChild(flow);

        }


    }

}

