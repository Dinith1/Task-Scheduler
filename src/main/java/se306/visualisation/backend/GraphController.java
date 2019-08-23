package se306.visualisation.backend;

import javafx.collections.FXCollections;
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
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;

import java.io.*;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    @FXML
    private Pane schedulePane;

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

    public void createSchedule() {
        String[] machines = new String[] { "Machine 1", "Machine 2", "Machine 3" };

        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();

        final SchedulesBar<Number,String> chart = new SchedulesBar<>(xAxis,yAxis);
        xAxis.setLabel("");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(machines)));

        chart.setTitle("Machine Monitoring");
        chart.setLegendVisible(false);
        chart.setBlockHeight( 50);
        String machine;

        machine = machines[0];
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(0, machine, new SchedulesBar.ExtraData( 1, "status-red")));
        series1.getData().add(new XYChart.Data(1, machine, new SchedulesBar.ExtraData( 1, "status-green")));
        series1.getData().add(new XYChart.Data(2, machine, new SchedulesBar.ExtraData( 1, "status-red")));
        series1.getData().add(new XYChart.Data(3, machine, new SchedulesBar.ExtraData( 1, "status-green")));

        machine = machines[1];
        XYChart.Series series2 = new XYChart.Series();
        series2.getData().add(new XYChart.Data(0, machine, new SchedulesBar.ExtraData( 1, "status-green")));
        series2.getData().add(new XYChart.Data(1, machine, new SchedulesBar.ExtraData( 1, "status-green")));
        series2.getData().add(new XYChart.Data(2, machine, new SchedulesBar.ExtraData( 2, "status-red")));

        machine = machines[2];
        XYChart.Series series3 = new XYChart.Series();
        series3.getData().add(new XYChart.Data(0, machine, new SchedulesBar.ExtraData( 1, "status-blue")));
        series3.getData().add(new XYChart.Data(1, machine, new SchedulesBar.ExtraData( 2, "status-red")));
        series3.getData().add(new XYChart.Data(3, machine, new SchedulesBar.ExtraData( 1, "status-green")));

        chart.getData().addAll(series1, series2, series3);

        schedulePane.getChildren().add(chart);
    }

    public void startTimeElapsed() {
        String newText = "";
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
        cpuMonitorBar.setLegendVisible(false);
        cpuMonitorBar.getData().addAll(series1);
        createSchedule(); //TODO remote later

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
