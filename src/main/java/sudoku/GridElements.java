package sudoku;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Contains static classes representing the elements of a Sudoku grid, namely digits, cells, rows,
 * columns, and boxes. The classes representing the last three implement {@code Iterable<Cell>}.
 * This class also exposes methods for iterating over the cells, rows, columns and boxes of a grid.
 */
@EqualsAndHashCode
public class GridElements {

    public static enum Digit {

        ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

        public int toInt() {
            return ordinal() + 1;
        }

        /**
         * @throws IllegalArgumentException if {@code d < 1} or {@code d > 9}
         */
        public static Digit fromInt(int d) {
            if (d < 1 || d > 9) {
                throw new IllegalArgumentException("Not a nonzero digit: " + d);
            }
            return values()[d - 1];
        }

        public char toChar() {
            return Character.forDigit(toInt(), 10);
        }

        /**
         * @throws IllegalArgumentException if {@code d < '1'} or {@code d > '9'}
         */
        public static Digit fromChar(char c) {
            if (c < '1' || c > '9') {
                throw new IllegalArgumentException("Not a nonzero digit: " + c);
            }
            return fromInt(Character.digit(c, 10));
        }
    }

    private static final List<Cell> cells = new ArrayList<>();
    private static final List<Row> rows = new ArrayList<>();
    private static final List<Column> columns = new ArrayList<>();
    private static final List<Box> boxes = new ArrayList<>();

    static {
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                cells.add(new Cell(i, j));
            }
        }
        for (int i=0; i<9; i++) {
            rows.add(new Row(i));
            columns.add(new Column(i));
        }
        for (int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
                boxes.add(new Box(3 * i, 3 * j));
            }
        }
    }

    public static void checkRowIndex(int row) {
        if (row < 0 || row >= 9) {
            throw new IndexOutOfBoundsException("Bad row index: " + row);
        }
    }

    public static void checkColumnIndex(int column) {
        if (column < 0 || column >= 9) {
            throw new IndexOutOfBoundsException("Bad column index: " + column);
        }
    }

    public static void checkIndices(int row, int column) {
        checkRowIndex(row);
        checkColumnIndex(column);
    }

    @EqualsAndHashCode
    public static class Cell {

        @Getter
        private final int row;

        @Getter
        private final int column;

        private Cell(int row, int column) {
            checkIndices(row, column);
            this.row = row;
            this.column = column;
        }

        public static Cell of(int row, int column) {
            checkIndices(row, column);
            return cells.get(index(row, column));
        }

        private static int index(int row, int column) {
            return 9 * row + column;
        }

        @Override
        public String toString() {
            return "(" + row + ", " + column + ")";
        }

    }

    @EqualsAndHashCode
    public static class Row implements Iterable<Cell> {

        @Getter
        private final int index;

        private Row(int index) {
            this.index = index;
        }

        public static Row of(int index) {
            return rows.get(index);
        }

        public static Row of(Cell cell) {
            return Row.of(cell.getRow());
        }

        @Override
        public Iterator<Cell> iterator() {
            return new Iterator<Cell>() {
                private int i = 0; // Loop variable

                @Override
                public boolean hasNext() {
                    return i < 9;
                }

                @Override
                public Cell next() {
                    return Cell.of(index, i++);
                }
            };
        }

    }

    @EqualsAndHashCode
    public static class Column implements Iterable<Cell> {

        @Getter
        private final int index;

        private Column(int index) {
            this.index = index;
        }

        public static Column of(int index) {
            return columns.get(index);
        }

        public static Column of(Cell cell) {
            return Column.of(cell.getColumn());
        }

        @Override
        public Iterator<Cell> iterator() {
            return new Iterator<Cell>() {
                private int i = 0; // Loop variable

                @Override
                public boolean hasNext() {
                    return i < 9;
                }

                @Override
                public Cell next() {
                    return Cell.of(i++, index);
                }
            };
        }

    }

    /** A 3 x 3 box in a Sudoku grid */
    @EqualsAndHashCode
    public static class Box implements Iterable<Cell> {

        @Getter
        // The cell in this box that has the smallest row and column indices
        private final Cell corner;

        private Box(int row, int column) {
            corner = Cell.of(row - row % 3, column - column % 3);
        }

        public static Box of(int row, int column) {
            checkIndices(row, column);
            return boxes.get(3 * (row / 3) + column / 3);
        }

        public static Box of(Cell cell) {
            return Box.of(cell.getRow(), cell.getColumn());
        }

        @Override
        public Iterator<Cell> iterator() {
            return new Iterator<Cell>() {
                private int i = 0; // Loop variable

                public boolean hasNext() {
                    return i < 9;
                }

                public Cell next() {
                    Cell cell = Cell.of(corner.getRow() + i / 3, corner.getColumn() + i % 3);
                    ++i;
                    return cell;
                }
            };
        }

    }

    public static Iterable<Cell> allCells() {
        return cells;
    }

    public static Iterable<Row> allRows() {
        return rows;
    }

    public static Iterable<Column> allColumns() {
        return columns;
    }

    public static Iterable<Box> allBoxes() {
        return boxes;
    }

}
