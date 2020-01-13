package szewoj.race2d.model;

public class Gearbox {
    private int gear;
    private int previous;
    private int ready;

    public static final double DIFF_RATIO = 3.42;
    public static final double TRANS_EFF = 0.9;

    public double getGearRatio(){

        switch( gear ){
            case -1:
                return -4.22;
            case 1:
                return 5.22;
            case 2:
                return 2.98;
            case 3:
                return 2.10;
            case 4:
                return 1.62;
            case 5:
                return 1.35;
            case 6:
                return 0.90;
            default:
                return 0;
        }
    }

    public void shift(){
        gear = ready;
        previous = ready;
    }


    public void upShiftReady(){
        if( previous < 6 ) {
            ready = previous + 1;
            gear = 0;
        }
    }

    public void downShiftReady(){
        if( previous > -1 ) {
            ready = previous - 1;
            gear = 0;
        }
    }

    public int getGear(){
        return gear;
    }

}
