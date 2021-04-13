package sudoku;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import lombok.Getter;
import sudoku.UnmodifiableGrid.GridParserException;

@TestInstance(Lifecycle.PER_CLASS)
public class TestsWithAllPuzzles {

    @Getter
    private List<UnmodifiableGrid> grids = null;

    @BeforeAll
    public void beforeAll() throws IOException, GridParserException {
        grids = App.readGrids();
    }

    @Test
    public void numberOfGrids() throws IOException, GridParserException {
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
