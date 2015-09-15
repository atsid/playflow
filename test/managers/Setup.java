package managers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;

import dao.Dao;
import models.Factory;
import models.Project;
import models.Station;
import models.User;
import models.WorkItem;
import models.WorkItemStateTransition;

public class Setup {

    Factory factory;
    Dao<Factory> factoryDaoMock;
    FactoryManager factoryManager;

    Station station;
    Dao<Station> stationDaoMock;
    StationManager stationManager;

    WorkItem workItem;
    Dao<WorkItem> workItemDaoMock;
    WorkItemManager workItemManager;

    WorkItemStateTransition transition;
    Dao<WorkItemStateTransition> historyDaoMock;

    User user;
    Dao<User> userDaoMock;
    UserManager userManager;

    Project project;
    Dao<Project> projectDaoMock;
    ProjectManager projectManager;

    /**
     * Make one of everything for testing. Any objects with special
     * characteristics will have to be generated in the tests separately.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        user = new User();
        user.id = new Long(1);
        userDaoMock = mock(Dao.class);
        when(userDaoMock.read(user.id)).thenReturn(user);
        userManager = new UserManager(userDaoMock);

        station = new Station();
        station.id = new Long(1);
        station.isFirst = true;
        station.isLast = true;
        station.workers = new ArrayList<User>();
        station.workers.add(user);
        stationDaoMock = mock(Dao.class);
        when(stationDaoMock.read(station.id)).thenReturn(station);
        stationManager = new StationManager(stationDaoMock);

        factory = new Factory();
        factory.id = new Long(1);
        factory.assemblyLine = new ArrayList<Station>();
        factory.assemblyLine.add(station);
        station.factory = factory;
        factoryDaoMock = mock(Dao.class);
        when(factoryDaoMock.read(factory.id)).thenReturn(factory);
        when(stationDaoMock.filter(any(Map.class))).thenReturn(factory.assemblyLine);
        factoryManager = new FactoryManager(factoryDaoMock, stationManager);

        transition = new WorkItemStateTransition(null, null, null, null, null, null, null, null);
        transition.id = new Long(1);
        historyDaoMock = mock(Dao.class);
        when(historyDaoMock.read(transition.id)).thenReturn(transition);
        when(historyDaoMock.update(transition)).thenReturn(transition);

        workItem = new WorkItem();
        workItem.id = new Long(1);
        workItem.history = new ArrayList<WorkItemStateTransition>();
        workItem.history.add(transition);
        workItem.station = station;
        station.workItems = new ArrayList<WorkItem>();
        station.workItems.add(workItem);
        workItemDaoMock = mock(Dao.class);
        when(workItemDaoMock.read(workItem.id)).thenReturn(workItem);
        when(workItemDaoMock.update(workItem)).thenReturn(workItem);
        when(workItemDaoMock.create(workItem)).thenReturn(workItem);
        workItemManager = new WorkItemManager(workItemDaoMock, historyDaoMock, new UserManager(userDaoMock),
                stationManager);

        project = new Project();
        project.id = new Long(1);
        project.workItems = new ArrayList<WorkItem>();
        project.workItems.add(workItem);
        project.workItems = new ArrayList<WorkItem>();
        project.workItems.add(workItem);
        workItem.project = project;
        projectDaoMock = mock(Dao.class);
        when(projectDaoMock.read(project.id)).thenReturn(project);
        projectManager = new ProjectManager(projectDaoMock, factoryManager, workItemManager);
    }

}