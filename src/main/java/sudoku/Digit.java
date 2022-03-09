package sudoku;

public enum Digit {

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
