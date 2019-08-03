package se306.Input;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InputReader {

    private List<Node> listOfNodes= new ArrayList<>();
    private List<Edge> listOfEdges = new ArrayList<>();

    /**
     * Takes in a dot file, and parses it into Nodes and Edges, which are added into their respective ArrayLists
     * @param isr
     * @throws IOException
     */
    public void readInput(InputStreamReader isr) throws IOException {

        BufferedReader bufRead = new BufferedReader(isr);

        //Skip first line of file
        bufRead.readLine();

        String line;

        while((line = bufRead.readLine()) != null){
            String end = line.substring(0,1);

            //stop reading once it reaches end of file
            if(end.equalsIgnoreCase("}")) {
                break;
            }

            //only start reading if first seen character of a line is an number
            line = line.replaceAll("\\s", "");
            String firstDigit = line.substring(0, 1);
            if (firstDigit.matches("\\d")) {

                //if -> appears, it means it is an edge, otherwise it is a node. Get the node identifier and weight,
                //then make a Node object
                if (line.indexOf("->") == -1) {
                    int currentWeight = parseNodeWeight(line);
                    String nodeIdentifier = parseNodeIdentifier(line);
                    makeNode(currentWeight, nodeIdentifier);

                    //Get positions of variables for all edges, then make the Edge object
                } else {

                    line = line.replaceAll("\\s", "");

                    //Get start node of edge
                    int nodeStartWall = line.indexOf("-");
                    String nodeStart = line.substring(0, nodeStartWall);

                    //Get end node of edge
                    int nodeEndStart = line.indexOf(">");
                    int nodeEndWall = line.indexOf("[");
                    String nodeEnd = line.substring(nodeEndStart + 1, nodeEndWall);

                    int currentWeight = parseNodeWeight(line);

                    makeEdge(nodeStart, nodeEnd, currentWeight);
                }

            }
        }
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
        System.out.println("Added node "  + currentNode.getNodeIdentifier() + " with weight = " +
                currentNode.getNodeWeight() +
                " to node list");
    }

    /**
     * Takes in parameters of the start node, end node and edge weight and creates a new edge object, then add
     * it to a list of edges
     * @param nodeStart
     * @param nodeEnd
     * @param edgeWeight
     */
    private void makeEdge(String nodeStart, String nodeEnd, int edgeWeight){
        Node startNode = null;
        Node endNode = null;
        for(int i = 0;i<listOfNodes.size();i++) {
            if (nodeStart.equals(listOfNodes.get(i).getNodeIdentifier())) {
                startNode = listOfNodes.get(i);
            }
            if (nodeEnd.equals(listOfNodes.get(i).getNodeIdentifier())) {
                endNode = listOfNodes.get(i);
            }
        }
        Edge currentEdge = new Edge(startNode,endNode,edgeWeight);
        startNode.addOutGoingEdges(currentEdge);
        endNode.addIncomingEdges(currentEdge);
        listOfEdges.add(currentEdge);
        System.out.println("Added edge from node " + currentEdge.getNodeStart().getNodeIdentifier() +
                " to node " + currentEdge.getNodeEnd().getNodeIdentifier() +
                " with edge weight= " + currentEdge.getEdgeWeight() + " to edge list");
    }

    /**
     * Takes in a line from the file and gets the weight of the node.
     * @param line
     * @return
     */
    private int parseNodeWeight(String line){
        String parts[] = line.split("Weight=");
        String weight = parts[1];
        weight = weight.replaceAll("\\D+","");
        return Integer.parseInt(weight);
    }
    

    /**
     * Takes in a line from the file and gets the node identifier.
     * @param line
     * @return
     */
    private String parseNodeIdentifier(String line){
        line = line.replaceAll("\\s","");
        int iEnd = line.indexOf("[");
        String nodeIdentifier = line.substring(0,iEnd);
        return nodeIdentifier;
    }
}
