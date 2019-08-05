package se306;

import se306.input.InputFileReader;

import java.io.*;

//Main class to test InputFileReader functionality
public class Main {


    public static void main(String[] args) throws IOException {
        
        // Example cases
        // "src/resources/Nodes_7_OutTree.dot"
        // "src/resources/Nodes_8_Random.dot"
        // "src/resources/Nodes_9_SeriesParallel.dot"
        // "src/resources/Nodes_10_Random.dot"
        // "src/resources/Nodes_11_OutTree.dot"

        InputStream in = Main.class.getResourceAsStream("/Nodes_10_Random.dot");
        InputStreamReader isr = new InputStreamReader(in);
        InputFileReader inputFileReader = new InputFileReader();
        inputFileReader.parseCommandLineProcessorCount(args);
        inputFileReader.readInput(isr);
    }
}

