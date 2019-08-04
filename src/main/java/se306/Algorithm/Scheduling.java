package se306.Algorithm;

import java.util.ArrayList;
import java.util.List;

public class Scheduling {

    // User defined available processors placed in a list
    List<Processor> processorsAvailable = new ArrayList<>();

    public void createProcessors(int number){
        for(int i = 0; i <= number;i++){
            processorsAvailable.add(new Processor());
        }
    }
    public void getSchedule(){

    }
}
