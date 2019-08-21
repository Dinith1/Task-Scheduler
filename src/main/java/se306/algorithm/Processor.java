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

    Processor(Processor processor){
        this.scheduledNodes = new ArrayList<>(processor.scheduledNodes);
        this.startTimes = new ArrayList<>(processor.startTimes);
        this.id = processor.id;
    }
    public int getProcessorID(){
        return id;
    }
    //Gets the ending time of the current process
    public int getCurrentCost(){
        if(scheduledNodes.size() ==0 || startTimes.size() == 0){
            return 0;
        }
        return startTimes.get(startTimes.size()-1)+scheduledNodes.get(scheduledNodes.size()-1).getNodeWeight();
    }

    public void addNode(Node node,PartialSchedule schedule ,int processorNumber){
        //adds node into the list
        startTimes.add(schedule.calculateStartTime(node,processorNumber));
        scheduledNodes.add(node);
    }

    public List<Node> getScheduledNodes(){
        return scheduledNodes;
    }
    public List<Integer> getStartTimes(){
        return startTimes;
    }

}
