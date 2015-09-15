package managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.Dao;
import models.Station;
import models.User;
import models.WorkItem;
import models.WorkItemStateTransition;
import models.WorkItemStateType;

/**
 * CRUD operations for WorkItems
 * 
 * @author bbenson
 * 
 */
public class WorkItemManager extends CrudManager<WorkItem> {

    private UserManager userManager;
    private StationManager stationManager;
    private final Dao<WorkItem> dao;
    private final Dao<WorkItemStateTransition> historyDao;

    /**
     * Constructor 
     * 
     * @param dao
     * @param historyDao
     * @param userManager
     * @param stationManager
     */
    public WorkItemManager(Dao<WorkItem> dao, Dao<WorkItemStateTransition> historyDao, UserManager userManager,
            StationManager stationManager) {
        super(WorkItem.class, dao);
        this.dao = dao;
        this.historyDao = historyDao;
        this.userManager = userManager;
        this.stationManager = stationManager;
    }

    /**
     * Lists all workItems.  Filters according to workItem's assignee, station, or state.
     * 
     * @param projectId
     * @param assigneeId
     * @param stationId
     * @param stateName
     * @return list of WorkItems matching filter
     */
    public List<WorkItem> list(Long projectId, Long assigneeId, Long stationId, String stateName) {
        Map<String, Object> filterParams = new HashMap<String, Object>();
        if (projectId != null) {
            filterParams.put("project_id", projectId);
        }
        if (assigneeId != null) {
            filterParams.put("assignee_id", assigneeId);
        }
        if (stationId != null) {
            filterParams.put("station_id", stationId);
        }
        if (stateName != null) {
            filterParams.put("state", stateName.toUpperCase());
        }
        filterParams.put("is_active", true);
        return dao.filter(filterParams);
    }

    public List<WorkItemStateTransition> getHistory(Long workItemId) {
        WorkItem workItem = this.read(workItemId);
        return workItem.history;
    }

    /**
     * Also delete workItem audit history when deleting workItem.
     */
    @Override
    public void delete(Long workItemId) {
        WorkItem workItem = this.read(workItemId);
        if (workItem.history != null) {
            for (WorkItemStateTransition transition : workItem.history) {
                transition.isActive = false;
                historyDao.update(transition);
            }
        }
        super.delete(workItemId);
    }

    /**
     * All workItems should begin in an initial state.
     */
    @Override
    public WorkItem create(WorkItem workItem) {
        if (workItem.state == null) {
            workItem.state = WorkItemStateType.INITIAL;
        }
        return super.create(workItem);
    }

    /**
     * Assigns a workItem to a particular user (assignee).  After
     * assignment, the workItem should be in an IN_PROCESS state.
     * 
     * @param workItemId
     * @param assigneeId
     * @param transitionerId
     * @param comment
     * @return the assigned workItem
     */
    public WorkItem assign(Long workItemId, Long assigneeId, Long transitionerId, String comment) {
        WorkItem workItem = this.read(workItemId);
        WorkItemStateType fromState = workItem.state;
        User fromAssignee = workItem.assignee;
        if (assigneeId == null) {
            workItem.assignee = null;
            workItem.state = WorkItemStateType.UNASSIGNED;
        } else {
            workItem.assignee = userManager.read(assigneeId);
            workItem.state = WorkItemStateType.IN_PROCESS;
        }
        User transitioner = null;
        if (transitionerId != null) {
            transitioner = userManager.read(transitionerId);
        }
        WorkItemStateTransition transition = new WorkItemStateTransition(fromAssignee, workItem.assignee,
                workItem.station, workItem.station, fromState, workItem.state, transitioner, comment);
        workItem.history.add(transition);
        return super.update(workItem);
    }

    /**
     * Indicates that a workItem has been processed.  WorkItem is moved to a
     * PROCESSED state unless its station is the last in the assembly line,
     * in which case it is moved to a COMPLETED state.
     * 
     * @param workItemId
     * @param transitionerId
     * @param comment
     * @return the processed workItem
     */
    public WorkItem process(Long workItemId, Long transitionerId, String comment) {
        // specifying update props so they can be nulled if necessary
        WorkItem workItem = this.read(workItemId);
        WorkItemStateType fromState = workItem.state;
        if (workItem.station != null && workItem.station.isLast) {
            workItem.state = WorkItemStateType.COMPLETED;
        } else {
            workItem.state = WorkItemStateType.PROCESSED;
        }
        User transitioner = null;
        if (transitionerId != null) {
            transitioner = userManager.read(transitionerId);
        }
        WorkItemStateTransition transition = new WorkItemStateTransition(workItem.assignee, workItem.assignee,
                workItem.station, workItem.station, fromState, workItem.state, transitioner, comment);
        workItem.history.add(transition);
        return super.update(workItem);
    }

    /**
     * Transition a workItem to the next station.  After the workItem is
     * transitioned, it should be in an unassigned state since it has
     * not yet been picked up by a worker for processing.
     * 
     * @param workItemId
     * @param nextStationId
     * @param transitionerId
     * @param comment
     * @return the station-transitioned workItem
     */
    public WorkItem next(Long workItemId, Long nextStationId, Long transitionerId, String comment) {
        WorkItem workItem = this.read(workItemId);
        WorkItemStateType fromState = workItem.state;
        // when moving to a new station, the work item will begin in an
        // unassigned state.
        workItem.state = WorkItemStateType.UNASSIGNED;
        User fromAssignee = workItem.assignee;
        workItem.assignee = null;
        Station fromStation = workItem.station;
        workItem.station = (nextStationId != null) ? stationManager.read(nextStationId) : null;
        User transitioner = null;
        if (transitionerId != null) {
            transitioner = userManager.read(transitionerId);
        }
        WorkItemStateTransition transition = new WorkItemStateTransition(fromAssignee, workItem.assignee, fromStation,
                workItem.station, fromState, workItem.state, transitioner, comment);
        workItem.history.add(transition);
        return super.update(workItem);
    }

}
