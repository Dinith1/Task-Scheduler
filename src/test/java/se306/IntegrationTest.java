package se306;

import org.junit.Before;
import org.junit.Test;
import se306.algorithm.Processor;
import se306.algorithm.Scheduling;
import se306.input.Edge;
import se306.input.Node;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    Node a;
    Node b;
    Node c;
    Node d;
    Node e;
    Node f;
    Node g;
    private List<Node> orderedListNode = new ArrayList<>();

    /*
     * This test case mimics Nodes_13.dot
     */
    @Before
    public void setupNodes_13() {

        // Create nodes
        a = new Node(5, "0");
        b = new Node(6, "1");
        c = new Node(7, "2");
        d = new Node(8, "3");
        e = new Node(9, "4");
        f = new Node(20, "5");
        g = new Node(123, "6");
        // Create and setup Edges
        Edge af = new Edge(a, f, 7);
        Edge bf = new Edge(b, f, 8);
        Edge cf = new Edge(c, f, 9);
        Edge df = new Edge(d, f, 10);
        Edge ef = new Edge(e, f, 11);

        // Initialise nodes with outgoing edges
        a.addOutGoingEdges(af);
        b.addOutGoingEdges(bf);
        c.addOutGoingEdges(cf);
        d.addOutGoingEdges(df);
        e.addOutGoingEdges(ef);

        // Initialise nodes with incoming edges
        f.addIncomingEdges(af);
        f.addIncomingEdges(bf);
        f.addIncomingEdges(cf);
        f.addIncomingEdges(df);
        f.addIncomingEdges(ef);

        f.addParent(a);
        f.addParent(b);
        f.addParent(c);
        f.addParent(d);
        f.addParent(e);

        orderedListNode.add(a);
        orderedListNode.add(b);
        orderedListNode.add(c);
        orderedListNode.add(d);
        orderedListNode.add(e);
        orderedListNode.add(g);
        orderedListNode.add(f);
    }

    /**
     * This test case mimics the Nodes_13.dot with 2 processors and checks
     * that the scheduling times are correct and each node is in the correct
     * processor.
     */

    @Test
    public void testCreateSchedulerWithNodes_13_2Processors() {
        Scheduling scheduler = new Scheduling();

        scheduler.createSchedule(2, orderedListNode);
        List<Processor> processList = scheduler.getProcessorList();

        Processor p1 = processList.get(0);
        Processor p2 = processList.get(1);

        // Check processor 1 schedules
        assertEquals((Integer) 5, p1.getSchedule().get(a));
        assertEquals((Integer) 12, p1.getSchedule().get(c));
        assertEquals((Integer) 21, p1.getSchedule().get(e));
        assertEquals((Integer) 51, p1.getSchedule().get(f));

        // Check processor 2 schedules
        assertEquals((Integer) 6, p2.getSchedule().get(b));
        assertEquals((Integer) 14, p2.getSchedule().get(d));
    }
}
