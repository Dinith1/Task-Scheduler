package se306.visualisation.backend;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSource;
import org.graphstream.ui.layout.*;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.ui.swingViewer.Viewer;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;

import java.io.*;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;

import guru.nidi.graphviz.*;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class GraphController {

    @FXML
    ImageView graphImage;

    @FXML
    Label timeElapsed, numberOfNodes, nodesToSchedule;

    public void createGraph() throws IOException {

        CommandLineParser parser = CommandLineParser.getInstance();
        String s = parser.getInputFileName();
        InputStream inputStream  = new FileInputStream(s);

        MutableGraph graph = Parser.read(inputStream);

        File file = new File("temp-graph.png");
        Graphviz.fromGraph(graph).width(1200).render(Format.PNG).toFile(file);

        setNumberOfNodes(Integer.toString(InputFileReader.numNodes));

        Image image = new Image(file.toURI().toString());
        graphImage.setImage(image);

    }

    public void setTimeElapsed(String newText) {
        timeElapsed.setText(newText);
    }

    public void setNumberOfNodes(String s) {
        numberOfNodes.setText(s);
    }

    public void setNodesToSchedule(String newText) {
        nodesToSchedule.setText(newText);
    }

}
