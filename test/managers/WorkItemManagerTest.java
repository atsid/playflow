package managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import models.WorkItemStateType;

public class WorkItemManagerTest extends Setup {

    @Test
    public void testDelete() {
        workItemManager.delete(workItem.id);
        assertFalse(transition.isActive);
        assertFalse(workItem.isActive);
    }

    @Test
    public void testCreate() {
        workItemManager.create(workItem);
        assertEquals(WorkItemStateType.INITIAL, workItem.state);
    }

    @Test
    public void testAssign() {
        workItemManager.assign(workItem.id, user.id, null, null);
        assertEquals(WorkItemStateType.IN_PROCESS, workItem.state);
        assertEquals(workItem.assignee.id, user.id);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toState, WorkItemStateType.IN_PROCESS);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toAssignee.id, user.id);
    }

    @Test
    public void testProcess() {
        // unassign the workitem from last station to test processing workItem
        // in non-final stations.
        workItem.station = null;
        workItemManager.process(workItem.id, null, null);
        assertEquals(WorkItemStateType.PROCESSED, workItem.state);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toState, WorkItemStateType.PROCESSED);
    }

    @Test
    public void testProcessFromLastStation() {
        // workItem is in last station so processing it means it is completed
        workItemManager.process(workItem.id, null, null);
        assertEquals(WorkItemStateType.COMPLETED, workItem.state);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toState, WorkItemStateType.COMPLETED);
    }

    @Test
    public void testNext() {
        workItemManager.next(workItem.id, station.id, null, null);
        assertEquals(WorkItemStateType.UNASSIGNED, workItem.state);
        assertEquals(workItem.assignee, null);
        assertEquals(workItem.station.id, station.id);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toState, WorkItemStateType.UNASSIGNED);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toAssignee, null);
        assertEquals(workItem.history.get(workItem.history.size() - 1).toStation.id, station.id);
    }

}