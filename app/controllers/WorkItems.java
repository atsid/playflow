package controllers;

import java.util.List;

import dao.Dao;
import exceptions.ExceptionMapper;
import managers.StationManager;
import managers.UserManager;
import managers.WorkItemManager;
import models.Station;
import models.User;
import models.WorkItem;
import models.WorkItemStateTransition;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * WorkItem  CRUD services.
 * 
 * @author: bbenson
 */
public class WorkItems extends Controller {

    private static final WorkItemManager workItemManager = new WorkItemManager(new Dao<WorkItem>(WorkItem.class),
            new Dao<WorkItemStateTransition>(WorkItemStateTransition.class), new UserManager(new Dao<User>(User.class)),
            new StationManager(new Dao<Station>(Station.class)));

    public static Result list() {
        try {
            Long projectId = toLong(play.data.Form.form().bindFromRequest().get("projectId"));
            Long assigneeId = toLong(play.data.Form.form().bindFromRequest().get("assigneeId"));
            Long stationId = toLong(play.data.Form.form().bindFromRequest().get("stationId"));
            String state = play.data.Form.form().bindFromRequest().get("state");
            List<WorkItem> workItems = (workItemManager.list(projectId, assigneeId, stationId, state));
            return ok(Json.toJson(workItems));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result read(Long workItemId) {
        try {
            WorkItem workItem = workItemManager.read(workItemId);
            return ok(Json.toJson(workItem));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result create() {
        try {
            WorkItem workItemIn = Json.fromJson(request().body().asJson(), WorkItem.class);
            WorkItem workItem = workItemManager.create(workItemIn);
            return created(Json.toJson(workItem));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result update(Long workItemId) {
        try {
            WorkItem workItemIn = Json.fromJson(request().body().asJson(), WorkItem.class);
            WorkItem workItem = workItemManager.update(workItemIn);
            return ok(Json.toJson(workItem));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result delete(Long workItemId) {
        try {
            workItemManager.delete(workItemId);
            return ok();
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result assign(Long workItemId) {
        try {
            Long assigneeId = toLong(play.data.Form.form().bindFromRequest().get("assigneeId"));
            Long transitionerId = toLong(play.data.Form.form().bindFromRequest().get("transitionerId"));
            String comment = play.data.Form.form().bindFromRequest().get("comment");
            WorkItem workItem = workItemManager.assign(workItemId, assigneeId, transitionerId, comment);
            return ok(Json.toJson(workItem));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result process(Long workItemId) {
        try {
            Long transitionerId = toLong(play.data.Form.form().bindFromRequest().get("transitionerId"));
            String comment = play.data.Form.form().bindFromRequest().get("comment");
            WorkItem workItem = workItemManager.process(workItemId, transitionerId, comment);
            return ok(Json.toJson(workItem));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result next(Long workItemId) {
        try {
            Long nextStationId = toLong(play.data.Form.form().bindFromRequest().get("nextStationId"));
            Long transitionerId = toLong(play.data.Form.form().bindFromRequest().get("transitionerId"));
            String comment = play.data.Form.form().bindFromRequest().get("comment");
            WorkItem workItem = workItemManager.next(workItemId, nextStationId, transitionerId, comment);
            return ok(Json.toJson(workItem));
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
