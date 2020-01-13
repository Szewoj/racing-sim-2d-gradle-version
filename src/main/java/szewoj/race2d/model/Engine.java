package szewoj.race2d.model;

/**
 * Engine physics simulation class.
 */
public class Engine {
    public static final double MAX_RPM = 6800;
    private double rpm;

    /**
     * Constructor of Engine.
     * Sets default rpm as minimal value.
     */
    public Engine(){
        rpm = 1000;
    }

    /**
     * Approximates torque as rpm function.
     *
     * @return  value of torque corresponding to current rpm
     */
    public double getTorque(){

        if(rpm < 2500)
            return 210 + rpm * 20/500;
        else if(rpm < 3750)
            return 310;
        else if(rpm < 4400)
            return 167 + rpm * 30/650;
        else if(rpm < 6000)
            return 444;
        else if(rpm < MAX_RPM)
            return 1368 - rpm*154/1000;
        else
            return (1000-rpm)/(MAX_RPM-1000) * 150 + (MAX_RPM-rpm) * 0.75;

    }

    /**
     * Simulates engine breaking phenomenon.
     *
     * @return      torque corresponding to engine braking
     */
    public double getEngineBraking(){
        if( rpm > MAX_RPM )
            return (1000-rpm)/(MAX_RPM-1000) * 150 + (MAX_RPM-rpm) * 0.75;
        else
            return (1000-rpm)/(MAX_RPM-1000) * 150;
    }

    /**
     * Sets rpm to value close to input. Simulates engine inertia.
     *
     * @param newValue  desired value of torque
     */
    public void setRpm( double newValue ){
        rpm += 0.20*(newValue-rpm);
        if(rpm < 1000)
            rpm = 1000;
    }

    /**
     * Simulates engine response on throttle with no load on engine shaft.
     *
     * @param throttle  value of throttle in range 0.0 - 1.0
     */
    public void setNeutralGearRpm(  double throttle ){
        if (rpm < MAX_RPM)
            rpm += 0.05*((1000 + (MAX_RPM - 500) * throttle)-rpm);
        else
            rpm -= 20;

        if(rpm < 1000)
            rpm = 1000;

    }

    /**
     * Getter of rpm property.
     *
     * @return  value of rpm
     */
    public double getRpm() {
        return rpm;
    }



}
