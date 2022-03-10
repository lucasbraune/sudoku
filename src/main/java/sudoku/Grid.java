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
import sudoku.GridElements.Row;
import sudoku.exceptions.GridOverwriteException;
import sudoku.exceptions.GridParserException;

/**
 * A 9 x 9 non-overwriteable Sudoku grid.
 * 
 * This class exposes methods for traversing over the empty or nonempty cells of
 * a grid or one of its rows, columns or boxes. It also exposes methods that
 * check whether a grid is consistent and whether it is solved.
 */
@EqualsAndHashCode
public class Grid {

    private final List<Optional<Digit>> data;

    /**
     * Constructs a grid all of whose cells are blank;
     */
    public Grid() {
        data = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            data.add(Optional.empty());
        }
    }

    /**
     * Constructs a deep copy of the specified grid.
     */
    public Grid(Grid grid) {
        data = new ArrayList<>(grid.data);
    }

    private static int index(Cell cell) {
        return cell.getRow() * 9 + cell.getColumn();
    }

    /**
     * Returns the digit at the specified cell, if that cell is not blank.
     */
    public final Optional<Digit> digitAt(Cell cell) {
        return data.get(index(cell));
    }

    /**
     * Sets the digit at the specified cell.
     * 
     * @throws GridOverwriteException if the cell is not blank
     */
    public void setDigit(Cell cell, Digit d) {
        if (digitAt(cell).isPresent()) {
            throw new GridOverwriteException(cell);
        }
        data.set(index(cell), Optional.of(d));
    }

    /**
     * Copies a source grid onto a target grid.
     * 
     * @throws GridOverwriteException if the target grid has a nonempty cell that is
     *                                either not set or set to a different value on
     *                                the source grid
     * @implNote this method is implemented in terms of the nonfinal method
     *           {@code setDigit(Cell, Digit)}
     */
    public static void copy(Grid source, Grid target) {
        for (Cell cell : GridElements.cells()) {
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

    private static char charFrom(Optional<Digit> d) {
        return d.isPresent() ? d.get().toChar() : '0';
    }

    /**
     * Returns the 81-character string obtained by filling the blank cells in this
     * grid with zeros, then concatenating rows.
     * 
     * Sample output:
     * 
     * "003020600" + "900305001" + "001806400" + "008102900" + "700000008" +
     * "006708200" + "002609500" + "800203009" + "005010300"
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        for (Cell cell : GridElements.cells()) {
            sb.append(charFrom(digitAt(cell)));
        }
        return sb.toString();
    }

    /**
     * @throws IllegalArgumentException if the given character is not a (radix 10)
     *                                  digit
     */
    private static Optional<Digit> optionalDigitFrom(char c) {
        return c == '0' ? Optional.empty() : Optional.of(Digit.fromChar(c));
    }

    /**
     * Returns a grid whose string representation (obtained from the
     * {@code Object.toString()} method) is the given string.
     * 
     * @throws GridParserException if the given string has length different from 81
     *                             or contains a character that is not a (radix 10)
     *                             digit
     */
    public static Grid fromString(String str) throws GridParserException {
        if (str.length() != 81) {
            throw new GridParserException("String of incorrect size: " + str.length());
        }
        Grid grid = new Grid();
        for (Cell cell : GridElements.cells()) {
            try {
                char c = str.charAt(index(cell));
                Optional<Digit> d = optionalDigitFrom(c);
                if (d.isPresent()) {
                    grid.setDigit(cell, d.get());
                }
            } catch (IllegalArgumentException e) {
                throw new GridParserException(e.getMessage());
            }
        }
        return grid;
    }

    /**
     * Returns the empty cells in the given range of cells (typically a row, a
     * column, a box, or the whole grid).
     */
    public Iterable<Cell> emptyCells(Iterable<Cell> cells) {
        return Util.filter(cells, cell -> !digitAt(cell).isPresent());
    }

    /**
     * Returns the nonempty cells in the given range of cells (typically a row, a
     * column, a box, or the whole grid).
     */
    public Iterable<Cell> nonEmptyCells(Iterable<Cell> cells) {
        return Util.filter(cells, cell -> digitAt(cell).isPresent());
    }

    /** Returns the empty cells in this grid */
    public Iterable<Cell> emptyCells() {
        return emptyCells(GridElements.cells());
    }

    /** Returns the nonempty cells in this grid */
    public Iterable<Cell> nonEmptyCells() {
        return nonEmptyCells(GridElements.cells());
    }

    public boolean hasEmptyCell() {
        return emptyCells().iterator().hasNext();
    }

    /**
     * Returns true if, and only if, the values in the nonempty cells within the
     * specified range (typically a row, column or box) are all distinct.
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
     * Returns true if, and only if, for each row, column or box, the values of the
     * nonempty cells in the row, column, and box are distinct. This method does not
     * determine whether this grid is solvable.
     */
    public final boolean isConsistent() {
        for (Row row : GridElements.rows()) {
            if (!isConsistent(row))
                return false;
        }
        for (Column column : GridElements.columns()) {
            if (!isConsistent(column))
                return false;
        }
        for (Box box : GridElements.boxes()) {
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
