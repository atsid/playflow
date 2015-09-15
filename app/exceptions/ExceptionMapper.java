package exceptions;

import javax.persistence.PersistenceException;

import play.Logger;
import play.mvc.Result;
import play.mvc.Results;

/**
 * Map exceptions so they can be returned in Play result.
 * 
 * @author bbenson
 *
 */
public class ExceptionMapper {

    public static Result toResult(RuntimeException re) {
        Logger.error("ERROR: ", re);
        String msg = re.getMessage();
        if (msg == null) {
            // api doesn't allow null strings
            msg = "";
        }
        re.printStackTrace();
        if (re instanceof NotFoundException) {
            return Results.notFound(msg);
        } else if (re instanceof ForbiddenException) {
            return Results.forbidden(msg);
        } else if (re instanceof IllegalStateException || re instanceof IllegalArgumentException
                || re instanceof UnsupportedOperationException || re instanceof PersistenceException) {
            return Results.badRequest(re.getMessage());
        }
        return Results.internalServerError(msg);
    }
}
