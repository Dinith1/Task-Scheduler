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
        while((line = bufRead.readLine()) != null){
            String end = line.substring(0,1);
            if(end.equalsIgnoreCase("}")){
                break;
            }

            //if -> appears, it means it is an edge, otherwise it is a node
            if(line.indexOf("->") == -1){
                int currentWeight = parseNodeWeight(line);
                String nodeIdentifier = parseNodeIdentifier(line);
                System.out.println(currentWeight);
                System.out.println(nodeIdentifier);
                makeNode(currentWeight,nodeIdentifier);
            }else{

            }

        }
    }

    private void makeNode(int weight, String nodeIdentifier){

        for(Node node: listOfNodes){
            if(!node.getNodeIdentifier().equals(nodeIdentifier)){
                Node currentNode = new Node(weight,nodeIdentifier);
                listOfNodes.add(currentNode);
            }else{
                System.out.println("Node Already in List");
            }
        }
    }

    private void makeEdge(Node nodeStart, Node nodeEnd, int edgeWeight){
        //TODO

    }

    private int parseNodeWeight(String line){
        String parts[] = line.split("Weight=");
        String weight = parts[1];
        weight = weight.replaceAll("\\D+","");
        return Integer.parseInt(weight);
    }

    private String parseNodeIdentifier(String line){
        line = line.replaceAll("\\s","");
        int iend = line.indexOf("[");
        String nodeIdentifier = line.substring(0,iend);
        return nodeIdentifier;
    }
}
