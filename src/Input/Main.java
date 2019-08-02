package Input;


import java.io.File;
import java.io.IOException;

//Main class to test InputReader functionality
public class Main {


    public static void main(String[] args) throws IOException {
        File file = new File("src/Input/Nodes_11_OutTree.dot").getAbsoluteFile();
        InputReader inputReader = new InputReader();
        inputReader.readInput(file);
    }
}
