package se306.algorithm;

import java.util.List;

public class SchedulePruner {

    public void duplicationDetection() {

    }

    public void processNormalisation(PartialSchedule schedule1, PartialSchedule schedule2) {
        // Get all processors from each partial schedule
        List<Processor> processorList1 = schedule1.getProcessorList();
        List<Processor> processorList2 = schedule2.getProcessorList();

        for (Processor p : processorList1) {
            for (Processor p2 : processorList2) {
                if (p.equals(p2)) {
                    processorList2.remove(p2);
                }
            }
        }
    }
}
