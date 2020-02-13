package szewoj.race2d.model;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import szewoj.race2d.utilities.Percent;
import szewoj.race2d.utilities.Vector2;

import java.util.List;

import static java.lang.Math.abs;

/**
 * Simulates Vehicle physics.
 * Implements bicycle car physics model.
 */
public class Vehicle {
    //Physics related variables and constants:
    private static final double TIME_CONST = 1.0/60;        //time of one tick
    private static final double AR_CONST = 0.2;             //air resistance coefficient
    private static final double RR_CONST = 8.4;             //rolling resistance coefficient
    private static final double BR_CONST = 10000;           //braking torque
    private static final double STIFFNESS = 24;             //tire stiffness
    private static final double MASS_CONST = 1420;
    private static final double WIDTH = 2;
    private static final double LENGTH = 4.5;
    private static final double CRITICAL_SPEED = 15;       //speed under which slip angle physics fails
    private static final double METER_TO_PIXEL_RATIO = 17;
    private Vector2 velocity;
    private double yawRate;
    private Engine engine;
    private Gearbox gearbox;
    private FuelTank fuelTank;
    private Wheel frontWheels;
    private Wheel rearWheels;
    private WeightTransfer weight;
    private double momentOfInertia;
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
        velocity = new Vector2( 0, 0);
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
        momentOfInertia = weight.getMass() * ( WIDTH*WIDTH + LENGTH*LENGTH) / 12;
        rearEffectiveWeight =  weight.getEffectiveWeightOnRear( 0 );
        frontEffectiveWeight = weight.getEffectiveWeightOnFront( 0 );
    }

    /**
     * Simulates input inertia.
     *
     * @param inputs    array of string codes
     */
    public void updateInputs(List<String> inputs){

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
     * Calculates reduction of traction based on longitudinal slip.
     * Simulates understeer when braking and oversteer when accelerating.
     *
     * @param longitudinalSlipRatio     longitudinal slip of tire
     * @return                          traction reduction coefficient
     */
    private double getSlipTractionCoefficient(double longitudinalSlipRatio ){
        return 1.0 / (1+10*Math.abs(longitudinalSlipRatio));
    }

    /**
     * Updates velocity vector of car due to collision.
     *
     * @param collision     vector from point of collision to centre of car
     */
    public void addCollision( Vector2 collision ){
        collision.setY( -collision.getY() / 17 );
        collision.setX( collision.getX() / 17 );

        double strength = (1 - collision.length()/5.20);

        velocity.setX( velocity.getX()  +  strength*collision.getX() * Math.abs(velocity.getX()) + strength*collision.getX() );
        velocity.setY( velocity.getY()  +  strength*collision.getY() * Math.abs(velocity.getY()) + strength*collision.getY() );
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
        Vector2 airDrag = new Vector2();
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

        //moment of inertia of cuboid with Steiner's equation for displaced axis
        momentOfInertia = weight.getMass() * ( ( WIDTH*WIDTH + LENGTH*LENGTH) / 12 + 1.6 * 1.6 );

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


        if( abs(velocity.getY()) > 0.5 ) {
        //Physics using slip
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
            frontLateralForce = -STIFFNESS * frontEffectiveWeight * Math.tan(frontSlipAngle) * frontWheels.getTraction() * getSlipTractionCoefficient(frontLongitudinalSlipRatio);
            rearLateralForce = -STIFFNESS * rearEffectiveWeight * Math.tan(rearSlipAngle) * rearWheels.getTraction() * getSlipTractionCoefficient(rearLongitudinalSlipRatio);

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
        //Physics using forces on centre of gravity

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

            //Wheels are still slipping to allow transitions between slip based and non slip based physics
            rearWheels.setRotationSpeed( 2*velocity.getY()/Wheel.RADIUS );
            frontWheels.setRotationSpeed( 1.5*velocity.getY()/Wheel.RADIUS );
        }


        if( Math.abs(velocity.getY()) > CRITICAL_SPEED ){
        //Application of turn forces and moments over critical speed
            yawRate += yawTorque / momentOfInertia * TIME_CONST;

            velocity.setY( velocity.getY() + resultantForce * Vehicle.TIME_CONST / weight.getMass() );
            velocity.setX( velocity.getX() + (corneringForce + airDrag.getX()) * Vehicle.TIME_CONST / weight.getMass() );

            translation.setX( METER_TO_PIXEL_RATIO * velocity.getX() * TIME_CONST );
            translation.setY( -METER_TO_PIXEL_RATIO * velocity.getY() * TIME_CONST );

            rotation.setPivotX(35.3);
            rotation.setPivotY( 90 );
            rotation.setAngle( Math.toDegrees( yawRate * TIME_CONST ) );

        } else if( Math.abs(velocity.getX()) > 0.05 ) {
        //Application of turn during free slide

            if( Math.abs(yawRate) > 0.1 ) {
                yawTorque = -20 * weight.getMass() * Math.signum(yawRate) - weight.getMass() * Math.signum(velocity.getY()) * Math.tan(steeringAngle);

            } else {
                yawTorque = -10 * weight.getMass() * yawRate - weight.getMass() * Math.signum(velocity.getY()) * Math.tan(steeringAngle);
            }

            if( abs(velocity.getX()) > 0.1 ) {
                corneringForce = -15 * weight.getMass() * Math.signum(velocity.getX());

            } else {
                corneringForce = -10 * weight.getMass() * velocity.getX();
            }

            yawRate += yawTorque / momentOfInertia * TIME_CONST;

            velocity.setY( velocity.getY() + resultantForce * Vehicle.TIME_CONST / weight.getMass() );
            velocity.setX( velocity.getX() + (corneringForce + airDrag.getX()) * Vehicle.TIME_CONST / weight.getMass() );

            translation.setX( METER_TO_PIXEL_RATIO * velocity.getX() * TIME_CONST );
            translation.setY( -METER_TO_PIXEL_RATIO * velocity.getY() * TIME_CONST );

            rotation.setPivotX(35.3);
            rotation.setPivotY( 90 );
            rotation.setAngle( Math.toDegrees( yawRate * TIME_CONST ) );

        } else {
        //Application of movement under critical speed
            if( steeringAngle != 0 ){
                double turnRadius = - (LENGTH) / Math.sin( steeringAngle );

                yawRate = velocity.getY() / turnRadius;

                turn.setPivotX(35.3 + METER_TO_PIXEL_RATIO * turnRadius);
                turn.setPivotY(130);
                turn.setAngle( yawRate );

                velocity.setY( velocity.getY() + resultantForce * Vehicle.TIME_CONST / weight.getMass() );

            } else {
                velocity.setY( velocity.getY() + resultantForce * Vehicle.TIME_CONST / weight.getMass() );
                translation.setY( -METER_TO_PIXEL_RATIO * velocity.getY() * TIME_CONST );
            }
        }

        velocity = new Vector2( velocity.getX()*Math.cos( Math.toRadians(rotation.getAngle()) ) - velocity.getY()*Math.sin( Math.toRadians(rotation.getAngle()) ),
                                 velocity.getX()*Math.sin( Math.toRadians(rotation.getAngle()) ) + velocity.getY()*Math.cos( Math.toRadians( rotation.getAngle() )) );

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
