package se306;

import org.junit.Before;
import org.junit.Test;
import se306.Input.InputReader;
import se306.Input.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppTest
{


    private InputReader inputReader;

    @Before
    public void setup() {
        inputReader = new InputReader();


    }

    /**
     * This "test" should be run to check any changes do not affect any examples
     */
    @Test
    public void testAllResources() {

        // Resources provided as example input graphs
        List<String> pathNames = new ArrayList<>();
        pathNames.add("src/resources/Nodes_7_OutTree.dot");
        pathNames.add("src/resources/Nodes_8_Random.dot");
        pathNames.add("src/resources/Nodes_9_SeriesParallel.dot");
        pathNames.add("src/resources/Nodes_10_Random.dot");
        pathNames.add("src/resources/Nodes_11_OutTree.dot");

        for (String path : pathNames) {
            File file = new File(path).getAbsoluteFile();
            InputReader inputReader = new InputReader();
            System.out.println(path);
            try {
                inputReader.readInput(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}