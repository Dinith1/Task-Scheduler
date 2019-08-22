package se306.output;

import se306.algorithm.Processor;
import se306.exceptions.InvalidInputException;
import se306.input.CommandLineParser;
import se306.input.InputFileReader;
import se306.logging.Log;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OutputFileGenerator {

	private PrintWriter writer;
	private static OutputFileGenerator outputFileGenerator = null;
	private List<Line> lineInformation = new ArrayList<>();
	public final String OUTPUT_FILE_NAME = CommandLineParser.getInstance().getOutputFileName();

	private OutputFileGenerator() {

	}

	public static OutputFileGenerator getInstance() {
		return (outputFileGenerator == null) ? (outputFileGenerator = new OutputFileGenerator()) : outputFileGenerator;
	}

	/**
	 * Steps to generate the file after gathering data from inputs. This method
	 * assigns processors to the nodes to be printed
	 *
	 */
	public void generateFile(List<Processor> processorList) {
		addProcessorsToLines(processorList);
		printLinesToFile();
		closeWriter();
	}

	/**
	 * Adds information on the process and start time to each line with node
	 * information, this information will need to be outputted. Directly accesses
	 * the Line class to make the change
	 *
	 * @param processorList
	 */
	private void addProcessorsToLines(List<Processor> processorList) {
		for (Processor processor : processorList) {
			for (Node n : processor.getScheduledNodes()) {

				for (Line line : this.lineInformation) {
					if (n.equals(line.node)) {
						line.setProcessor(processor);
						line.setNodeStartTime(processor.getStartTimes().get(processor.getScheduledNodes().indexOf(n)));
					}
				}
			}
		}
	}

	/**
	 * Prints each of the lines to the file, logic handled by the Line inner class
	 */
	private void printLinesToFile() {
		try {
			writer = new PrintWriter(OUTPUT_FILE_NAME);
			for (Line line : this.lineInformation) {
				writer.println(line.getStringLine());
			}
			writer.println("}");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the writer, so nothing breaks
	 */
	private void closeWriter() {
		writer.close();
	}

	/**
	 * Checks the object type of input line, if its a node, edge, or other redundant
	 * data Store the information appropriately, handled by the Line class
	 * 
	 * @param lineInfo
	 */
	public void readLine(Object lineInfo) {
		Line newLine = new Line();

		if (lineInfo instanceof Node) {
			newLine.setNode((Node) lineInfo);

		} else if (lineInfo instanceof Edge) {
			newLine.setEdge((Edge) lineInfo);

		} else if (lineInfo instanceof String) {
			newLine.recordLine((String) lineInfo);
		}

		this.lineInformation.add(newLine);
	}

	/**
	 * Stores information of lines
	 */
	private class Line {
		private int node = -1;
		private int[] edge = { -1, -1, -1 };
		private Processor processor; // Processor that the node represented in the line
		private String nonNodeLine; // Directly copy any strings that are not node information
		private int nodeStartTime; // Start time of node represented in this line

		void setNode(int node) {
			this.node = node;
		}

		void setEdge(int[] edge) {
			this.edge = edge;
		}

		void setNodeStartTime(int nodeStartTime) {
			this.nodeStartTime = nodeStartTime;
		}

		void setProcessor(Processor processor) {
			this.processor = processor;
		}

		void recordLine(String line) {
			this.nonNodeLine = line;
		}

		/**
		 * Outputs the line if its not a node line (i.e. if it's an edge line or
		 * redundant data line). Otherwise combine the node data to be formatted to the
		 * output.
		 * 
		 * @return The line
		 */
		String getStringLine() {
			if (this.node != -1) {
				StringBuilder sb = new StringBuilder();
				sb.append("\t");
				sb.append(InputFileReader.nodeNames.get(this.node));
				sb.append("\t\t[Weight=");
				sb.append(InputFileReader.nodeWeights.get(this.node));
				sb.append(",Start=");
				sb.append(this.nodeStartTime);
				sb.append(",Processor=");
				sb.append(processor.getProcessorID());
				sb.append("];");
				return sb.toString();
			}

			if (this.edge[0] != -1) {
				StringBuilder sb = new StringBuilder();
				sb.append("\t");
				sb.append(InputFileReader.nodeNames.get(this.edge[0]));
				sb.append(" -> ");
				sb.append(InputFileReader.nodeNames.get(this.edge[1]));
				sb.append("\t[Weight=");
				sb.append(this.edge[2]);
				sb.append("];");
				return sb.toString();
			}

			return this.nonNodeLine;
		}
	}

}
