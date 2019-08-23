package se306.algorithm;


import se306.input.InputFileReader;

import static se306.input.InputFileReader.childrenofParent;
import static se306.input.InputFileReader.parents;

public class CostFunctionCalculator {


    private PartialSchedule _ps;

    public int getCostFunction(PartialSchedule newPs, Node newestNode, int numOfProcessors, List<Node> free) {


        // Find all the free nodes AFTER node n has been scheduled
        // Loop through the free nodes
        // Loop through each processor
        // Schedule each free node onto each processor
        // data ready time = min(earliest start of each node on all processors)
        // f(drt) =   max( all  data ready times + bottom level)


        int maxDRT = 0;
        maxDRT = getDRT(newPs,newestNode,free);

        //Free var drt

        int maxBL = getBottomLevelRecursive(newestNode);
        //    int maxBL = 0;
        int maxIdleTime = getIdleTime(newPs, numOfProcessors);
        // int maxIdleTime = 0;
        int max = Math.max(Math.max(maxBL, maxDRT), maxIdleTime);

        //	System.out.println("maxBl = " + maxBL);
        //	System.out.println("maxDRT = " + maxDRT);
        System.out.println("idleTime= " + maxIdleTime);
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
        if(InputFileReader.childrenofParent.get(node) instanceof int[]){
           int upperBound = 0;
            //want to get those two children
            int[] arrayofChildren = (int[])childrenofParent.get(node);

            for(int i = 0;i<arrayofChildren.length;i++){
                if(arrayofChildren[i] == 1){
                    upperBound += getBottomLevelRecursive(i);
//                    int current = getBottomLevelRecursive(i);
//                    if(upperBound < current){
//                        upperBound = current;
//                    }
                }
            }
            return upperBound + InputFileReader.nodeWeights.get(node);
        }else{
            return InputFileReader.nodeWeights.get(node);
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

    public int getDRT(PartialSchedule newPs, Node newestNode, List<Node> free) {
        int maxDRT = 0;
        int dataReady;
        int bottomLevel;
        for (Node freeNode : free) {

            int minDRT = -1;

            bottomLevel = freeNode.getBottomLevel();

            for (Processor p : newPs.getProcessorList()) {
                dataReady = newPs.calculateStartTime(freeNode, p.getProcessorID());

                if (minDRT > dataReady) {
                    minDRT = dataReady;
                }
            }

            int dataReadyCost = bottomLevel + minDRT;

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


    public int getIdleTime(PartialSchedule ps, int numberOfProcessors) {

            
        int totalIdleTime = ps.getIdleTime();

        for (Processor p : ps.getProcessorList()) {
            totalIdleTime += p.calculateIdleTime();
        }
        //System.out.println(totalIdleTime);
        ps.setIdleTime(totalIdleTime);
        int totalWeight = 0;
        for (Node n : InputFileReader.listOfAvailableNodes) {
            totalWeight += n.getNodeWeight();
        }
        return (totalIdleTime + totalWeight) / numberOfProcessors;
    }
}
