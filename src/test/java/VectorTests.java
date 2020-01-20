import org.junit.Test;
import szewoj.race2d.utilities.Vector2D;

import static org.junit.Assert.*;


public class VectorTests {
    @Test
    public void vectorLengthCalculationTests(){

        Vector2D test = new Vector2D( 3, 4);

        assertEquals(5, (int) test.length() );

        test = new Vector2D( 1, 0 );

        assertEquals(1, (int) test.length() );

        test = new Vector2D( 0, 3 );

        assertEquals(3, (int) test.length() );

    }


}
