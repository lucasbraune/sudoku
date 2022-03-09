package sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import sudoku.GridElements.Box;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Column;
import sudoku.GridElements.Row;
import sudoku.UnmodifiableGrid.GridParserException;

@TestInstance(Lifecycle.PER_CLASS)
public class TestsWithFirstPuzzle {

    private UnmodifiableGrid grid = null;

    @BeforeAll
    public void beforeAll() throws GridParserException {
        grid = UnmodifiableGrid.fromString("003020600" + "900305001" + "001806400" + "008102900"
                + "700000008" + "006708200" + "002609500" + "800203009" + "005010300");
    }

    private AnnotatedGrid annotatedGrid = null;

    @BeforeEach
    public void beforeEach() {
        annotatedGrid = AnnotatedGrid.fromOrdinaryGrid(grid);
    }

    @Test
    public void emptyCellsOverride() {
        Set<Cell> emptyCells = new HashSet<>();
        grid.emptyCells().forEach(emptyCells::add);
        assertEquals(emptyCells, annotatedGrid.emptyCells());
    }

    @Test
    @DisplayName("A value is not a candidate if its appears in its cell's row, column and box.")
    public void noObviouslyWrongCandidates() {
        for (Cell emptyCell : annotatedGrid.emptyCells()) {
            Set<Digit> candidates = annotatedGrid.candidates(emptyCell);
            for (Iterable<Cell> gridElement : List.of(Row.of(emptyCell), Column.of(emptyCell),
                    Box.of(emptyCell))) {
                for (Cell nonEmptyCell : annotatedGrid.nonEmptyCells(gridElement)) {
                    Digit d = annotatedGrid.digitAt(nonEmptyCell).get();
                    assertFalse(candidates.contains(d),
                            d.toInt() + " is a candidate for cell " + nonEmptyCell);
                }
            }
        }
    }

    @Test
    public void hasCandidatesForEveryEmptyCell() {
        for (Cell cell : annotatedGrid.emptyCells()) {
            assertTrue(annotatedGrid.candidates(cell).size() > 0);
        }
    }

    @Test
    public void solveFirstPuzzle() {
        Optional<UnmodifiableGrid> solved = Solver.solve(grid);
        assertTrue(solved.isPresent());
        assertTrue(solved.get().isSolved());
    }

}
