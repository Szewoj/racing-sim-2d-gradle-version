package szewoj.race2d.utilities;

/**
 * Container for two double values with additional operations.
 */
public class Vector2D {
    private double x;
    private double y;

    /**
     * Default constructor.
     */
    public Vector2D(){
        x = 0;
        y = 0;
    }

    /**
     * Constructor for custom values.
     *
     * @param x x property of new Vector2d
     * @param y y property of new Vector2d
     */
    public Vector2D(double x, double y ){
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor for copy of existing Vector2d
     *
     * @param origin original Vector2d
     */
    public Vector2D(Vector2D origin ){
        x = origin.getX();
        y = origin.getY();
    }

    /**
     * Returns the length of vector.
     *
     * @return  vector length
     */
    public double length(){
        return Math.sqrt( x*x + y*y );
    }

    /**
     * Returns squared length of vector.
     *
     * @return  squared vector length
     */
    public double lengthSquared(){
        return x*x + y*y;
    }

    /**
     * Getter of x property.
     *
     * @return x property value
     */
    public double getX(){
        return x;
    }

    /**
     * Getter of y property.
     *
     * @return y property value
     */
    public double getY(){
        return y;
    }

    /**
     * Setter of x property.
     *
     * @param newX new x property value
     */
    public void setX( double newX ){
        x = newX;

    }

    /**
     * Setter of y property.
     *
     * @param newY new y property value
     */
    public void setY( double newY ){
        y = newY;
    }
}
