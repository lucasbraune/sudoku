package sudoku;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import lombok.Getter;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Row;
import sudoku.GridElements.Column;
import sudoku.GridElements.Box;
import sudoku.GridElements.Digit;

public class SelfAnalyzingGrid {

    @Getter
    private final Grid grid;

    @Getter
    private final Map<Cell, Set<Digit>> candidates;

    private SelfAnalyzingGrid(Grid grid, Map<Cell, Set<Digit>> candidates) {
        this.grid = grid;
        this.candidates = candidates;
    }

    private static Map<Cell, Set<Digit>> allCandidates(UnmodifiableGrid grid) {
        Map<Cell, Set<Digit>> all = new HashMap<>();
        for (Cell cell : grid.emptyCells()) {
            all.put(cell, EnumSet.allOf(Digit.class));
        }
        return all;
    }

    /**
     * @throws NoSuchElementException if the cell is empty.
     */
    private void removeCandidatesFromRowColumnAndBox(Cell nonEmptyCell) {
        Digit d = grid.digitAt(nonEmptyCell).get();
        for (Cell cell : grid.emptyCells(Row.of(nonEmptyCell))) {
            candidates.get(cell).remove(d);
        }
        for (Cell cell : grid.emptyCells(Column.of(nonEmptyCell))) {
            candidates.get(cell).remove(d);
        }
        for (Cell cell : grid.emptyCells(Box.of(nonEmptyCell))) {
            candidates.get(cell).remove(d);
        }
    }

    public SelfAnalyzingGrid(UnmodifiableGrid grid) {
        this(Grid.copyOf(grid), allCandidates(grid));
        for (Cell cell : grid.nonEmptyCells()) {
            removeCandidatesFromRowColumnAndBox(cell);
        }
    }

    /**
     * Returns a deep copy of this self analyzing grid.
     */
    @Override
    public SelfAnalyzingGrid clone() {
        return new SelfAnalyzingGrid(Grid.copyOf(grid), Util.copy(candidates));
    }

    Cell nextEmptyCell() {
        if (!grid.hasEmptyCell()) {
            throw new NoSuchElementException("The grid is full.");
        }
        Cell next = null;
        int minCandidates = 10;
        for (Cell cell : candidates.keySet()) {
            int numberOfCandidates = candidates.get(cell).size();
            if (numberOfCandidates < minCandidates) {
                next = cell;
                minCandidates = numberOfCandidates;
            }
        }
        return next;
    }

    void setCell(Cell cell, Digit d) {
        grid.setDigit(cell, d);
        candidates.remove(cell);
        removeCandidatesFromRowColumnAndBox(cell);
    }

    Digit candidateFor(Cell coords) {
        return candidates.get(coords).iterator().next();
    }

    boolean multipleCandidatesExistFor(Cell coords) {
        return candidates.get(coords).size() > 1;
    }

    private void removeCandidate(Cell coords, Digit d) {
        candidates.get(coords).remove(d);
    }

    private boolean ranOutOfCandidates() {
        for (Cell cell : candidates.keySet()) {
            if (candidates.get(cell).size() == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean solve() {
        while (grid.hasEmptyCell()) {
            if (!grid.isConsistent() || ranOutOfCandidates()) {
                return false;
            }
            Cell coords = nextEmptyCell();
            while (multipleCandidatesExistFor(coords)) {
                Digit d = candidateFor(coords);
                SelfAnalyzingGrid clone = clone();
                clone.setCell(coords, d);
                boolean solved = clone.solve();
                if (solved) {
                    Grid.copy(clone.grid, grid);
                    return true;
                }
                removeCandidate(coords, d);
            }
            setCell(coords, candidateFor(coords));
        }
        return grid.isConsistent();
    }

}
