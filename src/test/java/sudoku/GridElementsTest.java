package sudoku;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import sudoku.GridElements.Box;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Column;
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
        for (Row row : GridElements.rows()) {
            List<Cell> actualCells = new ArrayList<>();
            for (Cell cell : row) {
                actualCells.add(cell);
            }

            List<Cell> expectedCells = new ArrayList<>();
            for (int j=0; j<9; j++) {
                expectedCells.add(Cell.of(row.getIndex(), j));
            }
            assertEquals(expectedCells, actualCells);
        }
    }

    @Test
    public void testColumnIterator() {
        for (Column column : GridElements.columns()) {
            List<Cell> actualCells = new ArrayList<>();
            for (Cell cell : column) {
                actualCells.add(cell);
            }
            
            List<Cell> expectedCells = new ArrayList<>();
            for (int i=0; i<9; i++) {
                expectedCells.add(Cell.of(i, column.getIndex()));
            }
            assertEquals(expectedCells, actualCells);
        }
    }

    @Test
    public void testBoxGetter() {
        for (Cell cell : GridElements.cells()) {
            Cell corner = Box.of(cell).getCorner();

            assertEquals(cell.getRow() - cell.getRow() % 3, corner.getRow());
            assertEquals(cell.getColumn() - cell.getColumn() % 3, corner.getColumn());
        }
    }

    @Test
    public void testBoxIterator() {
        for (Box box : GridElements.boxes()) {
            int cornerRow = box.getCorner().getRow();
            int cornerColumn = box.getCorner().getColumn();
            
            List<Cell> actualCells = new ArrayList<>();
            for (Cell cell : box) {
                actualCells.add(cell);
            }
            
            List<Cell> expectedCells = new ArrayList<>();
            for (int i=0; i<3; i++) {
                for (int j=0; j<3; j++) {
                    expectedCells.add(Cell.of(cornerRow + i, cornerColumn + j));
                }
            }
            assertEquals(expectedCells, actualCells);
        }
    }

}
