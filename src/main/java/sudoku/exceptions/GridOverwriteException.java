package sudoku.exceptions;

import sudoku.GridElements.Cell;

public final class GridOverwriteException extends RuntimeException {

    public GridOverwriteException(Cell cell) {
        super("Illegal attempt to overwrite cell " + cell);
    }

    /**
     * See documentation of {@link java.io.Serializable}.
     */
    private static final long serialVersionUID = 1L;
}