package sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
         * Constructs a coordinates on 9 x 9 grid from a specified pair of integers.
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

    private static char charFrom(Optional<Digit> d) {
        return d.isPresent() ? Integer.toString(d.get().intValue()).charAt(0) : '0';
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row=0; row < 9; row++) {
            for (int column=0; column<9; column++) {
                sb.append(charFrom(this.get(row, column)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static class GridParsingException extends Exception {
        
        public GridParsingException(String message) {
            super(message);
        }

        /**
         * See documentation of {@link java.io.Serializable}.
         */
        private static final long serialVersionUID = -5219699469780289049L;
    }

    private static Optional<Digit> optionalDigitFrom(char d) throws GridParsingException {
        switch (d) {
        case '0':
            return Optional.empty();
        case '1':
            return Optional.of(Digit.ONE);
        case '2':
            return Optional.of(Digit.TWO);
        case '3':
            return Optional.of(Digit.THREE);
        case '4':
            return Optional.of(Digit.FOUR);
        case '5':
            return Optional.of(Digit.FIVE);
        case '6':
            return Optional.of(Digit.SIX);
        case '7':
            return Optional.of(Digit.SEVEN);
        case '8':
            return Optional.of(Digit.EIGHT);
        case '9':
            return Optional.of(Digit.NINE);
        default:
            throw new GridParsingException("Not a digit: " + d);
        }
    }

    public static Grid from(String str) throws GridParsingException {
        if (str.length() != 90) { // 9 rows * (9 digits + 1 new line character)
            throw new GridParsingException("String of incorrect size: " + str.length());
        }
        Grid grid = new Grid();
        for (int row=0; row<9; row++) {
            for (int column=0; column<9; column++) {
                int index = row * 10 + column;
                Optional<Digit> d = optionalDigitFrom(str.charAt(index));
                if (d.isPresent()) {
                    grid.set(row, column, d.get());
                }
            }
        }
        return grid;
    }

}
