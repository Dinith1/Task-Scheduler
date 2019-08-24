package se306.algorithm;

import se306.input.CommandLineParser;
import se306.output.OutputFileGenerator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class AStarScheduler {

    private PriorityQueue<PartialSchedule> open = new PriorityQueue<>(new CostFunctionComparator());
    private HashSet<PartialSchedule> createdSchedules = new HashSet<>();

    /**
     * This function uses the a star algorithm to find the most optimal schedule. It
     * uses a priority queue and creates each schedule by expanding the current one
     * in the queue until a complete state schedule is first in the queue.
     *
     * @param numberOfProcessors - number of processors specified by user
     * @return the optimal partial Schedule
     * @throws Exception place holder exception
     */
    private PartialSchedule aStarAlgorithm(int numberOfProcessors) throws Exception {
        // OPEN <-- S init
        getPartialScheduleInitial(numberOfProcessors);
        while (!open.isEmpty()) {
            // Retrieves head and removes it from the queue
            PartialSchedule currentSchedule = open.peek();
            if (currentSchedule != null) {
                if (currentSchedule.isComplete()) {
                    return currentSchedule;
                }

                // EXPAND currentSCHEDULE TO NEW POSSIBLE STATES
                HashSet<PartialSchedule> expandedCurrentSchedule = new HashSet<>(currentSchedule.expandNewStates());
                for (PartialSchedule s : expandedCurrentSchedule) {
                    if(!createdSchedules.contains(s)){
                        s.setCostFunction(s.calculateCostFunction());
                        open.add(s);
                        createdSchedules.add(s);
                    }
                }
                open.remove(currentSchedule);
            }
        }
        throw new Exception();
    }

    /**
     * This methods finds the optimal schedule by calling the a star algorithm
     */
    public void findOptimalSchedule() {
        try {
            PartialSchedule optimalSchedule = aStarAlgorithm(CommandLineParser.getInstance().getNumberOfProcessors());
            OutputFileGenerator.getInstance().generateFile(optimalSchedule.getProcessorList());
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }

    }

    /**
     * Initialisation method that finds the best initial schedule to expand with
     *
     * @param numberOfProcessors - number of processors specified by user
     * @return a partialSchedule which has the lowest cost function to start out
     *         with
     */
    public void getPartialScheduleInitial(int numberOfProcessors) {
        // Creates a schedule with the correct number of processors
        PartialSchedule schedule = new PartialSchedule(numberOfProcessors);
        HashSet<PartialSchedule> newScheduleList = schedule.expandNewStates();
        open.addAll(newScheduleList);
    }
}
