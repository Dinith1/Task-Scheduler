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

import org.graphstream.stream.GraphParseException;

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

    // public static int[][] parents; // parents[0] stores parents of node with id =
    // 0,
    // i.e. parents[0][1] = 1 means node with id = 1 is a
    // parent of node with id 0

    private OutputFileGenerator outputFileGenerator = OutputFileGenerator.getInstance();

    public InputFileReader() {
        NUM_NODES = GraphParser.totalNodes;
        NUM_EDGES = GraphParser.totalEdges;
        nodeIds = new int[NUM_NODES];
        // parents = new int[NUM_NODES][NUM_NODES];
        // parentsReverse = new int[NUM_NODES][NUM_NODES];
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

        // int id = 0; // For node ids
        int edgeNum = 0; // for adding to listOfEdges

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

                listOfEdges[edgeNum][0] = Integer.parseInt(parentNode);
                listOfEdges[edgeNum][1] = Integer.parseInt(childNode);
                listOfEdges[edgeNum][2] = weight;

                // // If the current node has an identical node, change the parent of the node
                // to the identical node (to prune the search space)
                // int currentNodeId = nodeNamesReverse.get(parentNode);
                // Integer identicalNodeId = identicalTaskExists(currentNodeId, weight,
                // parentNode);
                // if (identicalNodeId != null){
                // listOfEdges[edgeNum][0] = nodeNamesReverse.get(identicalNodeId);
                // parents[nodeNamesReverse.get(childNode)][identicalNodeId] = 1;
                // } else {
                // Store parent

                // TODO: Add parent to hash map where key is child node ID, value is int[] of
                // parents
                addParents(childNodeInt, parentNodeInt);

                // TODO: Add parent to hash map where key is parent node ID, value is int[] of
                // children
                addChildren(parentNodeInt, childNodeInt);

                outputFileGenerator.readLine(listOfEdges[edgeNum]);
                edgeNum++;
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
            System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
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

        // Map from parent node to list of children node ids
        HashMap<Integer, int[]> mapOfNodeParents = new HashMap<>();

        // Find all same weight nodes with the same parent
        for (int i : sameWeightNodes) {
            mapOfNodeParents = addIdToParentMap(i, mapOfNodeParents);
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

    private HashMap<Integer, int[]> addIdToParentMap(int id, HashMap<Integer, int[]> nodeParents) {
        int[] p = nodeParents.get(id);

        for (int parent : p) {
            if (nodeParents.containsKey(parent)) {
                // A node with the same weight already exists
                int[] listOfNodes = nodeParents.get(parent);
                int[] newListOfNodes = new int[(listOfNodes.length + 1)];
                System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
                newListOfNodes[newListOfNodes.length - 1] = id;
                nodeParents.put(parent, newListOfNodes);

            } else {
                // A node with the same weight does not exist
                int[] newListOfNodes = { id };
                nodeParents.put(parent, newListOfNodes);
            }
        }
        return nodeParents;
    }

    private void checkNodeChildren(int[] sameParentNodes) {
        if (sameParentNodes.length == 1) {
            return;
        }
        // Map from parent node to list of children node ids
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
            int[] sameOutgoingEdgeNodes = entry.getValue();
            // Check if value matches with given value
            if (sameOutgoingEdgeNodes.length == 1) {
                continue;
            } else {
                // If this point is reached, the nodes are identical
                // Chain the identical nodes
                chainIdenticalNodes(sameOutgoingEdgeNodes);
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

    private void chainIdenticalNodes(int[] sameOutgoingEdgeNodes) {
        // If reached, this means the nodes are identical
        // Set the parents and children of each identical node as each other
        int[] intermediateNodes = new int[sameOutgoingEdgeNodes.length - 2];

        // Iterate through all identical nodes and create an intermediate chain of nodes
        for (int i = 1; i < sameOutgoingEdgeNodes.length - 1; i++) {
            createIntermediateChain(i);
        }

        // Set the first identical node to keep its parent and set child to head of the
        // intermediate chain
        setChild(sameOutgoingEdgeNodes[0], intermediateNodes[0], true);
        setParent(sameOutgoingEdgeNodes[sameOutgoingEdgeNodes.length - 1],
                intermediateNodes[intermediateNodes.length - 1], true);
    }

    private void setChild(int nodeId, int childId, boolean removeAll) {
        // Update parents array
        if (removeAll) {
            removeChildrenNodes(parents, nodeId);
        } else {
            parents[childId][nodeId] = 1;
        }

        // TODO: Update edges e.g. remove edge between old children and create edge
        // between new children

    }

    private void setParent(int nodeId, int parentId, boolean removeAll) {
        // Update parents array
        if (removeAll) {
            // Set its parent to head of the intermediate chain
            parents[nodeId] = new int[NUM_NODES];
        } else {
            parents[nodeId][parentId] = 1;
        }

        // TODO: Update edges e.g. remove edge between old parents and create edge
        // between new parents
        // TODO: Double check id and index passing for all methods
    }

    private void createIntermediateChain(int id) {
        // Remove all parents and children of the intermediate nodes (nodes that are not
        // the head or tail of the chain)
        parents[id] = new int[NUM_NODES];
        int[] childrenNodes = removeChildrenNodes(parents, id);

        // Must also remove edges to and from children
        // TODO: Update edges
    }

    private int[] removeChildrenNodes(int[][] array, int col) {
        int[] columnArray = new int[array[0].length];
        int count = 0;
        for (int j = 0; j < array[0].length; j++) {
            for (int i = 0; i < array.length; i++) {
                if (j == col) {
                    columnArray[count] = 0;
                    count++;
                }
            }
        }
        return columnArray;
    }

    /**
     * Takes a node, and adds an immediate parent, putting
     * it into the nodeParents HashMap
     * 
     * @param child
     * @param parent
     */
    private void addParents(int child, int parent) {
        if (nodeParents.containsKey(child)) {
            // Node already has an existing parent
            int[] listOfNodes = nodeParents.get(child);
            int[] newListOfNodes = new int[(listOfNodes.length + 1)];
            System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
            newListOfNodes[newListOfNodes.length - 1] = parent;
            nodeParents.put(child, newListOfNodes);
            return;
        }

        // Node does not have a parent yet
        int[] newListOfNodes = { parent };
        nodeParents.put(child, newListOfNodes);
    }

    /**
     * Takes a node, and adds an immediate child, putting
     * it into the nodeChildren HashMap
     * 
     * @param parent
     * @param child
     */
    private void addChildren(int parent, int child) {
        if (nodeChildren.containsKey(parent)) {
            // Node already has an existing child
            int[] listOfNodes = nodeChildren.get(parent);
            int[] newListOfNodes = new int[(listOfNodes.length + 1)];
            System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
            newListOfNodes[newListOfNodes.length - 1] = child;
            nodeChildren.put(parent, newListOfNodes);
            return;
        }

        // Node does not have a child yet
        int[] newListOfNodes = { child };
        nodeParents.put(parent, newListOfNodes);
    }
}
