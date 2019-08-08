package se306;

import org.junit.Before;
import org.junit.Test;
import se306.input.Node;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NodesTest {

    @Before
    public void setUp(){

    }

    @Test
    public void testSingleNode(){
        List<Node> emptyList = new ArrayList<>();
        String nodeID = "0";
        int weight = 5;
        Node node = new Node(weight,nodeID);

        assertEquals("0",node.getNodeIdentifier());
        assertEquals(5,node.getNodeWeight());
        assertEquals(0,node.getNumberOfIncomingEdges());
        assertEquals(0,node.getNumberOfOutGoingEdges());
        assertEquals(emptyList,node.getParentNodes());

    }



    @Test
    public void testParentNode(){
        List<Node> parentNodes = new ArrayList<>();
        String nodeID = "0";
        String nodeID1 = "1";
        int weight = 5;
        Node child = new Node(weight,nodeID);
        Node parent = new Node(weight,nodeID1);
        parentNodes.add(parent);
        child.addParent(parent);

        assertEquals(parentNodes,child.getParentNodes());
    }
}
