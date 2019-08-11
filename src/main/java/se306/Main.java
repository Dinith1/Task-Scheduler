package se306;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import se306.exceptions.InvalidInputException;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;
import se306.logging.Log;

/**
 * Main class to test InputFileReader functionality
 * 
 * @param args
 * @throws IOException
 */
public class Main {

    public static void main(String[] args) throws IOException {

        // Example cases:
        // "src/resources/Nodes_7_OutTree.dot"
        // "src/resources/Nodes_8_Random.dot"
        // "src/resources/Nodes_9_SeriesParallel.dot"
        // "src/resources/Nodes_10_Random.dot"
        // "src/resources/Nodes_11_OutTree.dot"

        CommandLineParser parser = CommandLineParser.getInstance();

        try {
            parser.parseCommandLineArguments(args);

        } catch (InvalidInputException | NumberFormatException e) {
            Log.error(e.getMessage());
            parser.printUsage();
            return;
        }

        InputStream in = Main.class.getResourceAsStream("/" + parser.getInputFileName());

        if (in == null) {
            Log.error("Invalid input filename (please check the spelling)");
            return;
        }

        InputStreamReader isr = new InputStreamReader(in);
        InputFileReader ifr = new InputFileReader();

        Log.info("-- Starting scheduling --");
        long startTime = System.nanoTime();

        ifr.readInput(isr); // Start scheduling

        long endTime = System.nanoTime();
        Log.info("-- Finished scheduling --");

        long executionTime = endTime - startTime;
        Log.info("Execution Time: " + (executionTime / 1000000) + "ms");
    }
}
