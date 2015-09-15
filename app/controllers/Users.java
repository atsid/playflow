package controllers;

import java.util.List;

import dao.Dao;
import exceptions.ExceptionMapper;
import managers.UserManager;
import models.User;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * User CRUD services.
 * 
 * @author: bbenson
 */
public class Users extends Controller {

    private static final UserManager userManager = new UserManager(new Dao<User>(User.class));

    public static Result list() {
        try {
            List<User> users = userManager.list();
            return ok(Json.toJson(users));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result read(Long userId) {
        try {
            User user = userManager.read(userId);
            return ok(Json.toJson(user));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result create() {
        try {
            User userIn = Json.fromJson(request().body().asJson(), User.class);
            User user = userManager.create(userIn);
            return created(Json.toJson(user));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result update(Long userId) {
        try {

            User userIn = Json.fromJson(request().body().asJson(), User.class);
            User user = userManager.update(userIn);
            return ok(Json.toJson(user));

        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result delete(Long userId) {
        try {
            userManager.delete(userId);
            return ok();
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

}
