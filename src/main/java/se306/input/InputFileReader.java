package se306.input;

import se306.output.OutputFileGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFileReader {
    public static int NUM_NODES = 11; // Testing with Nodes_7 (change later to read number of nodes from GraphViz
                                     // graph generator)
    public static int NUM_EDGES = 10; // Testing with Nodes_7 (change later to read number of edges from GraphViz
                                     // graph generator)

    // public static int[] listOfAvailableNodes = new int[NUM_NODES]; // Each int is
    // the id (not the name) of the node
    // (DON"T EVEN NEED THIS AS CAN STORE TOTAL NUMBER OF
    // NODES INSTEAD -> KNOW THAT NODES WILL BE 0 to n-1)

    public static HashMap<Integer, String> nodeNames = new HashMap<Integer, String>(); // Map from id to name of node

    public static HashMap<String, Integer> nodeNamesReverse = new HashMap<String, Integer>(); // To find id for given
                                                                                              // node name

    public static HashMap<Integer, Integer> nodeWeights = new HashMap<Integer, Integer>(); // Map from id to weight of
                                                                                           // node -> MIGHT NEED TO
                                                                                           // CHANGE WEIGHT TO DOUBLE

    public static int[][] parents = new int[NUM_NODES][NUM_NODES]; // parents[0] stores parents of node with id = 0,
                                                                   // i.e. parents[0][1] = 1 means node with id = 1 is a
                                                                   // parent of node with id 0

    public static int[][] listOfEdges = new int[NUM_EDGES][3]; // {{from, to, weight}, {f, t, w}, ...} Each from/to is
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
                Integer idInt = new Integer(id);

                String name = findName(line);
                nodeNames.put(idInt, name);
                nodeNamesReverse.put(name, idInt);

                Integer weight = findWeight(line);
                nodeWeights.put(idInt, weight);

                outputFileGenerator.readLine(idInt);
                id++;

            } else { // Handle edges
                String startNode = line.substring(0, line.indexOf("-")).replaceAll("\\s+", "");
                String endNode = line.substring(line.indexOf(">") + 1, line.indexOf("[")).replaceAll("\\s+", "");
                int weight = findWeight(line);
            
                listOfEdges[edgeNum][0] = nodeNamesReverse.get(startNode);
                listOfEdges[edgeNum][1] = nodeNamesReverse.get(endNode);
                listOfEdges[edgeNum][2] = weight;

                // Store parent
                parents[nodeNamesReverse.get(endNode)][nodeNamesReverse.get(startNode)] = 1;

                outputFileGenerator.readLine(listOfEdges[edgeNum]);
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