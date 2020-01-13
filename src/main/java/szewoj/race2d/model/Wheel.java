package szewoj.race2d.model;

import szewoj.race2d.utilities.Percent;
import szewoj.race2d.utilities.Vector2d;

public class Wheel {
    public static final boolean FRONT = true;
    public static final boolean REAR = false;
    public static final double RADIUS = 0.4572;
    public static final double INERTIA = 60;
    private final double DEGRADATION_RATIO = 1.0/2000;
    private double rotationSpeed;
    private final boolean wheelType;
    private Percent durability;
    private Vector2d position;
    private double friction;

    public Wheel( boolean type, double positionX, double positionY ){
        wheelType = type;
        rotationSpeed = 0;
        durability = new Percent();
        durability.setPercent( 1 );
        position = new Vector2d( positionX, positionY );
    }

    public void increaseRotationSpeed( double difference ){
        this.rotationSpeed += difference;
    }

    public void setRotationSpeed( double newValue ){
        this.rotationSpeed = newValue;
    }

    public double getRotationSpeed(){
        return this.rotationSpeed;
    }

    public double getDurability(){
        return durability.getPercent();
    }

    public void setFriction( double value ){
        friction = value;
    }

    public double getFriction(){
        return friction;
    }

    public double getSlipRatio( double longitudinalVelocity ){

        if(Math.abs(longitudinalVelocity) == 0 )
            return Math.signum(rotationSpeed*Wheel.RADIUS - longitudinalVelocity) * 0.001;

        return (rotationSpeed*Wheel.RADIUS - longitudinalVelocity) / Math.abs(longitudinalVelocity);
    }

    public double getSlipAngle( Vector2d velocity, double carRotationSpeed, double steeringAngle ){
        if(Math.abs(velocity.getY()) == 0 )
            return 0;

        if(wheelType)
            return  -steeringAngle * Math.signum(velocity.getY()) + Math.atan( velocity.getX() + carRotationSpeed*Math.abs(position.getY()) / Math.abs(velocity.getY()) );

        return Math.atan( velocity.getX() - carRotationSpeed*Math.abs(position.getY()) / velocity.getY() );
    }

    public double getSlipAngle( Vector2d velocity, double carRotationSpeed ){
        if(Math.abs(velocity.getY()) == 0 )
            return 0;

        return Math.atan( velocity.getX() - carRotationSpeed*Math.abs(position.getY()) / velocity.getY() );
    }

    public void degradeTire( double slipRatio, double slipAngle ){
        durability.addPercent(- ( Math.abs(slipRatio) + Math.abs(slipAngle) ) * DEGRADATION_RATIO );
    }

    public double getTraction(){
        return (0.6 + 0.4 * durability.getPercent()) * friction;
    }

    public void switchTire(){
        durability.setPercent(1);
    }

}
