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
	private int numProcessors;
	private boolean visualise;
	private OptionsParser optParser;

	/**
	 * Private constructor only called by this class
	 */
	private CommandLineParser() {
		numProcessors = 1;
		outputFileName = "output.dot";
		optParser = OptionsParser.newOptionsParser(CommandLineArguments.class);
	}

	/**
	 * Static method to return single instance of singleton class
	 * 
	 * @return The single CommandLineParser object
	 */
	public static CommandLineParser getInstance() {
		return (cmdLineParser == null) ? (cmdLineParser = new CommandLineParser()) : cmdLineParser;
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
		Log.info("Number of processors to use: " + this.numProcessors);
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
			throw new InvalidInputException("Please enter the .dot input file AND number of processors to be used");
		}

		// Parse the number of processors to be used
		try {
			this.numProcessors = Integer.parseInt(input[1]);
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Please enter an integer for the number of processors to be used");
		}

		// Check that at least one process has been entered
		if (this.numProcessors < 1) {
			throw new InvalidInputException("Please enter a valid number of processors (at least 1)");
		}

		CommandLineArguments options = optParser.getOptions(CommandLineArguments.class);

		// Check that more than 0 cores (processors) are entered
		if (options.numCores < 1) {
			throw new InvalidInputException(
					"Please enter a valid number of cores (processors) for paralellising the search");
		} else if (options.numCores > 1) {
			Log.error("Cores have not yet been implemented. Only 1 core will be used.");
		}

		this.inputFileName = input[0];

		// Check that the file is a .dot file
		if (!this.inputFileName.toLowerCase().endsWith(".dot")) {
			throw new InvalidInputException("Please enter a file with the .dot extension");
		}

		// If the user does not specify an output, then set default to be INPUT-output.dot
		//else set the output file to be what the user specifies
		if (options.outputFile.equals("output.dot")) {
			String inputFileName = this.inputFileName;
			this.outputFileName = inputFileName.replace(".dot", "") + "-output.dot";
		} else {
			this.outputFileName = options.outputFile;
		}

		this.visualise = options.visualise;
	}

	/**
	 * Get the input number of processors
	 * 
	 * @return The number of processors
	 */
	public int getNumberOfProcessors() {
		return numProcessors;
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
	 * Finds whether the user specified to visualise the algorithm or not.
	 * @return true if visualisation was specified by the user, false otherwise.
	 */
	public boolean wantVisual() {
		return visualise;
	}

	/**
	 * Prints out how the user should run the program with appropriate arguments
	 */
	public void printUsage() {
		System.out.println("\nUsage: java -jar scheduler.jar <INPUT-FILE> <NUMBER-OF-PROCESSORS> [OPTIONS]\n");
		System.out.println("\t- <INPUT-FILE> should be a .dot file");
		System.out.println("\t- <NUMBER-OF-PROCESSORS> should be an integer greater than 0\n");
		System.out.println(
				optParser.describeOptions(Collections.<String, String>emptyMap(), OptionsParser.HelpVerbosity.LONG));
	}

}
