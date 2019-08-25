package se306.algorithm;

/**
 * This interface allows for the injection of other cost functions into the A*
 * Algorithm
 */

public interface CostFunctionInjector {

    /**
     * Calculates and sets the cost function to the specified schedule.
     * 
     * @param newPs
     * @param newestNode
     * @param numOfProcessors
     */
    public void calculateAndSetCostFunction(PartialSchedule newPs, int newestNode, int numOfProcessors);

}
