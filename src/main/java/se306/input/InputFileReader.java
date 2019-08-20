package se306.input;

import se306.output.OutputFileGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFileReader {

    public static Queue<Node> listOfAvailableNodes = new ArrayDeque<>();
    private List<Edge> listOfEdges = new ArrayList<>();
    private OutputFileGenerator outputFileGenerator = new OutputFileGenerator();

    /**
     * Takes in a dot file, and parses it into Nodes and Edges, which are added into
     * their respective ArrayLists
     *
     * @param isr
     * @throws IOException
     */
    public void readInput(InputStreamReader isr) throws IOException {

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
                int currentWeight = findEdgeWeight(line);
                String nodeIdentifier = parseNodeIdentifier(line);
                makeNode(currentWeight, nodeIdentifier);

            } else { // Handle edges
                // Get start node of edge
                String startNode = line.substring(0, line.indexOf("-"));
                startNode = startNode.replaceAll("\\s+", "");
                // Get end node of edge
                String endNode = line.substring(line.indexOf(">") + 1, line.indexOf("["));
                endNode = endNode.replaceAll("\\s+", "");
                int edgeWeight = findEdgeWeight(line);
                makeEdge(startNode, endNode, edgeWeight);
            }

        }

        buffRead.close();
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
        listOfAvailableNodes.add(currentNode);
        outputFileGenerator.readLine(currentNode);
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

        // Loop through nodes to find starting and ending nodes of the edge (assuming
        // the two nodes already exist as objects)
        for (Node tempNode : listOfAvailableNodes) {
            String tempNodeId = tempNode.getNodeIdentifier();
            startNode = (startNodeId.equals(tempNodeId)) ? tempNode : startNode;
            endNode = (endNodeId.equals(tempNodeId)) ? tempNode : endNode;
        }

        // Store parent node in child
        endNode.addParent(startNode);

        Edge currentEdge = new Edge(startNode, endNode, edgeWeight);
        startNode.addOutGoingEdges(currentEdge);
        endNode.addIncomingEdges(currentEdge);
        outputFileGenerator.readLine(currentEdge);
        listOfEdges.add(currentEdge);
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