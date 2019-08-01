package Input;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InputReader {

    private List<Node> listOfNodes= new ArrayList<>();
    private List<Edge> listOfEdges = new ArrayList<>();

    public void readInput(File file) throws IOException {

        BufferedReader bufRead = new BufferedReader(new FileReader(file));
        bufRead.readLine();
        String start;
        while((start = bufRead.readLine()) != null){

        }
    }

    private void makeNode(int weight, String nodeIdentifier){
        //TODO
    }

    private void makeEdge(Node nodeStart, Node nodeEnd, int edgeWeight){
        //TODO
    }
}
