package managers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import dao.Dao;
import exceptions.NotFoundException;
import models.IdModel;

public class CrudManagerTest extends Setup {

    CrudManager<TestObject> crudManager;
    TestObject testObject;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        Dao<TestObject> testObjectDaoMock = mock(Dao.class);
        crudManager = new CrudManager<TestObject>(TestObject.class, testObjectDaoMock);
        testObject = new TestObject();
        testObject.id = new Long(1);
        List<TestObject> testObjects = new ArrayList<TestObject>();
        testObjects.add(testObject);
        when(testObjectDaoMock.filter(any(Map.class))).thenReturn(testObjects);
        when(testObjectDaoMock.read(testObject.id)).thenReturn(testObject);
        when(testObjectDaoMock.create(testObject)).thenReturn(testObject);
        when(testObjectDaoMock.update(testObject)).thenReturn(testObject);
    }

    @Test
    public void testLis() {
        List<TestObject> testObjects = crudManager.list();
        assertEquals(1, testObjects.size());

    }

    @Test
    public void testRead() {
        TestObject daoTestObject = crudManager.read(testObject.id);
        assertEquals(this.testObject.id, daoTestObject.id);
    }

    @Test(expected = NotFoundException.class)
    public void testBadRead() {
        crudManager.read(new Long(44444444));
    }

    @Test
    public void testCreate() {
        TestObject newTestObject = new TestObject();
        newTestObject.id = new Long(2);
        TestObject daoTestObject = crudManager.create(newTestObject);
        assertEquals(daoTestObject.id, newTestObject.id);

    }

    @Test
    public void testUpdate() {
        testObject.isActive = false;
        TestObject daoTestObject = crudManager.update(testObject);
        assertEquals(false, daoTestObject.isActive);

    }

    @Test
    public void testDelete() {
        crudManager.delete(testObject.id);
        assertEquals(false, testObject.isActive);

    }

    private class TestObject extends IdModel {
    }
}