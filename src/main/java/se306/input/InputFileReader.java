package se306.input;

import se306.output.OutputFileGenerator;
import se306.util.HashMapGenerator;
import se306.util.IdenticalNodes;
import se306.visualisation.backend.GraphParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Singleton class for reading the input file and generating the required
 * datastructures.
 */
public class InputFileReader {
    private static InputFileReader inputFileReader = null;

    private int NUM_NODES;
    private int NUM_EDGES;

    // Values stored are node ids
    private int[] nodeIds;

    // Map from id to weight of node
    private HashMap<Integer, Integer> nodeWeights = new HashMap<Integer, Integer>();

    // Map from weight of node to list of node ids
    private HashMap<Integer, int[]> nodeWeightsReversed = new HashMap<Integer, int[]>();

    // Map from id to node's parents
    private HashMap<Integer, int[]> nodeParents = new HashMap<Integer, int[]>();

    // Map from id to node's children
    private HashMap<Integer, int[]> nodeChildren = new HashMap<Integer, int[]>();

    // {{from, to, weight}, {f, t, w}, ...} Each from/to is the id of the node
    private int[][] listOfEdges;

    private OutputFileGenerator outputFileGenerator = OutputFileGenerator.getInstance();

    private InputFileReader() {
        NUM_NODES = GraphParser.totalNodes;
        NUM_EDGES = GraphParser.totalEdges;
        nodeIds = new int[NUM_NODES];
        listOfEdges = new int[NUM_EDGES][3];
    }

    public static InputFileReader getInstance() {
        return (inputFileReader == null) ? (inputFileReader = new InputFileReader()) : inputFileReader;
    }

    public static InputFileReader getInstance(int numNodes, int numEdges) {
        return (inputFileReader == null) ? (inputFileReader = new InputFileReader(numNodes, numEdges))
                : inputFileReader;
    }

    /**
     * Overload constructor for testing purposes
     * 
     * @param numNodes
     * @param numEdges
     */
    private InputFileReader(int numNodes, int numEdges) {
        NUM_NODES = numNodes;
        NUM_EDGES = numEdges;
        nodeIds = new int[NUM_NODES];
        listOfEdges = new int[NUM_EDGES][3];
    }

    /**
     * For testing purposes
     */
    public void clearInputFileReader() {
        inputFileReader = null;
    }

    public int[] getNodeIds() {
        return this.nodeIds;
    }

    public HashMap<Integer, Integer> getNodeWeights() {
        return this.nodeWeights;
    }

    public HashMap<Integer, int[]> getNodeWeightsReversed() {
        return this.nodeWeightsReversed;
    }

    public HashMap<Integer, int[]> getNodeParents() {
        return this.nodeParents;
    }

    public HashMap<Integer, int[]> getNodeChildren() {
        return this.nodeChildren;
    }

    public int[][] getListOfEdges() {
        return this.listOfEdges;
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

                // Use hashmap util to instantiate buckets of nodes with the same weight

                HashMapGenerator.addNodeToWeightMap(nodeInt, weight, nodeWeightsReversed);

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
     * @param line String to parse to find the weight
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
     * @param line String to parse to find the name
     * @return The name
     */
    private String findName(String line) {
        line = line.replaceAll("\\s", "");
        int iEnd = line.indexOf("[");
        return line.substring(0, iEnd);
    }

    /**
     * <<<<<<< HEAD Adds nodes to a hashmap, where the key is the weight, and the
     * value is an array of all the node (IDs) with that weight.
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
=======
>>>>>>> feature/refactor-pruning
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
        IdenticalNodes identicalNodes = IdenticalNodes.getInstance();
        // Check for independent node graphs
        if (listOfEdges.length == 0) {
            return;
        } else {
            // Find all nodes with the same weight
            for (Map.Entry<Integer, int[]> entry : nodeWeightsReversed.entrySet()) {
                int[] sameWeightNodes = entry.getValue();
                // Check if there's at least two nodes with the same weight
                if (sameWeightNodes.length > 1) {
                    // Check if nodes have the same parents
                    int[] idenNodes = identicalNodes.getIdenticalNodes(sameWeightNodes);
                    if ((idenNodes != null) && (idenNodes.length > 1)) {
                        chainIdenticalNodes(idenNodes);
                    }
                }
            }
        }

    }

    /**
     * @param identicalNodes
     */
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
     * <<<<<<< HEAD HashMap
     *
     * ======= HashMap.
     * 
     * >>>>>>> feature/refactor-pruning
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
     * <<<<<<< HEAD HashMap
     *
     * ======= HashMap.
     * 
     * >>>>>>> feature/refactor-pruning
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