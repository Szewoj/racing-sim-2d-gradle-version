package szewoj.race2d.model;

/**Class designed to simulate fuel consumption
 */
public class FuelTank {
    public static final double FUEL_DENSITY = 0.75;

    private final double FUEL_CONSUMPTION_RATE = 1.0 / 4800000;
    private final double REFUEL_SPEED = 1.0/6;
    private final double MAX_VOLUME;

    private double volume;

    /**
     * Constructor of FuelTank.
     * Sets tank as full (volume = MAX_VOLUME = maxCapacity)
     *
     * @param maxCapacity   MAX_VOLUME initialization value
     */
    public FuelTank( double maxCapacity ){
        MAX_VOLUME = maxCapacity;
        volume = MAX_VOLUME;
    }

    /**
     * Calculates mass of fuel in tank.
     *
     * @return  value of mass of fuel
     */
    public double getMass(){
        return volume * FUEL_DENSITY;
    }

    /**
     * Calculates the percentage of filled tank volume.
     *
     * @return  double in range 0.0 - 1.0
     */
    public double getPercent(){
        return volume / MAX_VOLUME;
    }

    /**
     * Checks if fuel tank is empty.
     *
     * @return  boolean value true if tank is empty
     */
    public boolean isEmpty(){
        return volume == 0;
    }

    /**
     * Simulates fuel consumption.
     *
     * @param rpm       rpm of car
     * @param throttle  throttle position of car in range 0.0 - 1.0
     */
    public void consumeFuel( double rpm, double throttle ){
        double consumedFuel = rpm * (0.5 + 0.5 * throttle) * FUEL_CONSUMPTION_RATE;

        if( volume > consumedFuel )
            volume -= consumedFuel;
        else if( volume > 0 )
            volume = 0;
    }

    /**
     * Simulates refueling. Adds fixed amount of volume per function call.
     */
    public void refuel(){
        if( MAX_VOLUME - volume < REFUEL_SPEED )
            volume = MAX_VOLUME;
        else
            volume += REFUEL_SPEED;
    }

}
