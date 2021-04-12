package sudoku;

import java.util.Iterator;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class GridElements {

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
            return new Cell(row, column);
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
