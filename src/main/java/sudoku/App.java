package sudoku;

/**
 * Hello world!
 *
 */
public class App 
{
    public static boolean solve(UnmodifiableGrid grid) {
        return new SelfAnalyzingGrid(grid).solve();
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
