package sudoku;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import sudoku.GridElements.Cell;

public class GridElementsTest {
    
    @Test
    public void testCellGetter() {
        for (int row=0; row<9; row++) {
            for (int col=0; col<9; col++) {
                Cell cell = Cell.of(row, col);
                assertEquals(cell.getRow(), row);
                assertEquals(cell.getColumn(), col);
            }
        }
    }

}
