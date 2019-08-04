package se306;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import se306.Input.InputReader;
import se306.Input.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    private InputReader inputReader;

    @Before
    public void setup() {
        inputReader = new InputReader();


    }

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException {
        File file = new File("./src/resources/Nodes_7_OutTree.dot").getAbsoluteFile();
        inputReader.readInput(file);
        List<Node> listOfNodes = inputReader.getNodeList();
        listOfNodes.get(0);
    }
}
