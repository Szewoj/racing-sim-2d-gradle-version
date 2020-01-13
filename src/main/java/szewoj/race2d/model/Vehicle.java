package szewoj.race2d.model;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import szewoj.race2d.utilities.Percent;
import szewoj.race2d.utilities.Vector2d;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Simulates Vehicle physics.
 * Implements bicycle car physics model.
 */
public class Vehicle {
    //Physics related variables and constants:
    private static final double TIME_CONST = 1.0/60;
    private static final double AR_CONST = 0.2;
    private static final double RR_CONST = 8.4;
    private static final double BR_CONST = 10000;
    private static final double STIFFNESS = 24;
    private static final double MASS_CONST = 1420;
    private static final double WIDTH = 2;
    private static final double LENGTH = 4.5;
    private Vector2d velocity;
    private double yawRate;
    private Engine engine;
    private Gearbox gearbox;
    private FuelTank fuelTank;
    private Wheel frontWheels;
    private Wheel rearWheels;
    private WeightTransfer weight;
    private double momentOfInteria;
    private double rearEffectiveWeight;
    private double frontEffectiveWeight;

    //Input signal variables and constants:
    private static final double GAIN_IN = 3.0/60;
    private static final double LOSS_IN = 2.5/60;
    private Percent throttle;
    private Percent brake;
    private Percent steering;
    private boolean handbrake;

    /**
     * Default constructor of Vehicle
     */
    public Vehicle(){
        velocity = new Vector2d( 0, 0);
        yawRate = 0;
        engine = new Engine();
        throttle = new Percent();
        brake = new Percent();
        steering = new Percent( -1, 1 );
        gearbox = new Gearbox();
        fuelTank = new FuelTank( 54 );
        frontWheels = new Wheel(Wheel.FRONT, 0, 2.6);
        rearWheels = new Wheel(Wheel.REAR, 0, -1.4);
        weight = new WeightTransfer( MASS_CONST, 2.4, 0.5, 1.6 );
        momentOfInteria = weight.getMass() * ( WIDTH*WIDTH + LENGTH*LENGTH) / 12;
        rearEffectiveWeight =  weight.getEffectiveWeightOnRear( 0 );
        frontEffectiveWeight = weight.getEffectiveWeightOnFront( 0 );
    }

    /**
     * Simulates input inertia.
     *
     * @param inputs    array of string codes
     */
    public void updateInputs(ArrayList<String> inputs){

        if( inputs.contains("UP") || inputs.contains("W") )
            throttle.addPercent(GAIN_IN);
        else
            throttle.addPercent(-LOSS_IN);

        if( inputs.contains("DOWN") || inputs.contains("S") )
            brake.addPercent(GAIN_IN);
        else
            brake.addPercent(-LOSS_IN);

        handbrake = inputs.contains("SPACE");

        if( inputs.contains("RIGHT") || inputs.contains("D") ) {
            if( Math.signum(steering.getPercent()) < 0 )
                steering.setPercent( 0 );
            else
                steering.addPercent(GAIN_IN);
        }

        if( inputs.contains("LEFT") || inputs.contains("A")) {
            if( Math.signum(steering.getPercent()) > 0 )
                steering.setPercent( 0 );
            else
                steering.addPercent(-GAIN_IN);
        }

        if( !inputs.contains("RIGHT") && !inputs.contains("LEFT") && !inputs.contains("A") && !inputs.contains("D") ) {
            steering.addPercent(-LOSS_IN * Math.signum(steering.getPercent()));
            if( abs(steering.getPercent()) < 0.05 )
                steering.setPercent( 0 );
        }

    }

    /**
     * Calculates longitudinal velocity in kilometers per hour.
     *
     * @return longitudinal velocity in kilometers per hour
     */
    public int getSpeed(){
        return (int) Math.round( 3.6 * (velocity.length()));
    }

    /**
     * Passes value of getRpm() of engine property.
     *
     * @return  value of rpm
     */
    public double getRpm(){
        return engine.getRpm();
    }

    /**
     * Returns value of throttle.
     *
     * @return  value of throttle in range 0.0 - 1.0
     */
    public double getThrottle(){
        return throttle.getPercent();
    }

    /**
     * Returns value of brake.
     *
     * @return  value of brake in range 0.0 - 1.0
     */
    public double getBrake(){
        return brake.getPercent();
    }

    /**
     * Returns value of steering.
     *
     * @return  value of steering in range -1.0 - 1.0
     */
    public double getSteering(){
        return steering.getPercent();
    }

