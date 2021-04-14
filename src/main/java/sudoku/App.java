package sudoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import sudoku.GridElements.Cell;
import sudoku.UnmodifiableGrid.GridParserException;

public class App {

    public static final File inputFile = new File("src/main/input/puzzles");
    public static final File outputFile = new File("target/output/solutions");

    public static void main(String[] args) throws IOException {
        outputFile.getParentFile().mkdirs();
        try (BufferedReader input = new BufferedReader(new FileReader(inputFile));
                BufferedWriter output = new BufferedWriter(new FileWriter(outputFile))) {
            readGridsAndWriteSolutions(input, output);
        }
    }

    private static void readGridsAndWriteSolutions(BufferedReader input, Writer output)
            throws IOException {
        int inputCounter = 0;
        int projectEulerSum = 0;
        for (Optional<UnmodifiableGrid> grid = readGrid(input); grid.isPresent(); grid =
                readGrid(input)) {
            ++inputCounter;
            Optional<UnmodifiableGrid> solved = Solver.solve(grid.get());
            if (solved.isPresent()) {
                String solvedAsString = addLineFeeds(solved.get().toString());
                output.write("Solution to input " + inputCounter + ":\n" + solvedAsString + "\n");
                projectEulerSum += threeDigitNumber(solved.get());
            } else {
                output.write("Input " + inputCounter + " has no solution.\n\n");
            }
            output.flush();
        }
        output.write("Project Euler 96 sum: " + projectEulerSum);
        output.flush();
    }

    /**
     * Reads the next grid in the specified character-input stream.
     * 
     * This method scans the specified character-input stream for nine consecutive lines, each of
     * which represents a row. A line represents a row if it consists of nine digits. If this method
     * finds nine consecutive lines representing rows before reaching the end of the input stream,
     * it converts them into an {@code UnmodifiableGrid} using the static factory
     * {@code UnmodifiableGrid.fromString(String)}, and returns this grid to the caller. Otherwise,
     * it returns the empty optional.
     * 
     * The caller is responsible for closing the character-input stream given to this method.
     * 
     * @throws IOException
     */
    public static Optional<UnmodifiableGrid> readGrid(BufferedReader input) throws IOException {
        Stack<String> rows = new Stack<String>();
        while (rows.size() < 9) {
            String line = input.readLine();
            if (line == null) {
                return Optional.<UnmodifiableGrid>empty();
            }
            if (representsRow(line)) {
                rows.push(line);
            } else {
                rows.clear();
            }
        }
        String gridAsString = rows.stream().collect(Collectors.joining());
        try {
            return Optional.of(UnmodifiableGrid.fromString(gridAsString));
        } catch (GridParserException gpe) {
            // Doesn't happen
            throw new AssertionError();
        }
    }

    private static boolean representsRow(String str) {
        if (str.length() != 9) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a line feed character ("\n") after every 9th character of the given string.
     */
    private static String addLineFeeds(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length() / 9; i++) {
            String substr = str.substring(9 * i, 9 * (i + 1));
            sb.append(substr + "\n");
        }
        return sb.toString();
    }

    /**
     * Returns the integer obtained by concatenating the first three digits of the first row of the specified grid.
     * 
     * @throws IllegalArgumentException if one of the first three digits of the first row of the specified grid is blank
     */
    public static int threeDigitNumber(UnmodifiableGrid solved) {
        List<Integer> digits = new ArrayList<>();
        for (int j=0; j<3; j++) {
            try {
                digits.add(solved.digitAt(0, j).get().toInt());
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException("Grid has blank at " + Cell.of(0, j));
            }
        }
        return digits.get(0) * 100 + digits.get(1) * 10 + digits.get(2);
    }

}