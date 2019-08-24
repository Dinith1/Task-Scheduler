package se306.visualisation.backend;

import com.jezhumble.javasysmon.JavaSysMon;
import com.sun.javafx.runtime.VersionInfo;
import eu.hansolo.tilesfx.Tile;
import javafx.animation.AnimationTimer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
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
import se306.Main;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

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
    private AnchorPane schedulePane;

    @FXML
    private Button startBtn;

    @FXML
    private Tile progressTile;

    @FXML
    void handleStart(MouseEvent event) {
        Main.startScheduling();
        startTimeElapsed();
        startBtn.setDisable(true);
    }

    public void createGraph(MutableGraph graph) throws IOException {

        File file = new File("temp-graph.png");
        Graphviz.fromGraph(graph).width(1200).render(Format.PNG).toFile(file);

        Image image = new Image(file.toURI().toString());
        setNumberOfNodes(Integer.toString(GraphParser.totalNodes));
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
        series2.getData().add(new XYChart.Data(2, machine, new SchedulesBar.ExtraData( 20, "status-red")));

        machine = machines[2];
        XYChart.Series series3 = new XYChart.Series();
        series3.getData().add(new XYChart.Data(0, machine, new SchedulesBar.ExtraData( 1, "status-blue")));
        series3.getData().add(new XYChart.Data(3, machine, new SchedulesBar.ExtraData( 1, "status-green")));

        chart.getData().addAll(series1, series2, series3);
        chart.getStylesheets().add(getClass().getResource("/schedule.css").toExternalForm());
        schedulePane.setLeftAnchor(chart, 0.0);
        schedulePane.setRightAnchor(chart, 0.0);
        schedulePane.setTopAnchor(chart, 0.0);
        schedulePane.setBottomAnchor(chart, 0.0);

        schedulePane.getChildren().add(chart);
    }

    public void startTimeElapsed() {
        AnimationTimer timer = new AnimationTimer() {
            private long timestamp;
            private long time = 0;
            private long fraction = 0;

            @Override
            public void start() {
                // current time adjusted by remaining time from last run
                timestamp = System.currentTimeMillis() - fraction;
                super.start();
            }

            @Override
            public void stop() {
                super.stop();
                // save leftover time not handled with the last update
                fraction = System.currentTimeMillis() - timestamp;
            }

            @Override
            public void handle(long now) {
                long newTime = System.currentTimeMillis();
                if (timestamp <= newTime) {
                    long deltaT = (newTime - timestamp) ;
                    time += deltaT;
                    timestamp += deltaT;
                    timeElapsed.setText(Long.toString(time / 1000) + ":" + Long.toString(time % 1000));
                }
            }
        };

        timer.start();
    }

    public void setNumberOfNodes(String s) {
        numberOfNodes.setText(s);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCpuMonitor();
        createSchedule(); //TODO remove later
        populateTile();
        CommandLineParser parser = CommandLineParser.getInstance();
        if (!parser.wantVisual()) {
            Main.startScheduling();
        }
    }

    private void initCpuMonitor() {
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        cpuMonitorBar.setTitle("cpu Monitor");
        cpu.setLabel("cateogory");
        cpu.setTickLabelRotation(90);
        cpuId.setLabel("num");

        cpuMonitorBar.setLegendVisible(false);
        series1.setName("asdfasdf");
        JavaSysMon monitor = new JavaSysMon();
//
//        Task<Integer> task = new Task<Integer>() {
//            @Override protected Integer call() throws Exception {
//                while (true) {
//                    if (isCancelled()) {
//                        break;
//                    }
//                    System.out.println(monitor.cpuFrequencyInHz()/1000000);
//                }
//                return 0;
//            }
//        };
//
//        Thread thread = new Thread(task);
//        thread.start();


    }

    private void populateTile() {
        progressTile.setTitle("Schedule progress");
        progressTile.setSkinType(Tile.SkinType.CIRCULAR_PROGRESS);
        progressTile.setValue(new Random().nextDouble() * 120);
    }

}
