package se306.input;

import com.google.devtools.common.options.OptionsParser;
import se306.exceptions.InvalidInputException;
import se306.logging.Log;

import java.util.Collections;

/**
 * Singleton class for parsing command line inputs
 */
public class CommandLineParser {
	private static CommandLineParser cmdLineParser = null;
	private String inputFileName;
	private String outputFileName;
	private int numProcesses;
	private OptionsParser optParser;

	/**
	 * Private constructor only called by this class
	 */
	private CommandLineParser() {
		numProcesses = 1;
		outputFileName = "output.dot";
		optParser = OptionsParser.newOptionsParser(CommandLineArguments.class);
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
	 * Parse all command line arguments
	 * 
	 * @param input The string arguments
	 */
	public void parseCommandLineArguments(String[] input) throws InvalidInputException {
		optParser.parseAndExitUponError(input);
		checkInvalidArgs(input);

		Log.info("Input file entered: " + this.inputFileName);
		Log.info("Output will be saved to: " + this.outputFileName);
	}

	/**
	 * Checks the input for illegal arguments
	 * 
	 * @param input Arguments as strings to check for legality
	 * @throws InvalidInputException
	 */
	private void checkInvalidArgs(String[] input) throws InvalidInputException {
		// Check that all compulsory inputs have been entered (2 inputs)
		if (input.length < 2) {
			throw new InvalidInputException("Please enter the .dot input file AND number of processes to be used");
		}

		// Parse the number of processors to be used
		try {
			this.numProcesses = Integer.parseInt(input[1]);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Please enter an integer for the number of processes to be used");
		}

		// Check that at least one process has been entered
		if (this.numProcesses < 1) {
			throw new InvalidInputException("Please enter a valid number of processes (at least 1)");
		}

		CommandLineArguments options = optParser.getOptions(CommandLineArguments.class);

		// Check that more than 0 cores (processors) are entered
		if (options.numCores < 1) {
			throw new InvalidInputException(
					"Please enter a valid number of cores (processors) for paralellising the search");
		}

		this.inputFileName = input[0];

		// Check that the file is a .dot file
		if (!this.inputFileName.toLowerCase().endsWith(".dot")) {
			throw new InvalidInputException("Please enter a file with the .dot extension");
		}

		this.outputFileName = options.outputFile;
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
	 * Prints out how the user should run the program with appropriate arguments
	 */
	public void printUsage() {
		System.out.println("\nUsage: java -jar scheduler.jar <input-file> <number-of-processes> [options]\n");
		System.out.println("\t- <input-file> should be a .dot file");
		System.out.println("\t- <number-of-processes> should be an integer greater than 0\n");
		System.out.println(
				optParser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
	}

}
