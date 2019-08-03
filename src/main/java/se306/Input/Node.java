package se306.Input;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private List<Edge> ListOfOutgoingEdges;
    private List<Edge> ListOfIncomingEdges;
    private int nodeWeight;
    private String nodeIdentifier;



    public Node(int weight, String nodeIdentifier) {
        this.nodeWeight = weight;
        this.ListOfIncomingEdges = new ArrayList<>();
        this.ListOfOutgoingEdges = new ArrayList<>();
        this.nodeIdentifier = nodeIdentifier;
    }

    public int getNodeWeight(){
        return nodeWeight;
    }

    public int getNumberOfOutGoingEdges(){
        return ListOfOutgoingEdges.size();
    }

    public int getNumberOfIncomingEdges(){
        return ListOfIncomingEdges.size();
    }

    public void addOutGoingEdges(Edge edge){
        ListOfOutgoingEdges.add(edge);
    }
    public void addIncomingEdges(Edge edge){
        ListOfIncomingEdges.add(edge);
    }

    public String getNodeIdentifier(){
        return nodeIdentifier;
    }

}
