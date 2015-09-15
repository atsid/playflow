package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.api;
import views.html.index;

/**
 * Initial landing page and API
 * 
 * @author bryan
 *
 */
public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result api() {
        return ok(api.render());
    }

}
