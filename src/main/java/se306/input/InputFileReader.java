package se306.input;

import se306.output.OutputFileGenerator;
import se306.visualisation.backend.GraphParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFileReader {
    public static int NUM_NODES;
    public static int NUM_EDGES;

    // Values stored are node ids
    public static int[] nodeIds;

    // Map from id to weight of node
    public static HashMap<Integer, Integer> nodeWeights = new HashMap<Integer, Integer>();

    // Map from weight of node to list of node ids
    public static HashMap<Integer, int[]> nodeWeightsReversed = new HashMap<Integer, int[]>();

    // Map from id to node's parents
    public static HashMap<Integer, int[]> nodeParents = new HashMap<Integer, int[]>();

    // Map from id to node's children
    public static HashMap<Integer, int[]> nodeChildren = new HashMap<Integer, int[]>();

    // {{from, to, weight}, {f, t, w}, ...} Each from/to is the id of the node
    public static int[][] listOfEdges;

    private OutputFileGenerator outputFileGenerator = OutputFileGenerator.getInstance();

    public InputFileReader() {
        NUM_NODES = GraphParser.totalNodes;
        NUM_EDGES = GraphParser.totalEdges;
        nodeIds = new int[NUM_NODES];
        listOfEdges = new int[NUM_EDGES][3];
    }

    /**
     * Takes in a dot file, and parses it into Nodes and Edges, which are added into
     * their respective ArrayLists
     *
     * @param isr
     * @throws IOException
     */
    public void readInput(InputStreamReader isr) throws IOException {
        int nodeCount = 0; // for adding to nodeIds
        int edgeCount = 0; // for adding to listOfEdges

        BufferedReader buffRead = new BufferedReader(isr);

        // Skip first line of file
        String line = buffRead.readLine();
        outputFileGenerator.readLine(line);

        while ((line = buffRead.readLine()) != null) {
            String end = line.substring(0, 1);

            // Stop reading once it reaches end of file
            if (end.equalsIgnoreCase("}")) {
                break;
            }
            // If the line is not a line that includes a node or an edge
            Pattern p = Pattern.compile(".*\\[Weight=.*];");
            Matcher m = p.matcher(line);
            if (!m.matches()) {
                outputFileGenerator.readLine(line);
                continue;
            }

            // If "->" appears, it means it is an edge, otherwise it is a node.
            // Get the node identifier and weight then make a Node object
            if (line.indexOf("->") == -1) { // Handle nodes
                String node = findName(line);
                int nodeInt = Integer.parseInt(node);

                nodeIds[nodeCount] = nodeInt;
                nodeCount++;

                int weight = findWeight(line);
                nodeWeights.put(nodeInt, weight);

                // Create buckets of nodes with the same weight
                addIdToWeightMap(nodeInt, weight);

                outputFileGenerator.readLine(nodeInt);

            } else { // Handle edges
                // Parent node
                String parentNode = line.substring(0, line.indexOf("-")).replaceAll("\\s+", "");
                int parentNodeInt = Integer.parseInt(parentNode);
                // Child node
                String childNode = line.substring(line.indexOf(">") + 1, line.indexOf("[")).replaceAll("\\s+", "");
                int childNodeInt = Integer.parseInt(childNode);
                int weight = findWeight(line);

                listOfEdges[edgeCount][0] = Integer.parseInt(parentNode);
                listOfEdges[edgeCount][1] = Integer.parseInt(childNode);
                listOfEdges[edgeCount][2] = weight;

                // Add parent to hash map where key is child node ID, value is int[] of
                // parents
                addParent(childNodeInt, parentNodeInt);

                // Add parent to hash map where key is parent node ID, value is int[] of
                // children
                addChild(parentNodeInt, childNodeInt);

                outputFileGenerator.readLine(listOfEdges[edgeCount]);
                edgeCount++;
            }
        }

        buffRead.close();
    }

    /**
     * Takes in a line from the file and gets the weight.
     *
     * @param line
     * @return The weight
     */
    private int findWeight(String line) {
        String parts[] = line.split("Weight=");
        String weight = parts[1].replaceAll("\\D+", "");
        return Integer.parseInt(weight);
    }

    /**
     * Takes in a line from the file and gets the node name.
     *
     * @param line
     * @return The name
     */
    private String findName(String line) {
        line = line.replaceAll("\\s", "");
        int iEnd = line.indexOf("[");
        return line.substring(0, iEnd);
    }

    /**
     * Adds nodes to a hashmap, where the key is the weight, and the value is an
     * array of all the node (IDs) with that weight.
     * 
     * @param id     ID of the node
     * @param weight Weight of the node
     */
    private void addIdToWeightMap(int id, int weight) {
        if (nodeWeightsReversed.containsKey(weight)) {
            // A node with the same weight already exists, so add to existing node array
            int[] nodeList = nodeWeightsReversed.get(weight);
            int[] newNodeList = Arrays.copyOf(nodeList, nodeList.length + 1);
            newNodeList[newNodeList.length - 1] = id;
            nodeWeightsReversed.put(weight, newNodeList);
            return;
        }

        // A node with the same weight does not exist, So add first node
        nodeWeightsReversed.put(weight, new int[] { id });
    }

    /**
     * Checks if the task graph contains a task that is identical in every aspect
     * including: Same task weight, parents, children, incoming edge weights and
     * outgoing edge weights.
     * <p>
     * If the tasks are identical, insert virtual edges between the identical tasks
     * of a group to prune the state space. The edges should be between the last
     * scheduled node (chaining).
     * <p>
     * This method will daisy chain through other methods, ending in the nodeParents
     * and nodeChildren fields of InputFileReader being modified to reflect the
     * pruned graph.
     */
    public void pruneIdenticalNodes() {
        // Find all nodes with the same weight
        for (Map.Entry<Integer, int[]> entry : nodeWeightsReversed.entrySet()) {
            int[] sameWeightNodes = entry.getValue();
            // Check if there's at least two nodes with the same weight
            if (sameWeightNodes.length > 1) {
                // Check if nodes have the same parents
                checkNodeParents(sameWeightNodes);
            } else {
                continue;
            }
        }
    }

    /**
     * Checks if an array of nodes (which have the same weights) all have a common
     * parent.
     * 
     * @param sameWeightNodes Array of nodes to check.
     */
    private void checkNodeParents(int[] sameWeightNodes) {
        // Map from list of parent nodes to list of node ids
        HashMap<int[], int[]> mapOfNodeParents = new HashMap<>();

        // Find all (same weight) nodes with a parent in common
        for (int node : sameWeightNodes) {
            addNodeToParentMap(node, mapOfNodeParents);
        }

        for (Map.Entry<int[], int[]> entry : mapOfNodeParents.entrySet()) {
            int[] sameParentNodes = entry.getValue();

            // Check if there's at least two (same weight) nodes with the same parent
            if (sameParentNodes.length > 1) {
                // Check if nodes have the same children
                checkNodeChildren(sameParentNodes);
            }
        }
    }

    /**
     * 
     * @param node       Value stored in parents hashmap
     * @param parentsMap Key stored in parents hashmap
     */
    private void addNodeToParentMap(int node, HashMap<int[], int[]> parentsMap) {
        // When hash map is just created (empty), no need to check if any parent lists
        // already exist
        if (parentsMap.isEmpty()) {
            parentsMap.put(nodeParents.get(node), new int[] { node });
            return;
        }

        // If there are existing parent lists, sort parent lists before adding nodes to
        // hash map
        int[] parents = nodeParents.get(node);
        Arrays.sort(parents);

        // Find a matching parent array
        for (Map.Entry<int[], int[]> entry : parentsMap.entrySet()) {
            int[] existingParents = entry.getKey();
            if (Arrays.equals(parents, existingParents)) {
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
     * 
     * @param sameParentNodes
     */
    private void checkNodeChildren(int[] sameParentNodes) {
        // Map from child node to list of node ids
        HashMap<int[], int[]> mapOfNodeChildren = new HashMap<>();

        // Find all same weight nodes with the same children
        for (int node : sameParentNodes) {
            addNodeToChildrenMap(node, mapOfNodeChildren);
        }
        for (Map.Entry<int[], int[]> entry : mapOfNodeChildren.entrySet()) {
            int[] sameChildrenNodes = entry.getValue();

            // Check if value matches with given value
            if (sameChildrenNodes.length > 1) {
                // Check incoming edge weights
                checkNodeIncomingEdges(sameChildrenNodes);
            }
        }
    }

    /**
     * 
     * @param node        Value stored in children hashmap
     * @param childrenMap Key stored in children hashmap
     */
    private void addNodeToChildrenMap(int node, HashMap<int[], int[]> childrenMap) {
        // When hash map is just created (empty), no need to check if any children lists
        // already exist
        if (childrenMap.isEmpty()) {
            childrenMap.put(nodeChildren.get(node), new int[] { node });
            return;
        }

        // If there are existing children lists, sort children lists before adding nodes
        // to hash map
        int[] children = childrenMap.get(node);
        Arrays.sort(children);

        // Find a matching child array
        for (Map.Entry<int[], int[]> entry : childrenMap.entrySet()) {
            int[] existingChildren = entry.getKey();
            if (Arrays.equals(children, existingChildren)) {
                // A node with the same children already exists, so add to existing node array
                int[] nodeList = entry.getValue();
                int[] newNodeList = Arrays.copyOf(nodeList, nodeList.length + 1);
                newNodeList[newNodeList.length - 1] = node;
                childrenMap.put(existingChildren, newNodeList);
                return;
            }
        }
    }

    private int[] getColumnFromArray(int[][] array, int col) {
        int[] columnArray = new int[array[0].length];
        int count = 0;
        for (int j = 0; j < array[0].length; j++) {
            for (int i = 0; i < array.length; i++) {
                if (j == col) {
                    columnArray[count] = array[i][j];
                    count++;
                }
            }
        }
        return columnArray;
    }

    /**
     * 
     * @param sameChildrenNodes
     */
    private void checkNodeIncomingEdges(int[] sameChildrenNodes) {
        // Map from parent node to list of children node ids
        HashMap<Integer, int[]> mapOfNodeIncomingEdges = new HashMap<>();

        // Find all same weight nodes with the same children
        for (int i : sameChildrenNodes) {
            addNodeToIncomingEdgesMap(i, mapOfNodeIncomingEdges);
        }

        for (Map.Entry<Integer, int[]> entry : mapOfNodeIncomingEdges.entrySet()) {
            int[] sameIncomingEdgeNodes = entry.getValue();

            // Check if value matches with given value
            if (sameIncomingEdgeNodes.length > 1) {
                // Check outgoing edge weights
                checkNodeOutgoingEdges(sameIncomingEdgeNodes);
            }
        }
    }

    private void addNodeToIncomingEdgesMap(int id, HashMap<Integer, int[]> nodeIncomingEdgesMap) {
        int[] edges = getColumnFromArray(listOfEdges, id);

        for (int e : edges) {
            if (nodeIncomingEdgesMap.containsKey(e)) {
                // A node with the same weight already exists
                int[] listOfNodes = nodeIncomingEdgesMap.get(e);
                int[] newListOfNodes = new int[(listOfNodes.length + 1)];
                System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
                newListOfNodes[newListOfNodes.length - 1] = id;
                nodeIncomingEdgesMap.put(e, newListOfNodes);
            } else {
                // A node with the same weight does not exist
                int[] newListOfNodes = { id };
                nodeIncomingEdgesMap.put(e, newListOfNodes);
            }
        }
    }

    private void checkNodeOutgoingEdges(int[] sameIncomingNodes) {
        if (sameIncomingNodes.length == 1) {
            return;
        }
        // Map from parent node to list of children node ids
        HashMap<Integer, int[]> mapOfNodeOutgoingEdges = new HashMap<>();

        // Find all same weight nodes with the same children
        for (int i : sameIncomingNodes) {
            addNodeToOutgoingEdgeMap(i, mapOfNodeOutgoingEdges);
        }
        for (Map.Entry<Integer, int[]> entry : mapOfNodeOutgoingEdges.entrySet()) {
            int[] identicalNodes = entry.getValue();
            // Check if value matches with given value
            if (identicalNodes.length > 1) {
                // If this point is reached, the nodes are identical
                // Chain the identical nodes
                chainIdenticalNodes(identicalNodes);
            }
        }
    }

    private void addNodeToOutgoingEdgeMap(int id, HashMap<Integer, int[]> nodeOutgoingEdgesMap) {
        int[] edges = listOfEdges[id];

        for (int e : edges) {
            if (nodeOutgoingEdgesMap.containsKey(e)) {
                // A node with the same weight already exists
                int[] listOfNodes = nodeOutgoingEdgesMap.get(e);
                int[] newListOfNodes = new int[(listOfNodes.length + 1)];
                System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
                newListOfNodes[newListOfNodes.length - 1] = id;
                nodeOutgoingEdgesMap.put(e, newListOfNodes);
            } else {
                // A node with the same weight does not exist
                int[] newListOfNodes = { id };
                nodeOutgoingEdgesMap.put(e, newListOfNodes);
            }
        }
    }

    private void chainIdenticalNodes(int[] identicalNodes) {
        // If reached, this means the nodes are identical
        // Set the parents and children of each identical node as each other for the
        // body nodes
        // The body excludes the head and tail of identical nodes (hence -2)
        // Head = first element of array
        // Tail = last element of array

        int length = identicalNodes.length;

        for (int i = 0; i < length; i++) {
            if (i == 0) {
                // Delete all children of head
                nodeChildren.remove(identicalNodes[0]);
                // Set second node in array as the only child
                addChild(identicalNodes[0], identicalNodes[1]);

            } else if (i == (length - 1)) {
                // Delete all parents of tail
                nodeParents.remove(identicalNodes[0]);
                // Set parent of tail to be second to last element of array
                addParent(identicalNodes[length - 2], identicalNodes[length - 1]);

            } else {
                // Delete all parents
                nodeParents.remove(identicalNodes[i]);
                // Set parent to identical[i-1] (chain to previous identical node)
                addParent(identicalNodes[i], identicalNodes[i - 1]);
                // Delete all children
                nodeChildren.remove(identicalNodes[i]);
                // Set child to identical[i+1] (chain to next identical node)
                addChild(identicalNodes[i], identicalNodes[i + 1]);
            }
        }
    }

    /**
     * Takes a node, and adds an immediate parent, putting it into the nodeParents
     * HashMap
     * 
     * @param child
     * @param parent
     */
    private void addParent(int child, int parent) {
        if (nodeParents.containsKey(child)) {
            // Node already has an existing parent, so add to existing parent array
            int[] parentList = nodeParents.get(child);
            int[] newParentList = Arrays.copyOf(parentList, parentList.length + 1);
            newParentList[newParentList.length - 1] = parent;
            nodeParents.put(child, newParentList);
            return;
        }

        // Node does not have a parent yet, so add first parent
        nodeParents.put(child, new int[] { parent });
    }

    /**
     * Takes a node, and adds an immediate child, putting it into the nodeChildren
     * HashMap
     * 
     * @param parent
     * @param child
     */
    private void addChild(int parent, int child) {
        if (nodeChildren.containsKey(parent)) {
            // Node already has an existing child, so add to existing child array
            int[] childList = nodeChildren.get(parent);
            int[] newChildList = Arrays.copyOf(childList, childList.length + 1);
            newChildList[newChildList.length - 1] = child;
            nodeChildren.put(parent, newChildList);
            return;
        }

        // Node does not have a child yet, so add first child
        nodeChildren.put(parent, new int[] { child });
    }
}
