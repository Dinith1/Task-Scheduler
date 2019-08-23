package se306.algorithm;

import java.util.Comparator;

public class ProcessorHashCodeComparator implements Comparator<Processor> {

    public int compare(Processor p1, Processor p2){

        if(p1.hashCode() < p2.hashCode()){
            return -1;
        }
        else{
            return 1;
        }

}

}
