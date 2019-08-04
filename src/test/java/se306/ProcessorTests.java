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
    private Edge aToB;


    @Before
    public void setup() {
        // Create and setup nodes and processors
        proc1 = new Processor();
        A = new Node(3, "1");
        B = new Node(2, "2");
        C = new Node(10, "3");

        aToB = new Edge(A, B, 5);
    }

    @Test
    public void testInitialSchedule() {

        // Add node A to processor with a weight of 3
        proc1.addToSchedule(A);

        // Check that the current cost of the processor increases with node
        assertEquals(3, proc1.getCurrentCost());

    }

    @Test
    public void testIndependentSchedule() {
        // Add two nodes to processor with a weight of 3
        proc1.addToSchedule(A);
        proc1.addToSchedule(C);

        assertEquals(13, proc1.getCurrentCost());

    }

    @Test
    public void testDependentSchedule() {
        Processor proc2 = new Processor();
        proc2.addToSchedule(B);
        assertEquals(8, proc2.getCurrentCost());
    }
}
