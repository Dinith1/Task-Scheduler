package Input;

public class Edge {

    private Node nodeStart;
    private Node nodeEnd;
    private int edgeWeight;

    public Edge(Node nodeStart, Node nodeEnd, int edgeWeight){
        this.nodeStart = nodeStart;
        this.nodeEnd = nodeEnd;
        this.edgeWeight = edgeWeight;
    }

    public Node getnodeStart(){
        return nodeStart;
    }
    public Node getNodeEnd(){
        return nodeEnd;
    }
    public int getEdgeWeight(){
        return edgeWeight;
    }
}
