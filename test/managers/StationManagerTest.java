package managers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import models.Station;

public class StationManagerTest extends Setup {

    @Test
    public void testListFactory() {
        List<Station> stations = stationManager.list(factory.id, null, null);
        assertEquals(1, stations.size());

        stations = stationManager.list(null, user.id, null);
        assertEquals(1, stations.size());

        stations = stationManager.list(null, null, project.id);
        assertEquals(1, stations.size());

        // test id that doesn't exist
        stations = stationManager.list(null, null, new Long(4444444));
        assertEquals(0, stations.size());
    }

}