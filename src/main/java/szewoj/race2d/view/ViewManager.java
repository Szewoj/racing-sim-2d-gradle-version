package szewoj.race2d.view;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import szewoj.race2d.controller.GameController;
import szewoj.race2d.utilities.Vector2d;
import java.util.ArrayList;
import java.util.List;

/**
 * Class ViewManager is a view class that displays every item of the program
 */
public class ViewManager {

    private Rotate rpmPosition;
    private List<Label> recentTimes;
    private List<Label> differences;
    private List<Line> barriers;
    private Image trackMask;

    @FXML private AnchorPane homeScreen;
    @FXML private ProgressBar steerLeftPB, steerRightPB, throttlePB, brakePB, fuelPB, LFTirePB, RFTirePB, LRTirePB, RRTirePB, pitstopTiresPB, pitstopFuelPB;
    @FXML private Button fuelButton, tiresButton;
    @FXML private ImageView trackSprite, carSprite;
    @FXML private Group trackGroup, carGroup;
    @FXML private Label speedTxt, gearDisplay;
    @FXML private Label currentTime, bestTime, recentTime1, recentTime2, recentTime3;
    @FXML private Label difference1, difference2, difference3;
    @FXML private Polygon rpmMeter;
    @FXML private Rectangle carHitbox;
    @FXML private TitledPane pitstopPane;
    @FXML private Rectangle pitstopHitbox;
    @FXML private Line start, checkpoint1, checkpoint2;
    @FXML private Line barrier1, barrier2, barrier3, barrier4, barrier5, barrier6, barrier7, barrier8, barrier9, barrier10,
                        barrier11, barrier12, barrier13, barrier14, barrier15, barrier16, barrier17, barrier18, barrier19, barrier20,
                        barrier21, barrier22, barrier23, barrier24, barrier25, barrier26, barrier27, barrier28, barrier29, barrier30,
                        barrier31, barrier32, barrier33, barrier34, barrier35, barrier36, barrier37, barrier38, barrier39, barrier40;

    /**
     * Public constructor of the class.
     */
    public ViewManager() {
        rpmPosition = new Rotate( -90, -40, 40 );
        recentTimes = new ArrayList<>();
        differences = new ArrayList<>();
        barriers = new ArrayList<>();
        trackMask = new Image("/mask.png");
    }

    /**
     * JavaFX initializer for loading *.fxml files.
     */
    @FXML
    public void initialize(){
        carSprite.setCache(true);
        trackSprite.setCache(true);
        trackSprite.setCacheHint( CacheHint.SPEED );
        rpmMeter.getTransforms().add(rpmPosition);

        homeScreen.setVisible(true);

        recentTimes.add( recentTime1 );
        recentTimes.add( recentTime2 );
        recentTimes.add( recentTime3 );

        differences.add( difference1 );
        differences.add( difference2 );
        differences.add( difference3 );

        initBarriers();
    }

    /**
     * Converts coordinate system of the point.
     *
     * @param source    node of point's original coordinate system
     * @param target    node of point's desired coordinate system
     * @param point     original point
     * @return          converted point
     */
    public static Point2D convertPoint(Node source, Node target, Point2D point ){
        Point2D targetPoint = source.localToScene( point );
        targetPoint = target.sceneToLocal( targetPoint );
        return targetPoint;
    }

    /**
     * Adjusts rotate transform to make it applicable to background.
     *
     * @param rotate        original rotate transform
     * @param source        node of rotate's original coordinate system
     * @param background    node to which returned rotate transform will be applicable
     * @return              converted rotate transform
     */
    public static Rotate adjustToBackground(Rotate rotate, Node source, Node background ){
        Rotate newRotate = rotate.clone();
        Point2D pivot = ViewManager.convertPoint( source, background, new Point2D( rotate.getPivotX(), rotate.getPivotY()) );
        newRotate.setPivotX( pivot.getX() );
        newRotate.setPivotY( pivot.getY() );
        return newRotate;
    }

    /**
     * Checks if two Shape objects are intersecting.
     *
     * @param shape1    first shape
     * @param shape2    second shape
     * @return          boolean value, true if shapes intersect
     */
    public static boolean checkCollision(Shape shape1, Shape shape2 ){
        Shape intersect = Shape.intersect(shape1, shape2 );
        return intersect.getBoundsInLocal().getWidth() != -1;
    }

