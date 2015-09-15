package managers;

import dao.Dao;
import models.Factory;
import models.Station;

/**
 * CRUD operations for factories
 * 
 * @author: bbenson
 */

public class FactoryManager extends CrudManager<Factory> {
    private final StationManager stationManager;

    /**
     * Constructor
     * 
     * @param dao
     * @param stationManager
     */
    public FactoryManager(Dao<Factory> dao, StationManager stationManager) {
        super(Factory.class, dao);
        this.stationManager = stationManager;
    }

    /**
     * When a factory is deleted, all of its stations are also deleted.
     */
    @Override
    public void delete(Long factoryId) {
        Factory factory = this.read(factoryId);
        for (Station station : factory.assemblyLine) {
            stationManager.delete(station.id);
        }
        super.delete(factoryId);
    }

    /**
     * Loops through all stations in assembly line checking for first station.
     * Throws an exception if it finds more than one.
     * 
     * @param factoryId
     * @return first station
     */
    public Station getFirstStation(Long factoryId) {
        Station firstStation = null;
        Factory factory = this.read(factoryId);
        if (factory.assemblyLine != null) {
            for (Station station : factory.assemblyLine) {
                if (station.isFirst) {
                    if (firstStation != null) {
                        throw new IllegalStateException("There can't be more than one first station in a factory");
                    } else {
                        firstStation = station;
                    }
                }
            }
        }
        return firstStation;
    }

}
