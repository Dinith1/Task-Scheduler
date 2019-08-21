package se306.algorithm;

import se306.input.Node;

import java.util.ArrayList;
import java.util.List;

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
        if(scheduledNodes.size() == 0 || startTimes.size() == 0){
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

}
