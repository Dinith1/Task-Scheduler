package se306.algorithm;

import se306.input.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Scheduling {

    // User defined available processors placed in a list
    private List<se306.algorithm.Processor> processorList = new ArrayList<>();

    private void createProcessors(int numberOfProcessors){
        for(int i = 0; i < numberOfProcessors;i++){
            String processorIdentifier = Integer.toString(i);
            processorList.add(new se306.algorithm.Processor(processorIdentifier));
        }
    }
    /**
     * Takes a integer and a List of Nodes and adds them into the appropriate processors (greedy "Earliest
     * processor finishing time)
     * @param numberOfProcessors - user defined number of processors that are available to be used
     * @param listOfSortedNodes - the list of nodes that are in sorted order from InputReader class
     */
    public void createSchedule(int numberOfProcessors, List<Node> listOfSortedNodes){

        // Creates the number of processes specified by the user
        createProcessors(numberOfProcessors);

        /**
         * Comparator which sorts all the processors according to earliest finishing time and if equal then sorts
         * by process identifier number
         */
        Comparator<Processor> sortByEarliestFinishTime = new Comparator<Processor>() {
            public int compare(Processor p1, Processor p2) {
                if (p1.getCurrentCost() < p2.getCurrentCost()) {
                    return -1;
                }
                else if(p1.getCurrentCost() == p2.getCurrentCost()){
                    return sortByIdentifierNumber.compare(p1,p2);
                }
                else {
                    return 1;
                }

            }
        };
        // Loops through all the nodes available
        for(Node node: listOfSortedNodes){
            // Processor with earliest finish time will always be at index 0
            Collections.sort(processorList,sortByEarliestFinishTime);
            processorList.get(0).addToSchedule(node);
        }
    }

    /**
     * Returns list of Processor objects that have the nodes scheduled in order of the processor identifier number
     */
    public List<se306.algorithm.Processor> getProcessorList(){
        Collections.sort(processorList, sortByIdentifierNumber);
        return processorList;
    }

    /**
     * Comparator to be used with resorting the processor list back into the process identifier number order
     */
    private Comparator<se306.algorithm.Processor> sortByIdentifierNumber = new Comparator<se306.algorithm.Processor>() {
        public int compare(Processor p1, Processor p2) {
            if (Integer.parseInt(p1.getProcessorIdentifier()) < Integer.parseInt(p2.getProcessorIdentifier())) {
                return -1;
            }
            else {
                return 1;
            }

        }
    };
}
