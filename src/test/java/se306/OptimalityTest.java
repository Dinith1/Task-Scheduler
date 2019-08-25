package se306;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se306.algorithm.AStarScheduler;
import se306.algorithm.PartialSchedule;
import se306.input.InputFileReader;
import se306.logging.Log;
import se306.util.HashMapGenerator;
import se306.util.IdenticalNodes;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;

public class OptimalityTest {

    PartialSchedule ps;
    InputFileReader ifr;
    HashMapGenerator hmg;
    IdenticalNodes in;

    @Before
    public void initialise() {
        ps = null;

    }

    @After
    public void cleanUp() {
        ps = null;
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
                    totalNodes++;
                } else {
                    totalEdges++;
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
    public void Nodes_7_OutTree4Proc() throws Exception {

        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/Nodes_7_OutTree.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(4, 1);
        assertEquals(22, ps.getFinishTime());
    }

    @Test
    public void Nodes_7_OutTree2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/Nodes_7_OutTree.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(28, ps.getFinishTime());
    }

    @Test
    public void Nodes_11_OutTree4Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/Nodes_11_OutTree.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(4, 1);

        assertEquals(227, ps.getFinishTime());
    }

    @Test
    public void Fork_Join_Nodes_10_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_Fork_Join_Nodes_10_CCR_0.10_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(499, ps.getFinishTime());
    }

    @Test
    public void Fork_Nodes_10_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_Fork_Nodes_10_CCR_0.10_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(300, ps.getFinishTime());
    }

    @Test
    public void Independent_Nodes_21_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_Independent_Nodes_21_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(66, ps.getFinishTime());
    }

    @Test
    public void Pipeline_Nodes_21_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_Pipeline_Nodes_21_CCR_0.10_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(904, ps.getFinishTime());
    }

    @Test
    public void Join_Nodes_10_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_Join_Nodes_10_CCR_0.10_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(292, ps.getFinishTime());
    }

    @Test
    public void Intree_Balanced_Nodes_10_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_InTree-Balanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(222, ps.getFinishTime());
    }

    @Test
    public void Intree_Unbalanced_Nodes_10_2Proc() throws Exception {

        int[] costArray;

        InputStreamReader isr;
        File file = new File(
                "./src/test/resources/2p_InTree-Unbalanced-MaxBf-3_Nodes_10_CCR_0.10_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(344, ps.getFinishTime());
    }

    @Test
    public void OutTree_Balanced_Nodes_21_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File(
                "./src/test/resources/2p_OutTree-Balanced-MaxBf-3_Nodes_21_CCR_1.05_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(71, ps.getFinishTime());

    }

    @Test
    public void OutTree_Unbalanced_Nodes_21_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File(
                "./src/test/resources/2p_OutTree-Unbalanced-MaxBf-3_Nodes_21_CCR_0.99_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(72, ps.getFinishTime());
    }

    @Test
    public void Random_Nodes_10_Density_4_CCR_10_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_Random_Nodes_10_Density_4.50_CCR_10.00_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(66, ps.getFinishTime());
    }

    @Test
    public void Series_Parallel_10_MAXBF5_CCR_9_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_SeriesParallel-MaxBf-5_Nodes_10_CCR_9.97_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(59, ps.getFinishTime());
    }

    @Test
    public void Stencil_Nodes_21_CCR_10_2Proc() throws Exception {
        int[] costArray;

        InputStreamReader isr;
        File file = new File("./src/test/resources/2p_Stencil_Nodes_21_CCR_10.03_WeightType_Random.dot");

        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        ifr = InputFileReader.getInstance(costArray[0], costArray[1]);

        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();

        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2, 1);

        assertEquals(134, ps.getFinishTime());
    }
}
