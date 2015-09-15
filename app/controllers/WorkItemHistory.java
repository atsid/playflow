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
 * Work Item History (audit history) CRUD services.
 * 
 * @author: bbenson
 */
public class WorkItemHistory extends Controller {

    private static final WorkItemManager workItemManager = new WorkItemManager(new Dao<WorkItem>(WorkItem.class),
            new Dao<WorkItemStateTransition>(WorkItemStateTransition.class), new UserManager(new Dao<User>(User.class)),
            new StationManager(new Dao<Station>(Station.class)));

    public static Result list(Long workItemId) {
        try {
            List<WorkItemStateTransition> history = (workItemManager.getHistory(workItemId));
            return ok(Json.toJson(history));
        } catch (RuntimeException ex) {
            return ExceptionMapper.toResult(ex);
        }
    }

}
