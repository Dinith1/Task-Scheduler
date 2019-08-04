package se306.Algorithm;

import se306.Input.Node;

import java.util.HashMap;
import java.util.List;

public class Processor {

    private int currentCost;
    private HashMap<Node, Integer> scheduledNodes;

    public Processor() {
        scheduledNodes = new HashMap<>();
        currentCost = 0;
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
     * Takes a Node parameter as the node to be added to the processor, ensuring that communication costs are
     * also taken into consideration //TODO
     * @param node - the node to add to the schedule
     */
    public void addToSchedule(Node node) {

        // Add node into the hashmap schedule, where the value is calculated by
        // (weight of the node + current cost of this processor + any communication costs)
        scheduledNodes.put(node, node.getNodeWeight() + currentCost + calculateCommunicationCosts(node));
        node.assignProcessor(this);

        // Update current cost of the schedule
        currentCost = node.getNodeWeight() + currentCost + calculateCommunicationCosts(node);
    }


    /**
     * Takes a Node parameter as the node to use to calculate communication costs, if any.
     * The processor currently ensures that if there is at least one parent that has not been scheduled in the
     * current processor, communication costs exist.
     * @param node - the node to add to calculate communication costs
     */
    private int calculateCommunicationCosts(Node node) {

        // Obtain list of parents of the node
        List<Node> parentNodes = node.getParentNodes();

        // Check if there is a parent that is NOT scheduled in this processor
        for (Node parent : parentNodes) {

            if (!scheduledNodes.containsKey(parent)) {

                // If scheduled in a different processor, obtain the minimum time/cost that the child
                // node must be scheduled
                int parentScheduleEnd = parent.getProcessor().getSchedule().get(parent);
                int communicationCost = node.getIncomingEdge(parent).getEdgeWeight();

                if (parentScheduleEnd > this.currentCost) {
                    // Ensure that the child node is always scheduled after the parent node
                    return (parentScheduleEnd - this.currentCost) + communicationCost;
                } else {
                    return communicationCost;
                }
            }
        }

        return 0;
    }


}
