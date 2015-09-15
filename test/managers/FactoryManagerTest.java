package managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import models.Station;

public class FactoryManagerTest extends Setup {

    @Test
    public void testGetfirstStation() {
        assertEquals(factoryManager.getFirstStation(factory.id).id, station.id);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetfirstStationWhenMoreThanOne() {
        Station anotherFirstStation = new Station();
        anotherFirstStation.isFirst = true;
        factory.assemblyLine.add(anotherFirstStation);
        factoryManager.getFirstStation(factory.id);
    }

    @Test
    public void testDelete() {
        factoryManager.delete(factory.id);
        assertFalse(station.isActive);
        assertFalse(factory.isActive);
    }
}