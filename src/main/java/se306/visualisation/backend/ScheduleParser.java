package se306.visualisation.backend;

import se306.algorithm.PartialSchedule;
import se306.algorithm.Processor;

import java.util.Collection;

public class ScheduleParser {
	private static ScheduleParser scheduleParser = null;
	private Collection<Processor> processorList;
	private ScheduleParser() {
	}

	/**
	 * Static method to return single instance of singleton class
	 *
	 * @return The single ScheduleParser object
	 */
	public static ScheduleParser getInstance() {
		return (scheduleParser == null) ? (scheduleParser = new ScheduleParser()) : scheduleParser;
	}
	public void parseSchedule(PartialSchedule ps) {
		processorList = ps.getProcessorList().values();
	}

	public Collection<Processor> getProcessorList() {
		return processorList;
	}
}
