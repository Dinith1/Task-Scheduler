//package se306.algorithm;
//
//import se306.input.CommandLineParser;
//import se306.output.OutputFileGenerator;
//import se306.visualisation.backend.ScheduleParser;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.PriorityQueue;
//import java.util.concurrent.ForkJoinPool;
//import java.util.concurrent.ForkJoinTask;
//import java.util.concurrent.RecursiveAction;
//import java.util.concurrent.RecursiveTask;
//
//import static sun.tools.jconsole.Messages.THRESHOLD;
//
//public class AStarSchedulerParallel extends RecursiveAction<PartialSchedule> {
//
//    public static ForkJoinPool fjPool = ForkJoinPool.commonPool();
//    public static int numOfProcessors = 2;
//    private static int NUMCORES = 4;
//    private volatile int usedCores = 1;
//    private volatile PriorityQueue<PartialSchedule> open = new PriorityQueue<>(new CostFunctionComparator());
//    private HashSet<PartialSchedule> createdSchedules = new HashSet<>();
//    private PriorityQueue<PartialSchedule> optimal = new PriorityQueue<>(new CostFunctionComparator());
//
//    @Override
//    protected PartialSchedule compute() {
//
//
//
//
//        return optimal.peek();
//    }
//}
