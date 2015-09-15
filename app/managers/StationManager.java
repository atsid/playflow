package managers;

import java.util.ArrayList;
import java.util.List;

import dao.Dao;
import models.Station;
import models.User;
import models.WorkItem;

/**
 * 
 * CRUD operations for Stations.
 * 
 * @author: bbenson
 */

public class StationManager extends CrudManager<Station> {

    /**
     * Constructor
     * 
     * @param dao
     */
    public StationManager(Dao<Station> dao) {
        super(Station.class, dao);
    }

    /**
     * Lists all projects.  Filters the list according to factoryId
     * (stations that are in the given factory), workerId (stations that
     * have the given worker in their collection of workers) and projectId 
     * (stations that have workItems from the given project).
     * 
     * @param factoryId
     * @param workerId
     * @param projectId
     * @return filtered list of stations
     */
    public List<Station> list(Long factoryId, Long workerId, Long projectId) {
        List<Station> stations = super.list();
        List<Station> filteredStations = new ArrayList<Station>();

        // this filtering too complicated for dao filtering
        for (Station station : stations) {
            boolean included = true;
            if (included && factoryId != null) {
                included = station.factory != null && factoryId == station.factory.id;
            }
            if (included && workerId != null) {
                included = false;
                for (User worker : station.workers) {
                    if (worker.id == workerId) {
                        included = true;
                        break;
                    }
                }
            }
            if (included && projectId != null) {
                included = false;
                for (WorkItem workItem : station.workItems) {
                    if (workItem.project != null && workItem.project.id == projectId) {
                        included = true;
                        break;
                    }
                }
            }
            if (included && station.isActive) {
                filteredStations.add(station);
            }
        }
        return filteredStations;
    }

}
