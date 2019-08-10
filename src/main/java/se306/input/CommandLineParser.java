package se306.input;

import com.google.devtools.common.options.OptionsParser;
import se306.exceptions.InputMissingException;

import java.util.Collections;

/**
 * Singleton class for parsing command line inputs
 */
public class CommandLineParser {
	private static CommandLineParser commandLineParser_instance = null;
	private String inputFileName;
	private String outputFileName;
	private int numberOfProcesses;

	public int getNumberOfProcesses() {
		return numberOfProcesses;
	}

	public String getInputFileName() {
		return inputFileName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	/**
	 * Private constructor only called by this class
	 */
	private CommandLineParser()
	{
		numberOfProcesses = 0;
		outputFileName = "output.dot";
	}

	/**
	 * Static method to return single instance of singleton class
	 * @return
	 */
	public static CommandLineParser getInstance()
	{
		if (commandLineParser_instance == null)
			commandLineParser_instance = new CommandLineParser();

		return commandLineParser_instance;
	}

	/**
	 * Parse all command line arguments
	 * @param input
	 */
	public void parseCommandLineArguments(String[] input) throws InputMissingException {
		OptionsParser parser = OptionsParser.newOptionsParser(CommandLineArguments.class);
		parser.parseAndExitUponError(input);
		CommandLineArguments options = parser.getOptions(CommandLineArguments.class);
		if (options.inputFile.isEmpty() || options.numberOfCores < 0 || options.numberOfProcessors < 1) {
			printUsage(parser);
			throw(new InputMissingException());
		}

		numberOfProcesses = options.numberOfProcessors;
		System.out.println(numberOfProcesses);
		inputFileName = "/" + options.inputFile;
		System.out.println(inputFileName);
		outputFileName = options.outputFile;
		System.out.println(outputFileName);

	}

	private static void printUsage(OptionsParser parser) {
		System.out.println("Usage: java -jar scheduler.jar INPUT.dot P [OPTIONS]");
		System.out.println(parser.describeOptions(Collections.<String, String>emptyMap(),
				OptionsParser.HelpVerbosity.LONG));
	}
}
