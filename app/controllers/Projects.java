package controllers;

import java.util.List;

import dao.Dao;
import exceptions.ExceptionMapper;
import managers.FactoryManager;
import managers.ProjectManager;
import managers.StationManager;
import managers.UserManager;
import managers.WorkItemManager;
import models.Factory;
import models.Project;
import models.Station;
import models.User;
import models.WorkItem;
import models.WorkItemStateTransition;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Project CRUD services
 * 
 * @author: bbenson
 */
public class Projects extends Controller {

    private static final ProjectManager projectManager = new ProjectManager(new Dao<Project>(Project.class),
            new FactoryManager(new Dao<Factory>(Factory.class), new StationManager(new Dao<Station>(Station.class))),
            new WorkItemManager(new Dao<WorkItem>(WorkItem.class),
                    new Dao<WorkItemStateTransition>(WorkItemStateTransition.class),
                    new UserManager(new Dao<User>(User.class)), new StationManager(new Dao<Station>(Station.class))));

    public static Result list() {
        try {
            String state = play.data.Form.form().bindFromRequest().get("state");
            List<Project> projects = projectManager.list(state);
            return ok(Json.toJson(projects));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result read(Long projectId) {
        try {
            Project project = projectManager.read(projectId);
            return ok(Json.toJson(project));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result create() {
        try {
            Project projectIn = Json.fromJson(request().body().asJson(), Project.class);
            Project project = projectManager.create(projectIn);
            return created(Json.toJson(project));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result update(Long projectId) {
        try {

            Project projectIn = Json.fromJson(request().body().asJson(), Project.class);
            Project project = projectManager.update(projectIn);
            return ok(Json.toJson(project));

        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result delete(Long projectId) {
        try {
            projectManager.delete(projectId);
            return ok();
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

    public static Result assign(Long projectId) {
        try {
            Long factoryId = toLong(play.data.Form.form().bindFromRequest().get("factoryId"));
            Long transitionerId = toLong(play.data.Form.form().bindFromRequest().get("transitionerId"));
            String comment = play.data.Form.form().bindFromRequest().get("comment");
            Project project = projectManager.assign(projectId, factoryId, transitionerId, comment);
            return ok(Json.toJson(project));
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
