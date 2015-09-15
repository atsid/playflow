package exceptions;

/**
 * Thrown when a user shouldn't be accessing a particular resource.
 * 
 * @author bbenson
 *
 */
public class ForbiddenException extends RuntimeException {
    
    private static final long serialVersionUID = 8576054094339012029L;
    
    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
