package se306.algorithm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import se306.input.InputFileReader;

import java.util.*;
import java.sql.SQLOutput;

public class Processor {

    private HashMap<Integer,Integer> scheduledNodes;
    private HashMap<Integer,Integer> orderOfNodes;
    private int processorEndTime;
    private int id;

    public Processor(int pid) {
        this.id = pid;
        this.scheduledNodes = new HashMap<>();
        this.orderOfNodes = new HashMap<>();
        this.processorEndTime = 0;
    }

    /**
     * Copy constructor
     */
    public Processor(Processor processor) {
        this.scheduledNodes = new HashMap<>(processor.scheduledNodes);
        this.orderOfNodes = new HashMap<>(processor.orderOfNodes);
        this.id = processor.id;
        this.processorEndTime = processor.processorEndTime;
    }

    public int getProcessorID() {
        return id;
    }

    /**
     * Method returns the finishing time of the current process
     * 
     * @return finishing time
     */
    public int getCurrentCost() {
//        if (scheduledNodes.size() == 0 || startTimes.size() == 0) {
//            return 0;
//        }
////        System.out.println("START : " + startTimes.get(startTimes.size() - 1));
////        System.out.println("WEIGHT: " + InputFileReader.nodeWeights.get(scheduledNodes.get(scheduledNodes.size() - 1)));
//        return startTimes.get(startTimes.size() - 1)
//                + InputFileReader.nodeWeights.get(scheduledNodes.get(scheduledNodes.size() - 1)); // AUTOBOXING?
        return processorEndTime;
    }

    /**
     * This method handles addition of a new node to the current process as well as
     * calculates the starting time of the node
     * 
     * @param node
     * @param schedule
     * @param processorNumber
     */
    public void addNode(int node, PartialSchedule schedule, int processorNumber) {
        // Calculates time using the schedule the node needs to be added to and adds it
        // into the appropriate processor
        int order = scheduledNodes.size();
        scheduledNodes.put(node,schedule.calculateStartTime(node, processorNumber));
        processorEndTime = scheduledNodes.get(node) + InputFileReader.nodeWeights.get(node);
        orderOfNodes.put(order,node);

    }


    /**
     * Returns set of nodes that have been scheduled
     * 
     * @return
     */
    public Set<Integer> getScheduledNodes() {
        return scheduledNodes.keySet();
    }

    /**
     * Returns list of start times where index of the list corresponds to the index
     * of the scheduled nodes list
     * 
     * @return
     */
    public HashMap<Integer, Integer> getStartTimes() {
        return scheduledNodes;
    }

    /**
     * The equal method takes an Object obj as an argument and checks if obj is
     * equivalent to this Processor. This equivalence comparison is done by
     * comparing the scheduled nodes' starting and finishing times and the current
     * cost.
     *
     * @param obj - the object to compare with this Processor object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        
        Processor secondProcessor = (Processor) obj;
        return new EqualsBuilder()
//                 .appendSuper(super.equals(obj))
                .append(scheduledNodes, secondProcessor.scheduledNodes)
                .isEquals() && checkCurrentCost(secondProcessor.getCurrentCost());
    }


    /**
     * This method calculates the idle time. If it has more than one node scheduled, any idle times are totalled
     * and returned. If only one node is inside the processor, the start time is returned as the idle time.
     * This includes the node to be scheduled.
     * @return
     */
    public double calculateIdleTime(){
        double idleTime = 0;

        // If only one node is scheduled, get start time of the node
        if (this.scheduledNodes.size() == 1) {
            idleTime = this.scheduledNodes.get(orderOfNodes.get(0));

        } else {

            // Go through each node that is in the processor
            for (Integer i : orderOfNodes.keySet()) {
                if(i == 0){
                    continue;
                }
                int startOfCurrentNode = this.getStartTimes().get(orderOfNodes.get(i));
                int weightOfLastNode = InputFileReader.nodeWeights.get(orderOfNodes.get(i-1));
                int startOfLastNode = this.getStartTimes().get(orderOfNodes.get(i-1));

                // Calculate any idle times and add it to the total idle time
                if ((startOfCurrentNode) != (startOfLastNode + weightOfLastNode)) {
                    idleTime = idleTime + (double)((startOfCurrentNode) - (startOfLastNode + weightOfLastNode));
                }
            }
        }
        return idleTime;
    }

    /**
     * hashCode() must be overridden whenever equals() is overridden
     **/
    @Override
    public int hashCode() {
        // Hash table prime numbers from https://planetmath.org/goodhashtableprimes
        return new HashCodeBuilder(805306457, 402653189)
                .append(scheduledNodes)
                .append(getCurrentCost()).toHashCode();
    }

    private boolean checkCurrentCost(int currentCost) {
        return (this.getCurrentCost() == currentCost) ? true : false;
    }
}
