package sudoku;

import java.util.Optional;

/**
 * Hello world!
 *
 */
public class App 
{
    public static Optional<UnmodifiableGrid> solve(UnmodifiableGrid grid) {
        return SelfAnalyzingGrid.fromOrdinaryGrid(grid).solve();
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