    /**
     * Converts time described by long value into String.
     *
     * @param time      time in milliseconds
     * @return          time described by String
     */
    public static String timeToString( long time ){
        if( time < 0 )
            return "";

        int minutes = (int)(time/60000);
        time -= 60000 * minutes;
        int seconds = (int)(time/1000);
        time -= 1000 * seconds;
        int milliseconds = (int)time;

        return minutes + ":" + seconds + "." + milliseconds;
    }

    /**
     * Converts time described by long value into String with sign added.
     *
     * @param difference    time in milliseconds
     * @return              time described by String
     */
    public static String differenceToString( long difference ){
        String sign;

        if( difference >= 0)
            sign = "+";
        else
            sign = "-";

        difference = Math.abs(difference);

        int minutes = (int)(difference/60000);
        difference -= 60000 * minutes;
        int seconds = (int)(difference/1000);
        difference -= 1000 * seconds;
        int milliseconds = (int)difference;

        return sign + minutes + ":" + seconds + "." + milliseconds;
    }

    /**
     * Gets the friction from trackMask Image in point of the front of the carGroup Node.
     *
     * @return      friction value in range 0.0 - 1.0
     */
    public double getFrontFriction(){
        Point2D frontContactPoint = convertPoint( carGroup, trackSprite, new Point2D( 35.3, 20 ) );

        PixelReader reader = trackMask.getPixelReader();

        return reader.getColor( (int)(frontContactPoint.getX()), (int)(frontContactPoint.getY()) + 5 ).getRed();

    }
    /**
     * Gets the friction from trackMask Image in point of the rear of the carGroup Node.
     *
     * @return      friction value in range 0.0 - 1.0
     */
    public double getRearFriction(){
        Point2D rearContactPoint = convertPoint( carGroup, trackSprite, new Point2D( 35.3, 20 ) );

        PixelReader reader = trackMask.getPixelReader();

        return reader.getColor( (int)(rearContactPoint.getX()), (int)(rearContactPoint.getY()) + 5 ).getRed();

    }

    /**
     * Rotates the rpm-meter to match given rpm value.
     *
     * @param rpm   input rpm value
     */
    @FXML
    public void setRpmPosition( double rpm ){
        this.rpmPosition.setAngle( 135*rpm/7000 - 109.28 );
    }

    /**
     * Displays the gear as a literal on screen.
     *
     * @param gear  gear described as int, where any positive int is matching gear, 0 is treated as neutral, and -1 as reverse
     */
    @FXML
    public void setGearDisplay( int gear ){
        if( gear > 0 )
            gearDisplay.setText( gear + "" );
        else if( gear == 0 )
            gearDisplay.setText( "N" );
        else if( gear == -1 )
            gearDisplay.setText( "R" );
    }

    /**
     * Returns the stage that is currently managed by ViewManager class.
     *
     * @return  currently managed stage
     */
    @FXML
    public Stage getStage(){
        return (Stage) trackGroup.getScene().getWindow();
    }

    /**
     * Sets progress value of corresponding ProgressBar.
     *
     * @param progress  double value in range 0.0 - 1.0
     */
    @FXML
    public void setThrottleProgress( double progress ){
        throttlePB.setProgress( progress );
    }

    /**
     * Sets progress value of corresponding ProgressBar.
     *
     * @param progress  double value in range 0.0 - 1.0
     */
    @FXML
    public void setBrakeProgress( double progress ){
        brakePB.setProgress( progress );
    }

    /**
     * Sets progress value of one of corresponding ProgressBars.
     * Positive value is set to the right one, and negative to the left one.
     *
     * @param steering  double value in range -1.0 - 1.0
     */
    @FXML
    public void setSteeringProgress( double steering ){
        if(steering > 0){
            steerLeftPB.setProgress(0);
            steerRightPB.setProgress(steering);
        }else{
            steerLeftPB.setProgress(-steering);
            steerRightPB.setProgress(0);
        }
    }

    /**
     * Sets progress value of corresponding ProgressBar.
     * Adds special effects to low input values.
     *
     * @param fuel  double value in range 0.0 - 1.0
     */
    @FXML
    public void setFuelProgress(double fuel ){
        if( pitstopPane.isVisible() )
            pitstopFuelPB.setProgress( fuel );

        fuelPB.setProgress( fuel );
        if( fuel < 0.3 )
            fuelPB.setStyle("-fx-accent: RED");
        else
            fuelPB.setStyle("-fx-accent: BLACK");
    }

