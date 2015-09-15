package models;

import com.avaje.ebean.annotation.EnumValue;

/**
 * A WorkItem is INITIAL if it has not yet been assigned to a factory (not
 * yet at a station).  It is UNASSIGNED if it is at a station, but has not
 * yet been picked up by a worker.  It is IN_PROCESS if it is currently being
 * processed by a station worker.  It is PROCESSED if work has been completed
 * by the station worker.  It is COMPLETED when all work at all stations has
 * been completed on the workItem.
 * 
 * @author: bbenson
 */
public enum WorkItemStateType {
    @EnumValue("INITIAL") INITIAL,

    @EnumValue("UNASSIGNED") UNASSIGNED,

    @EnumValue("IN_PROCESS") IN_PROCESS,

    @EnumValue("PROCESSED") PROCESSED,

    @EnumValue("COMPLETED") COMPLETED;
}
