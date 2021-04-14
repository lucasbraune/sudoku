package sudoku;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import lombok.Getter;

@TestInstance(Lifecycle.PER_CLASS)
public class TestsWithAllPuzzles {

    @Getter
    private List<UnmodifiableGrid> grids = null;

    private static List<UnmodifiableGrid> readGridsFromInput() throws IOException {
        List<UnmodifiableGrid> grids = new ArrayList<>();
        try (BufferedReader input = new BufferedReader(new FileReader(App.inputFile))) {
            for (Optional<UnmodifiableGrid> optGrid = App.readGrid(input); optGrid
                    .isPresent(); optGrid = App.readGrid(input)) {
                grids.add(optGrid.get());
            }
        }
        return grids;
    }

    @BeforeAll
    public void beforeAll() throws IOException {
        grids = readGridsFromInput();
    }

    @Test
    public void numberOfGrids() {
        assertTrue(grids.size() == 50);
    }

    @ParameterizedTest
    @MethodSource("getGrids")
    public void isConsistent(UnmodifiableGrid grid) {
        assertTrue(grid.isConsistent());
    }

    @ParameterizedTest
    @MethodSource("getGrids")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    public void canSolve(UnmodifiableGrid grid) {
        assertTrue(Solver.solve(grid).isPresent());
    }
}
