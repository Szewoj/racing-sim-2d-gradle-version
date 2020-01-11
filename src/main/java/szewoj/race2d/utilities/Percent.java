package szewoj.race2d.utilities;

public class Percent {

    private double value;
    private double MAX_VALUE;
    private double MIN_VALUE;

    public Percent(){ //default constructor
        value = 0;
        MAX_VALUE = 1;
        MIN_VALUE = 0;
    }

    public Percent(double minVal, double maxVal){
        value = 0;
        MAX_VALUE = maxVal;
        MIN_VALUE = minVal;
    }

    public void setPercent(double newValue){
        value = newValue;

        if(value > MAX_VALUE )
            value = MAX_VALUE;

        else if(value < MIN_VALUE )
            value = MIN_VALUE;
    }

    public void addPercent(double diff){
        value += diff;

        if(value > MAX_VALUE )
            value = MAX_VALUE;

        else if(value < MIN_VALUE )
            value = MIN_VALUE;
    }

    public double getPercent(){
        return value;
    }

}
