package se306;

 import org.junit.Before;
 import org.junit.Test;
 import se306.algorithm.PartialSchedule;
 import se306.algorithm.Processor;

 import java.util.HashMap;
 import java.util.HashSet;

 import static junit.framework.TestCase.assertTrue;
 import static org.junit.Assert.assertEquals;

public class ProcessorTest {
//
    // private Processor proc1,proc2;
    // private Node A, B, C;

//    @Before
//    public void setup() {
//        // Create and setup nodes and processors
//        proc1 = new Processor("1");
//        A = new Node(3, "1");
//        B = new Node(2, "2");
//        C = new Node(10, "3");
//
//    }
//
//    @Test
//    public void testInitialSchedule() {
//
//        // Add node A to processor with a weight of 3
//        proc1.addNode(A);
//
//        // Check that the current cost of the processor increases with node
//        assertEquals(3, proc1.getCurrentCost());
//        assertEquals(proc1, A.getProcessor());
//
//    }
//
//    @Test
//    public void testIndependentSchedule() {
//        // Add two nodes to processor with a weight of 3
//        proc1.addNode(A);
//        proc1.addNode(C);
//
//        assertEquals(13, proc1.getCurrentCost());
//        assertEquals(proc1, A.getProcessor());
//        assertEquals(proc1, C.getProcessor());
//
//    }
//
//    @Test
//    public void testDependentSchedule() {
//
//        proc1.addNode(A);
//
//        Edge aToB = new Edge(A, B, 5);
//        B.addParent(A);
//        B.addIncomingEdges(aToB);
//        A.addOutGoingEdges(aToB);
//
//        Processor proc2 = new Processor("2");
//        proc2.addNode(B);
//        assertEquals(10, proc2.getCurrentCost());
//    }

     @Test
     public void checkProcessorHashCode() {

         // Create and setup nodes and processors

         PartialSchedule ps = new PartialSchedule(2);
         PartialSchedule ps2 = new PartialSchedule(2);

         Integer A = 1;
         Integer B = 2;
         Integer C = 3;

         // Add nodes to both processors
         ps.getProcessorList().get(0).addNode(A,ps,0);
         ps.getProcessorList().get(0).addNode(B,ps,0);
         ps.getProcessorList().get(0).addNode(C,ps,0);

         ps.getProcessorList().get(1).addNode(A,ps,1);
         ps.getProcessorList().get(1).addNode(C,ps,1);
         ps.getProcessorList().get(1).addNode(B,ps,1);

         ps2.getProcessorList().get(0).addNode(A,ps,0);
         ps2.getProcessorList().get(0).addNode(C,ps,0);
         ps2.getProcessorList().get(0).addNode(B,ps,0);

         ps2.getProcessorList().get(1).addNode(A,ps,1);
         ps2.getProcessorList().get(1).addNode(B,ps,1);
         ps2.getProcessorList().get(1).addNode(C,ps,1);

         System.out.println(ps.getProcessorList().get(0).hashCode());
         System.out.println(ps.getProcessorList().get(1).hashCode());
         System.out.println(ps2.getProcessorList().get(0).hashCode());
         System.out.println(ps2.getProcessorList().get(1).hashCode());
         HashSet<PartialSchedule> set = new HashSet<>();
         set.add(ps);
         System.out.println("Hash1: " + ps.getProcessorList().hashCode() + "Hash2" + ps2.getProcessorList().hashCode());
         System.out.println(ps.hashCode() + " " + ps2.hashCode());
         System.out.println(set.contains(ps2));
         System.out.println(ps.equals(ps2));
//         if (set.contains(ps2)){
//             assertTrue(true);
//         } else {
//             System.out.println(ps2.equals(ps));
//             System.out.println("FAILED");
//         }
     }
}
//}
