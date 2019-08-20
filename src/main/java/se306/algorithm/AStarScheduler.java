package se306.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

public class AStarScheduler {

    PriorityQueue<PartialSchedule> open = new PriorityQueue<>(new CostFunctionComparator());
    List<PartialSchedule> closed = new ArrayList<>();
    int numberOfProcessors;


    private boolean isDuplicate(PartialSchedule schedule){

        //Should check for duplicates.. Should be more complicated than this
        if(closed.contains(schedule) || open.contains(schedule)){
            return true;
        }
        return false;
    }

    public PartialSchedule aStarAlgorithm(){
        // OPEN <-- S init
        open.add(getPartialScheduleInit());
        while(!open.isEmpty()){
            //Retrieves head and removes it from the queue
            PartialSchedule currentSchedule = open.peek();

            if(currentSchedule.isComplete()){
                return currentSchedule;
            }
            //EXPAND currentSCHEDULE TO NEW POSSIBLE STATES
            currentSchedule.expandNewStates(numberOfProcessors);
            List<PartialSchedule> expandedCurrentSchedule = new ArrayList<>();

            for (PartialSchedule s:expandedCurrentSchedule) {
                s.setCostFunction(s.calculateCostFunction);
                if(!isDuplicate(s)){
                    open.add(s);
                }
            }
            open.remove();
            closed.add(currentSchedule);
        }
        return null;
    }

    public PartialSchedule getPartialScheduleInit(){
        return null;
    }
}
