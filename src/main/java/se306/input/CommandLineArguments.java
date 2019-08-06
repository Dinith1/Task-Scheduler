package se306.input;

import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionsBase;

/**
 * This class is used to store information on command line arguments only
 */
public class CommandLineArguments extends OptionsBase{
	@Option(
			name = "input-file",
			abbrev = 'i',
			help = "a task graph with integer weights in dot format.",
			defaultValue = "Nodes_10_Random.dot"
	)
	public String inputFile;

	@Option(
			name = "processors",
			abbrev = 'p',
			help = "number of processors to schedule the input graph on.",
			defaultValue = "2"
	)
	public int numberOfProcessors;

	@Option(
			name = "cores",
			abbrev = 'n',
			help = "use N cores for execution in parallel (default is sequential).",
			defaultValue = "1"
	)
	public int numberOfCores;

	@Option(
			name = "output-file",
			abbrev = 'o',
			help = "output file name",
			defaultValue = "output.dot"
	)
	public String outputFile;
}
