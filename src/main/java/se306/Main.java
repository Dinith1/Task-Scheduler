package se306;

import se306.exceptions.InputMissingException;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;

import java.io.*;
import java.util.Timer;

//Main class to test InputFileReader functionality
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
        }catch(InputMissingException e){
                if(e.checkProcessInput(parser)){ //This exception checks if the Processor input was missing
                    System.out.println("Please enter a valid processor input");
                    return;
                }
            System.out.println("Please check ur command line inputs");
            return;
        }
        InputStream in = Main.class.getResourceAsStream(parser.getInputFileName());

        InputStreamReader isr = new InputStreamReader(in);
        InputFileReader inputFileReader = new InputFileReader();
        inputFileReader.readInput(isr);
        long executionEndTime = System.nanoTime();
        long executionTIme = executionEndTime-executionStartTime;
        System.out.println("Execution Time in milliseconds: " + executionTIme/1000000);
    }
}

