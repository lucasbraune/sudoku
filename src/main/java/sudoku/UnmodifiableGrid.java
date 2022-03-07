package sudoku;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.EqualsAndHashCode;
import sudoku.GridElements.Box;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Column;
import sudoku.GridElements.Digit;
import sudoku.GridElements.Row;

/**
 * An unmodifiable 9 x 9 Sudoku grid.
 * 
 * Instances of this class can be created with the {@code fromString} factory method and with this
 * class' unique public constructor, which makes a deep copy of another {@code UnmodifiableGrid}.
 * Alternatively, instances can be created using the constructors and factory methods of the
 * subclasses {@code Grid} and {@code AnnotatedGrid}.
 * 
 * This class exposes methods for traversing over the empty or nonempty cells of a grid or one of
 * its rows, columns or boxes. It also exposes methods that check whether a grid is consistent and
 * whether it is solved.
 */
@EqualsAndHashCode
public class UnmodifiableGrid {

    private final List<Optional<Digit>> data;

    /**
     * Constructs a deep of the specified grid.
     */
    public UnmodifiableGrid(UnmodifiableGrid grid) {
        data = new ArrayList<>(grid.data);
    }

    /**
     * Constructs unmodifiable grid all of whose cells are blank;
     */
    protected UnmodifiableGrid() {
        data = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            data.add(Optional.empty());
        }
    }

    private static int arrayIndex(int row, int column) {
        return row * 9 + column;
    }

    private static int arrayIndex(Cell cell) {
        return arrayIndex(cell.getRow(), cell.getColumn());
    }

    /**
     * Returns the digit at the specified cell, if that cell is not blank.
     */
    public final Optional<Digit> digitAt(Cell cell) {
        return data.get(arrayIndex(cell));
    }

    /**
     * Returns the digit at the specified cell, if that cell is not blank.
     * 
     * @throws IndexOutOfBoundsException if either cell coordinate is {@code < 0} or {@code >= 9}
     */
    public final Optional<Digit> digitAt(int row, int column) {
        return data.get(arrayIndex(Cell.of(row, column)));
    }

    /**
     * Sets the optional digit at the cell with the specified coordinates.
     */
    protected final void setOptionalDigit(Cell cell, Optional<Digit> d) {
        data.set(arrayIndex(cell), d);
    }

    /**
     * Sets the optional digit at the cell with the specified coordinates.
     * 
     * @throws IndexOutOfBoundsException if either cell coordinate is {@code < 0} or {@code >= 9}
     */
    protected final void setOptionalDigit(int row, int column, Optional<Digit> d) {
        setOptionalDigit(Cell.of(row, column), d);
    }

    private static char charFrom(Optional<Digit> d) {
        return d.isPresent() ? d.get().toChar() : '0';
    }

    /**
     * Returns the 81-character string obtained by filling the blank cells in this grid with zeros,
     * then concatenating rows.
     * 
     * Sample output:
     * 
     * "003020600" + "900305001" + "001806400" + "008102900" + "700000008" + "006708200" +
     * "002609500" + "800203009" + "005010300"
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                sb.append(charFrom(digitAt(row, column)));
            }
        }
        return sb.toString();
    }

    public static final class GridParserException extends Exception {

        public GridParserException(String message) {
            super(message);
        }

        /**
         * See documentation of {@link java.io.Serializable}.
         */
        private static final long serialVersionUID = -5219699469780289049L;
    }

    /**
     * @throws IllegalArgumentException if the given character is not a (radix 10) digit
     */
    private static Optional<Digit> optionalDigitFrom(char c) {
        return c == '0' ? Optional.empty() : Optional.of(Digit.fromChar(c));
    }

    /**
     * Returns an UnmodifiableGrid whose string representation (obtained from the
     * {@code Object.toString()} method) is the given string.
     * 
     * @throws GridParserException if the given string has length different from 81 or contains a
     *                             character that is not a (radix 10) digit
     */
    public static UnmodifiableGrid fromString(String str) throws GridParserException {
        if (str.length() != 81) {
            throw new GridParserException("String of incorrect size: " + str.length());
        }
        UnmodifiableGrid grid = new UnmodifiableGrid();
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                int index = row * 9 + column;
                try {
                    Optional<Digit> d = optionalDigitFrom(str.charAt(index));
                    grid.setOptionalDigit(row, column, d);
                } catch (IllegalArgumentException e) {
                    throw new GridParserException(e.getMessage());
                }
            }
        }
        return grid;
    }

    /**
     * Returns the empty cells in the given range of cells (typically a row, a column, a box, or the
     * whole grid).
     */
    public Iterable<Cell> emptyCells(Iterable<Cell> cells) {
        return Util.filter(cells, cell -> !digitAt(cell).isPresent());
    }

    /**
     * Returns the nonempty cells in the given range of cells (typically a row, a column, a box, or
     * the whole grid).
     */
    public Iterable<Cell> nonEmptyCells(Iterable<Cell> cells) {
        return Util.filter(cells, cell -> digitAt(cell).isPresent());
    }

    /** Returns the empty cells in this grid */
    public Iterable<Cell> emptyCells() {
        return emptyCells(GridElements.allCells());
    }

    /** Returns the nonempty cells in this grid */
    public Iterable<Cell> nonEmptyCells() {
        return nonEmptyCells(GridElements.allCells());
    }

    public boolean hasEmptyCell() {
        return emptyCells().iterator().hasNext();
    }

    /**
     * Returns true if, and only if, the values in the nonempty cells within the specified range
     * (typically a row, column or box) are all distinct.
     */
    public final boolean isConsistent(Iterable<Cell> rowColumnOrBox) {
        Set<Digit> seen = EnumSet.noneOf(Digit.class);
        for (Cell cell : nonEmptyCells(rowColumnOrBox)) {
            Digit d = digitAt(cell).get();
            if (seen.contains(d)) {
                return false;
            }
            seen.add(d);
        }
        return true;
    }

    /**
     * Returns true if, and only if, for each row, column or box, the values of the nonempty cells
     * in the row, column, and box are distinct. This method does not determine whether this grid is
     * solvable.
     */
    public final boolean isConsistent() {
        for (Row row : GridElements.allRows()) {
            if (!isConsistent(row))
                return false;
        }
        for (Column column : GridElements.allColumns()) {
            if (!isConsistent(column))
                return false;
        }
        for (Box box : GridElements.allBoxes()) {
            if (!isConsistent(box))
                return false;
        }
        return true;
    }

    /**
     * Determines whether this Sudoku grid is solved.
     */
    public final boolean isSolved() {
        return !hasEmptyCell() && isConsistent();
    }

}
