package sudoku;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.Iterator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A 9 x 9 sudoku grid.
 */
@EqualsAndHashCode
public class Grid {

    /**
     * Coordinates on a 9 x 9 grid.
     */
    @EqualsAndHashCode
    @ToString
    public static class Coordinates {

        @Getter
        private final int row;

        @Getter
        private final int column;

        /**
         * Constructs coordinates on 9 x 9 grid from a specified pair of integers.
         * 
         * @param row    The first coordinate
         * @param column The second coordinate
         * @throws IndexOutOfBoundsException if either coordinate is < 0 or >= 9
         */
        public Coordinates(int row, int column) {
            if (row < 0 || row >= 9 || column < 0 || column >= 9) {
                throw new IndexOutOfBoundsException("row = " + row + ", column = " + column);
            }
            this.row = row;
            this.column = column;
        }

    }

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
        for (int i = 0; i < 9 * 9; i++) {
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

    private static int arrayIndex(Coordinates coords) {
        return coords.getRow() * 9 + coords.getColumn();
    }

    /**
     * Returns the digit at the cell with the specified coordinates, if that cell is
     * not blank.
     */
    public Optional<Digit> get(Coordinates coords) {
        return data.get(arrayIndex(coords));
    }

    /**
     * Returns the digit at the cell with the specified coordinates, if that cell is
     * not blank.
     * 
     * @throws IndexOutOfBoundsException if either coordinate is < 0 or >= 9
     */
    public Optional<Digit> get(int row, int column) {
        return get(new Coordinates(row, column));
    }

    public static class GridOverwriteException extends RuntimeException {
        @Getter
        private final Grid grid;

        @Getter
        private final Coordinates coords;

        public GridOverwriteException(Grid grid, Coordinates coords) {
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
    public void set(Coordinates coords, Digit d) {
        if (get(coords).isPresent()) {
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
        set(new Coordinates(row, column), d);
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
                sb.append(charFrom(this.get(row, column)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static Row row(int i) {
        return new Row(i);
    }

    public static class Row implements Iterable<Coordinates> {

        @Getter
        private final int rowIndex; // row index

        private Row(int i) {
            if (i < 0 || i >= 9) {
                throw new IndexOutOfBoundsException("Bad row index: " + i);
            }
            this.rowIndex = i;
        }

        private class RowIterator implements Iterator<Coordinates> {

            private int j = 0; // the loop variable

            public boolean hasNext() {
                return j < 9;
            }

            public Coordinates next() {
                try {
                    return new Coordinates(rowIndex, j++);
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }

        }

        @Override
        public Iterator<Coordinates> iterator() {
            return new RowIterator();
        }
    }

    public static Column column(int j) {
        return new Column(j);
    }

    public static class Column implements Iterable<Coordinates> {

        @Getter
        private final int columnIndex; // column index

        private Column(int j) {
            if (j < 0 || j >= 9) {
                throw new IndexOutOfBoundsException("Bad row index: " + j);
            }
            this.columnIndex = j;
        }

        private class ColumnIterator implements Iterator<Coordinates> {

            private int i = 0; // the loop variable

            public boolean hasNext() {
                return i < 9;
            }

            public Coordinates next() {
                try {
                    return new Coordinates(i++, columnIndex);
                } catch (IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }

        }

        @Override
        public Iterator<Coordinates> iterator() {
            return new ColumnIterator();
        }

    }

    /**
     * Returns the unique box containing the specified coordinates.
     */
    public static Box box(Coordinates coords) {
        return new Box(coords);
    }

    /**
     * Returns the unique box containing the specified coordinates.
     * @throws IndexOutOfBoundsException if either row or column is < 0 or >= 3
     */
    public static Box box(int row, int column) {
        return box(new Coordinates(row, column));
    }

    /**
     * One of the nine 3 x 3 boxes in a sudoku grid.
     */
    public static class Box implements Iterable<Coordinates> {
        @Getter
        private final int smallestRowIndex;
        @Getter
        private final int smallestColumnIndex;

        /**
         * Constructs the unique box containing the specified coordinates.
         */
        private Box(Coordinates coords) {
            smallestRowIndex = 3 * (coords.getRow() / 3);
            smallestColumnIndex = 3 * (coords.getColumn() / 3);
        }

        private class BoxIterator implements java.util.Iterator<Coordinates> {

            private int k = 0; // The loop variable

            public boolean hasNext() {
                return k < 9;
            }

            public Coordinates next() {
                Coordinates coords = new Coordinates(smallestRowIndex + k / 3, smallestColumnIndex + k % 3);
                ++k;
                return coords;
            }

        }

        @Override
        public Iterator<Coordinates> iterator() {
            return new BoxIterator();
        }

    }

    public boolean isConsistent(Iterable<Coordinates> rowColumnOrBox) {
        Set<Digit> seen = EnumSet.noneOf(Digit.class);
        for (Coordinates coords : rowColumnOrBox) {
            Optional<Digit> digit = this.get(coords);
            if (digit.isPresent()) {
                if (seen.contains(digit.get())) {
                    return false;
                }
                seen.add(digit.get());
            }
        }
        return true;
    }

    public boolean isConsistent() {
        for (int i=0; i<9; i++) {
            if (!isConsistent(row(i))) return false;
            if (!isConsistent(column(i))) return false;
        }
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                if (!isConsistent(box(3 * i, 3 * j))) return false;
            }
        }
        return true;
    }

    public boolean hasEmptyCell() {
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                if (!this.get(i, j).isPresent()) return true;
            }
        }
        return false;
    }

    public boolean isSolved() {
        return !hasEmptyCell() && isConsistent();
    }

}