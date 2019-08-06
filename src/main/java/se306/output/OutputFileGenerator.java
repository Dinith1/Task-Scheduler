package se306.output;

import se306.algorithm.Processor;
import se306.input.CommandLineParser;
import se306.input.Edge;
import se306.input.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OutputFileGenerator {

	private PrintWriter writer;
	private List<Line> lineInformation = new ArrayList<>();
	public final String OUTPUT_FILE_NAME = CommandLineParser.getInstance().getOutputFileName();

	/**
	 * Adds information on the process and start time to each line with node information,
	 * this information will need to be outputted. Directly accesses the Line class to make the
	 * change
	 *
	 * @param processorList
	 */
	private void addProcessorsToLines(List<Processor> processorList) {
		for (Processor processor : processorList) {
			Iterator it = processor.getSchedule().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				Node node = (Node) pair.getKey();
				for (Line line : lineInformation) {
					if (node.equals(line.node)) {
						line.setProcessor(processor);
						line.setNodeStartTime(processor.getSchedule().get(node).intValue());
					}
				}
				it.remove(); // avoids a ConcurrentModificationException
			}
		}
	}

	/**
	 * Steps to generate the file after gathering data from inputs.
	 * This method assigns processors to the nodes to be printed
	 *
	 */
	public void generateFile(List<Processor> processorList) {
		addProcessorsToLines(processorList);
		printLinesToFile();
		closeWriter();
	}

	/**
	 * Prints each of the lines to the file, logic handled by the Line inner class
	 */
	private void printLinesToFile() {
		try {
			writer = new PrintWriter(OUTPUT_FILE_NAME);
			for (Line line : lineInformation) {
				writer.println(line.getStringLine());
			}
			writer.println("}");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * checks the object type of input line, if its a node, edge, or other redundant data
	 * Store the information appropriately, handled by the Line class
	 * @param lineInfo
	 */
	public void readLine(Object lineInfo) {
		Line newLine = new Line();
		if (lineInfo instanceof Node) {
			newLine.setNode((Node)lineInfo);
		} else if (lineInfo instanceof Edge) {
			newLine.setEdge((Edge)lineInfo);
		} else if (lineInfo instanceof String) {
			newLine.recordLine((String)lineInfo);
		}

		lineInformation.add(newLine);
	}

	/**
	 * Closes the writer, so nothing breaks
	 */
	private void closeWriter() {
		writer.close();
	}

	/**
	 * Stores information of lines, may need to change later since its a data class, but I
	 * cannot think of any ways to do it another way.
	 */
	private class Line {
		private Node node;
		private Edge edge;
		private Processor processor; // processor that the node represented in the line
		private String nonNodeLine; //directly copy any strings that are not node information
		private int nodeStartTime; //start time of node represented in this line

		void setNode(Node node) {
			this.node = node;
		}

		void setProcessor(Processor processor) {
			this.processor = processor;
		}

		void recordLine(String line) {
			this.nonNodeLine = line;
		}

		void setEdge(Edge edge) {
			this.edge = edge;
		}

		void setNodeStartTime(int nodeStartTime) {
			this.nodeStartTime = nodeStartTime;
		}

		/**
		 * Outputs the line if its not a node line (if its an edge line or redundant data line)
		 * Otherwise combine the node data to be formatted to the output.
		 * @return
		 */
		String getStringLine() {
			if (node != null) {
				String lineToOutput = "\t" + node.getNodeIdentifier() + "\t\t[Weight=" + node.getNodeWeight()
						+ ",Start=" + this.nodeStartTime + ",Processor=" + processor.getProcessorIdentifier()
						+ "];";
				return lineToOutput;
			} else if (edge != null) {
				String lineToOutput = "\t" + edge.getNodeStart().getNodeIdentifier() + " -> " +
						edge.getNodeEnd().getNodeIdentifier() + "\t[Weight=" + edge.getEdgeWeight() + "];";
				return lineToOutput;
			} else {
				return this.nonNodeLine;
			}
		}
	}
}