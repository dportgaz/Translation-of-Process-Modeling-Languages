package org.bpmn.step1.process;

import com.google.gson.internal.LinkedTreeMap;
import org.bpmn.flowsObjects.objecttype.AbstractObjectType;
import org.bpmn.flowsObjects.objecttype.ObjectTypeMap;
import org.bpmn.step1.collaboration.participant.FlowsParticipant;
import org.bpmn.step1.process.activity.Task;
import org.bpmn.step1.process.dataobject.DataObject;
import org.bpmn.step1.process.event.EndEvent;
import org.bpmn.step1.process.event.StartEvent;
import org.bpmn.step1.process.flow.SequenceFlow;
import org.bpmn.step1.process.gateway.ExclusiveGateway;
import org.bpmn.step1.process.gateway.Predicate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.bpmn.step1.collaboration.participant.FillFlowsParticipant.getParticipants;

public class FillFlowsProcess {

    static ArrayList<FlowsProcess> processes = new ArrayList<FlowsProcess>();

    public void fillProcesses(Document doc, Element rootElement, ObjectTypeMap objectMap) throws FileNotFoundException {

        for (int i = 0; i < processes.size(); i++) {

            String key = objectMap.getObjectIdsList().get(i);
            FlowsProcess fp = processes.get(i);
            Element process = doc.createElement("bpmn:process");

            fillProcess(doc, rootElement, process, objectMap, fp, key, i);


        }

    }

    public static ArrayList<FlowsProcess> getProcesses() {
        return processes;
    }

    public static FlowsProcess getProcessById(String id) {
        for (FlowsProcess fp : processes) {
            // System.out.println("DASHIER: " + fp.getId() + "_____UNDDASHIER: " + id);
            if (fp.getId().equals(id)) {
                return fp;
            }
        }
        return null;
    }

    public FillFlowsProcess() throws FileNotFoundException {
        setProcessList();
    }

    public Task findTaskById(Double id, ObjectTypeMap objectMap, String key, FlowsProcess f) throws FileNotFoundException {

        for (Task task : f.getTaskList()) {

            if (task.getCreatedEntityId().equals(id)) {
                return task;
            }

        }
        return null;
    }

    public AbstractObjectType findObjectById(Double id, ObjectTypeMap objectMap, String key) throws FileNotFoundException {

        return objectMap.getObjectTypeObjects().get(key).stream().filter(obj -> obj != null && obj.getCreatedEntityId() != null && obj.getCreatedEntityId().equals(id)).collect(Collectors.toList()).get(0);
    }

    public void setProcessList() throws FileNotFoundException {

        boolean firstProcess = true;
        for (FlowsParticipant tempFlowsParticipant : getParticipants()) {

            if (firstProcess == true) {
                FlowsProcess fp = new FlowsProcess(tempFlowsParticipant.getProcessRef(), true);
                processes.add(fp);
                firstProcess = false;
            } else {
                FlowsProcess fp = new FlowsProcess(tempFlowsParticipant.getProcessRef(), false);
                processes.add(fp);
            }
        }
    }

    public void fillProcess(Document doc, Element rootElement, Element process, ObjectTypeMap objectMap, FlowsProcess fp, String key, int i) throws FileNotFoundException {

        addProcessHeader(rootElement, fp, process, i);
        addStartEvent(doc, fp, process);
        addPredicates(objectMap, key, i, fp, doc, process);
        // addPredicateSteps(objectMap, key, i, fp, doc, process);
        // System.out.println(fp.getPredicateStepTypes());
        // System.out.println(fp.getPredicateList());
        addActivities(objectMap, key, i, fp, doc, process);
        addSequenceFlows(objectMap, key, i, fp, doc, process);
        addDataObjects(objectMap, key, i, fp, doc, process);
        // addEndEvent(doc, fp, process, objectMap, key);
        // addEndEventSequenceFlows(objectMap, key, fp, doc, process);
        // addDecision(doc, fp, objectMap, key, process);
        addFlowsToActivities(objectMap, key, i, fp, doc, process);
        addFlowsToEvents(objectMap, key, i, fp, doc, process);
        addFlowsToGateways(objectMap, key, i, fp, doc, process);

        for (Task task : fp.getTaskList()) {

            for (AbstractObjectType obj : task.getSteps().values()) {

                System.out.println(task.getName() + " _____ " + obj.getCreatedEntityId() + " _____ " + obj);

            }

        }

    }

