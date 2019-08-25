package se306;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se306.algorithm.AStarScheduler;
import se306.algorithm.PartialSchedule;
import se306.input.InputFileReader;
import se306.logging.Log;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertEquals;

public class OptimalityTest {

    PartialSchedule ps;

    @Before
    public void initialise() {
        ps = null;
//        InputFileReader.nodeWeights.clear();
//        InputFileReader.nodeParents.clear();
//        InputFileReader.nodeWeightsReversed.clear();
//        InputFileReader.nodeIds = null;
//        InputFileReader.listOfEdges = null;
//        InputFileReader.nodeChildren = null;
    }

    @After
    public void cleanUp(){
        InputFileReader.nodeWeights.clear();
        InputFileReader.nodeParents.clear();
        InputFileReader.nodeWeightsReversed.clear();
        InputFileReader.nodeIds = null;
        InputFileReader.listOfEdges = null;
        InputFileReader.nodeChildren = null;
        InputFileReader.nodeWeights = null;
        InputFileReader.nodeParents = null;
        InputFileReader.nodeWeightsReversed = null;
        ps = null;
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
        File file = new File("Nodes_7_OutTree.dot");


        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        InputFileReader ifr = new InputFileReader(costArray[0], costArray[1]);


        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();


        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(4);
        System.out.println("hi");
        assertEquals(22, ps.getFinishTime());
    }
    @Test
    public void Nodes_7_OutTree2Proc() throws Exception{
        int[] costArray;

        InputStreamReader isr;
        File file = new File("Nodes_7_OutTree.dot");


        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        InputFileReader ifr = new InputFileReader(costArray[0], costArray[1]);



        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();


        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2);


        assertEquals(28, ps.getFinishTime());
    }
    @Test
    public void Nodes_11_OutTree4Proc() throws Exception{
        int[] costArray;

        InputStreamReader isr;
        File file = new File("Nodes_11_OutTree.dot");


        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        InputFileReader ifr = new InputFileReader(costArray[0], costArray[1]);



        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();


        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(4);


        assertEquals(227, ps.getFinishTime());
    }
    @Test
    public void Fork_Join_Nodes_10_2Proc() throws Exception{
        int[] costArray;

        InputStreamReader isr;
        File file = new File("2p_Fork_Join_Nodes_10_CCR_0.10_WeightType_Random.dot");


        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        InputFileReader ifr = new InputFileReader(costArray[0], costArray[1]);



        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();


        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2);


        assertEquals(499, ps.getFinishTime());
    }
    @Test
    public void Fork_Nodes_10_2Proc() throws Exception{
        int[] costArray;

        InputStreamReader isr;
        File file = new File("2p_Fork_Nodes_10_CCR_0.10_WeightType_Random.dot");


        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        InputFileReader ifr = new InputFileReader(costArray[0], costArray[1]);



        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();


        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2);


        assertEquals(300, ps.getFinishTime());
    }

    @Test
    public void Independent_Nodes_21_2Proc() throws Exception{
        int[] costArray;

        InputStreamReader isr;
        File file = new File("2p_Independent_Nodes_21_WeightType_Random.dot");


        isr = new FileReader(file);
        costArray = getNumberOfNodesAndEdgesTest(isr);
        InputFileReader ifr = new InputFileReader(costArray[0], costArray[1]);



        InputStreamReader isr2;
        isr2 = new FileReader(file);
        ifr.readInput(isr2);
        ifr.pruneIdenticalNodes();


        AStarScheduler scheduler = new AStarScheduler();
        ps = scheduler.aStarAlgorithm(2);


        assertEquals(66, ps.getFinishTime());


    }

}
