package sudoku;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import lombok.Getter;
import sudoku.Grid.Coordinates;
import sudoku.Grid.Digit;

public class SelfAnalyzingGrid {
    @Getter
    private final Grid grid;

    private final Map<Coordinates, Set<Digit>> possibilities;

    private SelfAnalyzingGrid(Grid grid, Map<Coordinates, Set<Digit>> possibilities) {
        this.grid = grid;
        this.possibilities = possibilities;
    }

    private static Map<Coordinates, Set<Digit>> allPossibilities(Grid grid) {
        Map<Coordinates, Set<Digit>> all = new HashMap<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Coordinates coords = new Coordinates(row, col);
                if (!grid.get(coords).isPresent()) {
                    all.put(coords, EnumSet.allOf(Digit.class));
                }
            }
        }
        return all;
    }

    public SelfAnalyzingGrid(Grid grid) {
        this(grid, allPossibilities(grid));
    }

    /**
     * Returns a deep copy of a map whose values are set of objets of an enumeration
     * class.
     */
    private static <K, E extends Enum<E>> Map<K, Set<E>> copy(Map<? extends K, ? extends Set<E>> map) {
        Map<K, Set<E>> copy = new HashMap<>();
        for (K key : map.keySet()) {
            copy.put(key, EnumSet.copyOf(map.get(key)));
        }
        return copy;
    }

    /**
     * Returns a deep copy of this self analyzing grid.
     */
    @Override
    public SelfAnalyzingGrid clone() {
        return new SelfAnalyzingGrid(grid.clone(), copy(possibilities));
    }

    private Set<Coordinates> emptyCells() {
        return possibilities.keySet();
    }

    public boolean isSolved() {
        return !hasEmptyCell() && isConsistent();
    }

    private void copySolutionFrom(Grid solved) {
        try {
            for (Coordinates coords : emptyCells()) {
                grid.set(coords, solved.get(coords).get());
            }
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("The given grid has empty cells");
        }
        if (!isSolved()) {
            throw new IllegalArgumentException("The given grid is not a solution of this grid.");
        }
        possibilities.clear();
    }

    private boolean hasEmptyCell() {
        return emptyCells().size() > 0;
    }

    private Coordinates nextEmptyCell() {
        // TODO
        return emptyCells().iterator().next();
    }

    private boolean isConsistent() {
        // TODO
        return false;
    }

    private void setCell(Coordinates coords, Digit d) {
        // TODO
    }

    private Digit candidateFor(Coordinates coords) {
        return possibilities.get(coords).iterator().next();
    }

    private boolean multipleCandidatesExistFor(Coordinates coords) {
        return possibilities.get(coords).size() > 1;
    }

    private void removeCandidate(Coordinates coords, Digit d) {
        // TODO
    }

    public boolean solve() {
        while (hasEmptyCell()) {
            if (!isConsistent()) {
                return false;
            }
            Coordinates coords = nextEmptyCell();
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
