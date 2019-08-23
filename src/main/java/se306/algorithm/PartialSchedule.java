package se306.algorithm;

import java.util.*;

import javafx.scene.input.InputMethodTextRun;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se306.input.InputFileReader;
import se306.algorithm.Processor;

public class PartialSchedule {

    // User defined available processors placed in a list
    private ArrayList<Processor> processorList = new ArrayList<>();
    private double costFunction;
    private ArrayList<Integer> freeNodes = new ArrayList<>();

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

    public List<PartialSchedule> expandNewStates() {
        List<PartialSchedule> newExpandedSchedule = new ArrayList<>();

        // Find how many nodes need to be scheduled for the expansion
        ArrayList<Integer> nodes = findSchedulableNodes();
        for (Integer node : nodes) {
            // Get each node that needs to be scheduled
            for (int j = 0; j < processorList.size(); j++) {
                PartialSchedule newSchedule = new PartialSchedule(this);
                // Add it to each processor and make that many corresponding schedules
                newSchedule.addToProcessor(j, node);

                this.assignCostFunction((this.calculateCostFunction(newSchedule, node, processorList.size())), newSchedule);

                if(AStarScheduler.closed.contains(newSchedule) || AStarScheduler.closed.contains(newSchedule)){
                    continue;
                }
                else {
                    // Add the schedule to overall expanded list
                    newExpandedSchedule.add(newSchedule);
                }
            }
        }

        return newExpandedSchedule;
    }

    public ArrayList<Integer> getFreeNodes() {
        return this.findSchedulableNodes();
    }

    /**
     * This method iterates through the list of available nodes and finds nodes in
     * which all the parents of that node have already been used into a schedule and
     * updates the list
     * 
     * @return freeNodes
     */

    private ArrayList<Integer> findSchedulableNodes() {
        freeNodes = new ArrayList<>();

        // Loops through all nodes
        for (int node = 0; node < InputFileReader.NUM_NODES; node++) {
            // Checks if the node is in used nodes already
            if (!this.getUsedNodes().contains(node)) {

                // If no parents then add to list
                if (!Arrays.stream(InputFileReader.parents[node]).anyMatch(i -> i == 1)) {
                    freeNodes.add(node); // AUTOBOXING
                }

                // If all parents are used add to list
                else {
                    boolean allParentsUsed = true; // Should be true even if the node has no parents

                    for (int i = 0; i < InputFileReader.NUM_NODES; i++) {
                        int parent = InputFileReader.parents[node][i];

                        if ((parent == 1) && !this.getUsedNodes().contains(i)) {
                            allParentsUsed = false;
                            break;
                        }
                    }

                    if (allParentsUsed) {
                        freeNodes.add(node);
                    }
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
    private void addToProcessor(int processorNumber, Integer node) {
        // Adds the node into the corresponding processor
        this.getProcessorList().get(processorNumber).addNode(node, this, processorNumber);

        //
    }

    private void assignCostFunction(double costFunction, PartialSchedule partialSchedule) {
        partialSchedule.setCostFunction(costFunction);
    }

    private double calculateCostFunction(PartialSchedule ps, int nodeToAdd, int numOfProcessors) {
        CostFunctionCalculator calculator = new CostFunctionCalculator();

        return calculator.getCostFunction(ps, nodeToAdd, numOfProcessors);
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
        for (int i = 0; i < InputFileReader.NUM_NODES; i++) {
            if (!getUsedNodes().contains(i)) {
                return false;
            }
        }

        return true;
    }

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

//    public int calculateCostFunction() {
//        return getFinishTime();
//    }

    public double getCostFunction() {
        return costFunction;
    }

    public void setCostFunction(double costFunction) {
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


    public int findStartTime(int node, Processor processor) {
        return processor.getScheduleStartTimes()[node];
    }


    /**
     * Method calculates the start time of the current node by finding the latest
     * parent that has been scheduled, as well as the communication costs
     *
     * This function finds the BEST start time to schedule the node, not the start times of nodes already scheduled
     * 
     * @param node
     * @param processorNumber
     * @return
     */
    public int calculateStartTime(int node, int processorNumber) {
        int maxStartTime = 0;

        // Best starting time of current node if no communication costs

        // Find parents of the current node
        int[] parentNodes = InputFileReader.parents[node];

        // If no parents
        if (!Arrays.stream(parentNodes).anyMatch(i -> i == 1)) {

            // Find the latest start time in the processor
            maxStartTime = processorList.get(processorNumber).getCurrentCost();
        }


        // For each processor
        for (Processor p : processorList) {

            // For each parent
            for (int parentID = 0; parentID < parentNodes.length; parentID++) {

                int parent = parentNodes[parentID];
                if (parent != 0) {

                    // ========================
                    // NEED TO CHECK EQUALS IN THIS METHOD AND ALL OTHER PLACES
                    // ========================



                    // If current processor contains a parent of "node"
                    if (p.getScheduledNodes().contains(parentID)) {

                        // Find the latest start time in the processor @TODO + communication cost
                        int currentStartTime = processorList.get(p.getProcessorID()).getCurrentCost();


                        // If the processor ID is not the original processor number
                        if (p.getProcessorID() != processorNumber) {

                            int startTime = p.getStartTimes().get(p.getScheduledNodes().indexOf(parentID));
                            int weight = InputFileReader.nodeWeights.get(parentID);

                            // Find end time of the parent node
                            int endTimeOfParent = p.getStartTimes().get(p.getScheduledNodes().indexOf(parentID))
                                    + InputFileReader.nodeWeights.get(parentID);
                            // Gets communication cost of the parent
                            int communicationCost = 0; // NEED TO CHECK THIS ====================================
                            for (int[] edge : InputFileReader.listOfEdges) {
                                if ((edge[0] == parentID) && (edge[1] == node)) {
                                    communicationCost = edge[2];
                                    break;
                                }
                            }

                            // If end time of parent is longer, that means we need to schedule when
                            // parent is finished
                            // instead of right when processor is free
                            if ((endTimeOfParent >= currentStartTime) ||
                                    (endTimeOfParent + communicationCost >= currentStartTime)) {
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
        }
        return maxStartTime;
    }
}
