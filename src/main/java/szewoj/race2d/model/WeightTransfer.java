package szewoj.race2d.model;

/**
 * Simulates load on wheel axes.
 */
public class WeightTransfer {
    private double mass;
    private final double CG;
    private final double HEIGHT;
    private double length;

    /**
     *
     * @param mass          mass of car in kilograms
     * @param defaultCG     position of centre of gravity in meters, where front axis is 0
     * @param heightCG      height of centre of gravity in metres
     * @param length        distance between axes in metres
     */
    public WeightTransfer( double mass, double defaultCG, double heightCG, double length ){
        CG =  defaultCG;
        HEIGHT = heightCG;
        this.length = length;
        this.mass = mass;
    }

    /**
     * Setter of mass property.
     *
     * @param newMass   new mass value
     */
    public void setMass( double newMass ){
        mass = newMass;
    }

    /**
     * Getter of mass property.
     *
     * @return  mass value
     */
    public double getMass(){
        return mass;
    }

    /**
     * Calculates effective weight on rear axis
     *
     * @param force     longitudinal force in newtons
     * @return          weight on rear axis in newtons
     */
    public double getEffectiveWeightOnRear( double force ){
        return ( CG * 9.81 * mass +  HEIGHT * force ) / length;
    }

    /**
     * Calculates effective weight on front axis
     *
     * @param force     longitudinal force in newtons
     * @return          weight on front axis in newtons
     */
    public double getEffectiveWeightOnFront( double force ){
        return ( ( length - CG ) * 9.81 * mass - HEIGHT * force ) / length;
    }

}
