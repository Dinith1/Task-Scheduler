package se306.algorithm;

import se306.input.InputFileReader;

import java.util.HashMap;
import java.util.Set;

public class CostFunctionCalculator {

    // Map from node id to bottom level weight
    private HashMap<Integer, Integer> bottomLevels = new HashMap<Integer, Integer>();

    public CostFunctionCalculator() {
        // Initialize map with all zeros
        for (int node : InputFileReader.nodeIds) {
            bottomLevels.put(node, 0);
        }
    }

    /**
     * This method calculates and assigns the total cost function of a partial
     * schedule in dependency of adding the newest node. The total cost function is
     * calculated by the maximum of: 1. Bottom Level 2. Data Ready Time 3. Idle Time
     * 
     * @param newPs
     * @param newestNode
     * @param numOfProcessors
     */
    public void calculateAndSetCostFunction(PartialSchedule newPs, int newestNode, int numOfProcessors) {

        // Find all the free nodes AFTER node n has been scheduled
        Set<Integer> free = newPs.getFreeNodes();

        double maxBL = 0;

        // For each processor, calculate the bottom level time and output the maximum
        // out of all processors
        for (Processor processor : newPs.getProcessorList().values()) {
            for (Integer nodeId : processor.getStartTimes().keySet()) {
                double BL = getBottomLevelRecursive(nodeId) + processor.getStartTimes().get(nodeId);
                if (BL > maxBL) {
                    maxBL = BL;
                }
            }
        }

        double maxDRT = getDRT(newPs, newestNode, free);

        double maxIdleTime = getIdleTime(newPs, numOfProcessors);

        double max = Math.max(Math.max(maxBL, maxDRT), maxIdleTime);

        // Assign cost function to the partial schedule
        newPs.setCostFunction(max);
    }

    /**
     * This method calculates recursively each node, starting from the input node,
     * it calls its children The children get their bottom level, and passes it back
     * to this current node
     *
     * @param node
     * @return
     */
    public int getBottomLevelRecursive(int node) {

        // If the supplied node has children
        if (InputFileReader.nodeChildren.get(node) instanceof int[]) {
            int upperBound = 0;
            // Want to get those children
            int[] arrayOfChildren = InputFileReader.nodeChildren.get(node);
            int current = 0;
            // Iterate through each child
            for (int i : arrayOfChildren) {
                current = getBottomLevelRecursive(i);
                this.bottomLevels.put(i, current);
                if (upperBound < current) {
                    upperBound = current;
                }
            }
            this.bottomLevels.put(node, current + InputFileReader.nodeWeights.get(node));
            return upperBound + InputFileReader.nodeWeights.get(node);
        } else {
            return InputFileReader.nodeWeights.get(node);
        }
    }

    /**
     * This method calculates the Data Ready Time of each node. This is inclusive of
     * the communication costs IF the node being scheduled is not on the same
     * processor as the parent node.
     * 
     * @param newPs
     * @param free
     * @return
     */
    public double getDRT(PartialSchedule newPs, int node, Set<Integer> free) {
        double bottomLevel;
        double maxDRT = this.bottomLevels.get(node) + InputFileReader.nodeWeights.get(node);
        for (Integer freeNode : free) {

            double maxStartTime = Double.POSITIVE_INFINITY;

            // Find the bottom level of the current free node that is being "applied"
            bottomLevel = this.bottomLevels.get(freeNode);

            // Trial every processor
            for (Processor p : newPs.getProcessorList().values()) {
                // Find the earliest start time that the node can be scheduled onto the current
                // processor
                int dataReady = newPs.calculateStartTime(freeNode, p.getProcessorID());

                // Update the maximum T(dr) once, but don't update again
                if ((maxStartTime > dataReady)) {
                    maxStartTime = dataReady;
                }
            }

            // Calculate the cost function DRT
            double dataReadyCost = bottomLevel + maxStartTime;

            if (dataReadyCost > maxDRT) {
                maxDRT = dataReadyCost;
            }
        }
        return maxDRT;

    }

    /**
     * This cost function returns the idle time + sum of each weight of every single
     * node of the graph
     * 
     * @param ps
     * @param numberOfProcessors
     * @return
     */
    public double getIdleTime(PartialSchedule ps, int numberOfProcessors) {
        // int totalIdleTime = ps.getIdleTime();
        double totalIdleTime = 0;

        // Iterate through each processor to find Idle Times
        for (Processor p : ps.getProcessorList().values()) {
            totalIdleTime += p.calculateIdleTime();
        }

        // Calculate the total weight of the graph
        double totalWeight = 0;
        for (int i : InputFileReader.nodeIds) {
            totalWeight += InputFileReader.nodeWeights.get(i);
        }

        // Calculate the cost function of Idle Time
        return ((totalIdleTime + totalWeight) / (double) numberOfProcessors);
    }

}
