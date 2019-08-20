package se306.algorithm;

import se306.input.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PartialSchedule {

    // User defined available processors placed in a list
    private List<Processor> processorList = new ArrayList<>();
    private int costFunction;

    /**
     * Comparator to be used with resorting the processor list back into the process
     * identifier number order
     */
    private Comparator<Processor> sortByIdentifierNumber = new Comparator<se306.algorithm.Processor>() {
        public int compare(Processor p1, Processor p2) {
            if (Integer.parseInt(p1.getProcessorIdentifier()) < Integer.parseInt(p2.getProcessorIdentifier())) {
                return -1;
            }
            return 1;
        }
    };
    public List<PartialSchedule> expandNewStates(){
        List<PartialSchedule> newExpandedSchedule = new ArrayList<>();
        //FIND HOW MANY NODES NEED TO BE SCHEDULED FOR THE EXPANSION
        int numberOfNodesThatNeedToBeScheduled = 1;
        for(int i = 0;i<numberOfNodesThatNeedToBeScheduled;i++){
            PartialSchedule currentSchedule = this;
            //Get each node that needs to be scheduled
            Node nodeThatNeedsToBeScheduled = new Node();
            for(int j = 0; j<processorList.size(); j++){
                //Add it to each processor and make that many corresponding schedules
                addToProcessor(j, nodeThatNeedsToBeScheduled);
            }
            //Add it to each processor and make that many corresponding schedules
            newExpandedSchedule.add(currentSchedule);
        }
    }

    private void addToProcessor(int processorNumber, Node node){
        //Adds the node into the corresponding processor
        this.getProcessorList().get(processorNumber).addNode(node);
    }
    /**
     * Add processors to the program
     *
     * @param numProcessors - Number of processors to add
     */
    private void createProcessors(int numProcessors) {
        for (int i = 0; i < numProcessors; i++) {
            String processorIdentifier = Integer.toString(i);
            processorList.add(new se306.algorithm.Processor(processorIdentifier));
        }
    }

    public boolean isComplete(){

        //THIS METHOD SHOULD CHECK IF ALL NODES HAVE BEEN SCHEDULED
        //(MIGHT CALL A METHOD IN PROCESSOR CLASS TO CHECK THE MAPS)
        return false;
    }

    /**
     * Returns list of Processor objects that have the nodes scheduled in order of
     * the processor identifier number
     */
    public List<Processor> getProcessorList() {
        Collections.sort(processorList, sortByIdentifierNumber);
        return processorList;
    }

    public int getCostFunction(){
        return costFunction;
    }

    public void setCostFunction(int costFunction){
        this.costFunction = costFunction;
    }

}
