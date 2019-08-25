package se306.visualisation.backend;

import com.sun.management.OperatingSystemMXBean;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import org.graphstream.graph.*;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.graphstream.graph.implementations.SingleGraph;
import se306.Main;
import se306.algorithm.Processor;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.*;


public class GraphController implements Initializable {

    @FXML
    ImageView graphImage;

    @FXML
    Label timeElapsed, numberOfNodes;

    @FXML
    CategoryAxis cpu;

    @FXML
    NumberAxis cpuId;

    @FXML
    private AnchorPane schedulePane;

    @FXML
    private Button startBtn;

    @FXML
    private Tile cpuUsage, memoryUsage;

    Timeline countProgress = new Timeline();
    private SchedulesBar<Number, String> chart;
    private NumberAxis xAxis;
    private CategoryAxis yAxis;
    private static final double STARTTIME = 0;
    private final DoubleProperty seconds = new SimpleDoubleProperty(STARTTIME);

    /**
     * This gets called when the controller is loaded for the first time
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTile();
        timeElapsed.textProperty().bind(((seconds.divide(1000.00)).asString()));
        CommandLineParser parser = CommandLineParser.getInstance();
        if (parser.wantVisual()) {
            Main.startScheduling();
        }
        initializeSchedule();
        try {
            createGraph();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates time for the timeelapsed value
     */
    private void updateTime(){
        double seconds = this.seconds.get();
        this.seconds.set(seconds+1);
    }

    /**
     * Listens for when the start button is pressed by the user
     * @param event
     */
    @FXML
    void handleStart(MouseEvent event) {
        Task<Void> schedule = new Task<Void>() {
            @Override
            public Void call(){
                Main.startScheduling();
                return null;
            }
        };
        schedule.setOnSucceeded(e -> { //Once tasks finished then it should re enable buttons
            countProgress.stop();
            populateSchedule();
        });

        startTimer();
        new Thread(schedule).start();
        countProgress.play();
        startBtn.setDisable(true);
    }

    /**
     * Starts timer of timeelapsed
     */
    private void startTimer(){
        countProgress = new Timeline(new KeyFrame(Duration.millis(1),evt-> updateTime()));
        countProgress.setCycleCount((Animation.INDEFINITE));
        seconds.set(STARTTIME);
    }

    /**
     * Creates the graph that displays the nodes
     * @throws IOException
     */
    public void createGraph() throws IOException {
        Graph graph = new SingleGraph("Nodes Graph");
        for (int i = 0; i < InputFileReader.NUM_NODES; i++) {
            Node node = graph.addNode("" + i);
            node.addAttribute("ui.label", InputFileReader.nodeWeights.get(i));
        }
        for (int i = 0; i < InputFileReader.NUM_EDGES; i++) {
            Edge e = graph.addEdge("" + i, "" + InputFileReader.listOfEdges[i][0], "" + InputFileReader.listOfEdges[i][1]);
            e.setAttribute("ui.label", InputFileReader.listOfEdges[i][2]);
        }
        graph.display();
    }

    /**
     * Sets up the schedule visualisation as an empty graph, to be populated by populateSchedule() method
     */
    public void initializeSchedule() {
        xAxis = new NumberAxis();
        yAxis = new CategoryAxis();

        chart = new SchedulesBar<>(xAxis,yAxis);
        xAxis.setLabel("");
        xAxis.setTickLabelFill(Color.WHITE);
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.WHITE);
        yAxis.setTickLabelGap(10);

        chart.setTitle("Final schedule");
        chart.setLegendVisible(false);
        schedulePane.setRightAnchor(chart, 0.0);
        schedulePane.getChildren().add(chart);
    }

    /**
     * This is called when the algorithm finalises, it builds the nodes for each processor to be visualised
     * based on the final output. The schedule graph reflects the output dot file.
     */
    public void populateSchedule() {
        CommandLineParser parser = CommandLineParser.getInstance();
        String[] processors = new String[parser.getNumberOfProcessors()];
        for (int i = 0; i < parser.getNumberOfProcessors(); i++) {
            processors[i] = "Processor" + (i);
        }

        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(processors)));
        chart.setBlockHeight( schedulePane.getPrefHeight() / (parser.getNumberOfProcessors() + 30));

        Collection<Processor> processorList = ScheduleParser.getInstance().getProcessorList();

        int i = 0;
        for (Processor p : processorList) {
            XYChart.Series series = new XYChart.Series();
            for (Integer j : p.getScheduledNodes()) {
                series.getData().add(new XYChart.Data(p.getStartTimes().get(j), processors[i], new SchedulesBar.ExtraData(InputFileReader.nodeWeights.get(j), "status-blue")));
            }
        i++;
        chart.getData().add(series);
        }

        chart.getStylesheets().add(getClass().getResource("/schedule.css").toExternalForm());
        schedulePane.setLeftAnchor(chart, 0.0);
        schedulePane.setRightAnchor(chart, 0.0);
        schedulePane.setTopAnchor(chart, 0.0);
        schedulePane.setBottomAnchor(chart, 0.0);

        schedulePane.getChildren().add(chart);
    }

    /**
     * The number of nodes specified in the input dot file
     * @param s
     */
    public void setNumberOfNodes(String s) {
        numberOfNodes.setText(s);
    }

    /**
     * Starts running the cpu usage and memory usage timelines in a new thread, is continuously changing
     */
    private void populateTile() {
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();
        cpuUsage.setSkinType(Tile.SkinType.SMOOTH_AREA_CHART);
        cpuUsage.setTitle("CPU Usage");
        cpuUsage.isAnimated();
        cpuUsage.setUnit("%");
        memoryUsage.setSkinType(Tile.SkinType.SMOOTH_AREA_CHART);
        memoryUsage.setTitle("Memory Usage");
        memoryUsage.isAnimated();
        memoryUsage.setUnit("%");

        List<ChartData> cpuUsageData = new LinkedList<>();
        List<ChartData> memoryUsageData = new LinkedList<>();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override public void run() {
                double currentCpuUsage =  bean.getSystemCpuLoad() * 100;
                long ramTotal = Runtime.getRuntime().totalMemory();
                long ramUsed = ramTotal - Runtime.getRuntime().freeMemory();
                double currentMemoryUsage = ((ramUsed * 1.0) / ramTotal) * 100;
                ((LinkedList<ChartData>) memoryUsageData).addLast(new ChartData("Item 1", currentMemoryUsage, Tile.BLUE));
                ((LinkedList<ChartData>) cpuUsageData).addLast(new ChartData("Item 1", currentCpuUsage, Tile.BLUE));
                if (cpuUsageData.size() > 20) {
                    ((LinkedList<ChartData>) cpuUsageData).removeFirst();
                    ((LinkedList<ChartData>) memoryUsageData).removeFirst();
                }
                Platform.runLater(() -> cpuUsage.setChartData(cpuUsageData));
                Platform.runLater(() -> memoryUsage.setChartData(memoryUsageData));

            }
        }, 0L, 200L);


    }

}
