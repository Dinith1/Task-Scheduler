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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

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

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        // What % CPU load this current JVM is taking, from 0.0-1.0
        System.out.println(osBean.getProcessCpuLoad());

        // What % load the overall system is at, from 0.0-1.0
        System.out.println(osBean.getSystemCpuLoad());

        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU cores: " + processors);

        // ------

        // System.out.println(graph);

        // String filePath = parser.getInputFileName();
        // Graph g = new DefaultGraph("g");
        // FileSource fs = FileSourceFactory.sourceFor(filePath);

        // GraphStream
        // fs.addSink(g);

        // try {
        // fs.begin(filePath);

        // while (fs.nextEvents()) {
        // // Optionally some code here ...
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // try {
        // fs.end();
        // } catch (IOException e) {
        // e.printStackTrace();
        // } finally {
        // fs.removeSink(g);
        // }

        // Graph g = new DefaultGraph("g");

        // double x = 0, y = 0, z = 0;

        // for (se306.input.Node n : InputFileReader.listOfSortedNodesStatic) {
        // Node node = g.addNode(n.getNodeIdentifier());
        // node.setAttribute("label", n.getNodeIdentifier());
        // node.setAttribute("x", x);
        // node.setAttribute("y", y);
        // // node.setAttribute("z", z);
        // // System.out.println(x);
        // node.setAttribute("shape", "freeplane");
        // node.setAttribute("size", "100px");
        // node.setAttribute("size-mode", "fit");

        // // + " shape: freeplane;"
        // // + " size: 100px;"
        // // + " size-mode: fit;"
        // x += 2000;
        // y -= 4000;
        // // z += 400;
        // }

        // for (se306.input.Edge e : InputFileReader.listOfEdgesStatic) {
        // String startNode = e.getNodeStart().getNodeIdentifier();
        // String endNode = e.getNodeEnd().getNodeIdentifier();
        // g.addEdge(startNode + endNode, startNode, endNode, true);
        // }

        // g.setAttribute("ui.stylesheet", styleSheet);

        CommandLineParser parser = CommandLineParser.getInstance();
        System.out.println("\n\n\nIFN: " + parser.getInputFileName() + "\n\n\n");
        Class<?> c = getClass();
        System.out.println("\n\n\nCLASS: " + c + "\n\n\n");
        // String s = "../" + parser.getInputFileName();
        String s = "Nodes_7_OutTree.dot";
        System.out.println("\n\n\nSTRING: " + s + "\n\n\n");
        InputStream is = GraphController.class.getClassLoader().getResourceAsStream(s);
        System.out.println("\n\n\nISR: " + is + "\n\n\n");
        MutableGraph graph = Parser.read(is);

        System.out.println("\n\n\n" + graph + "\n\n\n");

        Graphviz.fromGraph(graph).width(1200).render(Format.PNG).toFile(new File("temp-graph.png"));

        // graph.graphAttrs().add(Color.WHITE.gradient(Color.rgb("888888")).background().angle(90)).nodeAttrs()
        //         .add(Color.WHITE.fill()).nodes()
        //         .forEach(node -> node.add(Color.named(node.name().toString()), Style.lineWidth(4).and(Style.FILLED)));
        // Graphviz.fromGraph(graph).width(700).render(Format.PNG).toFile(new File("temp-graph.png"));

        // System.setProperty("org.graphstream.ui.renderer",
        // "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        // FileSinkImages pic = new FileSinkImages(FileSinkImages.OutputType.PNG, FileSinkImages.Resolutions.HD1080);
        // pic.setRenderer(FileSinkImages.RendererType.SCALA);
        // pic.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_AT_NEW_IMAGE);

        // try {
            // pic.writeAll(g, "sample.png");
            // File file = new File("sample.png");
            // Image image = new Image(file.toURI().toString());
            // graphImage.setImage(image);
            // System.out.println(graphImage);
        // } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        // }

        setNumberOfNodes(Integer.toString(InputFileReader.numNodes));

        // Iterable<Node> ite = (Iterable<Node>) g.getEachNode();

        // for (Node n : ite) {
        //     Iterable<String> attr = n.getAttributeKeySet();

        //     for (String s : attr) {
        //         Object o = n.getAttribute(s);
        //         System.out.println(s + ": " + o + "\n");
        //     }
        // }

        // Viewer v = g.display();
        // v.disableAutoLayout();

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

    // private String styleSheet = "" + "graph {" + " canvas-color: white; " + "
    // fill-mode: gradient-radial; "
    // + " fill-color: white, #EEEEEE; " + " padding: 60px; " + "}" + "" + "node {"
    // // + " shape: freeplane;"
    // + " size: 100px;"
    // // + " size-mode: fit;"
    // + " fill-mode: plain;" + " fill-color: rgba(255,0,0,128);" + " text-mode:
    // normal;"
    // + " text-alignment: center;" + " text-size: 50px;"
    // // + " stroke-mode: plain;"
    // // + " stroke-color: blue;"
    // // + " stroke-width: 3px;"
    // // + " padding: 5px, 1px;"
    // // + " shadow-mode: none;"
    // // + " icon-mode: at-left;"
    // // + " text-style: normal;"
    // // + " text-font: 'Droid Sans';"
    // + "}" + "" + "node:clicked {" + " stroke-mode: plain;" + " stroke-color:
    // red;" + "}" + ""
    // + "node:selected {" + " stroke-mode: plain;" + " stroke-color: blue;" + "}" +
    // ""
    // // + "sprite {"
    // // + " sprite-orientation: "
    // // + "}"
    // // + ""
    // + "edge {" + " shape: freeplane;" + " size: 10px;" + " fill-color: grey;" + "
    // fill-mode: plain;"
    // + " shadow-mode: none;" + " shadow-color: rgba(0,0,0,100);" + "
    // shadow-offset: 3px, -3px;"
    // + " shadow-width: 0px;" + " arrow-shape: arrow;" + " arrow-size: 100px,
    // 20px;" + "}";
}
