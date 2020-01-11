package szewoj.race2d.model;

import szewoj.race2d.utilities.Vector2d;

public class Wheel {
    public static final boolean FRONT = true;
    public static final boolean REAR = false;
    public static final double RADIUS = 0.4572;
    public static final double INERTIA = 60;
    private double rotationSpeed;
    private final boolean wheelType;
    private Vector2d position;

    public Wheel( boolean type, double positionX, double positionY ){
        this.wheelType = type;
        this.rotationSpeed = 0;
        this.position = new Vector2d( positionX, positionY );
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

    public double getSlipRatio( double longitudinalVelocity ){

        if(Math.abs(longitudinalVelocity) == 0 )
            return Math.signum(rotationSpeed*Wheel.RADIUS - longitudinalVelocity) * 0.001;

        return (rotationSpeed*Wheel.RADIUS - longitudinalVelocity) / Math.abs(longitudinalVelocity);
    }

    public double getSlipAngle( Vector2d velocity, double carRotationSpeed, double steeringAngle ){
        if(Math.abs(velocity.getY()) == 0 )
            return 0;

        if(wheelType)
            return  -steeringAngle * Math.signum(velocity.getY()) + Math.atan( velocity.getX() + carRotationSpeed*Math.abs(this.position.getY()) / Math.abs(velocity.getY()) );

        return Math.atan( velocity.getX() - carRotationSpeed*Math.abs(this.position.getY()) / velocity.getY() );
    }

    public double getSlipAngle( Vector2d velocity, double carRotationSpeed ){
        if(Math.abs(velocity.getY()) == 0 )
            return 0;

        return Math.atan( velocity.getX() - carRotationSpeed*Math.abs(this.position.getY()) / velocity.getY() );
    }

}
