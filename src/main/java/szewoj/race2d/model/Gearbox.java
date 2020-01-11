package szewoj.race2d.model;

public class Gearbox {
    private int gear;
    private int previous;
    private int ready;

    public static final double DIFF_RATIO = 3.42;
    public static final double TRANS_EFF = 0.9;

    public double getGearRatio(){

        switch( this.gear ){
            case -1:
                return -3.62;
            case 1:
                return 3.62;
            case 2:
                return 1.95;
            case 3:
                return 1.41;
            case 4:
                return 1.13;
            case 5:
                return 0.93;
            case 6:
                return 0.81;
            default:
                return 0;
        }
    }

    public void shift(){
        this.gear = this.ready;
        this.previous = this.ready;
    }


    public void upShiftReady(){
        if( this.previous < 6 ) {
            ready = this.previous + 1;
            this.gear = 0;
        }
    }

    public void downShiftReady(){
        if( this.previous > -1 ) {
            ready = this.previous - 1;
            this.gear = 0;
        }
    }

    public int getGear(){
        return this.gear;
    }

}
