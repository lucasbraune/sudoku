package sudoku;

import java.util.Iterator;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class GridElements {

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

        public static Cell from(int row, int column) {
            return new Cell(row, column);
        }

    }

    @EqualsAndHashCode
    public static class Row implements Iterable<Cell> {

        @Getter
        private final int index;

        private Row(int index) {
            checkRowIndex(index);
            this.index = index; 
        }

        public static Row from(int index) {
            return new Row(index);
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
                    return Cell.from(index, i++); 
                }
            };
        }

    }

    @EqualsAndHashCode
    public static class Column implements Iterable<Cell> {

        @Getter
        private final int index;

        private Column(int index) {
            checkColumnIndex(index);
            this.index = index; 
        }

        public static Column from(int index) {
            return new Column(index);
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
                    return Cell.from(i++, index); 
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
            corner = Cell.from(3 * (row / 3), 3 * (column / 3));
        }

        public static Box from(int row, int column) {
            checkIndices(row, column);
            return new Box(row, column);
        }

        public static Box from(Cell cell) {
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
                    Cell cell = Cell.from(corner.getRow() + i / 3, corner.getColumn() + i % 3);
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
                return Cell.from(i / 9, i % 9);
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
                return Row.from(i++);
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
                return Column.from(i++);
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
                return Box.from(3 * (i / 3), 3 * (i % 3));
            }  
        };
    }
    
}
