package se306.logging;

/**
 * Class used to print info or error messages
 */
public class Log {

    /**
     * Print information
     * 
     * @param s String to print
     */
    public static void info(String s) {
        System.out.printf("[INFO] %s\n", s);
    }

    /**
     * Print errors
     * 
     * @param s
     */
    public static void error(String s) {
        System.out.printf("[ERROR] %s\n", s);
    }

}