package sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.EqualsAndHashCode;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Row;
import sudoku.GridElements.Column;
import sudoku.GridElements.Box;

/**
 * A grid with a set of digits associated to each of its empty cells. The set
 * contains the candidate values for that cell.
 * 
 * When a blank cell is set to a digit, that digit is ruled out as a cadidate in
 * the blank cell's row, column and box. Users of this class can also manually
 * rule out a digit as a candidate for a given cell, using its public
 * {@code ruleOut} methods.
 * 
 * This class overrides the {@code equals()} and {@code hashCode()} methods of
 * its super class, while preserving their contracts. An instance of
 * {@code AnnotatedGrid} can only be equal to other instances of
 * {@code AnnotatedGrid}. Two instances of this class are equal if, and only if,
 * their underlying grids and sets of candidates are equal.
 */
@EqualsAndHashCode(callSuper = true)
public final class AnnotatedGrid extends Grid {

    private final Map<Cell, Set<Digit>> candidates;

    /** Creates an empty grid with all digits as candidates for all of its cells. */
    public AnnotatedGrid() {
        super();
        candidates = new HashMap<>();
        for (Cell cell : GridElements.cells()) {
            candidates.put(cell, EnumSet.allOf(Digit.class));
        }
    }

    /**
     * Creates a deep copy of the specified {@code AnnotatedGrid}. Both the
     * underlying grid and candidate sets are copied.
     */
    public AnnotatedGrid(AnnotatedGrid grid) {
        super(grid);
        candidates = Util.copy(grid.candidates);
    }

    /**
     * Removes the specified digit from the set of candidates for the specified
     * cell.
     * 
     * @throws NoSuchElementException if the specified cell is not blank.
     */
    public void ruleOut(Digit d, Cell cell) {
        try {
            candidates.get(cell).remove(d);
        } catch (NullPointerException npe) {
            throw new NoSuchElementException("The given cell is not empty");
        }
    }

    /**
     * Rules out the specified digit as a candidate for the empty cells in the given
     * range (typically a row, column or box).
     */
    public void ruleOut(Digit d, Iterable<Cell> cells) {
        for (Cell cell : emptyCells(cells)) {
            ruleOut(d, cell);
        }
    }

    /**
     * Sets the digit at specified empty cell. Rules out that digit as a candidate
     * for the empty cells in the given cell's row, column and box.
     * 
     * @throws GridOverwriteException if the cell is not blank
     */
    @Override
    public void setDigit(Cell cell, Digit d) {
        super.setDigit(cell, d);
        candidates.remove(cell);
        ruleOut(d, Row.of(cell));
        ruleOut(d, Column.of(cell));
        ruleOut(d, Box.of(cell));
    }

    /**
     * Creates an annotated grid from an ordinary grid. The annotated grid is a deep
     * copy of the ordinary grid. The candidate sets are created as if by setting
     * all digits as candidates for each empty cell, then ruling out the value of
     * each nonempty cell as a candidate along thenonempty cell's row, column and
     * box.
     */
    public static AnnotatedGrid fromOrdinaryGrid(UnmodifiableGrid grid) {
        AnnotatedGrid annotatedGrid = new AnnotatedGrid();
        Grid.copy(grid, annotatedGrid); // Calls the overriden method setDigit(Cell, Digit)
        return annotatedGrid;
    }

    @Override
    public Set<Cell> emptyCells() {
        return Collections.unmodifiableSet(candidates.keySet());
    }

    @Override
    public boolean hasEmptyCell() {
        return candidates.keySet().size() > 0;
    }

    /**
     * 
     * Returns a set of candidates for value of the specified cell in a solution of
     * this grid. This set is guaranteed to contain the value of this cell in every
     * solution of this grid if the user of this grid never calls its
     * {@code ruleOut} method manually (as opposed to automatically, when setting
     * the value of a cell).
     * 
     * @throws NoSuchElementException if the specified cell is not empty
     */
    public Set<Digit> candidates(Cell emptyCell) {
        if (emptyCells().contains(emptyCell)) {
            return Collections.unmodifiableSet(candidates.get(emptyCell));
        } else {
            throw new NoSuchElementException("The given cell is not empty");
        }
    }

    /**
     * Returns a string with one line for each empty cell. The lines are ordered
     * according to the number of candidates for the corresponding cell; fewer
     * candidates come first. Each line contains the coordinates of its cell
     * followed by its set of candidates.
     */
    public final String candidatesToString() {
        List<Cell> cells = new ArrayList<>(emptyCells());
        cells.sort(Comparator.comparingInt(cell -> candidates(cell).size()));
        StringBuilder sb = new StringBuilder();
        for (Cell cell : cells) {
            sb.append(cell + ": " + candidates(cell) + "\n");
        }
        return sb.toString();
    }

}
