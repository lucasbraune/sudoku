package sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import sudoku.GridElements.Cell;
import sudoku.exceptions.GridOverwriteException;
import sudoku.exceptions.GridParserException;

public class BasicTests {

    @Test
    public void gridReadAndWrite() {
        Grid grid = new Grid();
        Cell cell = Cell.of(0, 0);
        assertEquals(Optional.<Digit>empty(), grid.digitAt(cell));
        grid.setDigit(Cell.of(0, 0), Digit.ONE);
        assertEquals(Digit.ONE, grid.digitAt(cell).get());
        assertThrows(GridOverwriteException.class, () -> {
            grid.setDigit(Cell.of(0, 0), Digit.ONE);
        });
    }

    @Test
    public void digitConversions() {
        Digit seven = Digit.SEVEN;
        assertEquals(7, seven.toInt());
        assertEquals(seven, Digit.fromInt(7));
        assertEquals('7', seven.toChar());
        assertEquals(seven, Digit.fromChar('7'));
    }

    @Test
    public void gridStringConversions() throws GridParserException {
        String gridAsString = 
            "003020600" +
            "900305001" +
            "001806400" +
            "008102900" +
            "700000008" +
            "006708200" +
            "002609500" +
            "800203009" +
            "005010300";
        Grid grid = Grid.fromString(gridAsString);
        assertEquals(Digit.THREE, grid.digitAt(Cell.of(0, 2)).get());
        assertEquals(gridAsString, grid.toString());
    }

}
