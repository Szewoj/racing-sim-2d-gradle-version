package szewoj.race2d.utilities;

public class Vector2d {
    private double x;
    private double y;

    public Vector2d(){
        x = 0;
        y = 0;
    }

    public Vector2d( double x, double y ){
        this.x = x;
        this.y = y;
    }

    public Vector2d( Vector2d origin ){
        this.x = origin.getX();
        this.y = origin.getY();
    }

    public double length(){
        return Math.sqrt( x*x + y*y );
    }

    public double lengthSquared(){
        return x*x + y*y;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public void setX( double newX ){
        this.x = newX;

    }
    public void setY( double newY ){
        this.y = newY;
    }
}
