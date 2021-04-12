package sudoku;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import lombok.EqualsAndHashCode;
import sudoku.GridElements.Box;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Column;
import sudoku.GridElements.Digit;
import sudoku.GridElements.Row;

/**
 * An unmodifiable 9 x 9 sudoku grid.
 */
@EqualsAndHashCode
public class UnmodifiableGrid {

    private final List<Optional<Digit>> data;

    protected UnmodifiableGrid() {
        data = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            data.add(Optional.empty());
        }
    }

    private UnmodifiableGrid(List<Optional<Digit>> data) {
        this.data = data;
    }

    public static UnmodifiableGrid copyOf(UnmodifiableGrid grid) {
        return new UnmodifiableGrid(new ArrayList<>(grid.data));
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
     * @throws IndexOutOfBoundsException if either cell coordinate is < 0 or >= 9
     */
    public final Optional<Digit> digitAt(int row, int column) {
        return data.get(arrayIndex(Cell.of(row, column)));
    }

    /**
     * Sets the digit at the cell with the specified coordinates.
     */
    protected final void setOptionalDigit(Cell cell, Optional<Digit> d) {
        data.set(arrayIndex(cell), d);
    }

    /**
     * Sets the digit at the cell with the specified coordinates.
     * 
     * @throws IndexOutOfBoundsException if either cell coordinate is < 0 or >= 9
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

    private static Optional<Digit> optionalDigitFrom(char c) {
        return c == '0' ? Optional.empty() : Optional.of(Digit.fromChar(c));
    }

    public static UnmodifiableGrid fromString(String str) throws GridParserException {
        if (str.length() != 81) {
            throw new GridParserException("String of incorrect size: " + str.length());
        }
        UnmodifiableGrid grid = new UnmodifiableGrid();
        for (int row=0; row<9; row++) {
            for (int column=0; column<9; column++) {
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

    private Predicate<Cell> isEmpty() {
        return cell -> !digitAt(cell).isPresent();
    }

    public final Iterable<Cell> emptyCells(Iterable<Cell> cells) {
        return Util.filter(cells, isEmpty());
    }

    public final Iterable<Cell> nonEmptyCells(Iterable<Cell> cells) {
        return Util.filter(cells, isEmpty().negate());
    }

    public final Iterable<Cell> emptyCells() {
        return emptyCells(GridElements.allCells());
    }

    public final Iterable<Cell> nonEmptyCells() {
        return nonEmptyCells(GridElements.allCells());
    }

    public final boolean hasEmptyCell() {
        return emptyCells().iterator().hasNext();
    }

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

    public final boolean isSolved() {
        return !hasEmptyCell() && isConsistent();
    }

}
