package se306.Input;

import java.io.*;
import java.sql.SQLOutput;
import java.util.*;

public class InputReader {

    private Queue<Node> listOfNodes = new ArrayDeque<>();
    private List<Edge> listOfEdges = new ArrayList<>();
    private HashSet<Node> storedNodes = new HashSet<>();
    private List<Node> listOfSortedNodes = new ArrayList<>();
    private int c;

    /**
     * Takes in a dot file, and parses it into Nodes and Edges, which are added into their respective ArrayLists
     * @param file
     * @throws IOException
     */
    public void readInput(File file) throws IOException {

        BufferedReader bufRead = new BufferedReader(new FileReader(file));

        // Skip first line of file
        bufRead.readLine();

        String line;

        while((line = bufRead.readLine()) != null) {
            String end = line.substring(0,1);

            // Stop reading once it reaches end of file
            if(end.equalsIgnoreCase("}")) {
                break;
            }

            // Only start reading if first seen character of a line is an number
            line = line.replaceAll("\\s", "");
            String firstDigit = line.substring(0, 1);

            if (firstDigit.matches("\\d")) {

                // If "->" appears, it means it is an edge, otherwise it is a node.
                // Get the node identifier and weight then make a Node object
                if (line.indexOf("->") == -1) { // Handle nodes
                    int currentWeight = parseNodeWeight(line);
                    String nodeIdentifier = parseNodeIdentifier(line);
                    makeNode(currentWeight, nodeIdentifier);

                } else { // Handle edges
                    line = line.replaceAll("\\s", "");

                    // Get start node of edge
                    int nodeStartWall = line.indexOf("-");
                    String nodeStart = line.substring(0, nodeStartWall);

                    // Get end node of edge
                    int nodeEndStart = line.indexOf(">");
                    int nodeEndWall = line.indexOf("[");
                    String nodeEnd = line.substring(nodeEndStart + 1, nodeEndWall);

                    int currentWeight = parseNodeWeight(line);

                    makeEdge(nodeStart, nodeEnd, currentWeight);
                }

            }
        }
        Node node = (listOfNodes.size() > 0) ? listOfNodes.peek() : null;

        addToSchedule(node);


       for(int i = 0;i<listOfSortedNodes.size();i++){
           System.out.println(listOfSortedNodes.get(i).getNodeIdentifier());
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

        // Loop through nodes (assuming the two nodes already exist as objects)
        for(Node tempNode : listOfNodes) {
            String tempNodeId = tempNode.getNodeIdentifier();
            startNode = (nodeStart.equals(tempNodeId)) ? tempNode : startNode;
            endNode = (nodeEnd.equals(tempNodeId)) ? tempNode : startNode;
        }

        // Add parent to child
        endNode.addParent(startNode);

        Edge currentEdge = new Edge(startNode, endNode, edgeWeight);
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

    private void addToSchedule (Node currentNode) {
        while (!listOfNodes.isEmpty()){
            Node parent = null;
            if (currentNode.getParentNodes().isEmpty() || ((parent = getNextUnvisitedParent(currentNode)) == null)){
                listOfSortedNodes.add(currentNode);
                listOfNodes.remove(currentNode);
                System.out.println(currentNode.getNodeIdentifier());
            } else {
                currentNode = getNextUnvisitedParent(currentNode);
            }
        }
    }

//    private void addToSchedule(Node node) {
//        c++;
//       // System.out.println("........: " + c);
//        while (!listOfNodes.isEmpty()) {
//            Node parent = null;
//            if (node.getParentNodes().isEmpty() || (!node.getParentNodes().isEmpty() && ((parent = allParentsVisited(node)) == null))) { // Has no (unvisited) parents
//                listOfSortedNodes.add(node);
//                System.out.println("before pop " + node.getNodeIdentifier());
//                storedNodes.add(node);
//                listOfNodes.remove(node);
//
//                node = listOfNodes.poll();
//                System.out.println("after pop " + node.getNodeIdentifier());
//            } else { // node must have an unvisited parent
//                addToSchedule(parent);
//
//                // System.out.println(parent.getNodeIdentifier());
//            }
//        }
//        listOfSortedNodes.add(node);
//
//    }

      private boolean isAllParentsVisited(Node child){
        boolean isAllVisited = true;
        for (Node parent: child.getParentNodes()){
            if (!listOfSortedNodes.contains(parent)){
                isAllVisited = false;
            }
        }
        return isAllVisited;
      }

    private Node getNextUnvisitedParent(Node child){
        for (Node parent: child.getParentNodes()){
            if (!listOfSortedNodes.contains(parent)){
                return parent;
            }
        }
        return null;
    }

//    private Node allParentsVisited(Node child) {
//        for (Node parent: child.getParentNodes()) {
//            if (!storedNodes.contains(parent)) {
//                return parent;
//            }
//        }
//        return null;
//    }
}
