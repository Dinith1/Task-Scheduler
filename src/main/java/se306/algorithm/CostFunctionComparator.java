package se306.algorithm;

import java.util.Comparator;

/**
 * Comparator to check that the smallest cost function is at the start of the OPEN priority queue
 */
public class CostFunctionComparator implements Comparator<PartialSchedule> {

    public int compare(PartialSchedule s1, PartialSchedule s2){
        if(s1.getCostFunction()<s2.getCostFunction()){
            return -1;
        }
        else if(s1.getCostFunction() > s2.getCostFunction()){
            return 1;
        }
        else{
            if(s1.numberOfNodesScheduled >= s2.numberOfNodesScheduled) {
                return -1;
            }
            else{
                return 1;
            }
        }
    }
}