    /**
     * Sets progress value of corresponding ProgressBar.
     *
     * @param leftFront     durability of left front tire described by a double value in range 0.0 - 1.0
     * @param rightFront    durability of right front tire described by a double value in range 0.0 - 1.0
     * @param leftRear      durability of left rear tire described by a double value in range 0.0 - 1.0
     * @param rightRear     durability of right rear tire described by a double value in range 0.0 - 1.0
     */
    @FXML
    public void setTireDurabilityProgress( double leftFront, double rightFront, double leftRear, double rightRear ){
        LFTirePB.setProgress(leftFront);
        RFTirePB.setProgress(rightFront);
        LRTirePB.setProgress(leftRear);
        RRTirePB.setProgress(rightRear);
    }

    /**
     * Sets progress value of corresponding ProgressBar only if its parent pane is visible.
     *
     * @param progress  double value in range 0.0 - 1.0
     */
    @FXML
    public void setTireChangeProgress( double progress ){
        if( pitstopPane.isVisible() )
            pitstopTiresPB.setProgress( progress );
    }

    /**
     * Applies given transformations to carGroup Node.
     *
     * @param translation   linear displacement
     * @param turning       rotation around turn radius
     * @param rotation      rotation around centre
     */
    @FXML
    public void applyCarTransforms(Translate translation, Rotate turning, Rotate rotation ){
        carGroup.getTransforms().addAll( translation.clone(), turning.clone(), rotation.clone() );
    }

    /**
     * Applies inverse of given transformations to carGroup Node.
     *
     * @param translation   linear displacement
     * @param turning       rotation around turn radius
     * @param rotation      rotation around centre
     */
    @FXML
    public void applyCarTransformsReversed(Translate translation, Rotate turning, Rotate rotation ){

        Translate translate = new Translate( -translation.getX(), -translation.getY() );

        Rotate turn = turning.clone();
        turn.setAngle( -turn.getAngle() );

        Rotate rotate = rotation.clone();
        rotate.setAngle( -rotate.getAngle() );

        carGroup.getTransforms().addAll( rotate, turn, translate );
    }

    /**
     * Applies inverse of given transformations applicable to background to trackGroup Node.
     *
     * @param translation   linear displacement
     * @param turning       rotation around turn radius
     * @param rotation      rotation around centre
     */
    @FXML
    public void applyBackgroundTransformsAdjustedReversed( Translate translation, Rotate turning, Rotate rotation ){
        double yx = trackGroup.getLocalToSceneTransform().getMyx();
        double yy = trackGroup.getLocalToSceneTransform().getMyy();
        double angle = Math.atan2(yx, yy);

        Translate translate = new Translate( -translation.getY() * Math.sin(angle) - translation.getX() * Math.cos(angle), -translation.getY() * Math.cos(angle) + translation.getX() * Math.sin(angle) );
        trackGroup.getTransforms().add( translate );

        Rotate turn = ViewManager.adjustToBackground( turning, carGroup, trackGroup );
        turn.setAngle( -turn.getAngle() );
        trackGroup.getTransforms().add( turn );

        Rotate rotate = ViewManager.adjustToBackground( rotation, carGroup, trackGroup );
        rotate.setAngle( -rotate.getAngle() );
        trackGroup.getTransforms().add( rotate );

    }

    /**
     * Displays value of given speed on screen.
     *
     * @param speed     displayed speed
     */
    @FXML
    public void displaySpeed( int speed ){
        speedTxt.setText( Math.abs(speed) + "" );
    }

    /**
     * Displays given times in specified section of screen.
     *
     * @param current   current measured time
     * @param best      best lap time since start of application
     * @param recent    List of 3 most recent lap times
     */
    @FXML
    public void displayTimes( long current, long best, List<Long> recent){
        currentTime.setText( timeToString(current) );
        bestTime.setText( timeToString(best) );
        for( int i = 0; i < 3; ++i ){
            long time = recent.get(i);
            recentTimes.get(i).setText( timeToString(time) );
            if( time < 0 )
                differences.get(i).setText("");
            else
                differences.get(i).setText( differenceToString( time - best ) );
        }
    }

    /**
     * Shows pitstopPane if car entered pitstop.
     */
    @FXML
    public void showPitstopPane(){
        if( checkCollision( carHitbox, pitstopHitbox) )
            pitstopPane.setVisible(true);
        else
            pitstopPane.setVisible(false);
    }

