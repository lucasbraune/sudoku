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
import sudoku.Grid.Digit;
import util.Util;

public class SelfAnalyzingGrid {
    @Getter
    private final Grid grid;

    @Getter
    private final Map<Cell, Set<Digit>> candidates;

    private SelfAnalyzingGrid(Grid grid, Map<Cell, Set<Digit>> candidates) {
        this.grid = grid;
        this.candidates = candidates;
    }

    private static Map<Cell, Set<Digit>> allCandidates(Grid grid) {
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
        Digit d = grid.digit(nonEmptyCell).get();
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

    public SelfAnalyzingGrid(Grid grid) {
        this(grid, allCandidates(grid));
        for (Cell cell : grid.nonEmptyCells()) {
            removeCandidatesFromRowColumnAndBox(cell);
        }
    }

    /**
     * Returns a deep copy of this self analyzing grid.
     */
    @Override
    public SelfAnalyzingGrid clone() {
        return new SelfAnalyzingGrid(grid.clone(), Util.copy(candidates));
    }

    private Set<Cell> emptyCells() {
        return candidates.keySet();
    }

    public boolean isSolved() {
        return !hasEmptyCell() && isConsistent();
    }

    private void copySolutionFrom(Grid solved) {
        try {
            for (Cell coords : emptyCells()) {
                grid.set(coords, solved.digit(coords).get());
            }
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("The given grid has empty cells");
        }
        if (!isSolved()) {
            throw new IllegalArgumentException("The given grid is not a solution of this grid.");
        }
        candidates.clear();
    }

    private boolean hasEmptyCell() {
        return emptyCells().size() > 0;
    }

    private Cell nextEmptyCell() {
        // TODO
        return emptyCells().iterator().next();
    }

    private boolean isConsistent() {
        return grid.isConsistent();
    }

    private void setCell(Cell coords, Digit d) {
        // TODO
    }

    private Digit candidateFor(Cell coords) {
        return candidates.get(coords).iterator().next();
    }

    private boolean multipleCandidatesExistFor(Cell coords) {
        return candidates.get(coords).size() > 1;
    }

    private void removeCandidate(Cell coords, Digit d) {
        // TODO
    }

    public boolean solve() {
        while (hasEmptyCell()) {
            if (!isConsistent()) {
                return false;
            }
            Cell coords = nextEmptyCell();
            while (multipleCandidatesExistFor(coords)) {
                Digit d = candidateFor(coords);
                SelfAnalyzingGrid clone = clone();
                clone.setCell(coords, d);
                boolean solved = clone.solve();
                if (solved) {
                    copySolutionFrom(clone.getGrid());
                    return true;
                }
                removeCandidate(coords, d);
            }
            setCell(coords, candidateFor(coords));
        }
        return isConsistent();
    }

}
