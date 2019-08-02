package se306;


import se306.Input.InputReader;

import java.io.File;
import java.io.IOException;

//Main class to test InputReader functionality
public class Main {


    public static void main(String[] args) throws IOException {
        File file = new File("./src/resources/Nodes_9_SeriesParallel.dot").getAbsoluteFile();
        InputReader inputReader = new InputReader();
        inputReader.readInput(file);
    }

}