    /**
     * Returns gear as int where -1 is reverse and 0 is neutral.
     *
     * @return value of gear
     */
    public int getGear(){ return gearbox.getGear(); }

    /**
     * Returns percentage of remaining fuel.
     *
     * @return  remaining fuel in range 0.0 - 1.0
     */
    public double getFuel(){
        return fuelTank.getPercent();
    }

    /**
     * Returns durability of front wheel.
     *
     * @return  durability value in range 0.0 - 1.0
     */
    public double getFrontWheelDurability(){
        return frontWheels.getDurability();
    }

    /**
     * Returns durability of rear wheel.
     *
     * @return  durability value in range 0.0 - 1.0
     */
    public double getRearWheelDurability(){
        return rearWheels.getDurability();
    }

    /**
     * Resets values of properties to prevent unexpected behavior
     */
    private void roundVariablesToZero(){
        if(Math.abs(velocity.getX()) < 0.01)
            velocity.setX( 0 );
        if(Math.abs(velocity.getY()) < 0.01)
            velocity.setY( 0 );
        if(Math.abs(yawRate) < 0.001)
            yawRate = 0;
    }

    /**
     * Updates velocity vector of car due to collision.
     *
     * @param collision     vector from point of collision to centre of car
     */
    public void addCollision( Vector2d collision ){
        collision.setY( -collision.getY() / 17 );
        collision.setX( collision.getX() / 17 );

        double strength = (1 - collision.length()/5.20);

        velocity.setX( velocity.getX() * ( 1 + 2 * strength*collision.getX() ) + 0.5 * Math.signum(collision.getX()));
        velocity.setY( velocity.getY() * ( 1 +  strength*collision.getY() ) + 0.5 * Math.signum(collision.getY()));
    }

