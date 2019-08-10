package se306.exceptions;

import se306.input.CommandLineParser;

public  class InvalidInputException extends Exception {
    /**
     * @param parser
     * @return boolean to see if process input was missing or invalid
     */
    public boolean checkProcessInput(CommandLineParser parser) {
//        if (parser.getNumberOfProcesses() <= 1 || !(parser.getNumberOfProcesses() instanceof Integer)) {
//            return false;
//        } else {
//            return (true);
//        }
        return(parser.getNumberOfProcesses() <= 1);
    }
}
