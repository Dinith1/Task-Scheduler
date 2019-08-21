package se306.algorithm;

import se306.input.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Processor {

    private List<Node> scheduledNodes = new ArrayList<>();
    private List<Integer> startTimes = new ArrayList<>();
    private int id;

    Processor(int pid){
        this.id = pid;
    }
    //Copy constructor
    Processor(Processor processor){
        this.scheduledNodes = new ArrayList<>(processor.scheduledNodes);
        this.startTimes = new ArrayList<>(processor.startTimes);
        this.id = processor.id;
    }
    public int getProcessorID(){
        return id;
    }

    /**
     * Method returns the finishing time of the current process
     * @return finishing time
     */
    public int getCurrentCost(){
        if(scheduledNodes.size() ==0 || startTimes.size() == 0){
            return 0;
        }
        return startTimes.get(startTimes.size()-1)+scheduledNodes.get(scheduledNodes.size()-1).getNodeWeight();
    }

    /**
     * This method handles addition of a new node to the current process as well as calculates the starting time of the
     * node
     * @param node
     * @param schedule
     * @param processorNumber
     */
    public void addNode(Node node,PartialSchedule schedule ,int processorNumber){
        //Calculates time using the schedule the node needs to be added to and adds it into the appropriate processor
        startTimes.add(schedule.calculateStartTime(node,processorNumber));
        scheduledNodes.add(node);
    }

    /**
     * Returns list of nodes that have been scheduled
     * @return
     */
    public List<Node> getScheduledNodes(){
        return scheduledNodes;
    }

    /**
     * Returns lsit of start times where index of the list corresponds to the index of the scheduled nodes list
     * @return
     */
    public List<Integer> getStartTimes(){
        return startTimes;
    }


    /**
     * The equal method takes an Object o as an argument and checks if o is equivalent to this Processor. This equivalence
     * comparison is done by comparing the scheduled nodes' starting and finishing times and the current cost.
     *
     * @param o - the object to compare with this Processor object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }

        Processor secondProcessor = (Processor) o;

        // Check if the current cost of the second processor is the same
        if (currentCost != secondProcessor.getCurrentCost()) {
            return false;
        }

        // Check if all scheduled nodes in second processor have the same start time
        for (Map.Entry<Node, Integer> entry : secondProcessor.getStartTimes().entrySet()) {
            Node secondNode = entry.getKey();
            Integer secondNodeStartTime = entry.getValue();
            if (!startTimes.containsKey(secondNode)) {
                return false;
            }
            if (!startTimes.get(secondNode).equals(secondNodeStartTime)) {
                return false;
            }
        }

        // Check if all scheduled nodes in second processor have the same finishing time
        for (Map.Entry<Node, Integer> entry : secondProcessor.getSchedule().entrySet()) {
            Node secondNode = entry.getKey();
            Integer secondNodeFinishTime = entry.getValue();
            if (!scheduledNodes.containsKey(secondNode)) {
                return false;
            }
            if (!scheduledNodes.get(secondNode).equals(secondNodeFinishTime)) {
                return false;
            }
        }
        return true;
    }
}
