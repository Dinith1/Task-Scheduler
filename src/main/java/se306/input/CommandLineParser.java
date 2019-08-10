package se306.input;

import com.google.devtools.common.options.OptionsParser;
import se306.exceptions.InvalidInputException;
import java.util.Collections;

/**
 * Singleton class for parsing command line inputs
 */
public class CommandLineParser {
	private static CommandLineParser cmdLineParser = null;
	private String inputFileName;
	private String outputFileName;
	private int numProcesses;

	/**
	 * Private constructor only called by this class
	 */
	private CommandLineParser() {
		numProcesses = 1;
		outputFileName = "output.dot";
	}

	/**
	 * Static method to return single instance of singleton class
	 * 
	 * @return The single CommandLineParser object
	 */
	public static CommandLineParser getInstance() {
		return (cmdLineParser == null) ? new CommandLineParser() : cmdLineParser;
	}

	/**
	 * Get the input number of processes
	 * 
	 * @return The number of processes
	 */
	public int getNumberOfProcesses() {
		return numProcesses;
	}

	/**
	 * Get the input .dot file name
	 * 
	 * @return The input .dot file name
	 */
	public String getInputFileName() {
		return inputFileName;
	}

	/**
	 * Get the output .dot file name
	 * 
	 * @return The output .dot file name
	 */
	public String getOutputFileName() {
		return outputFileName;
	}

	/**
	 * Parse all command line arguments
	 * 
	 * @param input The string arguments
	 */
	public void parseCommandLineArguments(String[] input) throws InvalidInputException {
		// Set the OptionsParser to use the arguments specified in the CommandLineArguments class
		OptionsParser parser = OptionsParser.newOptionsParser(CommandLineArguments.class);

		parser.parseAndExitUponError(input);
		checkInvalidArgs(input, parser);

	}

	/**
	 * Checks the input for illegal arguments
	 * 
	 * @param input  Arguments as strings to check for legality
	 * @param parser The OptionsParser object used to parse/store the input
	 *               arguments
	 * @throws InvalidInputException
	 */
	private void checkInvalidArgs(String[] input, OptionsParser parser) throws InvalidInputException {
		// Check that all compulsory inputs have been entered (2 inputs)
		if (input.length < 2) {
			printUsage(parser);

			throw new InvalidInputException("Please enter the .dot input file AND number of processors to be used");
		}

		// Parse the number of processors to be used
		try {
			this.numProcesses = Integer.parseInt(input[1]);
		} catch (NumberFormatException e) {
			printUsage(parser);

			throw new NumberFormatException("Please enter an integer for the number of processors to be used");
		}

		// Check that at least one processor has been entered
		if (this.numProcesses < 1) {
			printUsage(parser);

			throw new InvalidInputException("Please enter a valid number of processors (at least 1)");
		}

		CommandLineArguments options = parser.getOptions(CommandLineArguments.class);

		// Checks that options are valid (currently only checks the cores option)
		if (options.numberOfCores < 0) {
			printUsage(parser);
			throw new InvalidInputException("Please enter a valid number of cores for paralellising the search");
		}

		System.out.println(this.numProcesses);
		this.inputFileName = "/" + input[0];

		// Checks that the file is a .dot file
		if (!this.inputFileName.toLowerCase().endsWith(".dot")) {
			printUsage(parser);
			throw new InvalidInputException("Please enter a valid .dot file");
		}

		System.out.println(inputFileName);
		outputFileName = options.outputFile;
		System.out.println(outputFileName);
	}

	/**
	 * Prints out how the user should run the program with appropriate arguments
	 * 
	 * @param parser
	 */
	private static void printUsage(OptionsParser parser) {
		System.out.println("Usage: java -jar scheduler.jar <INPUT.dot> P [OPTIONS]\n");
		System.out.println(
				parser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
	}

}
