package szewoj.race2d.controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import szewoj.race2d.model.Vehicle;
import szewoj.race2d.view.ViewManager;

import java.util.ArrayList;

public class GameController {

    private ArrayList<String> input;
    private Vehicle raceCarModel;
    private ViewManager mainViewManager;
    private Translate memTrans;
    private Rotate memRot;
    private Rotate memTurn;
    private Rotate rpmPosition;

    public GameController(ViewManager view ){
        input = new ArrayList<String>();
        raceCarModel = new Vehicle();
        mainViewManager = view;
        memTrans = new Translate(0, 0, 0 );
        memRot = new Rotate( 0 );
        memTurn = new Rotate( 0 );

        this.setupListeners( view.getStage() );

    }

    public void refresh(){
        raceCarModel.updateInputs(input);
        mainViewManager.setThrottleProgress(raceCarModel.getThrottle());
        mainViewManager.setBrakeProgress(raceCarModel.getBrake());
        mainViewManager.setSteeringProgress(raceCarModel.getSteering());

        mainViewManager.applyCarTransformsReversed( memTrans, memTurn, memRot );
        mainViewManager.applyBackgroundTransformsAdjustedReversed( memTrans, memTurn, memRot );

        memTrans = new Translate( 0, 0, 0 );
        memTurn = new Rotate( 0 );
        memRot = new Rotate( 0 );

        raceCarModel.calculateTransformation( memTrans, memTurn, memRot );

        mainViewManager.applyCarTransforms( memTrans, memTurn, memRot );

        mainViewManager.displaySpeed(raceCarModel.getSpeed());
        mainViewManager.setGearDisplay(raceCarModel.getGear());
        mainViewManager.setRpmPosition( raceCarModel.getRpm() );

        mainViewManager.updateHitboxes();
        mainViewManager.checkAllBarrierCollisions();
    }


    public void setupListeners(Stage stage){
        stage.getScene().setOnKeyPressed(
                new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        String keyCode = e.getCode().toString();

                        if(!input.contains(keyCode))
                            input.add(keyCode);

                        if( keyCode.equals("M") ) {
                            raceCarModel.upShiftReady();
                            System.out.println("ready up");
                        }

                        if( keyCode.equals("N") ) {
                            raceCarModel.downShiftReady();
                            System.out.println("ready down");
                        }
                    }
                }
        );

        stage.getScene().setOnKeyReleased(
                new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        String keyCode = e.getCode().toString();
                        input.remove(keyCode);

                        if( keyCode.equals("M") || keyCode.equals("N") )
                            raceCarModel.shift();

                    }
                }
        );
    }



}
