package controllers;

import java.util.List;

import dao.Dao;
import exceptions.ExceptionMapper;
import managers.FactoryManager;
import managers.StationManager;
import models.Factory;
import models.Station;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Factory CRUD services.
 * 
 * @author: bbenson
 */

public class Factories extends Controller {

    private static final FactoryManager factoryManager = new FactoryManager(new Dao<Factory>(Factory.class),
            new StationManager(new Dao<Station>(Station.class)));

    public static Result list() {
        try {
            List<Factory> factories = factoryManager.list();
            return ok(Json.toJson(factories));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result read(Long factoryId) {
        try {
            Factory factory = factoryManager.read(factoryId);
            return ok(Json.toJson(factory));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result create() {
        try {
            Factory factoryIn = Json.fromJson(request().body().asJson(), Factory.class);
            Factory factory = factoryManager.create(factoryIn);
            return created(Json.toJson(factory));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result update(Long factoryId) {
        try {

            Factory factoryIn = Json.fromJson(request().body().asJson(), Factory.class);
            Factory factory = factoryManager.update(factoryIn);
            return ok(Json.toJson(factory));

        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result delete(Long factoryId) {
        try {
            factoryManager.delete(factoryId);
            return ok();
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

}
