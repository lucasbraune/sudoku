package sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import sudoku.UnmodifiableGrid.GridParserException;

public class App 
{
    private static String readLines(BufferedReader br, int n) throws IOException {
        if (n < 0) throw new IllegalArgumentException("The number of lines given is negative (" + n + ")");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<n; i++) {
            sb.append(br.readLine());
        }
        return sb.toString();
    }

    public static List<UnmodifiableGrid> readGrids() throws IOException, GridParserException {
        List<UnmodifiableGrid> grids = new ArrayList<>();
        File file = new File("src/test/resources/puzzles.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (int i=0; i<50; i++) {
                br.readLine();
                UnmodifiableGrid grid = UnmodifiableGrid.fromString(readLines(br, 9));
                grids.add(grid);
            }
        }
        return grids;
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
