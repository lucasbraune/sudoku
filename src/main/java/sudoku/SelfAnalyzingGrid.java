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
import sudoku.GridElements.Digit;

@EqualsAndHashCode(callSuper = true)
public final class SelfAnalyzingGrid extends Grid {

    private final Map<Cell, Set<Digit>> candidates;

    public SelfAnalyzingGrid() {
        super();
        candidates = new HashMap<>();
        for (Cell cell : GridElements.allCells()) {
            candidates.put(cell, EnumSet.allOf(Digit.class));
        }
    }

    public SelfAnalyzingGrid(SelfAnalyzingGrid grid) {
        super(grid);
        candidates = Util.copy(grid.candidates);
    }

    /**
     * @throws NoSuchElementException if the specified cell is not empty
     */
    public void ruleOut(Digit d, Cell cell) {
        try {
            candidates.get(cell).remove(d);
        } catch (NullPointerException npe) {
            throw new NoSuchElementException("The given cell is not empty");
        }        
    }

    public void ruleOut(Digit d, Iterable<Cell> cells) {
        for (Cell cell : emptyCells(cells)) {
            ruleOut(d, cell);
        }
    }

    /**
     * Sets the digit at the cell with the specified coordinates.
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

    public static SelfAnalyzingGrid fromOrdinaryGrid(UnmodifiableGrid grid) {
        SelfAnalyzingGrid sag = new SelfAnalyzingGrid();
        Grid.copy(grid, sag); // Calls the overriden method setDigit(Cell, Digit)
        return sag;
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
     * @throws NoSuchElementException if the specified cell is not empty
     */
    public Set<Digit> candidates(Cell emptyCell) {
        if (emptyCells().contains(emptyCell)) {
            return Collections.unmodifiableSet(candidates.get(emptyCell));
        } else {
            throw new NoSuchElementException("The given cell is not empty");
        }
    }

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
