package se306.algorithm;


import se306.input.InputFileReader;

import java.util.ArrayList;

import static se306.input.InputFileReader.childrenOfParent;

public class CostFunctionCalculator {

    private double[] bottomLevels = new double[InputFileReader.NUM_NODES];


    private PartialSchedule ps;

    public double getCostFunction(PartialSchedule newPs, int newestNode, int numOfProcessors) {


        // Find all the free nodes AFTER node n has been scheduled
        // Loop through the free nodes
        // Loop through each processor
        // Schedule each free node onto each processor
        // data ready time = min(earliest start of each node on all processors)
        // f(drt) =   max( all  data ready times + bottom level)


//        double maxDRT = 0;

        // Get the freeNodes for the particular partial schedule
        ArrayList<Integer> free = newPs.getFreeNodes();

        double maxBL = 0;

        for (Processor processor : newPs.getProcessorList()) {
            for(int nodeId : processor.getScheduledNodes()) {
                double BL = getBottomLevelRecursive(nodeId) + processor.getStartTimes().get(processor.getScheduledNodes().indexOf(nodeId));
                if (BL > maxBL) {
                    maxBL = BL;
                }
            }
        }
//        System.out.println("MAXBL = " + maxBL);

        double maxDRT = getDRT(newPs, free);
//        System.out.println("MaxDrt = " + maxDRT);


        double maxIdleTime = getIdleTime(newPs, numOfProcessors);
//        System.out.println("idleTime= " + maxIdleTime);

        double max = Math.max(Math.max(maxBL, maxDRT), maxIdleTime);

        //	System.out.println("maxBl = " + maxBL);
        //	System.out.println("maxDRT = " + maxDRT);

//        System.out.println("Cost function is " + max);
        return max;
    }


    /**
     * This method calculates recursively each node, starting from the input node, it calls its children
     * The children get their bottom level, and passes it back to this current node
     *
     * @param node
     * @return
     */
    public int getBottomLevelRecursive(int node) {


        //If the supplied node has children
        if (InputFileReader.childrenOfParent.get(node) instanceof int[]) {
            int upperBound = 0;
            // Want to get those children
            int[] arrayOfChildren = (int[]) childrenOfParent.get(node);
            int current = 0;
            // Iterate through each child
            for (int i = 0; i < arrayOfChildren.length; i++) {
                // Check if node has child
                if (arrayOfChildren[i] == 1) {
//                    upperBound += getBottomLevelRecursive(i);
                    current = getBottomLevelRecursive(i);
                    this.bottomLevels[i] = current;
                    if(upperBound < current){
                        upperBound = current;
                    }
                }
            }
            this.bottomLevels[node] = current + InputFileReader.nodeWeights.get(node);
            return upperBound + InputFileReader.nodeWeights.get(node);
        } else {
            return InputFileReader.nodeWeights.get(node);
        }
    }

//
//        if (node.getNumberOfOutGoingEdges() > 0) {
//            int upperBound = 0;
//            List<Edge> listOfOutGoingEdges = node.getOutGoingEdges();
//            List<Node> listOfChildNodes = new ArrayList<>();
//            for (Edge e : listOfOutGoingEdges) {
//                listOfChildNodes.add(e.getNodeEnd());
//            }
//
//            for (Node n : listOfChildNodes) {
//                int current = getBottomLevelRecursive(n);
//                n.setBottomLevel(current);
//                if (upperBound < current) {
//                    upperBound = current;
//                }
//            }
//            return upperBound + node.getNodeWeight();
//        } else {
//            return node.getNodeWeight();
//        }
//    }


    /**
     * This method calculates the Data Ready Time of each node. This is inclusive of the
     * communication costs IF the node being scheduled is not on the same processor as
     * the parent node.
     * @param newPs
     * @param free
     * @return
     */
    public double getDRT (PartialSchedule newPs, ArrayList<Integer> free){
            double maxDRT = 0;
            double dataReady = 0;
            double bottomLevel;
            for (Integer freeNode : free) {

                double maxStartTime = 0;

                // Find the bottom level of the current free node that is being "applied"
                bottomLevel = this.bottomLevels[freeNode];

                // Trial every processor
                for (Processor p : newPs.getProcessorList()) {

                    // Find the earliest start time that the node can be scheduled onto the current processor
                    dataReady = newPs.calculateStartTime(freeNode, p.getProcessorID());

                    // Update the maximum T(dr)
                    if (maxStartTime < dataReady) {
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

//	public int getDRT(Node node, PartialSchedule ps, Processor processor) {
//		int upperBound = 0;
//		int drt = 0;
//		if (node.getNumberOfIncomingEdges() > 0) {
//			List<Edge> listofIncomingEdges = node.getIncomingEdges();
//			List<Node> listofParentNodes = new ArrayList<>();
//
//			// Gets each parent of node
//			for (Edge e : listofIncomingEdges) {
//				listofParentNodes.add(e.getNodeStart());
//			}
//
//			// For each parent
//			for (Node parentNode : listofParentNodes) {
//				//get finishing time
//
//				// If processor of parent is not the processor of the current node (child), add the communication cost
//				int finishTimeOfNode = ps.getProcessor(parentNode).getStartTime(parentNode) + parentNode.getNodeWeight();
//				if (ps.getProcessor(parentNode).getProcessorID() != processor.getProcessorID()) {
//					finishTimeOfNode = finishTimeOfNode + node.getIncomingEdge(parentNode).getEdgeWeight();
//				}
//
//				// If max upper bound is less than the new upper bound
//				if (upperBound < finishTimeOfNode) {
//					upperBound = finishTimeOfNode;
//					drt = upperBound + parentNode.getBottomLevel();
//				}
//
//			}
//			return drt;
//		}
//		return drt;


    /**
     * This cost function returns the idle time + sum of each weight of every single node of the graph
     * @param ps
     * @param numberOfProcessors
     * @return
     */
        public double getIdleTime(PartialSchedule ps, int numberOfProcessors){
            //  int totalIdleTime = ps.getIdleTime();
            double totalIdleTime = 0;

            // Iterate through each processor to find Idle Times
            for (Processor p : ps.getProcessorList()) {
//                System.out.println("Idle Time to add: " + p.calculateIdleTime());
                totalIdleTime += p.calculateIdleTime();
            }

            //System.out.println(totalIdleTime);
            //  ps.setIdleTime(totalIdleTime);

            // Calculate the total weight of the graph
            double totalWeight = 0;
            for (int i = 0; i < InputFileReader.NUM_NODES; i++) {
                totalWeight += InputFileReader.nodeWeights.get(i);
            }

            // Calculate the cost function of Idle Time
            return ((totalIdleTime + totalWeight) / (double)numberOfProcessors);
        }
    }

