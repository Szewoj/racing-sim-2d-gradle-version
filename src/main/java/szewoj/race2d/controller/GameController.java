package szewoj.race2d.controller;

import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import szewoj.race2d.model.Vehicle;
import szewoj.race2d.utilities.Percent;
import szewoj.race2d.view.ViewManager;

import java.util.ArrayList;

public class GameController {

    private ArrayList<String> input;
    private Vehicle raceCarModel;
    private ViewManager mainViewManager;
    private Translate memTrans;
    private Rotate memRot;
    private Rotate memTurn;
    private Percent tireChangeProgress;

    public GameController(ViewManager view ){
        input = new ArrayList<String>();
        raceCarModel = new Vehicle();
        mainViewManager = view;
        memTrans = new Translate(0, 0, 0 );
        memRot = new Rotate( 0 );
        memTurn = new Rotate( 0 );
        tireChangeProgress = new Percent();
        mainViewManager.setupKeyListeners( this );

    }

    public void refresh(){
        raceCarModel.updateInputs(input);
        handleButtonInputs();
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
        mainViewManager.setFuelProgress( raceCarModel.getFuel() );
        mainViewManager.setTireDurabilityProgress(raceCarModel.getFrontWheelDurability(),raceCarModel.getFrontWheelDurability(),raceCarModel.getRearWheelDurability(),raceCarModel.getRearWheelDurability());
        mainViewManager.setTireChangeProgress( tireChangeProgress.getPercent() );

        mainViewManager.updateHitboxes();
        mainViewManager.checkAllBarrierCollisions();
    }

    public void onKeyPressedHandle(KeyEvent e){
        String keyCode = e.getCode().toString();

        if(!input.contains(keyCode))
            input.add(keyCode);

        if( keyCode.equals("M") )
            raceCarModel.upShiftReady();

        if( keyCode.equals("N") )
            raceCarModel.downShiftReady();
    }

    public void onKeyReleasedHandle(KeyEvent e){
        String keyCode = e.getCode().toString();
        input.remove(keyCode);

        if( keyCode.equals("M") || keyCode.equals("N") )
            raceCarModel.shift();
    }

    public void handleButtonInputs(){
        if(mainViewManager.isFuelButtonPressed()){
            raceCarModel.refuel();
        }
        if(mainViewManager.isTiresButtonPressed()){

            tireChangeProgress.addPercent( 1.0/90 );

            if( tireChangeProgress.getPercent() == 1 ){
                raceCarModel.changeTires();
                tireChangeProgress.setPercent( 0 );
            }

        }else{
            tireChangeProgress.setPercent( 0 );
        }
    }

}
