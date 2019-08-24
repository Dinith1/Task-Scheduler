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

    private void addIdToWeightMap(int id, int weight) {
        if (nodeWeightsReversed.containsKey(weight)) {
            // A node with the same weight already exists
            int[] listOfNodes = nodeWeightsReversed.get(weight);
            int[] newListOfNodes = new int[(listOfNodes.length + 1)];
            System.arraycopy(listOfNodes, 0, newListOfNodes, 0, listOfNodes.length);
            newListOfNodes[newListOfNodes.length - 1] = id;
            nodeWeightsReversed.put(weight, newListOfNodes);
        } else {
            // A node with the same weight does not exist
            int[] newListOfNodes = { id };
            nodeWeightsReversed.put(weight, newListOfNodes);
        }
    }

    /**
     * Checks if the task graph contains a task that is identical in every aspect
     * including: Same task weight, parents, children, incoming edge weights and
     * outgoing edge weights.
     * <p>
     * If the tasks are identical, insert virtual edges between the identical tasks
     * of a group to prune the state space. The edges should be between the last
     * scheduled node (chaining).
     */
    public void pruneIdenticalNodes() {
        // Find all nodes with the same weight
        for (Map.Entry<Integer, int[]> entry : nodeWeightsReversed.entrySet()) {
            int[] sameWeightNodes = entry.getValue();
            // Check if value matches with given value
            if (sameWeightNodes.length == 1) {
                continue;
            } else {
                // Check if nodes have the same parents
                checkNodeParents(sameWeightNodes);
            }
        }
    }

    private void checkNodeParents(int[] sameWeightNodes) {
        if (sameWeightNodes.length == 1) {
            return;
        }

        // Map from parent node to list of node ids
        HashMap<Integer, int[]> mapOfNodeParents = new HashMap<>();

        // Find all same weight nodes with the same parent
        for (int node : sameWeightNodes) {
            addIdToParentMap(node, mapOfNodeParents);
        }

        for (Map.Entry<Integer, int[]> entry : mapOfNodeParents.entrySet()) {
            int[] sameParentNodes = entry.getValue();

            // Check if value matches with given value
            if (sameParentNodes.length == 1) {
                continue;
            } else {
                // Check if nodes have the same children
                checkNodeChildren(sameParentNodes);
            }
        }
    }

    private void addIdToParentMap(int node, HashMap<Integer, int[]> parents) {
        // If node has no parents
        if (!nodeParents.containsKey(node)) {
            return;
        }

        int[] nParents = nodeParents.get(node);

        int[] p = parents.get(node);

        for (int parent : p) {
            if (parents.containsKey(parent)) {
                // A node with the same weight already exists
                int[] listOfNodes = nodeParents.get(parent);
                int[] newListOfNodes = new int[(listOfNodes.length + 1)];
                System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
                newListOfNodes[newListOfNodes.length - 1] = node;
                parents.put(parent, newListOfNodes);

            } else {
                // A node with the same weight does not exist
                int[] newListOfNodes = { node };
                parents.put(parent, newListOfNodes);
            }
        }
    }

    private void checkNodeChildren(int[] sameParentNodes) {
        if (sameParentNodes.length == 1) {
            return;
        }
        // Map from child node to list of node ids
        HashMap<Integer, int[]> mapOfNodeChildren = new HashMap<>();

        // Find all same weight nodes with the same children
        for (int i : sameParentNodes) {
            mapOfNodeChildren = addIdToChildrenMap(i, mapOfNodeChildren);
        }
        for (Map.Entry<Integer, int[]> entry : mapOfNodeChildren.entrySet()) {
            int[] sameChildrenNodes = entry.getValue();

            // Check if value matches with given value
            if (sameChildrenNodes.length == 1) {
                continue;
            } else {
                // Check incoming edge weights
                checkNodeIncomingEdges(sameChildrenNodes);
            }
        }
    }

    private HashMap<Integer, int[]> addIdToChildrenMap(int id, HashMap<Integer, int[]> nodeChildren) {
        int[] c = nodeChildren.get(id);

        for (int child : c) {
            if (nodeChildren.containsKey(child)) {
                // A node with the same weight already exists
                int[] listOfNodes = nodeChildren.get(child);
                int[] newListOfNodes = new int[(listOfNodes.length + 1)];
                System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
                newListOfNodes[newListOfNodes.length - 1] = id;
                nodeChildren.put(child, newListOfNodes);

            } else {
                // A node with the same weight does not exist
                int[] newListOfNodes = { id };
                nodeChildren.put(child, newListOfNodes);
            }
        }
        return nodeChildren;
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

    private void checkNodeIncomingEdges(int[] sameChildrenNodes) {
        if (sameChildrenNodes.length == 1) {
            return;
        }
        // Map from parent node to list of children node ids
        HashMap<Integer, int[]> mapOfNodeIncomingEdges = new HashMap<>();

        // Find all same weight nodes with the same children
        for (int i : sameChildrenNodes) {
            mapOfNodeIncomingEdges = addIdToIncomingEdgeMap(i, mapOfNodeIncomingEdges);
        }

        for (Map.Entry<Integer, int[]> entry : mapOfNodeIncomingEdges.entrySet()) {
            int[] sameIncomingEdgeNodes = entry.getValue();

            // Check if value matches with given value
            if (sameIncomingEdgeNodes.length == 1) {
                continue;
            } else {
                // Check outgoing edge weights
                checkNodeOutgoingEdges(sameIncomingEdgeNodes);
            }
        }
    }

    private HashMap<Integer, int[]> addIdToIncomingEdgeMap(int id, HashMap<Integer, int[]> nodeIncomingEdges) {
        int[] edges = getColumnFromArray(listOfEdges, id);

        for (int e : edges) {
            if (nodeIncomingEdges.containsKey(e)) {
                // A node with the same weight already exists
                int[] listOfNodes = nodeIncomingEdges.get(e);
                int[] newListOfNodes = new int[(listOfNodes.length + 1)];
                System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
                newListOfNodes[newListOfNodes.length - 1] = id;
                nodeIncomingEdges.put(e, newListOfNodes);
            } else {
                // A node with the same weight does not exist
                int[] newListOfNodes = { id };
                nodeIncomingEdges.put(e, newListOfNodes);
            }
        }
        return nodeIncomingEdges;
    }

    private void checkNodeOutgoingEdges(int[] sameIncomingNodes) {
        if (sameIncomingNodes.length == 1) {
            return;
        }
        // Map from parent node to list of children node ids
        HashMap<Integer, int[]> mapOfNodeOutgoingEdges = new HashMap<>();

        // Find all same weight nodes with the same children
        for (int i : sameIncomingNodes) {
            mapOfNodeOutgoingEdges = addIdToOutgoingEdgeMap(i, mapOfNodeOutgoingEdges);
        }
        for (Map.Entry<Integer, int[]> entry : mapOfNodeOutgoingEdges.entrySet()) {
            int[] identicalNodes = entry.getValue();
            // Check if value matches with given value
            if (identicalNodes.length == 1) {
                continue;
            } else {
                // If this point is reached, the nodes are identical
                // Chain the identical nodes
                chainIdenticalNodes(identicalNodes);
            }
        }
    }

    private HashMap<Integer, int[]> addIdToOutgoingEdgeMap(int id, HashMap<Integer, int[]> nodeIncomingEdges) {
        int[] edges = listOfEdges[id];

        for (int e : edges) {
            if (nodeIncomingEdges.containsKey(e)) {
                // A node with the same weight already exists
                int[] listOfNodes = nodeIncomingEdges.get(e);
                int[] newListOfNodes = new int[(listOfNodes.length + 1)];
                System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
                newListOfNodes[newListOfNodes.length - 1] = id;
                nodeIncomingEdges.put(e, newListOfNodes);
            } else {
                // A node with the same weight does not exist
                int[] newListOfNodes = { id };
                nodeIncomingEdges.put(e, newListOfNodes);
            }
        }
        return nodeIncomingEdges;
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
