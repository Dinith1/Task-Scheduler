package Input;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InputReader {

    private List<Node> listOfNodes= new ArrayList<>();
    private List<Edge> listOfEdges = new ArrayList<>();

    public void readInput(File file) throws IOException {

        BufferedReader bufRead = new BufferedReader(new FileReader(file));

        //Skip first line of file
        bufRead.readLine();

        String line;
        while((line = bufRead.readLine()) != null) {
            String end = line.substring(0, 1);

            //stop reading when we reach end of file
            if (end.equalsIgnoreCase("}")) {
                break;
            }

            //only start reading if first seen character of a line is an number
            line = line.replaceAll("\\s", "");
            String firstDigit = line.substring(0, 1);
            if (firstDigit.matches("\\d")) {

                //if -> appears, it means it is an edge, otherwise it is a node
                if (line.indexOf("->") == -1) {
                    int currentWeight = parseNodeWeight(line);
                    String nodeIdentifier = parseNodeIdentifier(line);
                    makeNode(currentWeight, nodeIdentifier);
                } else {

                    //Get positions of variables for all edges
                    line = line.replaceAll("\\s", "");
                    int nodeStartWall = line.indexOf("-");
                    String nodeStart = line.substring(0, nodeStartWall);
                    int nodeEndStart = line.indexOf(">");
                    int nodeEndWall = line.indexOf("[");
                    String nodeEnd = line.substring(nodeEndStart + 1, nodeEndWall);
                    int currentWeight = parseNodeWeight(line);

                    makeEdge(nodeStart, nodeEnd, currentWeight);
                }

            }
        }
    }

    private void makeNode(int weight, String nodeIdentifier) {

        Node currentNode = new Node(weight, nodeIdentifier);
        listOfNodes.add(currentNode);
        System.out.println("Added node "  + currentNode.getNodeIdentifier() + " with weight = " +
                currentNode.getNodeWeight() +
                " to node list");
    }

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

    private int parseNodeWeight(String line){
        String parts[] = line.split("Weight=");
        String weight = parts[1];
        weight = weight.replaceAll("\\D+","");
        return Integer.parseInt(weight);
    }

    private String parseEdgeIdentifier(String line){
        //TODO
        return null;
    }

    private String parseNodeIdentifier(String line){
        line = line.replaceAll("\\s","");
        int iEnd = line.indexOf("[");
        String nodeIdentifier = line.substring(0,iEnd);
        return nodeIdentifier;
    }
}
