package se306.visualisation.backend;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;

import java.io.*;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class GraphController implements Initializable {

    @FXML
    ImageView graphImage;

    @FXML
    Label timeElapsed, numberOfNodes, nodesToSchedule;

    @FXML
    BarChart<String, Number> cpuMonitorBar;

    @FXML
    CategoryAxis cpu;

    @FXML
    NumberAxis cpuId;

    private int totalNodes = 0, totalEdges = 0;

    public void createGraph() throws IOException {

        CommandLineParser parser = CommandLineParser.getInstance();
        String s = parser.getInputFileName();
        InputStreamReader isr = new FileReader(parser.getInputFileName());
        InputStream inputStream  = new FileInputStream(s);

        getNumberOfNodesAndEdges(isr);
        MutableGraph graph = Parser.read(inputStream);

        File file = new File("temp-graph.png");
        Graphviz.fromGraph(graph).width(1200).render(Format.PNG).toFile(file);

        setNumberOfNodes(Integer.toString(InputFileReader.numNodes));

        Image image = new Image(file.toURI().toString());
        setNumberOfNodes(Integer.toString(totalNodes));
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        cpuMonitorBar.setTitle("cpu Monitor");
        cpu.setLabel("cateogory");
        cpu.setTickLabelRotation(90);
        cpuId.setLabel("num");

        series1.setName("asdfasdf");
        CommandLineParser parser = CommandLineParser.getInstance();
        series1.getData().add(new XYChart.Data<>("five", 1));
        series1.getData().add(new XYChart.Data<>("four", 1));
        series1.getData().add(new XYChart.Data<>("three", 1));
        series1.getData().add(new XYChart.Data<>("two", 1));
        cpuMonitorBar.getData().addAll(series1);

    }

    private void getNumberOfNodesAndEdges(InputStreamReader isr) {
        BufferedReader buffRead = new BufferedReader(isr);

        try {
            String line;
            buffRead.readLine();
            while ((line = buffRead.readLine()) != null) {
                String end = line.substring(0, 1);

                // Stop reading once it reaches end of file
                if (end.equalsIgnoreCase("}")) {
                    break;
                }
                // If the line is not a line that includes a node or an edge
                Pattern p = Pattern.compile(".*\\[Weight=.*];");
                Matcher m = p.matcher(line);
                if (!m.matches()) {
                    continue;
                }

                if (line.indexOf("->") == -1) { // Handle nodes
                    totalNodes++;
                } else {
                    totalEdges++;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
