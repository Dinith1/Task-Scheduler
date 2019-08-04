package se306;

import org.junit.Before;
import org.junit.Test;
import se306.Algorithm.Processor;
import se306.Input.Edge;
import se306.Input.Node;

import static org.junit.Assert.assertEquals;

public class ProcessorTests {

    private Processor proc1;
    private Node A, B, C;


    @Before
    public void setup() {
        // Create and setup nodes and processors
        proc1 = new Processor("1");
        A = new Node(3, "1");
        B = new Node(2, "2");
        C = new Node(10, "3");

    }

    @Test
    public void testInitialSchedule() {

        // Add node A to processor with a weight of 3
        proc1.addToSchedule(A);

        // Check that the current cost of the processor increases with node
        assertEquals(3, proc1.getCurrentCost());
        assertEquals(proc1, A.getProcessor());

    }

    @Test
    public void testIndependentSchedule() {
        // Add two nodes to processor with a weight of 3
        proc1.addToSchedule(A);
        proc1.addToSchedule(C);

        assertEquals(13, proc1.getCurrentCost());
        assertEquals(proc1, A.getProcessor());
        assertEquals(proc1, C.getProcessor());

    }

    @Test
    public void testDependentSchedule() {

        proc1.addToSchedule(A);

        Edge aToB = new Edge(A, B, 5);
        B.addParent(A);
        B.addIncomingEdges(aToB);
        A.addOutGoingEdges(aToB);

        Processor proc2 = new Processor("2");
        proc2.addToSchedule(B);
        assertEquals(10, proc2.getCurrentCost());
    }
}
