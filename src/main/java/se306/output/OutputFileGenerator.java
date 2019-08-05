package se306.output;

import se306.algorithm.Processor;
import se306.algorithm.Scheduling;
import se306.input.Node;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OutputFileGenerator {

	private PrintWriter writer;
	private static final String OUTPUT_FILE_NAME = "./output.dot";


	public OutputFileGenerator() {
		try {
			writer = new PrintWriter(OUTPUT_FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateFile(List<Processor> processorList) {
		for (Processor processor : processorList) {
			Iterator it = processor.getSchedule().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				Node node = (Node) pair.getKey();
				writer.println(node.getNodeIdentifier() + " = " + pair.getValue() + " = " + processor.getProcessorIdentifier());
				it.remove(); // avoids a ConcurrentModificationException
			}
		}
		closeWriter();
	}

	public void readLine(String line) {
		writer.println();
	}

	public void closeWriter() {
		writer.close();
	}

}
