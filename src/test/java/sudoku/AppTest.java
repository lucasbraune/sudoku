package sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import sudoku.Grid.GridOverwriteException;
import sudoku.GridElements.Box;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Column;
import sudoku.GridElements.Digit;
import sudoku.GridElements.Row;
import sudoku.UnmodifiableGrid.GridParserException;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void digitConversions() {
        Digit seven = Digit.SEVEN;
        assertTrue(seven.toInt() == 7);
        assertTrue(seven.equals(Digit.fromInt(7)));
        assertTrue(seven.toChar() == '7');
        assertTrue(seven.equals(Digit.fromChar('7')));
    }

    @Test
    public void gridStartsEmpty() {
        UnmodifiableGrid grid = new UnmodifiableGrid();
        for (Cell cell : GridElements.allCells()) {
            assertFalse(grid.digitAt(cell).isPresent());
        }
    }

    @Test
    public void badGridAccessThrowsException() {
        UnmodifiableGrid grid = new UnmodifiableGrid();
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.digitAt(-1, 0); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.digitAt(9, 0); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.digitAt(0, -1); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.digitAt(0, 9); });
    }

    @Test
    public void gridOverwriteThrowsException() {
        Grid grid = new Grid();
        assertThrows(GridOverwriteException.class, () -> {
            grid.setDigit(0, 0, Digit.ONE);
            grid.setDigit(0, 0, Digit.TWO);
        });
    }

    private static final String sampleGridAsString = 
            "003020600" +
            "900305001" +
            "001806400" +
            "008102900" +
            "700000008" +
            "006708200" +
            "002609500" +
            "800203009" +
            "005010300";

    private static UnmodifiableGrid sampleGrid() throws GridParserException {
        return UnmodifiableGrid.fromString(sampleGridAsString);
    }

    @Test
    public void gridToString() throws GridParserException {
        assertEquals(sampleGridAsString, sampleGrid().toString());
    }

    @Test
    public void emptyCellsOverride() throws GridParserException {
        UnmodifiableGrid grid = sampleGrid();
        Set<Cell> emptyCells = new HashSet<>();
        grid.emptyCells().forEach(emptyCells::add);
        SelfAnalyzingGrid sag = SelfAnalyzingGrid.fromOrdinaryGrid(grid);
        assertEquals(emptyCells, sag.emptyCells());
    }

    @Test @DisplayName("A value is not a candidate in its cell's row, column and box.")
    public void noObviouslyWrongCandidates() throws GridParserException {
        SelfAnalyzingGrid grid = SelfAnalyzingGrid.fromOrdinaryGrid(sampleGrid());
        for (Cell emptyCell : grid.emptyCells()) {
            Set<Digit> candidates = grid.candidates(emptyCell);
            for (Cell cell : grid.nonEmptyCells(Row.of(emptyCell))) {
                Digit d = grid.digitAt(cell).get();
                assertFalse(candidates.contains(d), d.toInt() + " is a candidate for cell " + cell);
            }
            for (Cell cell : grid.nonEmptyCells(Column.of(emptyCell))) {
                Digit d = grid.digitAt(cell).get();
                assertFalse(candidates.contains(d), d.toInt() + " is a candidate for cell " + cell);
            }
            for (Cell cell : grid.nonEmptyCells(Box.of(emptyCell))) {
                Digit d = grid.digitAt(cell).get();
                assertFalse(candidates.contains(d), d.toInt() + " is a candidate for cell " + cell);
            }
        }
    }

    @Test
    public void hasCandidatesForEveryCell() throws GridParserException {
        SelfAnalyzingGrid grid = SelfAnalyzingGrid.fromOrdinaryGrid(sampleGrid());
        assertTrue(grid.emptyCells().contains(Cell.of(0, 0)));
        for (Cell cell : grid.emptyCells()) {
            assertTrue(grid.candidates(cell).size() > 0);
        }
    }

    @Test
    public void solveFirstPuzzle() throws GridParserException {
        assertTrue(Solver.solve(sampleGrid()).isPresent());
    }

    private static String readLines(BufferedReader br, int n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("The number of lines given is negative (" + n + ")");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<n; i++) {
            sb.append(br.readLine());
        }
        return sb.toString();
    }

    private static List<UnmodifiableGrid> readGrids() throws IOException, GridParserException {
        List<UnmodifiableGrid> grids = new ArrayList<>();
        File file = new File("src/test/resources/puzzles.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i=0; i<50; i++) {
                br.readLine();
                UnmodifiableGrid grid = UnmodifiableGrid.fromString(readLines(br, 9));
                grids.add(grid);
            }
        }
        return grids;
    }

    @Test
    public void canReadGrids() throws IOException, GridParserException {
        List<UnmodifiableGrid> grids = readGrids();
        assertTrue(grids.size() == 50);
    }

    @ParameterizedTest
    @MethodSource("readGrids")
    public void sampleGridIsConsistent(UnmodifiableGrid grid) {
        assertTrue(grid.isConsistent());
    }

    @ParameterizedTest
    @MethodSource("readGrids")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void canSolveSampleGrid(UnmodifiableGrid grid) {
        assertTrue(Solver.solve(grid).isPresent());
    }

}
