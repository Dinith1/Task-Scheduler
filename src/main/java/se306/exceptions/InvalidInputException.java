package se306.exceptions;

import se306.input.CommandLineParser;

public class InvalidInputException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String msg) {
        super(msg);
	}

	/**
     * @param parser
     * @return boolean to see if process input was missing or invalid
     */
    public boolean checkProcessInput(CommandLineParser parser) {
        return (parser.getNumberOfProcesses() <= 0);
    }
    
}
