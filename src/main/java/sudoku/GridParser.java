package sudoku;

import java.util.Optional;

import sudoku.Grid.Digit;

public class GridParser {

    public static class GridParserException extends Exception {
        
        public GridParserException(String message) {
            super(message);
        }

        /**
         * See documentation of {@link java.io.Serializable}.
         */
        private static final long serialVersionUID = -5219699469780289049L;
    }

    private static Optional<Digit> optionalDigitFrom(char d) throws GridParserException {
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
            throw new GridParserException("Not a digit: " + d);
        }
    }

    public static Grid parse(String str) throws GridParserException {
        if (str.length() != 90) { // 9 rows * (9 digits + 1 new line character)
            throw new GridParserException("String of incorrect size: " + str.length());
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
