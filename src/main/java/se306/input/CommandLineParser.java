package se306.input;

import com.google.devtools.common.options.OptionsParser;
import se306.exceptions.InvalidInputException;

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
		numberOfProcesses = 1;
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
	public void parseCommandLineArguments(String[] input) throws InvalidInputException {
		OptionsParser parser = OptionsParser.newOptionsParser(CommandLineArguments.class);
		parser.parseAndExitUponError(input);

		// Checks that all compulsory inputs have been entered (2 inputs)
        if (input.length < 2) {
            System.out.println("Please enter both the *.dot input file AND the number of processors to be used.");
            printUsage(parser);
            throw new InvalidInputException();
        }

        // Parses the number of processors to be used
        try {
            numberOfProcesses = Integer.parseInt(input[1]);

            // Ensures that the number of processors must be 1 or more, otherwise throw error
            if (numberOfProcesses < 1) {
                System.out.println("Please enter a valid number of processors.");
                printUsage(parser);
                throw new InvalidInputException();
            }

            // Ensures that the processor input is indeed a number (no letters/symbols)
        } catch (NumberFormatException e) {
            System.out.println("Please enter a number for the processors to be used.");
            printUsage(parser);
            throw new NumberFormatException();
        }

		CommandLineArguments options = parser.getOptions(CommandLineArguments.class);

        // Checks that options are valid (currently only checks the cores option, add more later)
        // @TODO
		if (options.numberOfCores < 0 ) {
            System.out.println("Please enter a valid number of cores to be used to paralellise this search.");
            printUsage(parser);
            throw new InvalidInputException();
		}
		

		System.out.println(numberOfProcesses);
		inputFileName = "/" + input[0];

		//Checks that the file is a .dot file
		if(!inputFileName.toLowerCase().endsWith(".dot")){
			System.out.println("Please input a .dot file");
			printUsage(parser);
			throw new InvalidInputException();
		}

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
