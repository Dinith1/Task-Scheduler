package Input;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private List<Edge> outgoingEdges;
    private List<Edge> incomingEdges;
    private int nodeWeight;
    private String nodeIdentifier;



    public Node(int weight, String nodeIdentifier) {
        this.nodeWeight = weight;
        this.incomingEdges = new ArrayList<>();
        this.outgoingEdges = new ArrayList<>();
        this.nodeIdentifier = nodeIdentifier;
    }

    public int getNodeWeight(){
        return nodeWeight;
    }

    public int getNumberOfOutGoingEdges(){
        return outgoingEdges.size();
    }

    public int getNumberOfIncomingEdges(){
        return incomingEdges.size();
    }

    public String getNodeIdentifier(){
        return nodeIdentifier;
    }

}