    public static void addDataObjects(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) {

        String participantName = getParticipants().get(i).getName();

        for (Task task : fp.getTaskList()) {

            String dataObjName = participantName + " [" + task.getName().replaceAll(" " + participantName, "") + "]";
            task.getDataObject().setName(dataObjName);

            Element dataObjectRef = doc.createElement("bpmn:dataObjectReference");
            dataObjectRef.setAttribute("id", task.getDataObject().getRefId());
            dataObjectRef.setAttribute("name", dataObjName);
            dataObjectRef.setAttribute("dataObjectRef", task.getDataObject().getId());
            process.appendChild(dataObjectRef);

            Element dataObject = doc.createElement("bpmn:dataObject");
            dataObject.setAttribute("id", task.getDataObject().getId());
            process.appendChild(dataObject);
        }

    }

    public void addFlowsToGateways(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) {

        for (ExclusiveGateway gate : fp.getGateways()) {

            Element tempGate = doc.createElement("bpmn:exclusiveGateway");
            tempGate.setAttribute("id", gate.getId());
            process.appendChild(tempGate);

            for (SequenceFlow sf : fp.getSequenceFlowList()) {

                if (sf.getSourceRef().equals(gate.getId())) {
                    Element out = doc.createElement("bpmn:outgoing");
                    out.setTextContent(sf.getId());
                    tempGate.appendChild(out);
                }

                if (sf.getTargetRef().equals(gate.getId())) {
                    Element inc = doc.createElement("bpmn:incoming");
                    inc.setTextContent(sf.getId());
                    tempGate.appendChild(inc);
                }
            }
        }
    }

    public void addFlowsToEvents(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) {

        SequenceFlow tempStart = new SequenceFlow();

        Element endEvent = doc.createElement("bpmn:endEvent");
        endEvent.setAttribute("id", fp.getEndEvent().getId());
        process.appendChild(endEvent);

        for (SequenceFlow sf : fp.getSequenceFlowList()) {

            if (sf.getSourceRef().equals(fp.getStartEvent().getId())) {
                tempStart = sf;
            }

            if (sf.getTargetRef().equals(fp.getEndEvent().getId())) {
                Element tempEnd = doc.createElement("bpmn:incoming");
                tempEnd.setTextContent(sf.getId());
                endEvent.appendChild(tempEnd);
            }

        }

        Element startEvent = doc.createElement("bpmn:startEvent");
        startEvent.setAttribute("id", fp.getStartEvent().getId());
        process.appendChild(startEvent);

        Element flow = doc.createElement("bpmn:outgoing");
        flow.setTextContent(tempStart.getId());
        startEvent.appendChild(flow);

    }

    public void addFlowsToActivities(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) {


        for (Task task : fp.getTaskList()) {

            for (SequenceFlow sf : fp.getSequenceFlowList()) {

                if (task.getId().equals(sf.getSourceRef())) {
                    task.setOutgoing(sf);
                }

                if (task.getId().equals(sf.getTargetRef())) {
                    task.setIncoming(sf);
                }

            }

        }

        for (Task task : fp.getTaskList()) {
            Element activity;
            if (task.getSteps().size() == 0) {

                // add activities without subprocess
                activity = doc.createElement("bpmn:task");
                activity.setAttribute("id", task.getId());
                activity.setAttribute("name", task.getName());
                process.appendChild(activity);

            } else {

                // add activities as subprocess
                activity = doc.createElement("bpmn:subProcess");
                activity.setAttribute("id", task.getId());
                activity.setAttribute("name", task.getName());
                process.appendChild(activity);

            }

            // add flows
            Element inc = doc.createElement("bpmn:incoming");
            Element out = doc.createElement("bpmn:outgoing");
            inc.setTextContent(task.getIncoming().getId());
            out.setTextContent(task.getOutgoing().getId());

            activity.appendChild(inc);
            activity.appendChild(out);

            // add property
            Element prop = doc.createElement("bpmn:property");
            prop.setAttribute("id", task.getProperty());
            prop.setAttribute("name", "__targetRef_placeholder");
            activity.appendChild(prop);

            // add data input association

            if (task.getDataInputAssociation() != null) {
                //System.out.println(task + " ___ " + task.getDataInputAssociation());
                Element dataObjectRef = doc.createElement("bpmn:dataInputAssociation");
                dataObjectRef.setAttribute("id", task.getDataInputAssociation());
                Element source = doc.createElement("bpmn:sourceRef");
                Element target = doc.createElement("bpmn:targetRef");
                target.setTextContent(task.getProperty());
                source.setTextContent(task.getBefore().getDataObject().getRefId());
                dataObjectRef.appendChild(source);
                dataObjectRef.appendChild(target);
                activity.appendChild(dataObjectRef);
            }

            // add data output association
            Element dataObjectRef2 = doc.createElement("bpmn:dataOutputAssociation");
            dataObjectRef2.setAttribute("id", task.getDataOutputAssociation());
            Element target = doc.createElement("bpmn:targetRef");
            target.setTextContent(task.getDataObject().getRefId());
            dataObjectRef2.appendChild(target);
            activity.appendChild(dataObjectRef2);

        }

    }

