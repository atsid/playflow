package managers;

import java.util.ArrayList;
import java.util.List;

import dao.Dao;
import models.Project;
import models.Station;
import models.WorkItem;

/**
 * CRUD operations for projects
 * 
 * @author: bbenson
 */

public class ProjectManager extends CrudManager<Project> {

    private final FactoryManager factoryManager;
    private final WorkItemManager workItemManager;

    /**
     * Constructor
     * 
     * @param dao
     * @param factoryManager
     * @param workItemManager
     */
    public ProjectManager(Dao<Project> dao, FactoryManager factoryManager, WorkItemManager workItemManager) {
        super(Project.class, dao);
        this.factoryManager = factoryManager;
        this.workItemManager = workItemManager;
    }

    /**
     * Dao can't do filtering because project state is a calculated field.
     * 
     * @param stateName
     * @return list of projects with given state
     */
    public List<Project> list(String stateName) {
        List<Project> projects = super.list();
        if (stateName == null) {
            return projects;
        }
        List<Project> filteredProjects = new ArrayList<Project>();
        for (Project project : projects) {
            if (stateName.equalsIgnoreCase(project.getState().toString())) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }

    /**
     * When a project is deleted, all of its workItems are also deleted
     */
    @Override
    public void delete(Long projectId) {
        Project project = this.read(projectId);
        for (WorkItem workItem : project.workItems) {
            workItemManager.delete(workItem.id);
        }
        super.delete(projectId);
    }

    /**
     * Assign a project to a factory by taking all the project's workItems and
     * putting them in the first station of the factory. An error is thrown if
     * there is no first station in the factory. 
     * 
     * @param projectId
     * @param factoryId
     * @param transitionerId
     * @param comment
     * @return assigned project
     */
    public Project assign(Long projectId, Long factoryId, Long transitionerId, String comment) {
        Station firstStation = null;
        Project project = this.read(projectId);
        if (factoryId != null) {
            firstStation = factoryManager.getFirstStation(factoryId);
            if (firstStation == null) {
                throw new IllegalStateException("This factory has no first station in its assembly line");
            }
        }
        for (WorkItem workItem : project.workItems) {
            if (factoryId == null) {
                // transfer workItems out of all stations since factoryId not
                // specified
                workItemManager.next(workItem.id, null, transitionerId, comment);
            } else {
                workItemManager.next(workItem.id, firstStation.id, transitionerId, comment);
            }
        }
        return project;
    }

}
