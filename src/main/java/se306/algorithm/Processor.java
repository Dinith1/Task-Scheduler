package se306.algorithm;

import se306.input.Node;

import java.util.HashMap;
import java.util.List;

public class Processor {

    private int currentCost;
    private HashMap<Node, Integer> scheduledNodes;
    private HashMap<Node, Integer> startTimes;
    private String processorIdentifier;

    public Processor(String processorIdentifier) {
        scheduledNodes = new HashMap<>();
        startTimes = new HashMap<>();
        currentCost = 0;
        this.processorIdentifier = processorIdentifier;
    }

    /**
     * Gets the identifier for the processor
     * @return Identifier for the processor i.e processor 1, processor 2...
     */
    public String getProcessorIdentifier(){
        return processorIdentifier;
    }

    /**
     * Gets the current cost of the processor; the finishing time/cost of the last scheduled node
     *
     */
    public int getCurrentCost() {
        return this.currentCost;
    }


    /**
     * Gets the current schedule of the processor through a hashmap where the key is the Node itself
     * and the value is an Integer that represents the finishing time that it has been scheduled in the processor
     *
     */
    public HashMap<Node, Integer> getSchedule() {
        return this.scheduledNodes;
    }

    /**
     * Gets the current start times of the processor through a hashmap where the key is the Node itself
     * and the value is an Integer that represents the starting time that it has been scheduled in the processor
     *
     */
    public HashMap<Node, Integer> getStartTimes() { return this.startTimes; }


    /**
     * Takes a Node parameter as the node to be added to the processor, ensuring that communication costs are
     * also taken into consideration
     * @param node - the node to add to the schedule
     */
    public void addNode(Node node) {

        // Add node into the hashmap schedule, where the value is calculated by
        // (weight of the node + current cost of this processor + any communication costs)
        startTimes.put(node, currentCost + calculateCommunicationCosts(node));
        scheduledNodes.put(node, node.getNodeWeight() + currentCost + calculateCommunicationCosts(node));
        node.assignProcessor(this);

        // Update current cost of the schedule
        currentCost = node.getNodeWeight() + currentCost + calculateCommunicationCosts(node);
    }


    /**
     * Takes a Node parameter as the node to use to calculate communication costs, if any.
     * The processor currently ensures that if there is at least one parent that has not been scheduled in the
     * current processor, communication costs exist. It also ensures that all parents/dependencies are have
     * completed their schedule before the child can be scheduled.
     * @param node - the node to add to calculate communication costs
     */
    private int calculateCommunicationCosts(Node node) {

        // Obtain list of parents of the node
        List<Node> parentNodes = node.getParentNodes();

        // Keep track of the maximum cost of all parents and use as a basis to schedule the child
        int maxParentCost = 0;
        Node maxParent = null;

        // Find the parent with the latest schedule in another processor (if any)
        for (Node parent : parentNodes) {
            if (!scheduledNodes.containsKey(parent)) {

                // If scheduled in a different processor, obtain the minimum time/cost that the child
                // node must be scheduled
                int parentScheduleEnd = parent.getProcessor().getSchedule().get(parent);

                if (maxParentCost < parentScheduleEnd) {

                    maxParentCost = parentScheduleEnd;
                    maxParent = parent;
                }
            }
        }

        // If there exists a parent that is scheduled in another processor, use a communication cost
        if (maxParent != null) {
            int communicationCost = node.getIncomingEdge(maxParent).getEdgeWeight();

            if (maxParentCost > this.currentCost) {
                // Ensure that the child node is always scheduled after the parent node
                return (maxParentCost - this.currentCost) + communicationCost;

            } else { return communicationCost; }

        } else { return 0; }
    }
}
