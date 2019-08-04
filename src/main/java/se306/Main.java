package se306;


import se306.Input.InputReader;

import java.io.File;
import java.io.IOException;

//Main class to test InputReader functionality
public class Main {


    public static void main(String[] args) throws IOException {
        // Example cases
        // "src/resources/Nodes_7_OutTree.dot"
        // "src/resources/Nodes_8_Random.dot"
        // "src/resources/Nodes_9_SeriesParallel.dot"
        // "src/resources/Nodes_10_Random.dot"
        // "src/resources/Nodes_11_OutTree.dot"
        File file = new File("src/resources/Nodes_8_Random.dot").getAbsoluteFile();
        InputReader inputReader = new InputReader();
        inputReader.readInput(file);
    }
}

