package se306;

import se306.Input.InputReader;

import java.io.*;

//Main class to test InputReader functionality
public class Main {


    public static void main(String[] args) throws IOException {

        InputStream in = Main.class.getResourceAsStream("/Nodes_9_SeriesParallel.dot");
        InputStreamReader isr = new InputStreamReader(in);
        InputReader inputReader = new InputReader();
        inputReader.parseCommandLineProcessorCount(args);
        inputReader.readInput(isr);
    }

}

