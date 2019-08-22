package se306.input;

import se306.output.OutputFileGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFileReader {
    static int NUM_NODES = 7; // Testing with Nodes_7 (change later to read number of nodes from GraphViz
                              // graph generator)
    static int NUM_EDGES = 6; // Testing with Nodes_7 (change later to read number of edges from GraphViz
                              // graph generator)

    // public static int[] listOfAvailableNodes = new int[NUM_NODES]; // Each int is
    // the id (not the name) of the node
    // (DON"T EVEN NEED THIS AS CAN STORE TOTAL NUMBER OF
    // NODES INSTEAD -> KNOW THAT NODES WILL BE 0 to n-1)

    public static HashMap<Integer, String> nodeNames = new HashMap<Integer, String>(); // Map from id to name of node
    
    public static HashMap<String, Integer> nodeNamesReverse = new HashMap<String, Integer>(); // To find id for given node name

    public static HashMap<Integer, Integer> nodeWeights = new HashMap<Integer, Integer>(); // Map from id to weight of node ->
                                                                                    // MIGHT NEED TO CHANGE WEIGHT TO
                                                                                    // DOUBLE

    public static int[][] listOfEdges = new int[3][NUM_EDGES]; // {{from, to, weight}, {t, f, w}, ...} Each from/to is
                                                               // the id of the node

    private OutputFileGenerator outputFileGenerator = OutputFileGenerator.getInstance();

    /**
     * Takes in a dot file, and parses it into Nodes and Edges, which are added into
     * their respective ArrayLists
     *
     * @param isr
     * @throws IOException
     */
    public void readInput(InputStreamReader isr) throws IOException {

        int id = 0; // For node ids
        int edgeNum = 0; // for adding to listOfEdges

        BufferedReader buffRead = new BufferedReader(isr);

        // Skip first line of file
        String line = buffRead.readLine();

        outputFileGenerator.readLine(line);

        while ((line = buffRead.readLine()) != null) {
            String end = line.substring(0, 1);

            // Stop reading once it reaches end of file
            if (end.equalsIgnoreCase("}")) {
                break;
            }
            // If the line is not a line that includes a node or an edge
            Pattern p = Pattern.compile(".*\\[Weight=.*];");
            Matcher m = p.matcher(line);
            if (!m.matches()) {
                outputFileGenerator.readLine(line);
                continue;
            }

            // If "->" appears, it means it is an edge, otherwise it is a node.
            // Get the node identifier and weight then make a Node object
            if (line.indexOf("->") == -1) { // Handle nodes
                String name = findName(line);
                nodeNames.put(new Integer(id), name);
                nodeNamesReverse.put(name, new Integer(id));

                int weight = findWeight(line);
                nodeWeights.put(new Integer(id), new Integer(weight));

                id++;

            } else { // Handle edges
                String startNode = line.substring(0, line.indexOf("-")).replaceAll("\\s+", "");
                String endNode = line.substring(line.indexOf(">") + 1, line.indexOf("[")).replaceAll("\\s+", "");
                int weight = findWeight(line);

                listOfEdges[0][edgeNum] = nodeNamesReverse.get(startNode);
                listOfEdges[1][edgeNum] = nodeNamesReverse.get(endNode);
                listOfEdges[2][edgeNum] = weight;

                edgeNum++;
            }

        }

        buffRead.close();
    }

    /**
     * Takes in a line from the file and gets the weight.
     *
     * @param line
     * @return The weight
     */
    private int findWeight(String line) {
        String parts[] = line.split("Weight=");
        String weight = parts[1].replaceAll("\\D+", "");
        return Integer.parseInt(weight);
    }

    /**
     * Takes in a line from the file and gets the node name.
     *
     * @param line
     * @return The name
     */
    private String findName(String line) {
        line = line.replaceAll("\\s", "");
        int iEnd = line.indexOf("[");
        return line.substring(0, iEnd);
    }
}