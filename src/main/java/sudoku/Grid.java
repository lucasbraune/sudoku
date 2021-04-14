package sudoku;

import java.util.Optional;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Digit;

/**
 * A modifiable Sudoku grid.
 */
public class Grid extends UnmodifiableGrid {

    /** Creates new grid, all of whose cells are blank. */
    public Grid() {
        super();
    }

    /** Creates a deep copy of the specified grid. */
    public Grid(UnmodifiableGrid grid) {
        super(grid);
    }

    public static class GridOverwriteException extends RuntimeException {

        public GridOverwriteException(Cell cell) {
            super("Illegal attempt to overwrite cell " + cell);
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
    public void setDigit(Cell cell, Digit d) {
        if (digitAt(cell).isPresent()) {
            throw new GridOverwriteException(cell);
        }
        setOptionalDigit(cell, Optional.of(d));
    }

    /**
     * Sets the digit at the cell with the specified coordinates.
     * 
     * @throws GridOverwriteException    if the cell is not blank
     * @throws IndexOutOfBoundsException if either coordinate is {@code < 0} or {@code >= 9}
     * @implNote this method is implemented in terms of the nonfinal method {@code setDigit(Cell, Digit)}
     */
    public final void setDigit(int row, int column, Digit d) {
        setDigit(Cell.of(row, column), d);
    }

    /**
     * Copies a source UnmodifiableGrid onto a target Grid.
     * 
     * @throws GridOverwriteException if the target grid has a nonempty cell that is either not set
     *                                or set to a different value on the source grid
     * @implNote this method is implemented in terms of the nonfinal method {@code setDigit(Cell, Digit)}
     */
    public static void copy(UnmodifiableGrid source, Grid target) {
        for (Cell cell : GridElements.allCells()) {
            Optional<Digit> s = source.digitAt(cell);
            Optional<Digit> t = target.digitAt(cell);
            if (t.isPresent() && !s.equals(t)) {
                throw new GridOverwriteException(cell);
            }
            if (s.isPresent() && !t.isPresent()) {
                target.setDigit(cell, s.get());
            }
        }
    }

    /**
     * Returns an UnmodifiableGrid whose string representation (obtained from the
     * {@code Object.toString()} method) is the given string.
     * 
     * @throws GridParserException if the given string has length different from 81 or contains a
     *                             character that is not a (radix 10) digit
     */
    public static Grid fromString(String str) throws GridParserException {
        Grid result = new Grid();
        copy(UnmodifiableGrid.fromString(str), result);
        return result;
    }

}
