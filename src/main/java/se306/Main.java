package se306;

import java.io.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import se306.exceptions.InvalidInputException;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;
import se306.logging.Log;
import javafx.application.Application;
import javafx.stage.Stage;
import se306.visualisation.backend.BaseController;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

/**
 * Main class to test InputFileReader functionality
 * 
 * @param args
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

        ifr.readInput(isr); // Start scheduling

        long endTime = System.nanoTime();
        Log.info("-- Finished scheduling --");

        long executionTime = endTime - startTime;
        Log.info("Execution Time: " + (executionTime / 1000000) + "ms");

        String filePath = parser.getInputFileName();
        Graph g = new DefaultGraph("g");
        FileSource fs = FileSourceFactory.sourceFor(filePath);

        // GraphStream
        fs.addSink(g);

        try {
            fs.begin(filePath);

            while (fs.nextEvents()) {
                // Optionally some code here ...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fs.end();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fs.removeSink(g);
        }

        g.display();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Visualisation");
        primaryStage.setResizable(false);

        // Load the scene of the Player fxml file
        FXMLLoader menuLoader = new FXMLLoader(getClass().getClassLoader().getResource("MainMenu.fxml"));
        Parent menuPane = menuLoader.load();

        // BaseController controller = menuLoader.getController();

        // Passes required data to controllers
        // controller.setup(primaryStage);
        // controller.init();

        Scene menuScene = new Scene(menuPane);
        primaryStage.setScene(menuScene);
        primaryStage.sizeToScene();
        primaryStage.show();
        System.out.println("HELLO");
    }
}
