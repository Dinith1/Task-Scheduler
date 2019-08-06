package se306.input;

public class Edge {

    private Node nodeStart;
    private Node nodeEnd;
    private int edgeWeight;

    public Edge(Node nodeStart, Node nodeEnd, int edgeWeight){
        this.nodeStart = nodeStart;
        this.nodeEnd = nodeEnd;
        this.edgeWeight = edgeWeight;
    }

    public Node getNodeStart(){
        return nodeStart;
    }
    public Node getNodeEnd(){
        return nodeEnd;
    }
    public int getEdgeWeight(){
        return edgeWeight;
    }
}
