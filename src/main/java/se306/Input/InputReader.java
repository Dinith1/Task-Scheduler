package se306.Input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class InputReader {

    private Queue<Node> listOfNodes = new ArrayDeque<>();
    private List<Edge> listOfEdges = new ArrayList<>();
    private List<Node> listOfSortedNodes = new ArrayList<>();

    /**
     * Takes in a dot file, and parses it into Nodes and Edges, which are added into
     * their respective ArrayLists
     * 
     * @param file
     * @throws IOException
     */
    public void readInput(File file) throws IOException {

        BufferedReader buffRead = new BufferedReader(new FileReader(file));

        // Skip first line of file
        buffRead.readLine();

        String line;

        while ((line = buffRead.readLine()) != null) {
            String end = line.substring(0, 1);

            // Stop reading once it reaches end of file
            if (end.equalsIgnoreCase("}")) {
                break;
            }

            // Only start reading if first seen character of a line is an number
            line = line.replaceAll("\\s", "");
            String firstDigit = line.substring(0, 1);

            if (firstDigit.matches("\\d")) {

                // If "->" appears, it means it is an edge, otherwise it is a node.
                // Get the node identifier and weight then make a Node object
                if (line.indexOf("->") == -1) { // Handle nodes
                    int currentWeight = findEdgeWeight(line);
                    String nodeIdentifier = parseNodeIdentifier(line);
                    makeNode(currentWeight, nodeIdentifier);

                } else { // Handle edges
                    // Get start node of edge
                    String startNode = line.substring(0, line.indexOf("-"));

                    // Get end node of edge
                    String endNode = line.substring(line.indexOf(">") + 1, line.indexOf("["));

                    int edgeWeight = findEdgeWeight(line);

                    makeEdge(startNode, endNode, edgeWeight);
                }

            }
        }

        buffRead.close();

        Node node = (listOfNodes.size() > 0) ? listOfNodes.peek() : null;

        addToSchedule(node);

        for (Node n : listOfSortedNodes) {
            System.out.println(n.getNodeIdentifier());
        }
    }

    /**
     * Takes in parameters of the node weight and node identifier and creates a new
     * node object, then add it to a list of nodes
     * 
     * @param weight
     * @param nodeIdentifier
     */
    private void makeNode(int weight, String nodeIdentifier) {
        Node currentNode = new Node(weight, nodeIdentifier);
        listOfNodes.add(currentNode);
        System.out.println("Added node " + currentNode.getNodeIdentifier() + " with weight = "
                + currentNode.getNodeWeight() + " to node list");
    }

    /**
     * Takes in parameters of the start node, end node and edge weight and creates a
     * new edge object, then add it to a list of edges
     * 
     * @param startNodeId
     * @param endNodeId
     * @param edgeWeight
     */
    private void makeEdge(String startNodeId, String endNodeId, int edgeWeight) {
        Node startNode = null;
        Node endNode = null;

        // Loop through nodes to find starting and ending nodes of the edge (assuming the two nodes already exist as objects)
        for (Node tempNode : listOfNodes) {
            String tempNodeId = tempNode.getNodeIdentifier();
            startNode = (startNodeId.equals(tempNodeId)) ? tempNode : startNode;
            endNode = (endNodeId.equals(tempNodeId)) ? tempNode : endNode;
        }

        // Store parent node in child
        endNode.addParent(startNode);

        Edge currentEdge = new Edge(startNode, endNode, edgeWeight);
        startNode.addOutGoingEdges(currentEdge);
        endNode.addIncomingEdges(currentEdge);
        listOfEdges.add(currentEdge);

        System.out.println("Added edge from node " + currentEdge.getNodeStart().getNodeIdentifier() + " to node "
                + currentEdge.getNodeEnd().getNodeIdentifier() + " with edge weight = " + currentEdge.getEdgeWeight()
                + " to edge list");
    }

    /**
     * Takes in a line from the file and gets the weight of the edge.
     * 
     * @param line
     * @return
     */
    private int findEdgeWeight(String line) {
        String parts[] = line.split("Weight=");
        String weight = parts[1];
        weight = weight.replaceAll("\\D+", "");
        return Integer.parseInt(weight);
    }

    /**
     * Takes in a line from the file and gets the node identifier.
     * 
     * @param line
     * @return
     */
    private String parseNodeIdentifier(String line) {
        line = line.replaceAll("\\s", "");
        int iEnd = line.indexOf("[");
        String nodeIdentifier = line.substring(0, iEnd);
        return nodeIdentifier;
    }

    /**
     * Creates an order of nodes to be scheduled, only considering dependencies on parents.
     *
     * @param currentNode
     */
    private void addToSchedule(Node currentNode) {
        // Iterate through all input nodes WITHOUT assuming the first node is the root node
        while (!(listOfNodes.isEmpty())) {
            if (currentNode.getParentNodes().isEmpty() || isAllParentsVisited(currentNode)) {
                listOfSortedNodes.add(currentNode);
                listOfNodes.remove(currentNode);
                // Stop ordering once there are no more input nodes to iterate through
                if (!(listOfNodes.isEmpty())) {
                    currentNode = listOfNodes.element();
                }
            } else {
                // Parent node must be ordered before the child
                currentNode = getNextUnvisitedParent(currentNode);
            }
        }
    }

    /**
     * Checks if all the parents of a node have been visited and returns true or false
     *
     * @param child
     * @return boolean
     */
    private boolean isAllParentsVisited(Node child) {
        boolean isAllVisited = true;
        for (Node parent : child.getParentNodes()) {
            if (!listOfSortedNodes.contains(parent)) {
                isAllVisited = false;
            }
        }
        return isAllVisited;
    }

    /**
     * Finds the parents of a node and returns either an unvisited parent (in no particular order) or null if all have
     * been visited
     *
     * @param child
     * @return
     */
    private Node getNextUnvisitedParent(Node child) {
        for (Node parent : child.getParentNodes()) {
            if (!listOfSortedNodes.contains(parent)) {
                return parent;
            }
        }
        return null;
    }
}
