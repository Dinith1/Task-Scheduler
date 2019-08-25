package se306.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HashMapGenerator {

    private static HashMapGenerator hashMapGenerator = null;
    
    private HashMapGenerator(){
    }

    public static HashMapGenerator getInstance(){
		return (hashMapGenerator == null) ? (hashMapGenerator = new HashMapGenerator()) : hashMapGenerator;
    }

    /**
     * For testing purposes
     */
    public static void clearhHashMapGenerator() {
        hashMapGenerator = null;
    }

    /* Adds nodes to a hashmap, where the key is the weight, and the value is an
     * array of all the node (IDs) with that weight.
     * 
     * @param node  ID of the node
     * @param weight    Weight of the node
     * @param nodeWeightsReversed   Map of all node weights to nodes array
     */
    public static void addNodeToWeightMap(int node, int weight, HashMap<Integer, int[]> nodeWeightsReversed ) {
        if (nodeWeightsReversed.containsKey(weight)) {
            // A node with the same weight already exists, so add to existing node array
            int[] nodeList = nodeWeightsReversed.get(weight);
            int[] newNodeList = Arrays.copyOf(nodeList, nodeList.length + 1);
            newNodeList[newNodeList.length - 1] = node;
            nodeWeightsReversed.put(weight, newNodeList);
            return;
        }

        // A node with the same weight does not exist, So add first node
        nodeWeightsReversed.put(weight, new int[] { node });
    }

    /**
     * Adds the parents of a specified node and adds the array of parents to a hashmap, using the parents as key, children as the value.
     * 
     * @param node       Value stored in parents hashmap
     * @param parentsMap    Map of nodes with the same parents
     * @param nodeParents   Map of all ids to node's parents
     */
    public static void addNodeToParentMap(int node, HashMap<int[], int[]> parentsMap, HashMap<Integer, int[]> nodeParents) {
        // Sort parent lists before adding nodes to hash map
        int[] parents = nodeParents.get(node);

        if (parents != null) {
            Arrays.sort(parents);
        }

        // When hash map is just created (empty), no need to check if any parent lists
        // already exist
        if (parentsMap.isEmpty()) {
            parentsMap.put(nodeParents.get(node), new int[] { node });
            return;
        }

        // Find a matching parent array
        for (Map.Entry<int[], int[]> entry : parentsMap.entrySet()) {
            int[] existingParents = entry.getKey();
            if ((parents != null && Arrays.equals(parents, existingParents))
                    || ((parents == null) && (parents == existingParents))) {
                // A node with the same parents already exists, so add to existing node array
                int[] nodeList = entry.getValue();
                int[] newNodeList = Arrays.copyOf(nodeList, nodeList.length + 1);
                newNodeList[newNodeList.length - 1] = node;
                parentsMap.put(existingParents, newNodeList);
                return;
            }
        }
    }

    /**
     * Adds the children of a specified node and adds the array of children to a hashmap, using the children as key, parents as the value.
     * 
     * @param node  Value stored in children hashmap
     * @param childrenMap   Map of nodes with the same children
     * @param nodeChildren  Map from all ids to node's children
     */
    public static void addNodeToChildrenMap(int node, HashMap<int[], int[]> childrenMap, HashMap<Integer, int[]> nodeChildren) {
        // Sort children lists before adding nodes to hash map
        int[] children = nodeChildren.get(node);

        if (children != null) {
            Arrays.sort(children);
        }

        // When hash map is just created (empty), no need to check if any children lists
        // already exist
        if (childrenMap.isEmpty()) {
            childrenMap.put(nodeChildren.get(node), new int[] { node });
            return;
        }

        // Find a matching child array
        for (Map.Entry<int[], int[]> entry : childrenMap.entrySet()) {
            int[] existingChildren = entry.getKey();
            if (((children != null) && Arrays.equals(children, existingChildren))
                    || ((children == null) && (children == existingChildren))) {
                // A node with the same children already exists, so add to existing node array
                int[] nodeList = entry.getValue();
                int[] newNodeList = Arrays.copyOf(nodeList, nodeList.length + 1);
                newNodeList[newNodeList.length - 1] = node;
                childrenMap.put(existingChildren, newNodeList);
                return;
            }
        }
    }

    /**
     * Adds the edge weights of a specified node and adds the array of edges to a hashmap, using the array of edges as key, nodes with the same set of edge weights as the value.
     * 
     * @param node                 Value stored in incoming or outgoing edges hashmap
     * @param edgesWeightMap   Map of nodes with the same incoming or outgoing edges
     * @param listOfEdges   {{from, to, weight}, {f, t, w}, ...} Each from/to is the id of the node
     */
    public static void addNodeToEdgesMap(int node, HashMap<int[], int[]> edgesWeightMap, int[][] listOfEdges, int col) {

        // Sort edge lists before adding nodes to hash map
        int[] weights = getEdgeWeights(listOfEdges, node, col);

        if (weights != null) {
            Arrays.sort(weights);
        }

        // When hash map is just created (empty), no need to check if any outgoing edge
        // lists already exist
        if (edgesWeightMap.isEmpty()) {
            edgesWeightMap.put(weights, new int[] { node });
            return;
        }

        // Find a matching child array
        for (Map.Entry<int[], int[]> entry : edgesWeightMap.entrySet()) {
            int[] existingWeights = entry.getKey();
            if (((weights != null) && Arrays.equals(weights, existingWeights))
                    || ((weights == null) && (weights == existingWeights))) {
                // A node with the same children already exists, so add to existing node array
                int[] nodeList = entry.getValue();
                int[] newNodeList = Arrays.copyOf(nodeList, nodeList.length + 1);
                newNodeList[newNodeList.length - 1] = node;
                edgesWeightMap.put(existingWeights, newNodeList);
                return;
            }
        }
    }

    /**
     * Gets the weights of edges to a node.
     * 
     * @param listOfEdges   {{from, to, weight}, {f, t, w}, ...} Each from/to is the id of the node
     * @param node  Id of node to find edges to
     * @param col Incoming edges = 1, Outgoing edges = 2
     */
    private static int[] getEdgeWeights(int[][] listOfEdges, int node, int col) {
        int[] weights = new int[1];

        // Iterate through all rows containing edges
        for (int i = 0; i < listOfEdges.length; i++) {
            // If the 'to' node is the same as the specified node, store the weight
            if (listOfEdges[i][col] == node) {
                if (i == 0) {
                    // Don't increase size for first iteration
                    weights[weights.length - 1] = listOfEdges[i][2];
                    continue;
                }
                weights = Arrays.copyOf(weights, weights.length + 1);
                weights[weights.length - 1] = listOfEdges[i][2];
            }
        }
        return weights;
    }
}