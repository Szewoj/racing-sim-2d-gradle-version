package szewoj.race2d.model;

import szewoj.race2d.utilities.Percent;
import szewoj.race2d.utilities.Vector2D;

/**
 * Simulates wheel physics.
 */
public class Wheel {
    public static final boolean FRONT = true;
    public static final boolean REAR = false;
    public static final double RADIUS = 0.4572;
    public static final double INERTIA = 60;
    private final double DEGRADATION_RATIO = 1.0/2000;
    private double rotationSpeed;
    private final boolean wheelType;
    private Percent durability;
    private Vector2D position;
    private double friction;

    /**
     * Constructor of Wheel.
     *
     * @param type          type of wheel (Wheel.FRONT for steering wheel, and Wheel.REAR for fixed wheel)
     * @param positionX     position of wheel in metres, where 0 is the position of centre of gravity
     * @param positionY     position of wheel in metres, where 0 is the position of centre of gravity
     */
    public Wheel( boolean type, double positionX, double positionY ){
        wheelType = type;
        rotationSpeed = 0;
        durability = new Percent();
        durability.setPercent( 1 );
        position = new Vector2D( positionX, positionY );
    }

    /**
     * Increases rotational speed of the wheel.
     *
     * @param difference    value of increase
     */
    public void increaseRotationSpeed( double difference ){
        rotationSpeed += difference;
    }

    /**
     * Sets new rotational speed of the wheel.
     *
     * @param newValue      new value of rotational speed
     */
    public void setRotationSpeed( double newValue ){
        rotationSpeed = newValue;
    }

    /**
     * Getter for rotationSpeed property.
     *
     * @return  rotationSpeed value
     */
    public double getRotationSpeed(){
        return rotationSpeed;
    }

    /**
     * Getter for durability value.
     *
     * @return  durability value in range 0.0 - 1.0
     */
    public double getDurability(){
        return durability.getPercent();
    }

    /**
     * Setter for friction property.
     *
     * @param value new value of friction
     */
    public void setFriction( double value ){
        friction = value;
    }

    /**
     * Calculates slip ratio of the wheel.
     *
     * @param longitudinalVelocity  velocity longitudinal to wheel
     * @return                      value of slip ratio
     */
    public double getSlipRatio( double longitudinalVelocity ){

        if(Math.abs(longitudinalVelocity) == 0 )
            return Math.signum(rotationSpeed*Wheel.RADIUS - longitudinalVelocity) * 0.001;

        return (rotationSpeed*Wheel.RADIUS - longitudinalVelocity) / Math.abs(longitudinalVelocity);
    }

    /**
     * Calculates slip angle of steering or fixed wheel.
     *
     * @param velocity          velocity vector of car
     * @param carRotationSpeed  yaw rate of car
     * @param steeringAngle     angle of wheel
     * @return                  value of slip angle
     */
    public double getSlipAngle(Vector2D velocity, double carRotationSpeed, double steeringAngle ){
        if(Math.abs(velocity.getY()) == 0 )
            return 0;

        if(wheelType)
            return  -steeringAngle * Math.signum(velocity.getY()) + Math.atan( velocity.getX() + carRotationSpeed*Math.abs(position.getY()) / Math.abs(velocity.getY()) );

        return Math.atan( velocity.getX() - carRotationSpeed*Math.abs(position.getY()) / velocity.getY() );
    }

    /**
     * Calculates slip angle of fixed wheel.
     *
     * @param velocity          velocity vector of car
     * @param carRotationSpeed  yaw rate of car
     * @return                  value of slip angle
     */
    public double getSlipAngle(Vector2D velocity, double carRotationSpeed ){
        if(Math.abs(velocity.getY()) == 0 )
            return 0;

        return Math.atan( velocity.getX() - carRotationSpeed*Math.abs(position.getY()) / velocity.getY() );
    }

    /**
     * Reduces durability of tire, by calculating damage from slip.
     *
     * @param slipRatio     value of slip ratio
     * @param slipAngle     value of slip angle
     */
    public void degradeTire( double slipRatio, double slipAngle ){
        durability.addPercent(- ( Math.abs(slipRatio) + Math.abs(slipAngle) ) * DEGRADATION_RATIO );
    }

    /**
     * Calculates traction multiplier using friction and durability values
     *
     * @return  traction multiplier in range 0.0 - 1.0
     */
    public double getTraction(){
        return (0.6 + 0.4 * durability.getPercent()) * friction;
    }

    /**
     * Sets value of durability to 1.0.
     */
    public void switchTire(){
        durability.setPercent(1);
    }

}
