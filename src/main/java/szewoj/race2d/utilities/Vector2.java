package szewoj.race2d.utilities;

/**
 * Container for two double values with additional operations.
 */
public class Vector2 {
    private double x;
    private double y;

    /**
     * Default constructor.
     */
    public Vector2(){
        x = 0;
        y = 0;
    }

    /**
     * Constructor for custom values.
     *
     * @param x x property of new Vector2d
     * @param y y property of new Vector2d
     */
    public Vector2(double x, double y ){
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor for copy of existing Vector2d
     *
     * @param origin original Vector2d
     */
    public Vector2(Vector2 origin ){
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

    public double angleRadians(){
        return Math.atan2( y, x );
    }


    public double angleDegrees(){
        return Math.toDegrees( Math.atan2( y, x ) );
    }

    public void rotateInRadians( double angle ){
        double tempX = x*Math.cos( angle ) - y*Math.sin( angle );
        double tempY = x*Math.sin( angle ) + y*Math.cos( angle );

        x = tempX;
        y = tempY;
    }

    public void rotateInDegrees( double angle ){
        rotateInRadians( Math.toRadians(angle) );
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void setX( double newX ){
        x = newX;

    }

    public void setY( double newY ){
        y = newY;
    }
}
