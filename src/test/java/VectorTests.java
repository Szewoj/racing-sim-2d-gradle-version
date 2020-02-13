import org.junit.Test;
import static org.junit.Assert.*;
import szewoj.race2d.utilities.Vector2;


public class VectorTests {

    @Test
    public void vectorLengthCalculationTests(){

        Vector2 test = new Vector2( 3, 4);
        assertEquals(5, (int) test.length() );

        test = new Vector2( 1, 0 );
        assertEquals(1, (int) test.length() );

        test = new Vector2( 0, 1 );
        assertEquals(1, (int) test.length() );

    }

    @Test
    public void vectorAngleCalculationTest(){

        Vector2 test = new Vector2( 0, 0);
        assertEquals( 0, (int)test.angleDegrees() );

        test = new Vector2( 1, 1);
        assertEquals( 45, (int)test.angleDegrees() );

        test = new Vector2( 0, 1);
        assertEquals( 90, (int)test.angleDegrees() );

        test = new Vector2( -1, 1);
        assertEquals( 135, (int)test.angleDegrees() );

        test = new Vector2( -1, 0);
        assertEquals( 180, (int)test.angleDegrees() );

        test = new Vector2( -1, -1);
        assertEquals( -135, (int)test.angleDegrees() );

        test = new Vector2( 0, -1);
        assertEquals( -90, (int)test.angleDegrees() );

        test = new Vector2( 1, -1);
        assertEquals( -45, (int)test.angleDegrees() );

    }

    @Test
    public void vectorRotateTest(){

        Vector2 test = new Vector2( 1, 0);
        test.rotateInDegrees( 45 );
        assertEquals( 45, (int)test.angleDegrees() );

        test = new Vector2( 1, 0);
        test.rotateInDegrees( 90 );
        assertEquals( 90, (int)(test.angleDegrees() ));

        test = new Vector2( 1, 0);
        test.rotateInDegrees( 135 );
        assertEquals( 135, (int)test.angleDegrees() );

        test = new Vector2( 1, 0);
        test.rotateInDegrees( 180 );
        assertEquals( 180, (int)test.angleDegrees() );

        test = new Vector2( 1, 0);
        test.rotateInDegrees( 225 );
        assertEquals( -135, (int)test.angleDegrees() );

        test = new Vector2( 1, 0);
        test.rotateInDegrees( 270 );
        assertEquals( -90, (int)test.angleDegrees() );

        test = new Vector2( 1, 0);
        test.rotateInDegrees( 315 );
        assertEquals( -45, (int)test.angleDegrees() );

    }

}
