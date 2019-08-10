package se306.input;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

/**
 * This class is used to store information on command line arguments only
 */
public class CommandLineArguments extends OptionsBase {

	@Option(name = "help", abbrev = 'h', help = "Prints usage info.", defaultValue = "false")
	public boolean help;

	@Option(name = "cores", abbrev = 'p', help = "use N cores for execution in parallel (default is sequential).", defaultValue = "1")
	public int numberOfCores;

	@Option(name = "output-file", abbrev = 'o', help = "output file name", defaultValue = "output.dot")
	public String outputFile;

	@Option(name = "visualise", abbrev = 'v', help = "visualise the search", defaultValue = "false")
	public boolean visualise;
}
