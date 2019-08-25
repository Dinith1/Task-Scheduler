package se306;

import org.junit.Before;
import org.junit.Test;
import se306.exceptions.InvalidInputException;
import se306.input.CommandLineParser;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

/**
 * Tests the CommandLineParser class, and that the correct exceptions are thrown
 */
public class CommandLineParserTest {
    CommandLineParser cliArgumentParser;

    @Before
    public void init() {

    }

    /**
     * Test that a invalid input exception is not thrown with a valid command line input
     */
    @Test
    public void parseTwoArgsValid() {
        String[] input = new String[]{"hi.dot", "2"};
        cliArgumentParser = CommandLineParser.getInstance();
        try {
            cliArgumentParser.parseCommandLineArguments(input);
        } catch (InvalidInputException e) {
            fail();
        }
    }

    /**
     * Test that a NumberFormatException is caught when the second argument is not a number
     * @throws InvalidInputException
     */
    @Test
    public void parseSecondArgsNotNumber() throws InvalidInputException {
        String[] input = new String[]{"hi.dot", "x"};
        cliArgumentParser = CommandLineParser.getInstance();
        try {
            cliArgumentParser.parseCommandLineArguments(input);
        } catch (NumberFormatException e) {
            assertEquals("Please enter an integer for the number of processors to be used", e.getMessage());
        }
    }

    /**
     * Test that a InvalidInputException is caught when no arguments are passed in
     */
    @Test
    public void parseNoArgs() {
        String[] input = new String[0];
        cliArgumentParser = CommandLineParser.getInstance();
        try {
            cliArgumentParser.parseCommandLineArguments(input);
        } catch (InvalidInputException e) {
            assertEquals("Please enter the .dot input file AND number of processors to be used", e.getMessage());
        }
    }

    /**
     * Test that an InvalidInputException is caught when 0 processors have been specified
     */
    @Test
    public void parseNoProcessors() {
        String[] input = new String[]{"hi.dot", "0"};
        cliArgumentParser = CommandLineParser.getInstance();
        try {
            cliArgumentParser.parseCommandLineArguments(input);
        } catch (InvalidInputException e) {
            assertEquals("Please enter a valid number of processors (at least 1)", e.getMessage());
        }
    }

    /**
     * Test that an InvalidInputException is caught when 0 cores have been inputted
     */
    @Test
    public void parseIncorrectCores() {
        String[] input = new String[]{"hi.dot", "2", "-p", "0"};
        cliArgumentParser = CommandLineParser.getInstance();
        try {
            cliArgumentParser.parseCommandLineArguments(input);
        } catch (InvalidInputException e) {
            assertEquals("Please enter a valid number of cores (processors) for paralellising the search", e.getMessage());
        }
    }

    /**
     * Test that an InvalidInputException is caught, and the correct error message has been displayed when
     * a file with no .dot file has been inputted
     */
    @Test
    public void parseNoDotFile(){
        String[] input = new String[]{"hi", "2", "-p", "2"};
        cliArgumentParser = CommandLineParser.getInstance();
        try {
            cliArgumentParser.parseCommandLineArguments(input);
        } catch (InvalidInputException e) {
            assertEquals("Please enter a file with the .dot extension", e.getMessage());
        }
    }
}
