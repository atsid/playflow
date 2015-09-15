package managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import models.Factory;
import models.Project;
import models.WorkItem;
import models.WorkItemStateType;

public class ProjectManagerTest extends Setup {

    @Test
    public void testDelete() {
        projectManager.delete(project.id);
        assertFalse(workItem.isActive);
        assertFalse(project.isActive);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testList() {
        // initial project
        WorkItem initialWorkItem = new WorkItem();
        initialWorkItem.state = WorkItemStateType.INITIAL;
        WorkItem unassignedWorkItem = new WorkItem();
        unassignedWorkItem.state = WorkItemStateType.UNASSIGNED;
        WorkItem inProcessWorkItem = new WorkItem();
        inProcessWorkItem.state = WorkItemStateType.IN_PROCESS;
        WorkItem processedWorkItem = new WorkItem();
        processedWorkItem.state = WorkItemStateType.PROCESSED;
        WorkItem completedWorkItem = new WorkItem();
        completedWorkItem.state = WorkItemStateType.COMPLETED;

        Project initialProject = new Project();
        initialProject.workItems = new ArrayList<WorkItem>();
        initialProject.workItems.add(initialWorkItem);
        initialProject.workItems.add(unassignedWorkItem);
        initialProject.workItems.add(inProcessWorkItem);
        initialProject.workItems.add(processedWorkItem);
        initialProject.workItems.add(completedWorkItem);
        Project inProcessProject = new Project();
        inProcessProject.workItems = new ArrayList<WorkItem>();
        inProcessProject.workItems.add(unassignedWorkItem);
        inProcessProject.workItems.add(inProcessWorkItem);
        inProcessProject.workItems.add(processedWorkItem);
        inProcessProject.workItems.add(completedWorkItem);
        Project completedProject = new Project();
        completedProject.workItems = new ArrayList<WorkItem>();
        completedProject.workItems.add(completedWorkItem);

        List<Project> projects = new ArrayList<Project>();
        projects.add(initialProject);
        projects.add(inProcessProject);
        projects.add(completedProject);

        when(projectDaoMock.filter(any(Map.class))).thenReturn(projects);

        assertEquals(1, projectManager.list("INITIAL").size());
        assertEquals(1, projectManager.list("IN_PROCESS").size());
        assertEquals(1, projectManager.list("COMPLETED").size());
    }

    @Test
    public void testAssign() {
        projectManager.assign(project.id, factory.id, null, null);
        assertEquals(WorkItemStateType.UNASSIGNED, workItem.state);
        assertEquals(workItem.assignee, null);
        assertEquals(workItem.station.id, station.id);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toState, WorkItemStateType.UNASSIGNED);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toAssignee, null);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toStation.id, station.id);
    }

    @Test(expected = IllegalStateException.class)
    public void testAssignToFactoryWithoutFirstStation() {
        Factory badFactory = new Factory();
        badFactory.id = new Long(2);
        when(factoryDaoMock.read(badFactory.id)).thenReturn(badFactory);
        projectManager.assign(project.id, badFactory.id, null, null);
    }

}