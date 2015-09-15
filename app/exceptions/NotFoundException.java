package exceptions;

/**
 * Thrown when a particular resource can't be found (ID doesn't exist).
 * 
 * @author bbenson
 *
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 8576054094339012029L;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
