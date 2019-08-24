package se306.algorithm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import se306.input.InputFileReader;

import java.util.ArrayList;
import java.util.List;

public class Processor {

    private List<Integer> scheduledNodes;
    private List<Integer> startTimes;
    private int endTime;
    private int id;

    public Processor(int pid) {
        this.id = pid;
        this.scheduledNodes = new ArrayList<>();
        this.startTimes = new ArrayList<>();
    }

    /**
     * Copy constructor
     */
    public Processor(Processor processor) {
        this.scheduledNodes = new ArrayList<>(processor.scheduledNodes);
        this.startTimes = new ArrayList<>(processor.startTimes);
        this.id = processor.id;
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
        if (scheduledNodes.size() == 0 || startTimes.size() == 0) {
            return 0;
        }

//        System.out.println("START : " + startTimes.get(startTimes.size() - 1));
//        System.out.println("WEIGHT: " + InputFileReader.nodeWeights.get(scheduledNodes.get(scheduledNodes.size() - 1)));
        return startTimes.get(startTimes.size() - 1)
                + InputFileReader.nodeWeights.get(scheduledNodes.get(scheduledNodes.size() - 1)); // AUTOBOXING?
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
        startTimes.add(schedule.calculateStartTime(node, processorNumber));
        scheduledNodes.add(node);
    }

    /**
     * Returns list of nodes that have been scheduled
     * 
     * @return
     */
    public List<Integer> getScheduledNodes() {
        return scheduledNodes;
    }

    /**
     * Returns list of start times where index of the list corresponds to the index
     * of the scheduled nodes list
     * 
     * @return
     */
    public List<Integer> getStartTimes() {
        return startTimes;
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
                .append(scheduledNodes, secondProcessor.scheduledNodes).append(startTimes, secondProcessor.startTimes)
                .isEquals() && checkCurrentCost(secondProcessor.getCurrentCost());
    }

    /**
     * hashCode() must be overridden whenever equals() is overridden
     **/
    @Override
    public int hashCode() {
        // Hash table prime numbers from https://planetmath.org/goodhashtableprimes
        return new HashCodeBuilder(805306457, 402653189).append(scheduledNodes).append(startTimes)
                .append(getCurrentCost()).toHashCode();
    }

    private boolean checkCurrentCost(int currentCost) {
        return (this.getCurrentCost() == currentCost) ? true : false;
    }
}
