package se306.algorithm;

import se306.input.InputFileReader;
import se306.input.Node;

import java.util.*;

public class PartialSchedule {

    // User defined available processors placed in a list
    private List<Processor> processorList = new ArrayList<>();
    private int costFunction;

    public PartialSchedule(int processorNumber) {
        createProcessors(processorNumber);
    }
    public PartialSchedule(PartialSchedule ps){
        this.processorList = new ArrayList<>();
        for (Processor p:ps.getProcessorList()
             ) {
            Processor newProcessor = new Processor(p);
            this.processorList.add(newProcessor);

        }
        this.costFunction = ps.costFunction;

    }
    /**
     * Comparator to be used with resorting the processor list back into the process
     * identifier number order
     */
    private Comparator<Processor> sortByIdentifierNumber = new Comparator<se306.algorithm.Processor>() {
        public int compare(Processor p1, Processor p2) {
            if (p1.getProcessorID() < p2.getProcessorID()) {
                return -1;
            }
            return 1;
        }
    };

    /**
     * This method finds the nodes that already have been scheduled in this schedule
     * @return scheduledNodes
     */
    public Set<Node> getUsedNodes(){
        Set<Node> scheduledNodes = new HashSet<>();
        for (Processor p: processorList) {
            //For each processor node map turn it into a hashSet of keys
            scheduledNodes.addAll(p.getScheduledNodes());
        }
        return scheduledNodes;
    }

    public List<PartialSchedule> expandNewStates(){
        List<PartialSchedule> newExpandedSchedule = new ArrayList<>();
        //FIND HOW MANY NODES NEED TO BE SCHEDULED FOR THE EXPANSION
        List<Node> nodes = findSchedulableNodes();
        for(int i = 0;i<nodes.size();i++){
            Node currentNode = nodes.get(i);
            //Get each node that needs to be scheduled
            for(int j = 0; j<processorList.size(); j++){
                PartialSchedule newSchedule = new PartialSchedule(this);
                //Add it to each processor and make that many corresponding schedules
                newSchedule.addToProcessor(j, currentNode);
                //Add the schedule to overall expanded list
                newExpandedSchedule.add(newSchedule);
            }
        }
        return newExpandedSchedule;
    }
    /**
     *  This method iterates through the list of available nodes and finds nodes in which all the parents of that node
     *  have already been used into a schedule and updates the list
     * @return freeNodes
     */

    private List<Node> findSchedulableNodes(){
        List<Node> freeNodes = new ArrayList<>();
        //Loops through all nodes
        for (Node currentNode : InputFileReader.listOfAvailableNodes) {
            //Checks if the node is in used nodes already
            if(!this.getUsedNodes().contains(currentNode)){
                //if no parents then add to list
                if(currentNode.getParentNodes().size() == 0){
                    freeNodes.add(currentNode);
                }
                //if all parents are used add to list
                else if(this.getUsedNodes().containsAll(currentNode.getParentNodes())){
                    freeNodes.add(currentNode);
                }
            }
        }
        return freeNodes;
    }
    /**
     * This method adds the node into the specified processor number
     *
     * @param processorNumber - the processor for the node to be added to
     * @param node - the node to be added
     */
    private void addToProcessor(int processorNumber, Node node){
        //Adds the node into the corresponding processor
        this.getProcessorList().get(processorNumber).addNode(node,this,processorNumber);
    }
    /**
     * Creates processors and adds it to the list
     *
     * @param numProcessors - Number of processors to add
     */
    private void createProcessors(int numProcessors) {
        for (int i = 0; i < numProcessors; i++) {
            processorList.add(new se306.algorithm.Processor(i));
        }
    }

    /**
     * Method that checks that every node in listOfAvailableNodes are in used nodes for this
     * current schedule
     *
     * @return true if all nodes used or else false
     */
     boolean isComplete(){
        return (getUsedNodes().containsAll(InputFileReader.listOfAvailableNodes));
    }

    /**
     * Returns list of Processor objects that have the nodes scheduled in order of
     * the processor identifier number
     */
    public List<Processor> getProcessorList() {
        Collections.sort(processorList,sortByIdentifierNumber);
        return processorList;
    }

    public int getFinishTime() {
        int finishTime = 0;
        for (Processor p : processorList) {
            if (p.getCurrentCost() > finishTime) {
                finishTime = p.getCurrentCost();
            }
        }
        return finishTime;
    }

    public int calculateCostFunction() {
        return getFinishTime();
    }

    public int getCostFunction(){
        return costFunction;
    }

    public void setCostFunction(int costFunction){
        this.costFunction = costFunction;
    }

    public int calculateStartTime(Node node, int processorNumber){
        int maxStartTime = 0;
        //Best starting time of current node if no communication costs
        int currentStartTime = processorList.get(processorNumber).getCurrentCost();
        List<Node> parentNodes = node.getParentNodes();
        for (Processor p:processorList) {
            for (Node n: parentNodes) {
                //If current processor contains a parent of "node" then calculate the the start time needed
                if(p.getScheduledNodes().contains(n)){
                    //If parent node is not scheduled in same processor
                    if(p.getProcessorID() != processorNumber){

                        //Find end time of the parent node
                        int endTimeOfParent = p.getStartTimes().get(p.getScheduledNodes().indexOf(n)) + n.getNodeWeight();
                        int communicationCost = node.getIncomingEdge(n).getEdgeWeight();

                        if(endTimeOfParent > currentStartTime){
                            currentStartTime = endTimeOfParent + communicationCost;
                        }


                    }
                    if(maxStartTime < currentStartTime){
                        maxStartTime = currentStartTime;
                    }
                }
            }
        }
        return maxStartTime;
    }
}
