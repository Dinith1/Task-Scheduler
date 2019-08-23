package se306;

import se306.algorithm.AStarScheduler;
import se306.exceptions.InvalidInputException;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;
import se306.logging.Log;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main class to test InputFileReader functionality
 * 
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

        InputStreamReader isr;
        try {
            isr = new FileReader(parser.getInputFileName());
        } catch (FileNotFoundException e) {
            Log.error("Invalid input filename (please check the spelling)");
            return;
        }

        InputFileReader ifr = new InputFileReader();

        Log.info("-- Starting scheduling --");
        long startTime = System.nanoTime();

        ifr.readInput(isr);
        ifr.pruneIdenticalNodes();
        AStarScheduler scheduler = new AStarScheduler();
        scheduler.findOptimalSchedule();// Start scheduling

        long endTime = System.nanoTime();
        Log.info("-- Finished scheduling --");

        long executionTime = endTime - startTime;
        Log.info("Execution Time: " + (executionTime / 1000000) + "ms");
    }
}
