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

    static {
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                cells.add(new Cell(i, j));
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
            checkRowIndex(index);
            return new Row(index);
        }

        public static Row of(Cell cell) {
            return new Row(cell.getRow());
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
            checkColumnIndex(index);
            return new Column(index);
        }

        public static Column of(Cell cell) {
            return new Column(cell.getColumn());
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
            corner = Cell.of(3 * (row / 3), 3 * (column / 3));
        }

        public static Box of(int row, int column) {
            checkIndices(row, column);
            return new Box(row, column);
        }

        public static Box of(Cell cell) {
            return new Box(cell.getRow(), cell.getColumn());
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
        return () -> new Iterator<Cell>() {
            private int i = 0; // Loop variable

            @Override
            public boolean hasNext() {
                return i < 81;
            }

            @Override
            public Cell next() {
                Cell cell = Cell.of(i / 9, i % 9);
                ++i;
                return cell;
            }
        };
    }

    public static Iterable<Row> allRows() {
        return () -> new Iterator<Row>() {
            private int i = 0; // Loop variable

            @Override
            public boolean hasNext() {
                return i < 9;
            }

            @Override
            public Row next() {
                return Row.of(i++);
            }
        };
    }

    public static Iterable<Column> allColumns() {
        return () -> new Iterator<Column>() {
            private int i = 0; // Loop variable

            @Override
            public boolean hasNext() {
                return i < 9;
            }

            @Override
            public Column next() {
                return Column.of(i++);
            }
        };
    }

    public static Iterable<Box> allBoxes() {
        return () -> new Iterator<Box>() {
            private int i = 0; // Loop variable

            @Override
            public boolean hasNext() {
                return i < 9;
            }

            @Override
            public Box next() {
                Box next = Box.of(3 * (i / 3), 3 * (i % 3));
                ++i;
                return next;
            }
        };
    }

}
