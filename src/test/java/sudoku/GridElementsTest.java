package sudoku;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sudoku.GridElements.Cell;
import sudoku.GridElements.Row;

public class GridElementsTest {
    
    @Test
    public void testCellGetter() {
        for (int row=0; row<9; row++) {
            for (int col=0; col<9; col++) {
                Cell cell = Cell.of(row, col);
                assertEquals(row, cell.getRow());
                assertEquals(col, cell.getColumn());
            }
        }
    }

    @Test
    public void testRowIterator() {
        for (int i=0; i<9; i++) {
            Row row = Row.of(i);
            
            List<Cell> expectedCells = new ArrayList<>();
            for (int j=0; j<9; j++) {
                expectedCells.add(Cell.of(i, j));
            }
            
            List<Cell> actualCells = new ArrayList<>();
            for (Cell cell : row) {
                actualCells.add(cell);
            }

            assertEquals(expectedCells, actualCells);
        }
    }

}
