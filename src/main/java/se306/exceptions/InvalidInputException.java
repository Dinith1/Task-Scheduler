package se306.exceptions;

import se306.input.CommandLineParser;

public  class InvalidInputException extends Exception {
    /**
     * @param parser
     * @return boolean to see if process input was missing or invalid
     */
    public boolean checkProcessInput(CommandLineParser parser) {
        return(parser.getNumberOfProcesses() <= 0);
    }
}
