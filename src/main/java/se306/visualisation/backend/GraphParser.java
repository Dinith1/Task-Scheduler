package se306.visualisation.backend;

import se306.input.CommandLineParser;
import se306.input.InputFileReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses the input file graph and stores relevant data for the graph
 */
public class GraphParser {

	public static int totalNodes = 0, totalEdges = 0; // public static so that all other classes only see a single value

	/**
	 * Begins parsing the graph and stores the new graph as a field, also calculates
	 * the total nodes and edges
	 *
	 */
	public void parseGraph() {
		try {
			CommandLineParser parser = CommandLineParser.getInstance();
			InputStreamReader isr = new FileReader(parser.getInputFileName());

			getNumberOfNodesAndEdges(isr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the total number of nodes and edges. This is to be used by the
	 * InputFileReader
	 * 
	 * @param isr
	 */
	private void getNumberOfNodesAndEdges(InputStreamReader isr) {
		BufferedReader buffRead = new BufferedReader(isr);

		try {
			String line;
			buffRead.readLine();
			while ((line = buffRead.readLine()) != null) {
				String end = line.substring(0, 1);

				// Stop reading once it reaches end of file
				if (end.equalsIgnoreCase("}")) {
					break;
				}
				// If the line is not a line that includes a node or an edge
				Pattern p = Pattern.compile(".*\\[Weight=.*];");
				Matcher m = p.matcher(line);
				if (!m.matches()) {
					continue;
				}

				if (line.indexOf("->") == -1) { // Handle nodes
					totalNodes++;
				} else {
					totalEdges++;
				}
			}

			CommandLineParser parser = CommandLineParser.getInstance();
			InputStreamReader isr1 = new FileReader(parser.getInputFileName());
			InputFileReader ifr = InputFileReader.getInstance();
			ifr.readInput(isr1);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
