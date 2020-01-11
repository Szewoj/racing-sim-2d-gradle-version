package szewoj.race2d.model;

public class WeightTransfer {
    private double mass;
    private final double CG;
    private final double HEIGHT;
    private double length;

    public WeightTransfer( double mass, double defaultCG, double heightCG, double length ){
        this.CG =  defaultCG;
        this.HEIGHT = heightCG;
        this.length = length;
        this.mass = mass;
    }

    public void setMass( double newMass ){
        this.mass = newMass;
    }

    public double getMass(){
        return this.mass;
    }

    public double getEffectiveWeightOnRear( double force ){
        return ( this.CG * 9.81 * this.mass +  this.HEIGHT * force ) / length;
    }

    public double getEffectiveWeightOnFront( double force ){
        return ( ( this.length - this.CG ) * 9.81 * this.mass - this.HEIGHT * force ) / length;
    }

    public double getDistanceToFront(){
        return this.CG;
    }

    public double getDistanceToRear(){
        return this.length - this.CG;
    }

}
