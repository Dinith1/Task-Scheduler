package se306;

import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;
        import se306.input.InputFileReader;

        import java.io.*;
        import java.util.HashMap;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

        import static junit.framework.TestCase.assertEquals;

public class InputFileReaderTest {

    File noHeader;
    InputFileReader ifr;
    int[] nodeWeightsNoHeaderCorrect = new int[7];


    @Before
    public void init() {
        noHeader = new File("./src/test/resources/Nodes_7_OutTree.dot");
        nodeWeightsNoHeaderCorrect[0] = 5;
        nodeWeightsNoHeaderCorrect[1] = 6;
        nodeWeightsNoHeaderCorrect[2] = 5;
        nodeWeightsNoHeaderCorrect[3] = 6;
        nodeWeightsNoHeaderCorrect[4] = 4;
        nodeWeightsNoHeaderCorrect[5] = 7;
        nodeWeightsNoHeaderCorrect[6] = 7;

    }


    @After
    public void cleanUp() {
        ifr.clearInputFileReader();
    }

    private int[] getNumberOfNodesAndEdgesTest(InputStreamReader isr) {
        BufferedReader buffRead = new BufferedReader(isr);
        int[] costArray = new int[2];
        int totalNodes = 0;
        int totalEdges = 0;
        try {
            String line;
            buffRead.readLine();
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
                    continue;
                }

                if (line.indexOf("->") == -1) { // Handle nodes
                    totalNodes = totalNodes + 1;
                } else {
                    totalEdges = totalEdges + 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        costArray[0] = totalNodes;
        costArray[1] = totalEdges;
        return costArray;
    }

    @Test
    public void testNoHeaderNodeWeights() throws IOException {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/Nodes_7_OutTree.dot");


        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);

        HashMap<Integer, Integer> nodeWeights = ifr.getNodeWeights();
        for (int i = 0; i < nodeWeights.size(); i++) {
            assertEquals(new Integer(nodeWeightsNoHeaderCorrect[i]), nodeWeights.get(i));
        }

    }
}