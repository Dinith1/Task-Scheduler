package se306.algorithm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import se306.input.InputFileReader;

import java.util.ArrayList;
import java.util.List;

public class Processor {

    private List<Integer> scheduledNodes = new ArrayList<Integer>();
    private List<Integer> startTimes = new ArrayList<Integer>();
    private int[] scheduleStartTime = new int[InputFileReader.NUM_NODES];
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

        // If the processor doesn't have anything scheduled in it
        if (scheduledNodes.size() == 0 || startTimes.size() == 0) {
            return 0;
        }

//        System.out.println("START : " + startTimes.get(startTimes.size() - 1));
//        System.out.println("WEIGHT: " + InputFileReader.nodeWeights.get(scheduledNodes.get(scheduledNodes.size() - 1)));

        // Get the start time of last node in list + weight
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
        System.out.println("Start time is : " + schedule.calculateStartTime(node, processorNumber) + " for node " + node
        + " PS: " + schedule + " and on Processor " + processorNumber);
        startTimes.add(schedule.calculateStartTime(node, processorNumber));
//        scheduleStartTime.add()
        scheduledNodes.add(node);

    }

    public int[] getScheduleStartTimes() {
        return this.scheduleStartTime;
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
                // .appendSuper(super.equals(obj))
                .append(scheduledNodes, secondProcessor.scheduledNodes).append(startTimes, secondProcessor.startTimes)
                .isEquals() && checkCurrentCost(secondProcessor.getCurrentCost());
    }

    public double calculateIdleTime(){
        double idleTime = 0;


        // @starttime is the time it is scheduled on the current processor

        // this only checks if there is more than one node scheduled. what if there is one node scheduled but still
        // has idle time?

        // THIS INCLUDES THE NODE TO BE SCHEDULED (trial)

        if (this.scheduledNodes.size() == 1) {
            idleTime = this.startTimes.get(0);
        } else {
            for (int i = 1; i < this.scheduledNodes.size(); i++) {
//            int finishingTime = InputFileReader.nodeWeights.get(this.scheduledNodes.get(i)) + this.startTimes.get(i);

                int processorID = this.getProcessorID();

                int startOfCurrentNode = this.startTimes.get(i);
                int weightOfCurrentNode = InputFileReader.nodeWeights.get(scheduledNodes.get(i));
                int weightOfLastNode = InputFileReader.nodeWeights.get(scheduledNodes.get(i - 1));
                int startOfLastNode = this.startTimes.get(i - 1);


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
        return new HashCodeBuilder(805306457, 402653189).append(scheduledNodes).append(startTimes)
                .append(getCurrentCost()).toHashCode();
    }

    private boolean checkCurrentCost(int currentCost) {
        return (this.getCurrentCost() == currentCost) ? true : false;
    }
}
