package sudoku.exceptions;

public final class GridParserException extends Exception {

    public GridParserException(String message) {
        super(message);
    }

    /**
     * See documentation of {@link java.io.Serializable}.
     */
    private static final long serialVersionUID = -5219699469780289049L;
}
