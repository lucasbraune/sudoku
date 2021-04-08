package sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import sudoku.Grid.Digit;
import sudoku.Grid.GridOverwriteException;
import sudoku.GridElements.Box;
import sudoku.GridElements.Cell;
import sudoku.GridElements.Column;
import sudoku.GridElements.Row;
import sudoku.GridParser.GridParserException;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void gridStartsEmpty() {
        Grid grid = new Grid();
        assertFalse(grid.digit(0, 0).isPresent());
    }

    @Test
    public void getAfterSet() {
        Grid grid = new Grid();
        grid.set(0, 0, Digit.ONE);
        assertEquals(Digit.ONE, grid.digit(0, 0).get());
    }

    @Test
    public void badGridAccessThrowsException() {
        Grid grid = new Grid();
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.digit(-1, 0); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.digit(9, 0); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.digit(0, -1); });
        assertThrows(IndexOutOfBoundsException.class, () -> { grid.digit(0, 9); });
    }

    @Test
    public void gridOverwriteThrowsException() {
        Grid grid = new Grid();
        assertThrows(GridOverwriteException.class, () -> {
            grid.set(0, 0, Digit.ONE);
            grid.set(0, 0, Digit.TWO);
        });
    }

    private static Grid sampleGrid() {
        Grid g = new Grid();
        // 003020600
        g.set(0, 2, Digit.THREE);
        g.set(0, 4, Digit.TWO);
        g.set(0, 6, Digit.SIX);
        // 900305001
        g.set(1, 0, Digit.NINE);
        g.set(1, 3, Digit.THREE);
        g.set(1, 5, Digit.FIVE);
        g.set(1, 8, Digit.ONE);
        // 001806400
        g.set(2, 2, Digit.ONE);
        g.set(2, 3, Digit.EIGHT);
        g.set(2, 5, Digit.SIX);
        g.set(2, 6, Digit.FOUR);
        // 008102900
        g.set(3, 2, Digit.EIGHT);
        g.set(3, 3, Digit.ONE);
        g.set(3, 5, Digit.TWO);
        g.set(3, 6, Digit.NINE);
        // 700000008
        g.set(4, 0, Digit.SEVEN);
        g.set(4, 8, Digit.EIGHT);
        // 006708200
        g.set(5, 2, Digit.SIX);
        g.set(5, 3, Digit.SEVEN);
        g.set(5, 5, Digit.EIGHT);
        g.set(5, 6, Digit.TWO);
        // 002609500
        g.set(6, 2, Digit.TWO);
        g.set(6, 3, Digit.SIX);
        g.set(6, 5, Digit.NINE);
        g.set(6, 6, Digit.FIVE);
        // 800203009
        g.set(7, 0, Digit.EIGHT);
        g.set(7, 3, Digit.TWO);
        g.set(7, 5, Digit.THREE);
        g.set(7, 8, Digit.NINE);
        // 005010300
        g.set(8, 2, Digit.FIVE);
        g.set(8, 4, Digit.ONE);
        g.set(8, 6, Digit.THREE);
        return g;
    } 

    private static final String sampleGridAsString = 
            "003020600\n" +
            "900305001\n" +
            "001806400\n" +
            "008102900\n" +
            "700000008\n" +
            "006708200\n" +
            "002609500\n" +
            "800203009\n" +
            "005010300\n";

    @Test
    public void gridToString() {
        assertEquals(sampleGridAsString, sampleGrid().toString());
    }

    @Test
    public void stringToGrid() throws GridParserException {
        assertEquals(sampleGrid(), GridParser.parse(sampleGridAsString));
    }

    private static void print(Map<Cell, Set<Digit>> candidates) {
        StringBuilder sb = new StringBuilder();
        List<Cell> emptyCells = new ArrayList<>(candidates.keySet());
        Collections.sort(emptyCells, (cell1, cell2) -> Integer.compare(
                candidates.get(cell1).size(), candidates.get(cell2).size()));
        for (Cell cell : emptyCells) {
            sb.append(cell + ": ");
            for (Digit d : candidates.get(cell)) {
                sb.append(d.intValue() + ", ");
            }
            sb.append("\n");
        }
        System.out.print(sb.toString());
    }

    @Test
    public void digitNotCandidateInOwnRowColumnOrBox() throws GridParserException {
        SelfAnalyzingGrid sag = new SelfAnalyzingGrid(sampleGrid());
        Grid grid = sag.getGrid();
        for (Cell emptyCell : grid.emptyCells()) {
            Set<Digit> candidates = sag.getCandidates().get(emptyCell);
            for (Cell cell : grid.nonEmptyCells(Row.of(emptyCell))) {
                Digit d = grid.digit(cell).get();
                assertFalse(candidates.contains(d), d.intValue() + " is a candidate for cell " + cell);
            }
            for (Cell cell : grid.nonEmptyCells(Column.of(emptyCell))) {
                Digit d = grid.digit(cell).get();
                assertFalse(candidates.contains(d), d.intValue() + " is a candidate for cell " + cell);
            }
            for (Cell cell : grid.nonEmptyCells(Box.of(emptyCell))) {
                Digit d = grid.digit(cell).get();
                assertFalse(candidates.contains(d), d.intValue() + " is a candidate for cell " + cell);
            }
        }
        // print(sag.getCandidates());
    }

    @Test
    public void detectCellWithUniqueCandidate() throws GridParserException {
        SelfAnalyzingGrid sag = new SelfAnalyzingGrid(sampleGrid());
        for (int i=0; i<49; i++) {
            Cell cell = sag.nextEmptyCell();
            assertFalse(sag.multipleCandidatesExistFor(cell));
            sag.setCell(cell, sag.candidateFor(cell));
        }
        // print(sag.getCandidates());
        assertTrue(sag.getGrid().isSolved());
    }

    @Test
    public void solveFirstPuzzle() throws GridParserException {
        Grid grid = GridParser.parse(sampleGridAsString);
        assertTrue(App.solve(grid));
    }

    private static String readLines(BufferedReader br, int n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("The number of lines given is negative (" + n + ")");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<n; i++) {
            sb.append(br.readLine());
        }
        return sb.toString();
    }

    private static List<Grid> readGrids() throws IOException, GridParserException {
        List<Grid> grids = new ArrayList<>();
        File file = new File("src/test/resources/puzzles.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i=0; i<50; i++) {
                br.readLine();
                Grid grid = GridParser.parse(readLines(br, 9), /* separator */ "");
                grids.add(grid);
            }
        }
        return grids;
    }

    @Test
    public void canReadGrids() throws IOException, GridParserException {
        List<Grid> grids = readGrids();
        assertTrue(grids.size() == 50);
    }

    @ParameterizedTest
    @MethodSource("readGrids")
    public void sampleGridIsConsistent(Grid grid) {
        assertTrue(grid.isConsistent());
    }

    @ParameterizedTest
    @MethodSource("readGrids")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void canSolveSampleGrid(Grid grid) {
        assertTrue(App.solve(grid));
    }

}
