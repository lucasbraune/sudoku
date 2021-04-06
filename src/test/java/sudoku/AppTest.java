package sudoku;

import sudoku.Grid;
import sudoku.Grid.Digit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void testGetSet()
    {
        Grid grid = new Grid();
        assertFalse(grid.get(0, 0).isPresent());
        grid.set(0, 0, Digit.ONE);
        assertEquals(Digit.ONE, grid.get(0, 0).get());
    }
}
