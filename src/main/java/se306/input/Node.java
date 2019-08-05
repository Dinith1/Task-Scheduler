package se306.input;

import se306.algorithm.Processor;

import java.util.ArrayList;
import java.util.List;


public class Node {

    private List<Edge> listOfOutgoingEdges;
    private List<Edge> listOfIncomingEdges;
    private int nodeWeight;
    private String nodeIdentifier;
    private List<Node> parentNodes = new ArrayList<>();
    private Processor procScheduledInto;



    public Node(int weight, String nodeIdentifier) {
        this.nodeWeight = weight;
        this.listOfIncomingEdges = new ArrayList<>();
        this.listOfOutgoingEdges = new ArrayList<>();
        this.nodeIdentifier = nodeIdentifier;
    }

    public int getNodeWeight(){
        return this.nodeWeight;
    }

    public int getNumberOfOutGoingEdges(){
        return this.listOfOutgoingEdges.size();
    }

    public int getNumberOfIncomingEdges(){
        return this.listOfIncomingEdges.size();
    }

    public void addOutGoingEdges(Edge edge){
        this.listOfOutgoingEdges.add(edge);
    }
    public void addIncomingEdges(Edge edge){
        this.listOfIncomingEdges.add(edge);
    }

    public Edge getIncomingEdge(Node parent) {
        for (Edge edges : this.listOfIncomingEdges) {
            if (edges.getNodeStart().equals(parent)) {
                return edges;
            }
        }
        // No match was found
        return null;
    }

    public Edge getOutgoingEdge(Node child) {
        for (Edge edges : this.listOfOutgoingEdges) {
            if (edges.getNodeStart().equals(child)) {
                return edges;
            }
        }
        // No match was found
        return null;
    }

    public String getNodeIdentifier(){
        return this.nodeIdentifier;
    }

    public List<Node> getParentNodes(){
        return this.parentNodes;
    }

    public void addParent(Node parent) {
        this.parentNodes.add(parent);
    }

    public void assignProcessor(Processor proc) { this.procScheduledInto = proc; }

    public Processor getProcessor() { return this.procScheduledInto; }


}