    /**
     * Checks if car collides with any barrier, and if yes, then it returns the vector of collision.
     *
     * @return      vector from point of collision to centre of car
     */
    @FXML
    public Vector2d getBarrierCollisionVector(){
        Vector2d out = new Vector2d(0, 0);

        Shape intersect = null;
        for(Line barrier : barriers ){
            intersect = Shape.intersect( barrier, carHitbox );
            if(intersect.getBoundsInLocal().getWidth() != -1)
                break;
            else
                intersect = null;
        }
        if(intersect != null){
            Point2D intersectCenter = new Point2D( intersect.getBoundsInLocal().getCenterX(), intersect.getBoundsInLocal().getCenterY());
            Point2D carCenter = convertPoint(carHitbox, intersect, new Point2D( carHitbox.getBoundsInLocal().getCenterX(), carHitbox.getBoundsInLocal().getCenterY()));

            out.setX( carCenter.getX() - intersectCenter.getX() );
            out.setY( carCenter.getY() - intersectCenter.getY() );
        }
        return out;
    }

    /**
     * Checks if car crossed any checkpoint, and if yes, then it returns the checkpoint's index.
     * If none of known checkpoints are crossed, method returns code of value -1.
     *
     * @return      checkpoint index
     */
    @FXML
    public int getCrossedCheckpoint(){
        if(checkCollision(carHitbox, start))
            return 0;
        if(checkCollision(carHitbox, checkpoint1))
            return 1;
        if(checkCollision(carHitbox, checkpoint2))
            return 2;
        return-1;
    }

    /**
     * Sets handles for key control.
     *
     * @param controller    GameController class providing handles
     */
    @FXML
    public void setupKeyListeners(GameController controller){
        getStage().getScene().setOnKeyPressed(
                controller::onKeyPressedHandle
        );
        getStage().getScene().setOnKeyReleased(
                controller::onKeyReleasedHandle
        );

    }

    /**
     * Passes value of isPressed() of fuelButton property.
     *
     * @return      value of isPressed() of fuelButton property
     */
    @FXML
    public boolean isFuelButtonPressed(){
        return fuelButton.isPressed();
    }

    /**
     * Passes value of isPressed() of tiresButton property.
     *
     * @return      value of isPressed() of tiresButton property
     */
    @FXML
    public boolean isTiresButtonPressed(){
        return tiresButton.isPressed();
    }

    /**
     * Passes value of isDisabled() of homeScreen property.
     *
     * @return      value of isDisabled() of homeScreen property
     */
    @FXML
    public boolean isHomeScreenDisabled(){
        return homeScreen.isDisabled();
    }

    /**
     * Disables and sets homeScreen property invisible.
     */
    @FXML
    public void disableHomeScreen(){
        homeScreen.setVisible(false);
        homeScreen.setDisable(true);
    }

    /**
     * Initializes barriers property with all Line barrier* property.
     */
    @FXML
    private void initBarriers(){
        barriers.add(barrier1);
        barriers.add(barrier2);
        barriers.add(barrier3);
        barriers.add(barrier4);
        barriers.add(barrier5);
        barriers.add(barrier6);
        barriers.add(barrier7);
        barriers.add(barrier8);
        barriers.add(barrier9);
        barriers.add(barrier10);
        barriers.add(barrier11);
        barriers.add(barrier12);
        barriers.add(barrier13);
        barriers.add(barrier14);
        barriers.add(barrier15);
        barriers.add(barrier16);
        barriers.add(barrier17);
        barriers.add(barrier18);
        barriers.add(barrier19);
        barriers.add(barrier20);
        barriers.add(barrier21);
        barriers.add(barrier22);
        barriers.add(barrier23);
        barriers.add(barrier24);
        barriers.add(barrier25);
        barriers.add(barrier26);
        barriers.add(barrier27);
        barriers.add(barrier28);
        barriers.add(barrier29);
        barriers.add(barrier30);
        barriers.add(barrier31);
        barriers.add(barrier32);
        barriers.add(barrier33);
        barriers.add(barrier34);
        barriers.add(barrier35);
        barriers.add(barrier36);
        barriers.add(barrier37);
        barriers.add(barrier38);
        barriers.add(barrier39);
        barriers.add(barrier40);
    }
}
