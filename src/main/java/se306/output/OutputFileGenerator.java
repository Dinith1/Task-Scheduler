package se306.output;

import se306.algorithm.Processor;
import se306.input.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OutputFileGenerator {

	private PrintWriter writer;
	private List<Line> lineInformation = new ArrayList<>();
	private static final String OUTPUT_FILE_NAME = "./output.dot";


	public OutputFileGenerator() {
		try {
			writer = new PrintWriter(OUTPUT_FILE_NAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds information on the process and start time to each line with node information,
	 * this information will need to be outputted. Directly accesses the Line class to make the
	 * change
	 *
	 * @param processor
	 * @param node
	 */
	private void addProcessorsToLines(Processor processor, Node node) {
		for (Line line : lineInformation) {
			if (node.equals(line.node)) {
				line.setProcessor(processor);
				line.setNodeStartTime(processor.getSchedule().get(node).intValue());
			}
		}
	}

	/**
	 * Steps to generate the file and writes it
	 *
	 * @param processorList
	 */
	public void generateFile(List<Processor> processorList) {
		for (Processor processor : processorList) {
			Iterator it = processor.getSchedule().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Node node = (Node) pair.getKey();
//				writer.println(node.getNodeIdentifier() + " = " + pair.getValue() + " = " + processor.getProcessorIdentifier());
				addProcessorsToLines(processor, node);
				it.remove(); // avoids a ConcurrentModificationException
				//TODO: remove code from below here, debugging purposes only
				System.out.println(pair.getValue());
			}
		}
		printLinesToFile();
		closeWriter();
	}

	private void printLinesToFile() {
		for (Line line : lineInformation) {
			writer.println(line.getStringLine());
		}
	}

	/**
	 * Reads the line as InputFileReader is doing its thing and gets the information of the nodes
	 *
	 * @param node
	 */
	public void readLine(Node node) {
		Line newLine = new Line();
		newLine.setNode(node);
		lineInformation.add(newLine);
	}

	/**
	 * Reads the lines that do not specify a node, e.g. lines for edges or other redundant lines
	 * and directly prints it out
	 *
	 * @param line
	 */
	public void readLine(String line) {
		Line newLine = new Line();
		newLine.recordLine(line);
		lineInformation.add(newLine);
	}

	/**
	 * Closes the writer, so nothing breaks
	 */
	private void closeWriter() {
		writer.close();
	}

	/**
	 * Stores information on lines, may need to change later since its a data class, but I
	 * cannot think of any ways to do it another way.
	 */
	private class Line {
		private Node node;
		private Processor processor;
		private String nonNodeLine;
		private int nodeStartTime;

		void setNode(Node node) {
			this.node = node;
		}

		void setProcessor(Processor processor) {
			this.processor = processor;
		}

		void recordLine(String line) {
			this.nonNodeLine = line;
		}

		void setNodeStartTime(int nodeStartTime) {
			this.nodeStartTime = nodeStartTime;
		}

		Node getNode() {
			return this.node;
		}

		Processor getProcessor() {
			return this.processor;
		}

		String getStringLine() {
			if (node != null) {
				String lineToOutput = node.getNodeIdentifier() + "\t" + "[Weight=" + node.getNodeWeight()
						+ ",Start=" + this.nodeStartTime + ",Processor=" + processor.getProcessorIdentifier()
						+ "];";
				return lineToOutput;
			} else {
				return this.nonNodeLine;
			}
		}
	}
}
