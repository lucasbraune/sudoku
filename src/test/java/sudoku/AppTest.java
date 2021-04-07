package sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sudoku.Grid.Digit;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void gridStartsEmpty() {
        Grid grid = new Grid();
        assertFalse(grid.get(0, 0).isPresent());
    }

    @Test
    public void getAfterSet() {
        Grid grid = new Grid();
        grid.set(0, 0, Digit.ONE);
        assertEquals(Integer.valueOf(1), grid.get(0, 0).get());
    }

    @Test
    public void getWithBadIndicesThrowsException() {
        Grid grid = new Grid();
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.get(-1, 0); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.get(9, 0); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.get(0, -1); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.get(0, 9); });
    }

    @Test
    public void settingCellTwiceThrowsException() {
        Grid grid = new Grid();
        assertThrows(IllegalArgumentException.class, () -> {
            grid.set(0, 0, Digit.ONE);
            grid.set(0, 0, Digit.TWO);
        });
    }
}