    /**
     * Main working loop of car simulation.
     * Calculates new values of properties.
     * Edits value of parameters to reflect displacement of the car.
     *
     * @param translation   linear displacement
     * @param turn          rotation around turn radius
     * @param rotation      rotation around centre
     */
    public void calculateTransformation( Translate translation, Rotate turn,  Rotate rotation ){
        Vector2d airDrag = new Vector2d();
        double rollRes;
        double rearBrakingTorque;
        double frontBrakingTorque;
        double driveTorque;
        double engineBrakingTorque;
        double rearResultantTorque;
        double frontResultantTorque;
        double resultantForce;
        double rearLongitudinalSlipRatio;
        double frontLongitudinalSlipRatio;
        double rearTractiveForce;
        double frontTractiveForce;
        double frontSlipAngle;
        double rearSlipAngle;
        double frontLateralForce;
        double rearLateralForce;
        double corneringForce = 0;
        double yawTorque = 0;
        double steeringAngle = Math.toRadians( -45 * steering.getPercent() );

        weight.setMass( Vehicle.MASS_CONST + fuelTank.getMass() );
        momentOfInteria = weight.getMass() * ( ( WIDTH*WIDTH + LENGTH*LENGTH) / 12 + 1.6 * 1.6 );

        if( handbrake ) {
            rearWheels.setRotationSpeed(0);
        }

        airDrag.setX( -AR_CONST * velocity.getX() * abs(velocity.getX()) );
        airDrag.setY( -AR_CONST * velocity.getY() * abs(velocity.getY()) );

        rollRes = -RR_CONST * velocity.getY();

        if( fuelTank.isEmpty() )
            driveTorque = 0;
        else
            driveTorque = -engine.getTorque() * gearbox.getGearRatio() * Gearbox.DIFF_RATIO * Gearbox.TRANS_EFF * throttle.getPercent();

        engineBrakingTorque =  -engine.getEngineBraking() * gearbox.getGearRatio() * Gearbox.DIFF_RATIO * Gearbox.TRANS_EFF * (1 - throttle.getPercent());

        if( abs(velocity.getY()) > 0.3 ) {
            //Calculating slip:

            frontSlipAngle = frontWheels.getSlipAngle(velocity, yawRate, steeringAngle );

            if (frontSlipAngle > 0.08)
                frontSlipAngle = 0.07;
            if (frontSlipAngle < -0.08)
                frontSlipAngle = -0.07;

            rearSlipAngle = rearWheels.getSlipAngle(velocity, yawRate);

            if (rearSlipAngle > 0.08)
                rearSlipAngle = 0.07;
            if (rearSlipAngle < -0.08)
                rearSlipAngle = -0.07;

            rearLongitudinalSlipRatio = rearWheels.getSlipRatio(velocity.getY());

            if (rearLongitudinalSlipRatio > 0.06)
                rearLongitudinalSlipRatio = 0.05;
            if (rearLongitudinalSlipRatio < -0.06)
                rearLongitudinalSlipRatio = -0.05;

            frontLongitudinalSlipRatio = frontWheels.getSlipRatio(velocity.getY());

            if (frontLongitudinalSlipRatio > 0.06)
                frontLongitudinalSlipRatio = 0.05;
            if (frontLongitudinalSlipRatio < -0.06)
                frontLongitudinalSlipRatio = -0.05;

            //Calculating lateral forces:
            frontLateralForce = -STIFFNESS * frontEffectiveWeight * Math.tan(frontSlipAngle) * frontWheels.getTraction() / (1+10*Math.abs(frontLongitudinalSlipRatio));
            rearLateralForce = -STIFFNESS * rearEffectiveWeight * Math.tan(rearSlipAngle) * rearWheels.getTraction() / (1+10*Math.abs(rearLongitudinalSlipRatio));

            corneringForce = rearLateralForce + Math.cos(steeringAngle) * frontLateralForce + airDrag.getX();

            yawTorque = -rearLateralForce * 1.4 + frontLateralForce * 2.6;

            //Calculating longitudinal forces:

            if( abs(rearWheels.getRotationSpeed()) > 0.05 )
                rearBrakingTorque = rearWheels.getRotationSpeed()/ abs(rearWheels.getRotationSpeed()) * BR_CONST * brake.getPercent();
            else
                rearBrakingTorque = (rearWheels.getRotationSpeed() / 0.05) * BR_CONST * brake.getPercent();

            if( abs(frontWheels.getRotationSpeed()) > 0.05 )
                frontBrakingTorque = frontWheels.getRotationSpeed()/ abs(frontWheels.getRotationSpeed()) * BR_CONST * brake.getPercent();
            else
                frontBrakingTorque = (frontWheels.getRotationSpeed() / 0.05) * BR_CONST * brake.getPercent();

            rearTractiveForce = STIFFNESS * rearLongitudinalSlipRatio * rearWheels.getTraction() * rearEffectiveWeight;
            frontTractiveForce = STIFFNESS * frontLongitudinalSlipRatio * frontWheels.getTraction() * frontEffectiveWeight;

            resultantForce = rearTractiveForce + frontTractiveForce + rollRes + airDrag.getY();

            rearResultantTorque = driveTorque + engineBrakingTorque + rearBrakingTorque + ( rearTractiveForce + rollRes/2 + airDrag.getY()/2 ) * Wheel.RADIUS;
            frontResultantTorque = frontBrakingTorque + ( -frontTractiveForce + rollRes/2 + airDrag.getY()/2 ) * Wheel.RADIUS;

            rearWheels.increaseRotationSpeed(-rearResultantTorque * Vehicle.TIME_CONST / Wheel.INERTIA);
            frontWheels.increaseRotationSpeed(-frontResultantTorque * Vehicle.TIME_CONST / Wheel.INERTIA);

            frontWheels.degradeTire( frontLongitudinalSlipRatio, frontSlipAngle );
            rearWheels.degradeTire( rearLongitudinalSlipRatio, rearSlipAngle );
        } else {

            if( abs(velocity.getY()) > 0.1 )
                rearBrakingTorque = velocity.getY()/ abs(velocity.getY()) * BR_CONST * brake.getPercent();
            else
                rearBrakingTorque = (velocity.getY() / 0.1) * BR_CONST * brake.getPercent();

            if( abs(frontWheels.getRotationSpeed()) > 0.1 )
                frontBrakingTorque = frontWheels.getRotationSpeed()/ abs(frontWheels.getRotationSpeed()) * BR_CONST * brake.getPercent();
            else
                frontBrakingTorque = (frontWheels.getRotationSpeed() / 0.1) * BR_CONST * brake.getPercent();

            rearResultantTorque = driveTorque + engineBrakingTorque + rearBrakingTorque;
            frontResultantTorque = frontBrakingTorque;
            resultantForce = -(rearResultantTorque + frontResultantTorque)/Wheel.RADIUS + rollRes + airDrag.getY() ;
            rearWheels.setRotationSpeed( 2*velocity.getY()/Wheel.RADIUS );
            frontWheels.setRotationSpeed( 1.5*velocity.getY()/Wheel.RADIUS );
        }

        if( Math.abs(velocity.getY()) > 14 ){

            yawRate += yawTorque / momentOfInteria * TIME_CONST;

            velocity.setY( velocity.getY() + resultantForce * Vehicle.TIME_CONST / weight.getMass() );
            velocity.setX( velocity.getX() + (corneringForce + airDrag.getX()) * Vehicle.TIME_CONST / weight.getMass() );

            translation.setX(17 * velocity.getX() * TIME_CONST);
            translation.setY(-17 * velocity.getY() * TIME_CONST);

            rotation.setPivotX(35.3);
            rotation.setPivotY( 90 );
            rotation.setAngle( Math.toDegrees( yawRate * TIME_CONST ) );

        } else if( Math.abs(velocity.getX()) > 0.05 ) {

            if( Math.abs(yawRate) > 0.1 ) {
                yawTorque = -20 * weight.getMass() * Math.signum(yawRate) - weight.getMass() * Math.signum(velocity.getY()) * Math.tan(steeringAngle);

            } else {
                yawTorque = -10 * weight.getMass() * yawRate - weight.getMass() * Math.signum(velocity.getY()) * Math.tan(steeringAngle);
            }

            if( abs(velocity.getX()) > 0.1 ) {
                corneringForce = -10 * weight.getMass() * Math.signum(velocity.getX());

            } else {
                corneringForce = -5 * weight.getMass() * velocity.getX();
            }

            yawRate += yawTorque / momentOfInteria * TIME_CONST;

            velocity.setY( velocity.getY() + resultantForce * Vehicle.TIME_CONST / weight.getMass() );
            velocity.setX( velocity.getX() + (corneringForce + airDrag.getX()) * Vehicle.TIME_CONST / weight.getMass() );

            translation.setX(17 * velocity.getX() * TIME_CONST);
            translation.setY(-17 * velocity.getY() * TIME_CONST);

            rotation.setPivotX(35.3);
            rotation.setPivotY( 90 );
            rotation.setAngle( Math.toDegrees( yawRate * TIME_CONST ) );

        } else {
            if( steeringAngle != 0 ){
                double turnRadius = - (LENGTH) / Math.sin( steeringAngle );

                yawRate = velocity.getY() / turnRadius;

                turn.setPivotX(35.3 + 17 * turnRadius);
                turn.setPivotY(130);
                turn.setAngle( yawRate );

                velocity.setY( velocity.getY() + resultantForce * Vehicle.TIME_CONST / weight.getMass() );

            } else {
                velocity.setY( velocity.getY() + resultantForce * Vehicle.TIME_CONST / weight.getMass() );
                translation.setY(-17 * velocity.getY() * TIME_CONST);
            }
        }

        velocity = new Vector2d( velocity.getX()*Math.cos( Math.toRadians(rotation.getAngle()) ) - velocity.getY()*Math.sin( Math.toRadians(rotation.getAngle()) ), velocity.getX()*Math.sin( Math.toRadians(rotation.getAngle()) ) + velocity.getY()*Math.cos( Math.toRadians( rotation.getAngle() )) );

        rearEffectiveWeight = weight.getEffectiveWeightOnRear( resultantForce );
        frontEffectiveWeight = weight.getEffectiveWeightOnFront( resultantForce );

        if (gearbox.getGearRatio() != 0)
            engine.setRpm(rearWheels.getRotationSpeed() * gearbox.getGearRatio() * Gearbox.DIFF_RATIO * 60 / (2 * Math.PI));
        else
            engine.setNeutralGearRpm( throttle.getPercent() );

        fuelTank.consumeFuel( engine.getRpm(), throttle.getPercent() );

        roundVariablesToZero();
    }

    /**
     * Calls shift() method of gearbox property.
     */
    public void shift() {
        gearbox.shift();
    }

    /**
     * Calls upShiftReady() method of gearbox property.
     */
    public void upShiftReady() {
        gearbox.upShiftReady();
    }

    /**
     * Calls downShiftReady() method of gearbox property.
     */
    public void downShiftReady() {
        gearbox.downShiftReady();
    }

    /**
     * Calls refuel() method of fuelTank property.
     */
    public void refuel(){
        fuelTank.refuel();
    }

    /**
     * Calls switchTire() method of frontWheels and rearWheels properties.
     */
    public void changeTires(){
        frontWheels.switchTire();
        rearWheels.switchTire();
    }

    /**
     * Sets new value of friction property of frontWheels property.
     *
     * @param value new value of friction
     */
    public void setFrontFriction(double value){
        frontWheels.setFriction( value );
    }
    /**
     * Sets new value of friction property of rearWheels property.
     *
     * @param value new value of friction
     */
    public void setRearFriction(double value){
        rearWheels.setFriction( value );
    }

}
