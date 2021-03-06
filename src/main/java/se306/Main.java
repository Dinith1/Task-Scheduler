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

import se306.visualisation.backend.GraphController;
import se306.visualisation.backend.GraphParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main class to test InputFileReader functionality
 * 
 * @throws IOException
 */
public class Main extends Application {

    public static void main(String[] args) throws IOException {

        // Stop random info messages being printed on console
        stopPackagesPrintingInfo();

        // Parse the command line arguments in
        CommandLineParser parser = CommandLineParser.getInstance();
        try {
            parser.parseCommandLineArguments(args);
            GraphParser graphParser = new GraphParser();
            graphParser.parseGraph();

        } catch (InvalidInputException | NumberFormatException e) {
            Log.error(e.getMessage());
            parser.printUsage();
            System.exit(0);
        }

        // Check if visualisation is enabled
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

        InputFileReader ifr = InputFileReader.getInstance();

        Log.info("-- Starting scheduling --");
        long startTime = System.nanoTime();

        Log.info("preprocessing...");
        ifr.pruneIdenticalNodes();

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

        Scene menuScene = new Scene(menuPane);
        primaryStage.setScene(menuScene);
        primaryStage.sizeToScene();
        // primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Stop packages printing unnecessary information.
     */
    public static void stopPackagesPrintingInfo() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.WARNING);
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.WARNING);
        }
    }
}
