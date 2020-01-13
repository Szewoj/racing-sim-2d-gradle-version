package szewoj.race2d.utilities;

/**
 * Container for double values that keeps them in desired range.
 */
public class Percent {

    private double value;
    private double MAX_VALUE;
    private double MIN_VALUE;

    /**
     * Default constructor for range 0.0 - 1.0
     */
    public Percent(){
        value = 0;
        MAX_VALUE = 1;
        MIN_VALUE = 0;
    }

    /**
     * Constructor for custom range.
     *
     * @param minVal    minimal value
     * @param maxVal    maximal value
     */
    public Percent(double minVal, double maxVal){
        value = 0;
        MAX_VALUE = maxVal;
        MIN_VALUE = minVal;
    }

    /**
     * Set new value as kept. If new value exceeds the range, it is rounded to the closest in range value.
     *
     * @param newValue new desired value
     */
    public void setPercent(double newValue){
        value = newValue;

        if(value > MAX_VALUE )
            value = MAX_VALUE;

        else if(value < MIN_VALUE )
            value = MIN_VALUE;
    }

    /**
     * Performs += addition. If new value exceeds the range, it is rounded to the closest in range value.
     *
     * @param diff  difference between current value and desired value
     */
    public void addPercent(double diff){
        value += diff;

        if(value > MAX_VALUE )
            value = MAX_VALUE;

        else if(value < MIN_VALUE )
            value = MIN_VALUE;
    }

    /**
     * Returns currently kept value.
     *
     * @return kept value
     */
    public double getPercent(){
        return value;
    }

}
