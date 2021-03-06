package sudoku;

import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;
import sudoku.GridElements.Cell;

public class Solver {

    /**
     * Returns a solution to the given Sudoku grid, if one exists.
     * 
     * This method offers correctness, but not a performance guarantee. On my machine, all 50
     * puzzles from Project Euler's Problem 96 can be solved in well under one second.
     */
    public static Optional<Grid> solve(Grid grid) {
        return solve(AnnotatedGrid.fromOrdinaryGrid(grid));
    }

    private static Optional<Grid> solve(AnnotatedGrid grid) {
        while (grid.hasEmptyCell()) {
            if (!grid.isConsistent() || ranOutOfCandidates(grid)) {
                return Optional.empty();
            }
            Cell cell = cellWithFewestCandidates(grid);
            while (multipleCandidatesExistFor(grid, cell)) {
                Digit d = candidateFor(grid, cell);
                AnnotatedGrid clone = new AnnotatedGrid(grid);
                clone.setDigit(cell, d);
                Optional<Grid> solved = solve(clone);
                if (solved.isPresent()) {
                    return solved;
                }
                grid.ruleOut(d, cell);
            }
            grid.setDigit(cell, candidateFor(grid, cell));
        }
        return grid.isConsistent() ? Optional.of(new Grid(grid)) : Optional.empty();
    }

    private static Digit candidateFor(AnnotatedGrid grid, Cell cell) {
        return grid.candidates(cell).iterator().next();
    }

    private static boolean multipleCandidatesExistFor(AnnotatedGrid grid, Cell coords) {
        return grid.candidates(coords).size() > 1;
    }

    private static boolean ranOutOfCandidates(AnnotatedGrid grid) {
        for (Cell cell : grid.emptyCells()) {
            if (grid.candidates(cell).size() == 0) {
                return true;
            }
        }
        return false;
    }

    private static Cell cellWithFewestCandidates(AnnotatedGrid grid) {
        try {
            return Collections.min(grid.emptyCells(),
                    Comparator.comparingInt(cell -> grid.candidates(cell).size()));
        } catch (NullPointerException e) {
            throw new NoSuchElementException("The grid is full.");
        }
    }

}
