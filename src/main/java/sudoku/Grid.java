package sudoku;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import sudoku.GridElements.Box;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Column;
import sudoku.GridElements.Row;
import util.Util;

/**
 * A 9 x 9 sudoku grid.
 */
@EqualsAndHashCode
public class Grid {

    public static enum Digit {

        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9);

        private final int intValue;

        private Digit(int d) {
            intValue = d;
        }

        public int intValue() {
            return intValue;
        }
        
    }

    private final List<Optional<Digit>> data;

    public Grid() {
        data = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            data.add(Optional.empty());
        }
    }

    private Grid(List<Optional<Digit>> data) {
        this.data = data;
    }

    /**
     * Returns a deep copy of this grid.
     */
    @Override
    public Grid clone() {
        // This copy is deep, because instances of Optional<Digit> are immutable.
        return new Grid(new ArrayList<>(data));
    }

    private static int arrayIndex(int row, int column) {
        return row * 9 + column;
    }

    private static int arrayIndex(Cell cell) {
        return arrayIndex(cell.getRow(), cell.getColumn());
    }

    /**
     * Returns the digit at the specified cell, if that cell is not blank.
     * @throws IndexOutOfBoundsException if either cell coordinate is < 0 or >= 9
     */
    public Optional<Digit> digit(int row, int column) {
        GridElements.checkIndices(row, column);
        return data.get(arrayIndex(row, column));
    }

    /**
     * Returns the digit at the specified cell, if that cell is not blank.
     */
    public Optional<Digit> digit(Cell cell) {
        return data.get(arrayIndex(cell));
    }

    public static class GridOverwriteException extends RuntimeException {
        @Getter
        private final Grid grid;

        @Getter
        private final Cell coords;

        public GridOverwriteException(Grid grid, Cell coords) {
            super("Illegal attempt to overwrite grid.");
            this.coords = coords;
            this.grid = grid;
        }

        /**
         * See documentation of {@link java.io.Serializable}.
         */
        private static final long serialVersionUID = 1L;
    }

    /**
     * Sets the digit at the cell with the specified coordinates.
     * 
     * @throws GridOverwriteException if the cell is not blank
     */
    public void set(Cell coords, Digit d) {
        if (digit(coords).isPresent()) {
            throw new GridOverwriteException(this, coords);
        }
        data.set(arrayIndex(coords), Optional.of(d));
    }

    /**
     * Sets the digit at the cell with the specified coordinates.
     * 
     * @throws GridOverwriteException    if the cell is not blank
     * @throws IndexOutOfBoundsException if either coordinate is < 0 or >= 9
     */
    public void set(int row, int column, Digit d) {
        set(Cell.from(row, column), d);
    }

    public static char charFrom(Optional<Digit> d) {
        return d.isPresent() ? Integer.toString(d.get().intValue()).charAt(0) : '0';
    }

    /**
     * Sample output:
     * 
     * "003020600\n" + "900305001\n" + "001806400\n" + "008102900\n" + "700000008\n"
     * + "006708200\n" + "002609500\n" + "800203009\n" + "005010300\n"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                sb.append(charFrom(digit(row, column)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean isConsistent(Iterable<Cell> rowColumnOrBox) {
        Set<Digit> seen = EnumSet.noneOf(Digit.class);
        for (Cell cell : rowColumnOrBox) {
            Optional<Digit> d = digit(cell);
            if (d.isPresent()) {
                if (seen.contains(d.get())) {
                    return false;
                }
                seen.add(d.get());
            }
        }
        return true;
    }

    public boolean isConsistent() {
        for (Row row : GridElements.allRows()) {
            if (!isConsistent(row)) return false;
        }
        for (Column column : GridElements.allColumns()) {
            if (!isConsistent(column)) return false;
        }
        for (Box box : GridElements.allBoxes()) {
            if (!isConsistent(box)) return false;
        }
        return true;
    }

    public boolean hasEmptyCell() {
        return emptyCells().iterator().hasNext();
    }

    public Iterable<Cell> emptyCells() {
        return Util.filter(GridElements.allCells(), cell -> !digit(cell).isPresent());
    }

    public boolean isSolved() {
        return !hasEmptyCell() && isConsistent();
    }

}