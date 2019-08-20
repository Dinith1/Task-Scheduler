package se306.algorithm;

import se306.input.CommandLineParser;
import se306.input.InputFileReader;
import se306.input.Node;
import se306.output.OutputFileGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStarScheduler {

    PriorityQueue<PartialSchedule> open = new PriorityQueue<>(new CostFunctionComparator());
    List<PartialSchedule> closed = new ArrayList<>();
    public static List<Node> usedNodes = new ArrayList<>();

    private boolean isDuplicate(PartialSchedule schedule){

        //Should check for duplicates.. Should be more complicated than this
        if(closed.contains(schedule) || open.contains(schedule)){
            return true;
        }
        return false;
    }

    /**
     * This function uses the a star algorithm to find the most optimal schedule. It uses a priority queue
     * and creates each schedule by expanding the current one in the queue until a complete state schedule
     * is first in the queue.
     *
     * @param numberOfProcessors
     * @return the optimal partial exception
     * @throws Exception place holder exception
     */
    private PartialSchedule aStarAlgorithm(int numberOfProcessors) throws Exception{
        // OPEN <-- S init
        open.add(getPartialScheduleInit());
        while(!open.isEmpty()){
            //Retrieves head and removes it from the queue
            PartialSchedule currentSchedule = open.peek();

            if(currentSchedule.isComplete()){
                return currentSchedule;
            }
            //EXPAND currentSCHEDULE TO NEW POSSIBLE STATES
            List<PartialSchedule> expandedCurrentSchedule = currentSchedule.expandNewStates();

            for (PartialSchedule s:expandedCurrentSchedule) {
               // s.setCostFunction(s.calculateCostFunction);
                if(!isDuplicate(s)){
                    open.add(s);
                }
            }
            open.remove();
            closed.add(currentSchedule);
        }

        throw new Exception();
    }

    /**
     * This methods finds the optimal schedule by calling the a star algorithm
     */
    public void findOptimalSchedule(){
        try {
            PartialSchedule optimalSchedule = aStarAlgorithm(CommandLineParser.getInstance().getNumberOfProcessors());
            new OutputFileGenerator().generateFile(optimalSchedule.getProcessorList());
        }
        catch (Exception e){
            e.getMessage();
            System.out.print("FAILED");
            }

        }

    /**
     *  This method iterates through the list of available nodes and finds nodes in which all the parents of that node
     *  have already been used into a schedule and updates the list
     * @return freeNodes
     */

    public List<Node> findSchedueableNodes(){
        List<Node> freeNodes = new ArrayList<>();
        for (Node currentNode : InputFileReader.listOfAvailableNodes) {
            if(!usedNodes.contains(currentNode)){
                if(currentNode.getParentNodes().size() == 0){
                    freeNodes.add(currentNode);
                    InputFileReader.listOfAvailableNodes.remove(currentNode);
                }
                else if(usedNodes.containsAll(currentNode.getParentNodes())){
                        freeNodes.add(currentNode);
                        InputFileReader.listOfAvailableNodes.remove(currentNode);
                }
            }
        }
        return freeNodes;
    }

    public PartialSchedule getPartialScheduleInit(){
        return null;
    }
}
