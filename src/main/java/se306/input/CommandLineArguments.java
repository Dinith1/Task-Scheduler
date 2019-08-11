package se306.input;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

/**
 * This class is used to store information on command line arguments only
 */
public class CommandLineArguments extends OptionsBase {

	@Option(name = "help", abbrev = 'h', help = "Prints usage info", category = "Startup", defaultValue = "false")
	public boolean help;

	@Option(name = "cores", abbrev = 'p', help = "Number of cores (processors) to use to produce the schedule", category = "Startup", defaultValue = "1")
	public int numCores;

	@Option(name = "output-file", abbrev = 'o', help = "Output file name", category = "Startup", defaultValue = "output.dot")
	public String outputFile;

	@Option(name = "visualise", abbrev = 'v', help = "Visualise the search", category = "Startup", defaultValue = "false")
	public boolean visualise;
}
