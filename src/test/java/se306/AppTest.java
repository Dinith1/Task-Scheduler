package se306;

import org.junit.Test;
import se306.input.InputFileReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AppTest 
{
    /**
     * This "test" should be run to check any changes do not affect any examples
     */
	@Test
	public void testAllResources () throws IOException {

		// Resources provided as example input graphs
		List<String> pathNames = new ArrayList<>();
		pathNames.add("/Nodes_7_OutTree.dot");
		pathNames.add("/Nodes_8_Random.dot");
		pathNames.add("/Nodes_9_SeriesParallel.dot");
		pathNames.add("/Nodes_10_Random.dot");
		pathNames.add("/Nodes_11_OutTree.dot");

		for (String path : pathNames) {
			InputStream in = Main.class.getResourceAsStream(path);
			InputStreamReader isr = new InputStreamReader(in);

			InputFileReader inputFileReader = new InputFileReader();
			String[] commandLineInput = {"2"};
			inputFileReader.parseCommandLineProcessorCount(commandLineInput);

			try {
				inputFileReader.readInput(isr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
