package se306.algorithm;

import se306.input.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Schedule {

    // User defined available processors placed in a list
    private List<Processor> processorList = new ArrayList<>();

    /**
     * Comparator to be used with resorting the processor list back into the process
     * identifier number order
     */
    private Comparator<Processor> sortByIdentifierNumber = new Comparator<se306.algorithm.Processor>() {
        public int compare(Processor p1, Processor p2) {
            if (Integer.parseInt(p1.getProcessorIdentifier()) < Integer.parseInt(p2.getProcessorIdentifier())) {
                return -1;
            }

            return 1;
        }
    };

    /**
     * Takes a integer and a List of Nodes and adds them into the appropriate
     * processors (greedy "Earliest processor finishing time)
     * 
     * @param numProcessors  - User defined number of processors that are available
     *                       to be used
     * @param sortedNodeList - The list of nodes that are in sorted order from
     *                       InputReader class
     */
    public void createSchedule(int numProcessors, List<Node> sortedNodeList) {

        // Create the number of processes specified by the user
        createProcessors(numProcessors);

        /**
         * Comparator which sorts all the processors according to earliest finishing
         * time. If equal then sorts by process identifier number.
         */
        Comparator<Processor> sortByEarliestFinishTime = new Comparator<Processor>() {
            public int compare(Processor p1, Processor p2) {
                if (p1.getCurrentCost() < p2.getCurrentCost()) {
                    return -1;
                }
                
                if (p1.getCurrentCost() == p2.getCurrentCost()) {
                    return sortByIdentifierNumber.compare(p1, p2);
                }

                return 1;
            }
        };

        // Loop through all the available nodes
        for (Node node : sortedNodeList) {
            // Processor with earliest finish time will always be at index 0
            Collections.sort(processorList, sortByEarliestFinishTime);
            processorList.get(0).addToSchedule(node);
        }
    }

    /**
     * Add processors to the program
     * 
     * @param numProcessors - Number of processors to add
     */
    private void createProcessors(int numProcessors) {
        for (int i = 0; i < numProcessors; i++) {
            String processorIdentifier = Integer.toString(i);
            processorList.add(new se306.algorithm.Processor(processorIdentifier));
        }
    }

    /**
     * Returns list of Processor objects that have the nodes scheduled in order of
     * the processor identifier number
     */
    public List<Processor> getProcessorList() {
        Collections.sort(processorList, sortByIdentifierNumber);
        return processorList;
    }

}