    public void addEndEventSequenceFlows(ObjectTypeMap objectMap, String key, FlowsProcess fp, Document doc, Element process) {

        EndEvent endEventTemp = new EndEvent();
        fp.setEndEvent(endEventTemp);

        for (int i = 0; i < fp.getEndTasks().size(); i++) {

            Task task = fp.getEndTasks().get(i);

            SequenceFlow sf = new SequenceFlow();
            sf.setSourceRef(task.getId());
            sf.setTargetRef(endEventTemp.getId());

            fp.addSequenceFlow(sf);

        }

    }

    public void addEndEvent(Document doc, FlowsProcess fp, Element process, ObjectTypeMap objectMap, String key) throws FileNotFoundException {
        for (Task task : fp.taskList) {
            boolean temp = false;
            for (SequenceFlow sf : fp.getSequenceFlowList()) {
                if (task.getId().equals(sf.getSourceRef())) {
                    temp = true;

                }

            }
            if (!temp) {
                fp.getEndTasks().add(task);
            }

        }
    }

    public void addProcessHeader(Element rootElement, FlowsProcess fp, Element process, int i) {

        process.setAttribute("id", getParticipants().get(i).getProcessRef());
        process.setAttribute("isExecutable", new Boolean(fp.getIsExecutable()).toString());
        rootElement.appendChild(process);

    }

    public void addStartEvent(Document doc, FlowsProcess fp, Element process) {

        StartEvent startEventTemp = new StartEvent();
        fp.setStartEvent(startEventTemp);

    }

