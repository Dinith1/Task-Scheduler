package se306.Algorithm;

import se306.Input.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduling {

    // User defined available processors placed in a list
    public List<Processor> processorList = new ArrayList<>();

    private void createProcessors(int numberOfProcessors){
        for(int i = 0; i <= numberOfProcessors;i++){
            processorList.add(new Processor());
        }
    }
    /**
     * Takes a integer and a List of Nodes and adds them into the appropriate processors (greedy "Earliest
     * processor finishing time)
     * @param numberOfProcessors - user defined number of processors that are available to be used
     * @param listOfSortedNodes - the list of nodes that are in sorted order from InputReader class
     */
    public void createSchedule(int numberOfProcessors, List<Node> listOfSortedNodes){
        createProcessors(numberOfProcessors);

        // Comparator which sorts all the processors according to earliest finishing time
        Comparator<Processor> processorComparator = new Comparator<Processor>() {
            public int compare(Processor p1, Processor p2) {
                if (p1.getCurrentCost() < p2.getCurrentCost()) {
                    return -1;
                }
                else if(p1.getCurrentCost() == p2.getCurrentCost()){
                    return 0;
                }
                else {
                    return 1;
                }

            }
        };
        // Loops through all the nodes available
        for(Node node: listOfSortedNodes){
            // Processor with earliest finish time will always be at index 0
            Collections.sort(processorList,processorComparator);
            processorList.get(0).addToSchedule(node);
        }
    }

    /**
     * Returns list of Processor objects that have the nodes scheduled
     */
    public List<Processor> getProcessorList(){
        return processorList;
    }
}
