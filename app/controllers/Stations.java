package controllers;

import java.util.List;

import dao.Dao;
import exceptions.ExceptionMapper;
import managers.StationManager;
import models.Station;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Station CRUD services.
 * 
 * @author: bbenson
 */
public class Stations extends Controller {

    private static final StationManager stationManager = new StationManager(new Dao<Station>(Station.class));

    public static Result list() {
        try {
            Long factoryId = toLong(play.data.Form.form().bindFromRequest().get("factoryId"));
            Long workerId = toLong(play.data.Form.form().bindFromRequest().get("workerId"));
            Long projectId = toLong(play.data.Form.form().bindFromRequest().get("projectId"));
            List<Station> stations = (stationManager.list(factoryId, workerId, projectId));
            return ok(Json.toJson(stations));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result read(Long stationId) {
        try {
            Station station = stationManager.read(stationId);
            return ok(Json.toJson(station));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result create() {
        try {
            Station stationIn = Json.fromJson(request().body().asJson(), Station.class);
            Station station = stationManager.create(stationIn);
            return created(Json.toJson(station));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result update(Long stationId) {
        try {
            Station stationIn = Json.fromJson(request().body().asJson(), Station.class);
            Station station = stationManager.update(stationIn);
            return ok(Json.toJson(station));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result delete(Long stationId) {
        try {
            stationManager.delete(stationId);
            return ok();
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    private static Long toLong(String parm) {
        Long converted = null;
        if (parm != null) {
            converted = Long.parseLong(parm);
        }
        return converted;
    }

}
