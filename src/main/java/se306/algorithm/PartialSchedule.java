package se306.algorithm;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import se306.input.InputFileReader;

/**
 * A partial schedule represents a partial solution to obtaining the optimal schedule, each partial schedule has
 * a list of processes which contain their respective nodes.
 */
public class PartialSchedule {
    private InputFileReader ifr = InputFileReader.getInstance();

    // User defined available processors placed in a list
    private HashMap<Integer, Processor> processorList = new HashMap<Integer, Processor>();
    private double costFunction;
    public int numberOfNodesScheduled;
    static ExecutorService multiThreadExecutor;

    PartialSchedule(int processorNumber) {
        createProcessors(processorNumber);
    }

    /**
     * Copy constructor creates a new object with the same attributes
     * @param ps
     */
    private PartialSchedule(PartialSchedule ps) {
        for (Integer i : ps.getProcessorList().keySet()) {
            Processor p = ps.getProcessorList().get(i);
            this.processorList.put(p.getProcessorID(), new Processor(p));
        }

        this.costFunction = ps.costFunction;
    }

    /**
     * Helper function to choose a parallelised algorithm or sequential algorithm
     * @param numOfCores
     * @return
     */
    public HashSet<PartialSchedule> chooseExpansionAlgorithm(int numOfCores) {
        // If cores is 1 use old method to execute expansion
        if (numOfCores == 1) {
            return expandNewStates();
        }
        // Creates a list of callable objects
        List<Callable<HashSet<PartialSchedule>>> tasks = new ArrayList<>();
        // Loop through the number of free nodes and add them to the task list
        for (Integer i : getFreeNodes()) {
            tasks.add(() -> expandNewStatesParallel(i));
        }
        // Initialises output HashSet
        HashSet<PartialSchedule> output = new HashSet<>();
        try {
            // Invokes all tasks in the lists
            List<Future<HashSet<PartialSchedule>>> futureTasks = multiThreadExecutor.invokeAll(tasks);
            for (Future<HashSet<PartialSchedule>> item : futureTasks) {
                // Retrieves output
                output.addAll(item.get());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Parallel helper method for expanding new states where it takes a node as input so each expansion can be
     * run on a different thread
     * @param node
     * @return
     */
    private HashSet<PartialSchedule> expandNewStatesParallel(Integer node) {
        HashSet<PartialSchedule> newExpandedSchedule = new HashSet<>();
        for (int j = 0; j < processorList.size(); j++) {
            PartialSchedule newSchedule = new PartialSchedule(this);

            // Add it to each processor and make that many corresponding schedules
            newSchedule.addToProcessor(j, node);
            calculateCostFunction(newSchedule, node, processorList.size());
            // Add the schedule to overall expanded list
            newExpandedSchedule.add(newSchedule);
        }
        return newExpandedSchedule;
    }

    /**
     * This function expands the new states according to how many free nodes are available to be scheduled
     * It adds each node that is available onto every processor and creates that new state to be expanded
     * @return newExpandedSchedule
     */
    public HashSet<PartialSchedule> expandNewStates() {
        HashSet<PartialSchedule> newExpandedSchedule = new HashSet<>();
        Set<Integer> nodes = findSchedulableNodes();

        // Find how many nodes need to be scheduled for the expansion
        for (Integer node : nodes) {
            // Get each node that needs to be scheduled
            for (int j = 0; j < processorList.size(); j++) {
                PartialSchedule newSchedule = new PartialSchedule(this);

                // Add it to each processor and make that many corresponding schedules
                newSchedule.addToProcessor(j, node);
                calculateCostFunction(newSchedule, node, processorList.size());
                // Add the schedule to overall expanded list
                newExpandedSchedule.add(newSchedule);
            }
        }

        return newExpandedSchedule;
    }

    /**
     * Retrieves that nodes that are able to be scheduled
     * @return
     */
    public Set<Integer> getFreeNodes() {
        return this.findSchedulableNodes();
    }

    /**
     * This method iterates through the list of available nodes and finds nodes in
     * which all the parents of that node have already been used into a schedule and
     * updates the list
     *
     * @return freeNodes
     */
    private Set<Integer> findSchedulableNodes() {
        Set<Integer> freeNodes = new HashSet<>();

        // Loops through all nodes
        for (int node : ifr.getNodeIds()) {
            // Check if the node is in usedNodes already
            if (!this.getUsedNodes().contains(node)) {

                // If no parents then add to list
                if (!ifr.getNodeParents().containsKey(node)) {
                    freeNodes.add(node); // AUTOBOXING
                }

                // If all parents are used add to list
                else {
                    boolean allParentsUsed = true; // Should be true even if the node has no parents

                    for (int parent : ifr.getNodeParents().get(node)) {
                        if (!this.getUsedNodes().contains(parent)) {
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
     * This method adds the node into the processor with the specified processor
     * number
     *
     * @param processorNumber - the processor for the node to be added to
     * @param node            - the node to be added
     */
    private void addToProcessor(int processorNumber, Integer node) {
        this.getProcessorList().get(processorNumber).addNode(node, this, processorNumber);

        //
    }

    /**
     * Retrieves the cost function
     * @return
     */
    public double getCostFunction() {
        return costFunction;
    }

    public void setCostFunction(double costFunction) {
        this.costFunction = costFunction;
    }

    /**
     * This method calls the calculateAndSetCostFunction method to set an updated
     * cost function for the partial schedule AFTER the new node is trialled
     *
     * @param ps
     * @param nodeToAdd
     * @param numOfProcessors
     */
    private void calculateCostFunction(PartialSchedule ps, int nodeToAdd, int numOfProcessors) {
        CostFunctionCalculator calculator = new CostFunctionCalculator();
        calculator.calculateAndSetCostFunction(ps, nodeToAdd, numOfProcessors);
    }


    /**
     * Creates processors and adds it to the list
     *
     * @param numProcessors - Number of processors to add
     */
    private void createProcessors(int numProcessors) {
        for (int i = 0; i < numProcessors; i++) {
            processorList.put(i, new Processor(i));
        }
    }

    /**
     * Method that checks that every node in listOfAvailableNodes are in used nodes
     * for this current schedule
     *
     * @return true if all nodes used or else false
     */
    boolean isComplete() {
        for (int node : ifr.getNodeIds()) {
            if (!getUsedNodes().contains(node)) {
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
    private Set<Integer> getUsedNodes() {
        Set<Integer> scheduledNodes = new HashSet<>();
        int count = 0;
        for (Integer i : processorList.keySet()) {
            Processor p = processorList.get(i);
            
            // For each processor node map turn it into a hashSet of keys
            for (Integer nodes : p.getScheduledNodes()) {
                scheduledNodes.add(nodes);
                count++;
            }
        }
        numberOfNodesScheduled = count;
        return scheduledNodes;
    }

    /**
     * Retrieves max time of a schedule
     * @return
     */
    public int getFinishTime() {
        int finishTime = 0;
        for (Processor p : processorList.values()) {
            if (p.getCurrentCost() > finishTime) {
                finishTime = p.getCurrentCost();
            }
        }
        return finishTime;
    }

    /**
     * Returns list of Processor objects that have the nodes scheduled in order of
     * the processor identifier number
     */

    public HashMap<Integer, Processor> getProcessorList() {
        return processorList;
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
        return (processorList.hashCode() == (secondSchedule.processorList.hashCode()));

    }

    /**
     * hashCode() must be overridden whenever equals() is overridden
     **/
    @Override
    public int hashCode() {
        // Hash table prime numbers from https://planetmath.org/goodhashtableprimes
        return new HashCodeBuilder().append(processorList).toHashCode();
    }

    /**
     * Method calculates the start time of the current node by finding the latest
     * parent that has been scheduled, as well as the communication costs
     *
     * This function finds the BEST start time to schedule the node, not the start
     * times of nodes already scheduled
     *
     * @param node
     * @param processorNumber
     * @return
     */
    public int calculateStartTime(int node, int processorNumber) {

        // If no parents
        if (!ifr.getNodeParents().containsKey(node)) {
            return processorList.get(processorNumber).getCurrentCost();
        }

        // Gets parents of the current node
        int[] parentNodes = ifr.getNodeParents().get(node);
        int maxStartTime = 0;

        for (Integer i : processorList.keySet()) {
            Processor p = processorList.get(i);

            for (int parentID : parentNodes) {
                // Best start time for the node being inserted to the specific processor
                int currentStartTime = processorList.get(processorNumber).getCurrentCost();

                // If current processor contains a parent of "node" then calculate the the start
                // time needed
                if (p.getScheduledNodes().contains(parentID)) {

                    // If parent node is not scheduled in same processor
                    if (p.getProcessorID() != processorNumber) {

                        // Find end time of the parent node
                        int endTimeOfParent = p.getStartTimes().get(parentID)
                                + ifr.getNodeWeights().get(parentID);

                        // Gets communication cost of the parent
                        int communicationCost = 0; // NEED TO CHECK THIS ====================================
                        for (int[] edge : ifr.getListOfEdges()) {
                            if ((edge[0] == parentID) && (edge[1] == node)) {
                                communicationCost = edge[2];
                                break;
                            }
                        }

                        // If end time of parent is longer than that means we need to schedule when
                        // parent is finished
                        // instead of right when processor is free
                        if ((endTimeOfParent >= currentStartTime)
                                || (endTimeOfParent + communicationCost >= currentStartTime)) {
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
