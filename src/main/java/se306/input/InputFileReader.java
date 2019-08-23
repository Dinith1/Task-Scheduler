package se306.input;

import se306.output.OutputFileGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFileReader {
    public static int NUM_NODES = 7; // Testing with Nodes_7 (change later to read number of nodes from GraphViz
                                     // graph generator)
    public static int NUM_EDGES = 6; // Testing with Nodes_7 (change later to read number of edges from GraphViz
                                     // graph generator)

    // public static int[] listOfAvailableNodes = new int[NUM_NODES]; // Each int is
    // the id (not the name) of the node
    // (DON"T EVEN NEED THIS AS CAN STORE TOTAL NUMBER OF
    // NODES INSTEAD -> KNOW THAT NODES WILL BE 0 to n-1)

    public static HashMap<Integer, String> nodeNames = new HashMap<Integer, String>(); // Map from id to name of node

    public static HashMap<String, Integer> nodeNamesReverse = new HashMap<String, Integer>(); // To find id for given
                                                                                              // node name

    public static HashMap<Integer, Integer> nodeWeights = new HashMap<Integer, Integer>(); // Map from id to weight of
                                                                                           // node -> MIGHT NEED TO
                                                                                           // CHANGE WEIGHT TO DOUBLE

    public static HashMap<Integer, int[]> mapOfNodeWeights = new HashMap<Integer, int[]>(); // Map from weight of node to list of node ids

    public static int[][] parents = new int[NUM_NODES][NUM_NODES]; // parents[0] stores parents of node with id = 0,
                                                                   // i.e. parents[0][1] = 1 means node with id = 1 is a
                                                                   // parent of node with id 0

    public static int[][] listOfEdges = new int[NUM_EDGES][3]; // {{from, to, weight}, {f, t, w}, ...} Each from/to is
                                                               // the id of the node

    private OutputFileGenerator outputFileGenerator = OutputFileGenerator.getInstance();

    /**
     * Takes in a dot file, and parses it into Nodes and Edges, which are added into
     * their respective ArrayLists
     *
     * @param isr
     * @throws IOException
     */
    public void readInput(InputStreamReader isr) throws IOException {

        int id = 0; // For node ids
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
                Integer idInt = new Integer(id);

                String name = findName(line);
                nodeNames.put(idInt, name);
                nodeNamesReverse.put(name, idInt);

                Integer weight = findWeight(line);
                nodeWeights.put(idInt, weight);

                // Create buckets of nodes with the same weight
                addIdToWeightMap(idInt, weight);

                outputFileGenerator.readLine(idInt);
                id++;

            } else { // Handle edges
                // Parent node
                String startNode = line.substring(0, line.indexOf("-")).replaceAll("\\s+", "");
                // Child node
                String endNode = line.substring(line.indexOf(">") + 1, line.indexOf("[")).replaceAll("\\s+", "");
                int weight = findWeight(line);

                listOfEdges[edgeNum][1] = nodeNamesReverse.get(endNode);

//                // If the current node has an identical node, change the parent of the node to the identical node (to prune the search space)
//                int currentNodeId = nodeNamesReverse.get(startNode);
//                Integer identicalNodeId = identicalTaskExists(currentNodeId, weight, startNode);
//                if (identicalNodeId != null){
//                    listOfEdges[edgeNum][0] = nodeNamesReverse.get(identicalNodeId);
//                    parents[nodeNamesReverse.get(endNode)][identicalNodeId] = 1;
//                } else {
                // Store parent
                listOfEdges[edgeNum][0] = nodeNamesReverse.get(startNode);
                parents[nodeNamesReverse.get(endNode)][nodeNamesReverse.get(startNode)] = 1;
//                }

                listOfEdges[edgeNum][2] = weight;

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
        if (mapOfNodeWeights.containsKey(weight)) {
            // A node with the same weight already exists
            int[] listOfNodes = mapOfNodeWeights.get(weight);
            int[] newListOfNodes = new int[(listOfNodes.length + 1)];
            System.arraycopy(listOfNodes, 0, newListOfNodes, 0, newListOfNodes.length);
            newListOfNodes[newListOfNodes.length - 1] = id;
            mapOfNodeWeights.put(weight, newListOfNodes);
        } else {
            // A node with the same weight does not exist
            int[] newListOfNodes = {id};
            mapOfNodeWeights.put(weight, newListOfNodes);
        }
    }

    /**
     * Checks if the task graph contains a task that is identical in every aspect including:
     * Same task weight, parents, children, incoming edge weights and outgoing edge weights.
     * <p>
     * If the tasks are identical, insert virtual edges between the identical tasks of a group
     * to prune the state space. The edges should be between the last scheduled node (chaining).
     */
    public void pruneIdenticalNodes() {
        //Find all nodes with the same weight [/]
        for (Map.Entry<Integer, int[]> entry : mapOfNodeWeights.entrySet()) {
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

        //Find all same weight nodes with the same parent
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
        int[] p = parents[id];

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
                int[] newListOfNodes = {id};
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

        //Find all same weight nodes with the same children
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
        int[] c = parents[id]; // Need to change this to children

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
                int[] newListOfNodes = {id};
                nodeChildren.put(child, newListOfNodes);
            }
        }
        return nodeChildren;
    }

    private void checkNodeIncomingEdges(int[] sameChildrenNodes) {
        if (sameChildrenNodes.length == 1) {
            return;
        }
        // Map from parent node to list of children node ids
        HashMap<Integer, int[]> mapOfNodeIncomingEdges = new HashMap<>();

        //Find all same weight nodes with the same children
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
        int[] edges = parents[id]; // TODO: Need to change this to incoming edges
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
                int[] newListOfNodes = {id};
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

        //Find all same weight nodes with the same children
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
        int[] edges = parents[id]; // TODO: Need to change this to outgoing edges
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
                int[] newListOfNodes = {id};
                nodeIncomingEdges.put(e, newListOfNodes);
            }
        }
        return nodeIncomingEdges;
    }

    private void chainIdenticalNodes(int[] sameOutgoingEdgeNodes) {
        // TODO: Set the parents and children of each identical node as each other
    }
}