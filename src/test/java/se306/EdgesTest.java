//package se306;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import se306.input.Edge;
//import se306.input.Node;
//import static junit.framework.TestCase.assertEquals;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//
//
//@RunWith(MockitoJUnitRunner.class)
//public class EdgesTest {
//
//    @InjectMocks
//    Edge edge;
//
//    @Mock
//    Node startNodeMock;
//
//    @Mock
//    Node endNodeMock;
//
//
//    /**
//     * Initialises the objects annotated with Mockito annotations.
//     */
//    @Before
//    public void setUp(){
//        MockitoAnnotations.initMocks(this);
//    }
//
//
//    /**
//     * Tests the Edge class methods with a mock Node object
//     */
//    @Test
//    public void testSingleEdge() {
//        int weight = 4;
//        edge = new Edge(startNodeMock,endNodeMock,weight);
//        assertEquals(startNodeMock,edge.getNodeStart());
//        assertEquals(endNodeMock,edge.getNodeEnd());
//        assertEquals(weight,edge.getEdgeWeight());
//
//
//        //Verify that these methods have been called
//        startNodeMock.addOutGoingEdges(edge);
//        endNodeMock.addIncomingEdges(edge);
//        verify(startNodeMock).addOutGoingEdges(edge);
//        verify(endNodeMock).addIncomingEdges(edge);
//        verifyNoMoreInteractions(endNodeMock);
//
//    }
//}
