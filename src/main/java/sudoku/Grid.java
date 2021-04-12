package sudoku;

import java.util.Optional;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Digit;

public class Grid extends UnmodifiableGrid {

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
     * @throws IndexOutOfBoundsException if either coordinate is < 0 or >= 9
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

    public static Grid copyOf(UnmodifiableGrid grid) {
        Grid result = new Grid();
        copy(grid, result);
        return result;
    }

    public static Grid fromString(String str) throws GridParserException {
        Grid result = new Grid();
        copy(UnmodifiableGrid.fromString(str), result);
        return result;
    }

}
