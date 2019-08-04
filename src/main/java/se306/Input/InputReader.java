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
    private int numberOfProcesses;

    /**
     * Takes in a dot file, and parses it into Nodes and Edges, which are added into their respective ArrayLists
     * @param isr
     * @throws IOException
     */
    public void readInput(InputStreamReader isr) throws IOException {

        BufferedReader bufRead = new BufferedReader(isr);

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
     * Takes in the command line arguments and produces number
     * of processors to be used in algorithm
     * @param input
     */
    public void parseCommandLineProcessorCount(String[] input) {
        if (input.length == 0) {
            System.out.println("No processors specified, defaulted to 1.");
        } else if (input.length == 1 ) {
            try {
                int i = Integer.parseInt(input[0]);
                numberOfProcesses = i;
                System.out.println("Number of processors: " + i);
                return;
            } catch(NumberFormatException e) {
                System.out.println("Unrecognised option, defaulted to 1 processor");
            }
        } else {
            System.out.println("Too many arguments provided, defaulted to 1 processor");
        }
        numberOfProcesses = 1;
    }

    /**
     * Takes in parameters of the node weight and node identifier and creates a new node object, then add it
     * to a list of nodes
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

}
