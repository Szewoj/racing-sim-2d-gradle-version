package szewoj.race2d.model;

/**Class designed to simulate fuel consumption
 */
public class FuelTank {
    public static final double FUEL_DENSITY = 0.75; /**in kg/l*/

    private final double FUEL_CONSUMPTION_RATE = 1.0 / 4800000;
    private final double MAX_VOLUME;/**in litres*/

    private double volume;/**in litres*/

    public FuelTank( double maxCapacity ){
        MAX_VOLUME = maxCapacity;
        volume = MAX_VOLUME;
    }

    public double getVolume(){
        return volume;
    }

    public double getMass(){
        return volume * FUEL_DENSITY;
    }

    public double getPercent(){
        return volume / MAX_VOLUME;
    }

    public boolean isEmpty(){
        return volume == 0;
    }

    public void consumeFuel( double rpm, double throttle ){
        double consumedFuel = rpm * (0.5 + 0.5 * throttle) * FUEL_CONSUMPTION_RATE;

        if( volume > consumedFuel )
            volume -= consumedFuel;
        else if( volume > 0 )
            volume = 0;
    }

}