    public void addSteps(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {

            for (Task task : fp.getTaskList()) {
                if (obj != null) {
                    if (obj.getMethodName().equals("AddStepType")) {
                        //System.out.println("1");
                        Double temp = (Double) obj.getParameters().get(0);
                        if (task.getCreatedEntityId().equals(temp)) {
                            //System.out.println("2");
                            try {
                                // trim steps by removing default steps
                                objectMap.getObjectTypeObjects().get(key).forEach(obj2 -> {
                                    //System.out.println("3");
                                    if (obj2 != null) {
                                        //System.out.println("4");
                                        if (obj2.getMethodName().equals("UpdateStepAttributeType")) {
                                            //System.out.println("5" + obj2 + " _____ " + temp);
                                            if (obj2.getParameters().get(0).equals(obj.getCreatedEntityId())) {
                                                //System.out.println("6");
                                                //check if predicate
                                                try {
                                                    if (!stepIsPredicate(objectMap, key, (Double) obj2.getParameters().get(1), fp, doc, process)) {
                                                        task.getSteps().put(obj.getCreatedEntityId(), obj);
                                                    }
                                                } catch (FileNotFoundException e) {
                                                    throw new RuntimeException(e);
                                                }


                                            }
                                        }
                                    }
                                });
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean stepIsPredicate(ObjectTypeMap objectMap, String key, Double id, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {
            //System.out.println("3");
            if (obj != null) {
                //System.out.println("4");
                if (obj.getMethodName().equals("UpdatePredicateStepTypeExpression")) {
                    LinkedTreeMap link = (LinkedTreeMap) obj.getParameters().get(1);
                    LinkedTreeMap innerLink = (LinkedTreeMap) link.get("Left");
                    if (innerLink.get("AttributeTypeId").equals(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void addActivities(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        // add activities
        String participantName = getParticipants().get(i).getName();

        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            if (obj != null) {
                if (obj.getMethodName().equals("UpdateStateType")) {
                    Task task = new Task();
                    String activityName = obj.getParameters().get(1) + " " + participantName;

                    task.setCreatedEntityId((Double) obj.getParameters().get(0));
                    task.setName(activityName);

                    // Fixes New State and double Edit/Submit/etc. problem
                    // System.out.println(obj + "  EARLY: " + fp.getTaskList());
                    if (fp.containsTask(task)) {
                        // System.out.println("ASDIASDASDIASDIASDASIASDASD  " + task);
                        // fp.removeTaskFromList(task);
                        int cnt = 0;
                        for (Task teemo : fp.getTaskList()) {
                            if (teemo.getCreatedEntityId().equals(task.getCreatedEntityId())) {
                                // System.out.println("MARKO");
                                DataObject dObj = new DataObject();
                                fp.getDataObjects().put(dObj.getRefId(), dObj);
                                dObj.setAssociatedTask(task);
                                teemo.setDataObject(dObj);
                                teemo.setName(activityName);
                                if (cnt > 0) {
                                    teemo.setDataInputAssociation();
                                    cnt++;
                                }
                                // task.setDataInputAssociation();
                                teemo.setDataOutputAssociation();
                                teemo.setProperty();
                            }
                        }
                    } else {

                        DataObject dObj = new DataObject();
                        fp.getDataObjects().put(dObj.getRefId(), dObj);
                        dObj.setAssociatedTask(task);
                        task.setDataObject(dObj);
                        if (fp.getTaskList().size() > 0) {
                            task.setDataInputAssociation();
                        }
                        // task.setDataInputAssociation();
                        task.setDataOutputAssociation();
                        task.setProperty();

                        fp.addTask(task);
                    }

                }
            }
        });
        addSteps(objectMap, key, i, fp, doc, process);
    }

    public void addPredicates(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        // add predicates
        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            if (obj != null) {
                if (obj.getMethodName().equals("AddPredicateStepType")) {

                    Predicate p = null;
                    try {
                        p = findPredicate(obj.getCreatedEntityId(), objectMap, key, i, fp, doc, process);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    p.setCreatedEntityId(obj.getCreatedEntityId());
                    fp.addPredicate(p);
                }
            }
        });
        System.out.println("PREDICATELIST: " + fp.getPredicateList());
    }

    public Predicate findPredicate(Double id, ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {
            //System.out.println("3");
            if (obj != null) {
                //System.out.println("4");
                if (obj.getMethodName().equals("UpdatePredicateStepTypeExpression")) {
                    Double stepId = (Double) obj.getParameters().get(0);
                    LinkedTreeMap link = (LinkedTreeMap) obj.getParameters().get(1);
                    LinkedTreeMap innerLeft = (LinkedTreeMap) link.get("Left");
                    LinkedTreeMap innerRight = (LinkedTreeMap) link.get("Right");
                    if (stepId.equals(id)) {

                        String att = findAttributeOfPredicate((Double) innerLeft.get("AttributeTypeId"), objectMap, key);


                        Predicate p = new Predicate();
                        p.setCondition(att + " " + innerRight.get("Value"));
                        return p;
                    }
                }
            }
        }
        return null;
    }

    public String findAttributeOfPredicate(Double id, ObjectTypeMap objectMap, String key) throws FileNotFoundException {

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {
            //System.out.println("3");
            if (obj != null) {

                Pattern p = Pattern.compile("^Update.*AttributeType$");
                Matcher m = p.matcher(obj.getMethodName());

                if (m.find() && obj.getParameters().get(0).equals(id)) {
                    return (String) obj.getParameters().get(1);
                }
            }

        }
        return null;
    }


    /*
    public void addPredicateSteps(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element process) throws FileNotFoundException {

        // add predicate steptypes
        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            if (obj != null) {
                if (obj.getMethodName().equals("AddStepType")) {

                    for (Predicate p : fp.getPredicateList()) {

                        if (p.getCreatedEntityId().equals(obj.getCreatedEntityId())) {
                            fp.addPredicateStepType(obj);
                        }

                    }
                }
            }
        });

        System.out.println(fp.getPredicateStepTypes());
    }

     */


    public void addSequenceFlows(ObjectTypeMap objectMap, String key, int i, FlowsProcess fp, Document doc, Element
            process) throws FileNotFoundException {

        // add SequenceFlows
        SequenceFlow startFlow = new SequenceFlow();
        startFlow.setSourceRef(fp.getStartEvent().getId());
        startFlow.setTargetRef(fp.getTaskList().get(0).getId());
        fp.addSequenceFlow(startFlow);

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {

            if (obj != null && obj.getMethodName().equals("AddTransitionType")) {
                Double source = (Double) obj.getParameters().get(0);
                Double target = (Double) obj.getParameters().get(1);

                // check whether source and/or target are predicates
                // in this case, change parameter of transition to his respective step

                AbstractObjectType sourceTemp = getPredicate(source, objectMap, key);
                AbstractObjectType targetTemp = getPredicate(target, objectMap, key);

                if (sourceTemp != null && sourceTemp.getMethodName().equals("AddPredicateStepType")) {
                    source = (Double) getPredicate(source, objectMap, key).getParameters().get(0);
                }

                if (targetTemp != null && targetTemp.getMethodName().equals("AddPredicateStepType")) {
                    target = (Double) getPredicate(target, objectMap, key).getParameters().get(0);
                }

                try {
                    Double sourceObjectId = (Double) findObjectById(source, objectMap, key).getParameters().get(0);
                    Double targetObjectId = (Double) findObjectById(target, objectMap, key).getParameters().get(0);
                    if (!sourceObjectId.equals(targetObjectId)) {
                        SequenceFlow sf = new SequenceFlow();
                        Task task1 = findTaskById(sourceObjectId, objectMap, key, fp);
                        Task task2 = findTaskById(targetObjectId, objectMap, key, fp);

                        //System.out.println(source + " ___ " + task1 + " ___ " + target + " ___ " + task2);

                        if (task1 != null && task2 != null) {
                            sf.setSourceRef(task1.getId());
                            sf.setTargetRef(task2.getId());
                            if (fp.containsFlow(sf)) {
                                fp.removeFlowFromList(sf);
                            }
                            fp.addSequenceFlow(sf);
                        }
                    }

                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        addEndEvent(doc, fp, process, objectMap, key);
        addEndEventSequenceFlows(objectMap, key, fp, doc, process);

        addLoop(doc, fp, objectMap, key, process);
        addDecision(doc, fp, objectMap, key, process);
        combineArtifcats(doc, fp, objectMap, key, process);

        int decisionFlowsCnt = 0;
        for (SequenceFlow sequenceFlow : fp.getSequenceFlowList()) {

            Element flow = doc.createElement("bpmn:sequenceFlow");
            flow.setAttribute("id", sequenceFlow.getId());
            flow.setAttribute("sourceRef", sequenceFlow.getSourceRef());
            flow.setAttribute("targetRef", sequenceFlow.getTargetRef());

            if (fp.getDecisionFlows().containsKey(sequenceFlow.getId())) {
                sequenceFlow.setName(fp.getPredicateList().get(decisionFlowsCnt).getCondition());
                decisionFlowsCnt++;
                flow.setAttribute("name", sequenceFlow.getName());
            }

            process.appendChild(flow);
        }

        // System.out.println(fp.getTaskList());

        for (int k = 0; k < fp.getTaskList().size(); k++) {

            Task task = fp.getTaskList().get(k);

            if (k == 0) {
                task.setBeforeEvent(fp.getStartEvent());
                task.setAfter(fp.getTaskList().get(k + 1));
                //System.out.println("1: " + task.getBeforeEvent() + " --> " + task + " --> " + task.getAfter());
            } else if (k == fp.getTaskList().size() - 1) {
                task.setAfterEvent(fp.getEndEvent());
                task.setBefore(fp.getTaskList().get(k - 1));
                //System.out.println("2: " + task.getBefore() + " --> " + task + " --> " + task.getAfterEvent());
            } else {
                if (fp.getDecisionTasks().containsKey(task.getId())) {
                    task.setBefore(fp.getTaskList().get(k - 1));
                    int indexAfter = k + fp.getDecisionTasks().get(task.getId()).size() + 1;

                    for (String id : fp.getDecisionTasks().get(task.getId())) {
                        for (int m = 0; m < fp.getTaskList().size(); m++) {
                            Task tempTask = fp.getTaskList().get(m);
                            if (tempTask.getId().equals(id)) {
                                tempTask.setBefore(task);
                                if (indexAfter < fp.getTaskList().size()) {
                                    tempTask.setAfter(fp.getTaskList().get(indexAfter));
                                    //System.out.println("4: " + tempTask.getBefore() + " --> " + tempTask + " --> " + tempTask.getAfter());

                                } else {
                                    tempTask.setAfterEvent(fp.getEndEvent());
                                    //System.out.println("4: " + tempTask.getBefore() + " --> " + tempTask + " --> " + tempTask.getAfterEvent());
                                }
                            }
                        }
                    }
                    k += fp.getDecisionTasks().size() + 1;
                } else {
                    task.setBefore(fp.getTaskList().get(k - 1));
                    task.setAfter(fp.getTaskList().get(k + 1));
                    //System.out.println("3: " + task.getBefore() + " --> " + task + " --> " + task.getAfter());
                }
            }
        }

    }

    public Task getTaskById(FlowsProcess fp, String id) {
        for (Task task : fp.getTaskList()) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    private AbstractObjectType getPredicate(Double source, ObjectTypeMap objectMap, String key) throws
            FileNotFoundException {

        for (AbstractObjectType obj : objectMap.getObjectTypeObjects().get(key)) {

            if (obj != null && obj.getCreatedEntityId() != null && obj.getCreatedEntityId().equals(source)) {
                if (obj.getMethodName().equals("AddPredicateStepType")) {
                    return obj;
                }
            }

        }

        return null;
    }

    public void addLoop(Document doc, FlowsProcess fp, ObjectTypeMap objectMap, String key, Element process) throws
            FileNotFoundException {

        // gateways in case of loop
        objectMap.getObjectTypeObjects().get(key).forEach(obj -> {
            if (obj != null && obj.getMethodName().equals("AddBackwardsTransitionType")) {
                Double source = (Double) obj.getParameters().get(1);
                Double target = (Double) obj.getParameters().get(0);

                try {
                    Double sourceObjectId = findObjectById(source, objectMap, key).getCreatedEntityId();
                    Double targetObjectId = findObjectById(target, objectMap, key).getCreatedEntityId();

                    SequenceFlow sf = new SequenceFlow();
                    Task sourceTask = findTaskById(sourceObjectId, objectMap, key, fp);
                    Task targetTask = findTaskById(targetObjectId, objectMap, key, fp);

                    SequenceFlow flowBeforeStart = fp.getFlowBySource(sourceTask);
                    SequenceFlow flowAfterEnd = fp.getFlowByTarget(targetTask);

                    ExclusiveGateway startGate = new ExclusiveGateway();
                    ExclusiveGateway endGate = new ExclusiveGateway();

                    fp.getGateways().add(startGate);
                    fp.getGateways().add(endGate);

                    SequenceFlow sf1 = new SequenceFlow();
                    sf1.setSourceRef(flowBeforeStart.getSourceRef());
                    sf1.setTargetRef(startGate.getId());
                    flowBeforeStart.setSourceRef(startGate.getId());

                    SequenceFlow sf2 = new SequenceFlow();
                    sf2.setSourceRef(endGate.getId());
                    sf2.setTargetRef(flowAfterEnd.getTargetRef());
                    flowAfterEnd.setTargetRef(endGate.getId());

                    SequenceFlow sf3 = new SequenceFlow();
                    sf3.setSourceRef(endGate.getId());
                    sf3.setTargetRef(startGate.getId());

                    fp.addSequenceFlow(sf1);
                    fp.addSequenceFlow(sf2);
                    fp.addSequenceFlow(sf3);


                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }

        });

    }

    public void addDecision(Document doc, FlowsProcess fp, ObjectTypeMap objectMap, String key, Element process) throws
            FileNotFoundException {

        Pattern pattern = Pattern.compile("Activity_*");
        LinkedHashSet<SequenceFlow> temp = new LinkedHashSet<>();


        for (int i = 0; i < fp.getSequenceFlowList().size() - 1; i++) {

            Matcher matcher = pattern.matcher(fp.getSequenceFlowList().get(i).getSourceRef());
            String outerSourceRef = fp.getSequenceFlowList().get(i).getSourceRef();
            boolean duplicate = false;

            ArrayList<SequenceFlow> flows = new ArrayList<>();
            flows.add(fp.getSequenceFlowList().get(i));

            if (matcher.find()) {

                for (int j = i + 1; j < fp.getSequenceFlowList().size(); j++) {

                    Matcher datcher = pattern.matcher(fp.getSequenceFlowList().get(j).getSourceRef());
                    String innerSourceRef = fp.getSequenceFlowList().get(j).getSourceRef();

                    if (datcher.find() && outerSourceRef.equals(innerSourceRef)) {

                        duplicate = true;
                        flows.add(fp.getSequenceFlowList().get(j));
                        fp.removeSequenzeFlow(fp.getSequenceFlowList().get(j));

                    }
                }

                if (duplicate) {
                    openDecisionFlows(flows, doc, fp, objectMap, key, process);
                    fp.removeSequenzeFlow(fp.getSequenceFlowList().get(i));
                }

            }

        }

    }

    public void openDecisionFlows(ArrayList<SequenceFlow> flows, Document doc, FlowsProcess fp, ObjectTypeMap
            objectMap, String key, Element process) {

        ExclusiveGateway gate = new ExclusiveGateway();
        fp.getGateways().add(gate);
        ArrayList<String> tempTasks = new ArrayList<>();

        SequenceFlow toGateway = new SequenceFlow();
        toGateway.setSourceRef(flows.get(0).getSourceRef());
        toGateway.setTargetRef(gate.getId());
        fp.addSequenceFlow(toGateway);

        // System.out.println(toGateway);


        for (int i = 0; i < flows.size(); i++) {

            SequenceFlow fromGateway = new SequenceFlow();
            fromGateway.setSourceRef(gate.getId());
            fromGateway.setTargetRef(flows.get(i).getTargetRef());
            // System.out.println(fromGateway);
            fp.addSequenceFlow(fromGateway);
            fp.getGateways().add(gate);
            tempTasks.add(fromGateway.getTargetRef());
            fp.getDecisionFlows().put(fromGateway.getId(), fromGateway);
        }

        fp.getDecisionTasks().put(toGateway.getSourceRef(), tempTasks);
        System.out.println("DECISION FLOWS: " + fp.getDecisionFlows());


    }

    public void combineArtifcats(Document doc, FlowsProcess fp, ObjectTypeMap objectMap, String key, Element
            process) {

        HashSet<SequenceFlow> temp = new HashSet<>();
        Pattern pattern = Pattern.compile("Gateway_*");

        for (int i = 0; i < fp.getSequenceFlowList().size() - 1; i++) {
            Matcher matcher = pattern.matcher(fp.getSequenceFlowList().get(i).getTargetRef());
            boolean duplicate = false;
            if (!matcher.find()) {

                SequenceFlow outerFlow = fp.getSequenceFlowList().get(i);
                ExclusiveGateway gate = new ExclusiveGateway();

                for (int j = i + 1; j < fp.getSequenceFlowList().size(); j++) {
                    Matcher datcher = pattern.matcher(fp.getSequenceFlowList().get(j).getTargetRef());
                    if (!datcher.find()) {
                        SequenceFlow innerFlow = fp.getSequenceFlowList().get(j);

                        if (outerFlow.getTargetRef().equals(innerFlow.getTargetRef()) && !temp.contains(outerFlow)) {

                            duplicate = true;
                            temp.add(outerFlow);
                            innerFlow.setTargetRef(gate.getId());
                            fp.getGateways().add(gate);

                        }

                    }

                    if (duplicate) {
                        SequenceFlow sf = new SequenceFlow();
                        sf.setSourceRef(gate.getId());
                        sf.setTargetRef(outerFlow.getTargetRef());
                        fp.addSequenceFlow(sf);
                        outerFlow.setTargetRef(gate.getId());
                        duplicate = false;
                    }
                }
            }
        }
    }
}