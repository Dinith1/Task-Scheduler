package se306.algorithm;

import se306.input.CommandLineParser;
import se306.output.OutputFileGenerator;
import se306.visualisation.backend.ScheduleParser;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;

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
    public PartialSchedule aStarAlgorithm(int numberOfProcessors, int numberOfCores) throws Exception {
        // OPEN <-- S init
        getPartialScheduleInitial(numberOfProcessors);
        while (!open.isEmpty()) {
            // Retrieves head and removes it from the queue
            PartialSchedule currentSchedule = open.peek();

            if (currentSchedule != null) {
                if (currentSchedule.isComplete()) {
                    return currentSchedule;
                }

                // Expand currentSchedule to new possible states
                HashSet<PartialSchedule> expandedCurrentSchedule = new HashSet<>(
                        currentSchedule.chooseExpansionAlgorithm(numberOfCores));
                for (PartialSchedule s : expandedCurrentSchedule) {
                    if (!createdSchedules.contains(s)) {
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
            CommandLineParser clp = CommandLineParser.getInstance();
            // Creates thread executor
            PartialSchedule.multiThreadExecutor = Executors.newWorkStealingPool(clp.getNumberOfCores());
            PartialSchedule optimalSchedule = aStarAlgorithm(clp.getNumberOfProcessors(), clp.getNumberOfCores());
            // Shuts down the processors
            PartialSchedule.multiThreadExecutor.shutdown();
            OutputFileGenerator.getInstance().generateFile(optimalSchedule.getProcessorList());

            ScheduleParser.getInstance().parseSchedule(optimalSchedule);
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
    private void getPartialScheduleInitial(int numberOfProcessors) {
        // Creates a schedule with the correct number of processors
        PartialSchedule schedule = new PartialSchedule(numberOfProcessors);
        HashSet<PartialSchedule> newScheduleList = schedule.expandNewStates();
        open.addAll(newScheduleList);
    }
}
