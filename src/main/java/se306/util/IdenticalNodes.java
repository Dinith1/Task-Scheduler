package se306.util;

import java.util.HashMap;
import java.util.Map;

import se306.input.InputFileReader;

public class IdenticalNodes {

    private static IdenticalNodes identicalNodes = null;
    private InputFileReader ifr;

    private IdenticalNodes() {
        this.ifr = InputFileReader.getInstance();
    }

    public static IdenticalNodes getInstance() {
        return (identicalNodes == null) ? (identicalNodes = new IdenticalNodes()) : identicalNodes;
    }

    /**
     * For testing purposes
     */
    public static void clearIdenticalNodes() {
        identicalNodes = null;
    }

    /**
     * 
     * @param nodes
     * @return An int array containing IDs of the identical children. Returns null
     *         if no identical children exist
     */
    public int[] getIdenticalNodes(int[] nodes) {
        return checkNodeParents(nodes);
    }

    /**
     * Checks if an array of nodes (which have the same weights) all have a common
     * parent.
     * 
     * @param sameWeightNodes Array of nodes to check
     */
    private int[] checkNodeParents(int[] sameWeightNodes) {
        // Map from list of parent nodes to list of node ids
        HashMap<int[], int[]> mapOfNodeParents = new HashMap<>();

        // Find all (same weight) nodes with a parent in common
        for (int node : sameWeightNodes) {
            HashMapGenerator.addNodeToParentMap(node, mapOfNodeParents, ifr.getNodeParents());
        }

        for (Map.Entry<int[], int[]> entry : mapOfNodeParents.entrySet()) {
            int[] sameParentNodes = entry.getValue();

            // Check if there are at least two (same weight) nodes with the same parent
            if (sameParentNodes.length > 1) {
                // Check if nodes have the same children
                return checkNodeChildren(sameParentNodes);
            }
        }

        // No identical children
        return null;
    }

    /**
     * @param sameParentNodes
     */
    private int[] checkNodeChildren(int[] sameParentNodes) {
        // Map from child node to list of node ids
        HashMap<int[], int[]> mapOfNodeChildren = new HashMap<>();

        // Find all same weight nodes with the same children
        for (int node : sameParentNodes) {
            HashMapGenerator.addNodeToChildrenMap(node, mapOfNodeChildren, ifr.getNodeChildren());
        }

        for (Map.Entry<int[], int[]> entry : mapOfNodeChildren.entrySet()) {
            int[] sameChildrenNodes = entry.getValue();

            // Check if value matches with given value
            if (sameChildrenNodes.length > 1) {
                // Check incoming edge weights
                return checkNodeIncomingEdges(sameChildrenNodes);
            }
        }

        // No identical children
        return null;
    }

    /**
     * @param sameChildrenNodes
     */
    private int[] checkNodeIncomingEdges(int[] sameChildrenNodes) {
        // Map from incoming edges to list of node ids
        HashMap<int[], int[]> mapOfNodeIncomingEdges = new HashMap<>();

        // Find all same weight nodes with the same children
        for (int node : sameChildrenNodes) {
            HashMapGenerator.addNodeToEdgesMap(node, mapOfNodeIncomingEdges, ifr.getListOfEdges(), 1);
        }

        for (Map.Entry<int[], int[]> entry : mapOfNodeIncomingEdges.entrySet()) {
            int[] sameIncomingEdgeNodes = entry.getValue();

            // Check if value matches with given value
            if (sameIncomingEdgeNodes.length > 1) {
                // Check outgoing edge weights
                return checkNodeOutgoingEdges(sameIncomingEdgeNodes);
            }
        }

        // No identical children
        return null;
    }

    /**
     * @param sameIncomingNodes
     */
    private int[] checkNodeOutgoingEdges(int[] sameIncomingNodes) {
        // Map from parent node to list of children node ids
        HashMap<int[], int[]> mapOfNodeOutgoingEdges = new HashMap<>();

        // Find all nodes with the same incoming edges
        for (int i : sameIncomingNodes) {
            HashMapGenerator.addNodeToEdgesMap(i, mapOfNodeOutgoingEdges, ifr.getListOfEdges(), 0);
        }

        for (Map.Entry<int[], int[]> entry : mapOfNodeOutgoingEdges.entrySet()) {
            int[] identicalNodes = entry.getValue();

            // Check if value matches with given value
            if (identicalNodes.length > 1) {
                // If this point is reached, the nodes are identical
                // Chain the identical nodes
                return identicalNodes;
            }
        }

        // No identical children
        return null;
    }

}