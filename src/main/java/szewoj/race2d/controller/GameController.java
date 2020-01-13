package szewoj.race2d.controller;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import szewoj.race2d.model.LapTimer;
import szewoj.race2d.model.Vehicle;
import szewoj.race2d.utilities.Percent;
import szewoj.race2d.view.ViewManager;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Controller class of application.
 * Manages user inputs and communication between view and model classes.
 */
public class GameController {

    private ArrayList<String> input;
    private Vehicle raceCarModel;
    private ViewManager mainViewManager;
    private Translate memTrans;
    private Rotate memRot;
    private Rotate memTurn;
    private Percent tireChangeProgress;
    private LapTimer timer;
    private LinkedList<Long> recentTimes;
    private long bestTime;

    /**
     * Constructor of GameController initializes object with default values
     *
     * @param view  instance of ViewManager managed by GameController
     */
    public GameController(ViewManager view ){
        input = new ArrayList<String>();
        raceCarModel = new Vehicle();
        mainViewManager = view;
        memTrans = new Translate(0, 0, 0 );
        memRot = new Rotate( 0 );
        memTurn = new Rotate( 0 );
        tireChangeProgress = new Percent();
        mainViewManager.setupKeyListeners( this );
        timer = new LapTimer(3);
        recentTimes = new LinkedList<Long>();
        recentTimes.add(-1L);
        recentTimes.add(-1L);
        recentTimes.add(-1L);
        bestTime = -1;
    }

    /**
     * Main game loop function. Has to be put in any repetitive method of application for it to work.
     */
    public void refresh(){
        if( mainViewManager.isHomeScreenDisabled() ) {
            raceCarModel.updateInputs(input);
            handleButtonInputs();
            handleLapTimes();
            mainViewManager.setThrottleProgress(raceCarModel.getThrottle());
            mainViewManager.setBrakeProgress(raceCarModel.getBrake());
            mainViewManager.setSteeringProgress(raceCarModel.getSteering());

            mainViewManager.applyCarTransformsReversed(memTrans, memTurn, memRot);
            mainViewManager.applyBackgroundTransformsAdjustedReversed(memTrans, memTurn, memRot);

            memTrans = new Translate(0, 0, 0);
            memTurn = new Rotate(0);
            memRot = new Rotate(0);

            raceCarModel.setFrontFriction(mainViewManager.getFrontFriction());
            raceCarModel.setRearFriction(mainViewManager.getRearFriction());

            raceCarModel.calculateTransformation(memTrans, memTurn, memRot);

            mainViewManager.applyCarTransforms(memTrans, memTurn, memRot);

            mainViewManager.displaySpeed(raceCarModel.getSpeed());
            mainViewManager.setGearDisplay(raceCarModel.getGear());
            mainViewManager.setRpmPosition(raceCarModel.getRpm());
            mainViewManager.setFuelProgress(raceCarModel.getFuel());
            mainViewManager.setTireDurabilityProgress(raceCarModel.getFrontWheelDurability(), raceCarModel.getFrontWheelDurability(), raceCarModel.getRearWheelDurability(), raceCarModel.getRearWheelDurability());
            mainViewManager.setTireChangeProgress(tireChangeProgress.getPercent());

            mainViewManager.showPitstopPane();
            raceCarModel.addCollision(mainViewManager.getBarrierCollisionVector());
        }
    }

    /**
     * Handle of KeyPressed event.
     *
     * @param e     KeyEvent passed to handle
     */
    public void onKeyPressedHandle(KeyEvent e){
        String keyCode = e.getCode().toString();

        if(!input.contains(keyCode))
            input.add(keyCode);

        if( keyCode.equals("M") )
            raceCarModel.upShiftReady();

        if( keyCode.equals("N") )
            raceCarModel.downShiftReady();

        if( e.getCode().equals(KeyCode.ENTER) )
            mainViewManager.disableHomeScreen();
    }

    /**
     * Handle of KeyReleased event.
     *
     * @param e     KeyEvent passed to handle
     */
    public void onKeyReleasedHandle(KeyEvent e){
        String keyCode = e.getCode().toString();
        input.remove(keyCode);

        if( keyCode.equals("M") || keyCode.equals("N") )
            raceCarModel.shift();
    }

    /**
     * Performs button management.
     */
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

    /**
     * Performs lap time management.
     */
    public void handleLapTimes(){
        mainViewManager.displayTimes( timer.getCurrentLapTime(), bestTime, recentTimes );

        int cpIndex = mainViewManager.getCrossedCheckpoint();
        if( cpIndex > -1 ){
            timer.checkpointCrossed(cpIndex);
            if(timer.isReady()){
                long newTime = timer.pullFinishedLapTime();
                recentTimes.addFirst(newTime);
                recentTimes.removeLast();
                if(newTime < bestTime | bestTime < 0)
                    bestTime = newTime;
            }
        }
    }

}
