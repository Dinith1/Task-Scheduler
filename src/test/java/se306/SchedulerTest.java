//package se306;
//
//import org.junit.Before;
//import org.junit.Test;
//import se306.algorithm.Processor;
//import se306.algorithm.PartialSchedule;
//import se306.input.Edge;
//import se306.input.Node;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//public class SchedulerTest {
//
//    private List<Node> orderedListNode = new ArrayList<>();
//    Node a;
//    Node b;
//    Node c;
//    Node d;
//    Node e;
//    Node f;
//    Node g;
//
//    /*
//     * This test case mimics Nodes_7_OutTree.dot
//     */
//    @Before
//    public void setupNodes_7_OutTree() {
//
//        // Create nodes
//        a = new Node(5, "0");
//        b = new Node(6, "1");
//        c = new Node(5, "2");
//        d = new Node(6, "3");
//        e = new Node(4, "4");
//        f = new Node(7, "5");
//        g = new Node(7, "6");
//        // Create and setup Edges
//        Edge ab = new Edge(a, b, 15);
//        Edge ac = new Edge(a, c, 11);
//        Edge ad = new Edge(a, d, 11);
//        Edge be = new Edge(b, e, 19);
//        Edge bf = new Edge(b, f, 4);
//        Edge bg = new Edge(b, g, 21);
//
//        // Initialise node a
//        a.addOutGoingEdges(ab);
//        a.addOutGoingEdges(ac);
//        a.addOutGoingEdges(ad);
//        // Initialise node b
//        b.addIncomingEdges(ab);
//        b.addOutGoingEdges(be);
//        b.addOutGoingEdges(bf);
//        b.addOutGoingEdges(bg);
//
//        // Initialise rest (only outgoing)
//        c.addIncomingEdges(ac);
//        d.addIncomingEdges(ad);
//        e.addIncomingEdges(be);
//        f.addIncomingEdges(bf);
//        g.addIncomingEdges(bg);
//
//        b.addParent(a);
//        c.addParent(a);
//        d.addParent(a);
//        e.addParent(b);
//        f.addParent(b);
//        g.addParent(b);
//
//        orderedListNode.add(a);
//        orderedListNode.add(b);
//        orderedListNode.add(c);
//        orderedListNode.add(d);
//        orderedListNode.add(e);
//        orderedListNode.add(f);
//        orderedListNode.add(g);
//
//    }
//
//    /**
//     * This test case mimics the Nodes_7_OutTree.dot with 2 processors and checks
//     * that the scheduling times are correct and each node is in the correct
//     * processor
//     */
//
//    @Test
//    public void testCreateSchedulerWithNodes_7_OutTree_2Processors() {
//        PartialSchedule scheduler = new PartialSchedule();
//
//        scheduler.createSchedule(2, orderedListNode);
//        List<Processor> processList = scheduler.getProcessorList();
//
//        Processor p1 = processList.get(0);
//        Processor p2 = processList.get(1);
//
//        assertEquals((Integer) 5, p1.getSchedule().get(a));
//        assertEquals((Integer) 10, p1.getSchedule().get(c));
//        assertEquals((Integer) 16, p1.getSchedule().get(d));
//        assertEquals((Integer) 49, p1.getSchedule().get(e));
//        assertEquals((Integer) 26, p2.getSchedule().get(b));
//        assertEquals((Integer) 33, p2.getSchedule().get(f));
//        assertEquals((Integer) 40, p2.getSchedule().get(g));
//
//    }
//
//    /**
//     * This test case checks that the start times of the schedule correspond to the
//     * start time of the schedule (before the current node begins)
//     */
//    @Test
//    public void testStartTimes() {
//        PartialSchedule scheduler = new PartialSchedule();
//
//        scheduler.createSchedule(2, orderedListNode);
//        List<Processor> processList = scheduler.getProcessorList();
//
//        Processor p1 = processList.get(0);
//        Processor p2 = processList.get(1);
//
//        assertEquals((Integer) 0, p1.getStartTimes().get(a));
//        assertEquals((Integer) 5, p1.getStartTimes().get(c));
//        assertEquals((Integer) 10, p1.getStartTimes().get(d));
//        assertEquals((Integer) 45, p1.getStartTimes().get(e));
//        assertEquals((Integer) 20, p2.getStartTimes().get(b));
//        assertEquals((Integer) 26, p2.getStartTimes().get(f));
//        assertEquals((Integer) 33, p2.getStartTimes().get(g));
//    }
//
//    /**
//     * Tests that the comparator works for reordering based on process identifier
//     * number
//     */
//    @Test
//    public void testGetProcessList() {
//        PartialSchedule scheduler = new PartialSchedule();
//
//        scheduler.createSchedule(4, orderedListNode);
//        List<Processor> processList = scheduler.getProcessorList();
//
//        assertEquals("0", processList.get(0).getProcessorIdentifier());
//        assertEquals("1", processList.get(1).getProcessorIdentifier());
//        assertEquals("2", processList.get(2).getProcessorIdentifier());
//        assertEquals("3", processList.get(3).getProcessorIdentifier());
//
//    }
//
//}
