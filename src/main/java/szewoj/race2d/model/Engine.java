package szewoj.race2d.model;

public class Engine {
    public static final double MAX_RPM = 6800;
    private double rpm;

    public Engine(){
        this.rpm = 1000;
    }

    public double getTorque(){

        if(this.rpm < 2500)
            return 210 + this.rpm * 20/500;
        else if(this.rpm < 3750)
            return 310;
        else if(this.rpm < 4400)
            return 167 + this.rpm * 30/650;
        else if(this.rpm < 6000)
            return 444;
        else if(this.rpm < MAX_RPM)
            return 1368 - this.rpm*154/1000;
        else
            return (1000-this.rpm)/(MAX_RPM-1000) * 150 + (MAX_RPM-this.rpm) * 0.75;

    }

    public double getEngineBraking(){
        if( this.rpm > MAX_RPM )
            return (1000-this.rpm)/(MAX_RPM-1000) * 150 + (MAX_RPM-this.rpm) * 0.75;
        else
            return (1000-this.rpm)/(MAX_RPM-1000) * 150;
    }

    public void setRpm( double newValue ){
        this.rpm += 0.20*(newValue-this.rpm);
        if(this.rpm < 1000)
            this.rpm = 1000;
    }

    public void setNeutralGearRpm(  double throttle ){
        if (this.rpm < MAX_RPM)
            this.rpm += 0.05*((1000 + (MAX_RPM - 500) * throttle)-this.rpm);
        else
            this.rpm -= 20;

        if(this.rpm < 1000)
            this.rpm = 1000;

    }

    public double getRpm() {
        return this.rpm;
    }



}
