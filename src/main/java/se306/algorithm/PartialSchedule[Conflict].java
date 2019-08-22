package se306.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se306.input.InputFileReader;
import se306.algorithm.Processor;

public class PartialSchedule {

    // User defined available processors placed in a list
    private ArrayList<Processor> processorList = new ArrayList<>();
    private int costFunction;

    public PartialSchedule(int processorNumber) {
        createProcessors(processorNumber);
    }

    public PartialSchedule(PartialSchedule ps) {
        for (Processor p : ps.getProcessorList()) {
            this.processorList.add(new Processor(p));
        }

        this.costFunction = ps.costFunction;
    }

    /**
     * Comparator to be used with resorting the processor list back into the process
     * identifier number order
     */
    private Comparator<Processor> sortByIdentifierNumber = new Comparator<Processor>() {
        public int compare(Processor p1, Processor p2) {
            if (p1.getProcessorID() < p2.getProcessorID()) {
                return -1;
            }

            return 1;
        }
    };

    /**
     * This method finds the nodes that already have been scheduled in this schedule
     * 
     * @return scheduledNodes
     */
    public Set<Integer> getUsedNodes() {
        Set<Integer> scheduledNodes = new HashSet<>();

        for (Processor p : processorList) {
            // For each processor node map turn it into a hashSet of keys
            scheduledNodes.addAll(p.getScheduledNodes());
        }
        return scheduledNodes;
    }

    public ArrayList<PartialSchedule> expandNewStates() {
        ArrayList<PartialSchedule> newExpandedSchedule = new ArrayList<PartialSchedule>();

        // Find how many nodes need to be scheduled for the expansion
        ArrayList<Integer> nodes = findSchedulableNodes();

        for (int i = 0; i < nodes.size(); i++) {
            Node currentNode = nodes.get(i);
            // Get each node that needs to be scheduled
            for (int j = 0; j < processorList.size(); j++) {
                PartialSchedule newSchedule = new PartialSchedule(this);
                // Add it to each processor and make that many corresponding schedules
                newSchedule.addToProcessor(j, currentNode);
                // Add the schedule to overall expanded list
                newExpandedSchedule.add(newSchedule);
            }
        }
        return newExpandedSchedule;
    }

    /**
     * This method iterates through the list of available nodes and finds nodes in
     * which all the parents of that node have already been used into a schedule and
     * updates the list
     * 
     * @return freeNodes
     */

    private ArrayList<Integer> findSchedulableNodes() {
        ArrayList<Integer> freeNodes = new ArrayList<>();

        // Loops through all nodes
        for (int node = 0; node < InputFileReader.NUM_NODES; node++) {
            // Checks if the node is in used nodes already
            if (!this.getUsedNodes().contains(node)) {

                if (!Arrays.asList(InputFileReader.parents[node]).contains(1)) {
                    // if no parents then add to list
                    freeNodes.add(node); // AUTOBOX
                }

                // if all parents are used add to list
                else if (this.getUsedNodes().containsAll(currentNode.getParentNodes())) {
                    freeNodes.add(currentNode);
                }
            }
        }
        return freeNodes;
    }

    /**
     * This method adds the node into the specified processor number
     *
     * @param processorNumber - the processor for the node to be added to
     * @param node            - the node to be added
     */
    private void addToProcessor(int processorNumber, Node node) {
        // Adds the node into the corresponding processor
        this.getProcessorList().get(processorNumber).addNode(node, this, processorNumber);
    }

    /**
     * Creates processors and adds it to the list
     *
     * @param numProcessors - Number of processors to add
     */
    private void createProcessors(int numProcessors) {
        for (int i = 0; i < numProcessors; i++) {
            processorList.add(new Processor(i));
        }
    }

    /**
     * Method that checks that every node in listOfAvailableNodes are in used nodes
     * for this current schedule
     *
     * @return true if all nodes used or else false
     */
    boolean isComplete() {
        return (getUsedNodes().containsAll(InputFileReader.listOfAvailableNodes));
    }

    /**
     * Returns list of Processor objects that have the nodes scheduled in order of
     * the processor identifier number
     */
    public ArrayList<Processor> getProcessorList() {
        Collections.sort(processorList, sortByIdentifierNumber);
        return processorList;
    }

    public int getFinishTime() {
        int finishTime = 0;
        for (Processor p : processorList) {
            if (p.getCurrentCost() > finishTime) {
                finishTime = p.getCurrentCost();
            }
        }
        return finishTime;
    }

    public int calculateCostFunction() {
        return getFinishTime();
    }

    public int getCostFunction() {
        return costFunction;
    }

    public void setCostFunction(int costFunction) {
        this.costFunction = costFunction;
    }

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
        PartialSchedule secondSchedule = (PartialSchedule) obj;
        return new EqualsBuilder()
                // .appendSuper(super.equals(obj))
                .append(processorList, secondSchedule.processorList).append(costFunction, secondSchedule.costFunction)
                .isEquals();

        // // Check for process normalisation
        // if (!processNormalisation(secondSchedule.getProcessorList())){
        // return false;
        // }
    }

    /**
     * hashCode() must be overridden whenever equals() is overridden
     **/
    @Override
    public int hashCode() {
        // Hash table prime numbers from https://planetmath.org/goodhashtableprimes
        return new HashCodeBuilder(805306457, 402653189).append(processorList).append(costFunction).toHashCode();
    }

    private boolean processNormalisation() {
        return true;
    }

    /**
     * Method calculates the start time of the current node by finding the latest
     * parent that has been scheduled, as well as the communication costs
     * 
     * @param node
     * @param processorNumber
     * @return
     */
    public int calculateStartTime(Node node, int processorNumber) {
        int maxStartTime = 0;
        // Best starting time of current node if no communication costs
        List<Node> parentNodes = node.getParentNodes();
        if (parentNodes.size() == 0) {
            maxStartTime = processorList.get(processorNumber).getCurrentCost();
            ;
        }
        for (Processor p : processorList) {
            for (Node n : parentNodes) {
                int currentStartTime = processorList.get(processorNumber).getCurrentCost();

                // If current processor contains a parent of "node" then calculate the the start
                // time needed
                if (p.getScheduledNodes().contains(n)) {
                    // If parent node is not scheduled in same processor
                    if (p.getProcessorID() != processorNumber) {
                        // Find end time of the parent node
                        int endTimeOfParent = p.getStartTimes().get(p.getScheduledNodes().indexOf(n))
                                + n.getNodeWeight();
                        // Gets communication cost of the parent
                        int communicationCost = node.getIncomingEdge(n).getEdgeWeight();
                        // If end time of parent is longer than that means we need to schedule when
                        // parent is finished
                        // instead of right when processor is free
                        if (endTimeOfParent >= currentStartTime
                                || endTimeOfParent + communicationCost >= currentStartTime) {
                            currentStartTime = endTimeOfParent + communicationCost;
                        }
                    }
                    // Finds the most start time as it is dependent on all parents
                    if (maxStartTime < currentStartTime) {
                        maxStartTime = currentStartTime;
                    }
                }
            }
        }
        return maxStartTime;
    }
}
