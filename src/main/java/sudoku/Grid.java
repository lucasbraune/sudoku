package sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Grid {

    public enum Digit {
        ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;
    }

    private final List<Optional<Digit>> data = new ArrayList<>();

    public Grid() {
        for (int i=0; i<9*9; i++) {
            data.add(Optional.empty());
        }
    }

    private static void checkBounds(int row, int col) throws IndexOutOfBoundsException {
        if (row < 0 || row >= 9 || col < 0 || col >= 9) {
            throw new IndexOutOfBoundsException("row = " + row + ", column = " + col);
        }
    }

    private static int arrayIndex(int row, int col) {
        return row * 9 + col;
    }
    
    public Optional<Digit> get(int row, int col) {
        checkBounds(row, col);
        return data.get(arrayIndex(row, col));
    }

    public void set(int row, int col, Digit d) {
        checkBounds(row, col);
        if (data.get(arrayIndex(row, col)).isPresent()) {
            throw new IllegalArgumentException("Cell already set.");
        }
        data.set(arrayIndex(row, col), Optional.of(d));
    }
    
}
