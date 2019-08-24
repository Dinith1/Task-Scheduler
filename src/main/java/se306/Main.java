package se306;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import guru.nidi.graphviz.model.MutableGraph;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import se306.algorithm.AStarScheduler;
import se306.exceptions.InvalidInputException;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;
import se306.logging.Log;
import javafx.application.Application;
import javafx.stage.Stage;
import net.bytebuddy.asm.Advice.Exit;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import se306.visualisation.backend.GraphController;
import se306.visualisation.backend.GraphParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main class to test InputFileReader functionality
 * 
 * @throws IOException
 */
public class Main extends Application {

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
            GraphParser graphParser = new GraphParser();
            graphParser.parseGraph();

        } catch (InvalidInputException | NumberFormatException e) {
            Log.error(e.getMessage());
            parser.printUsage();
            return;
        }

        if (parser.wantVisual()) {
            launch(args);
        } else {
            startScheduling();
        }

        System.exit(0);
    }

    public static void startScheduling() {
        CommandLineParser parser = CommandLineParser.getInstance();
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

        try {
            ifr.readInput(isr);
            // ifr.pruneIdenticalNodes();

        } catch (IOException e) {
            e.printStackTrace();
        }
        AStarScheduler scheduler = new AStarScheduler();
        scheduler.findOptimalSchedule();// Start scheduling

        long endTime = System.nanoTime();
        Log.info("-- Finished scheduling --");

        long executionTime = endTime - startTime;
        Log.info("Execution Time: " + (executionTime / 1000000) + "ms");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Visualisation");
        primaryStage.setResizable(false);

        // Load the scene of the Player fxml file
        FXMLLoader menuLoader = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
        Parent menuPane = menuLoader.load();

        // Passes required data to controllers
        GraphController ctrl = menuLoader.getController();
        ctrl.createGraph(GraphParser.g);

        Scene menuScene = new Scene(menuPane);
        primaryStage.setScene(menuScene);
        primaryStage.sizeToScene();
        // primaryStage.setMaximized(true);
        CommandLineParser parser = CommandLineParser.getInstance();
        primaryStage.show();
    }
}
