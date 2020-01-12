package szewoj.race2d.view;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import szewoj.race2d.controller.GameController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class ViewManager {

    private Rotate rpmPosition;
    private ArrayList<Label> recentTimes;
    private ArrayList<Label> differences;

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
    @FXML private Line barrier1;
    @FXML private Line start, checkpoint1, checkpoint2;

    public ViewManager() {
        rpmPosition = new Rotate( -90, -40, 40 );
        recentTimes = new ArrayList<Label>();
        differences = new ArrayList<Label>();
    }

    @FXML
    public void initialize(){
        carSprite.setCache(true);
        trackSprite.setCache(true);
        trackSprite.setCacheHint( CacheHint.SPEED );
        rpmMeter.getTransforms().add(rpmPosition);

        recentTimes.add( recentTime1 );
        recentTimes.add( recentTime2 );
        recentTimes.add( recentTime3 );

        differences.add( difference1 );
        differences.add( difference2 );
        differences.add( difference3 );
    }

    public static Point2D convertPoint(Node source, Node target, Point2D point ){
        Point2D targetPoint = source.localToScene( point );
        targetPoint = target.sceneToLocal( targetPoint );
        return targetPoint;
    }

    public static Rotate adjustToBackground(Rotate rotate, Node source, Node background ){
        Rotate newRotate = rotate.clone();
        Point2D pivot = ViewManager.convertPoint( source, background, new Point2D( rotate.getPivotX(), rotate.getPivotY()) );
        newRotate.setPivotX( pivot.getX() );
        newRotate.setPivotY( pivot.getY() );
        return newRotate;
    }

    public static boolean checkCollision(Shape shape1, Shape shape2 ){
        Shape intersect = Shape.intersect(shape1, shape2 );
        return intersect.getBoundsInLocal().getWidth() != -1;
    }

    public static String timeToString( long time ){
        if( time < 0 )
            return "";

        int minutes = (int)(time/60000);
        time -= 60000 * minutes;
        int seconds = (int)(time/1000);
        time -= 1000 * seconds;
        int miliseconds = (int)time;

        return minutes + ":" + seconds + "." + miliseconds;
    }

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
        int miliseconds = (int)difference;

        return sign + minutes + ":" + seconds + "." + miliseconds;
    }

    @FXML
    public void setRpmPosition( double rpm ){
        this.rpmPosition.setAngle( 135*rpm/7000 - 109.28 );
    }

    @FXML
    public void setGearDisplay( int gear ){
        if( gear > 0 )
            gearDisplay.setText( gear + "" );
        else if( gear == 0 )
            gearDisplay.setText( "N" );
        else if( gear == -1 )
            gearDisplay.setText( "R" );
    }

    @FXML
    public Stage getStage(){
        return (Stage) trackGroup.getScene().getWindow();
    }

    @FXML
    public void setThrottleProgress( double progress ){
        throttlePB.setProgress( progress );
    }

    @FXML
    public void setBrakeProgress( double progress ){
        brakePB.setProgress( progress );
    }

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

    @FXML
    public void setTireDurabilityProgress( double leftFront, double rightFront, double leftRear, double rightRear ){
        LFTirePB.setProgress(leftFront);
        RFTirePB.setProgress(rightFront);
        LRTirePB.setProgress(leftRear);
        RRTirePB.setProgress(rightRear);
    }

    @FXML
    public void setTireChangeProgress( double progress ){
        if( pitstopPane.isVisible() )
            pitstopTiresPB.setProgress( progress );
    }

    @FXML
    public void applyCarTransforms(Translate translation, Rotate turning, Rotate rotation ){
        carGroup.getTransforms().addAll( translation.clone(), turning.clone(), rotation.clone() );
    }

    @FXML
    public void applyCarTransformsReversed(Translate translation, Rotate turning, Rotate rotation ){

        Translate translate = new Translate( -translation.getX(), -translation.getY() );

        Rotate turn = turning.clone();
        turn.setAngle( -turn.getAngle() );

        Rotate rotate = rotation.clone();
        rotate.setAngle( -rotate.getAngle() );

        carGroup.getTransforms().addAll( rotate, turn, translate );
    }

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

    @FXML
    public void displaySpeed( int speed ){
        speedTxt.setText( Math.abs(speed) + "" );
    }

    @FXML
    public void displayTimes( long current, long best, LinkedList<Long> recent){
        currentTime.setText( timeToString(current) );
        bestTime.setText( timeToString(best) );
        for( int i = 0; i < 3; ++i ){
            long time = recent.get(i);
            recentTimes.get(i).setText( timeToString(time) );
            if( time < 0 )
                differences.get(i).setText("");
            else
                differences.get(i).setText( differenceToString( best - time ) );
        }
    }

    @FXML
    public void updateHitboxes(){
        if( checkCollision( carHitbox, pitstopHitbox) )
            pitstopPane.setVisible(true);
        else
            pitstopPane.setVisible(false);
    }

    @FXML
    public void checkAllBarrierCollisions(){
        //Point2D test = convertPoint( carGroup, trackSprite, new Point2D(0, 0));
        //System.out.println( test.getX() + " " + test.getY() );
    }

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

    @FXML
    public void setupKeyListeners(GameController controller){
        getStage().getScene().setOnKeyPressed(
                new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        controller.onKeyPressedHandle( e );
                    }
                }
        );
        getStage().getScene().setOnKeyReleased(
                new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        controller.onKeyReleasedHandle( e );
                    }
                }
        );

    }

    @FXML
    public boolean isFuelButtonPressed(){
        return fuelButton.isPressed();
    }

    @FXML
    public boolean isTiresButtonPressed(){
        return tiresButton.isPressed();
    }

}
