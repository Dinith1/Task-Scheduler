package se306;

import se306.exceptions.InvalidInputException;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;
import java.io.*;

/**
 * Main class to test InputFileReader functionality
 * 
 * @param args
 * @throws IOException
 */
public class Main {

    public static void main(String[] args) throws IOException {

        // Example cases
        // "src/resources/Nodes_7_OutTree.dot"
        // "src/resources/Nodes_8_Random.dot"
        // "src/resources/Nodes_9_SeriesParallel.dot"
        // "src/resources/Nodes_10_Random.dot"
        // "src/resources/Nodes_11_OutTree.dot"

        long executionStartTime = System.nanoTime();
        CommandLineParser parser = CommandLineParser.getInstance();

        try {
            parser.parseCommandLineArguments(args);

        } catch (InvalidInputException e) { // Processor input missing
            System.err.println(e.getMessage() + "\n");
            return;

        } catch (NumberFormatException e) { // Processor input was not a number
            System.err.println(e.getMessage() + "\n");
            return;
        }

        // Temporary fix to catch egregious file names
        // TODO
        // Preferably try to move this out of main
        InputStream in = null;
        in = Main.class.getResourceAsStream(parser.getInputFileName());
        System.out.println(in);
        
        if (in != null) {
            InputStreamReader isr = new InputStreamReader(in);
            InputFileReader inputFileReader = new InputFileReader();
            inputFileReader.readInput(isr);

        } else {
            throw new IllegalArgumentException("invalid filename");
        }

        long executionEndTime = System.nanoTime();
        long executionTIme = executionEndTime - executionStartTime;
        System.out.println("Execution Time in milliseconds: " + executionTIme / 1000000);
    }
}
