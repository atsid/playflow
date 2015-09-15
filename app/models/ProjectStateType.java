package models;

/**
 * If any one workItem is in an INITIAL state, the entire project is 
 * considered to be INITIAL. If there aren't workItems in an INITIAL 
 * state, but there are are workItems in other non-complete states, 
 * the entire project is considered to be IN_PROCESS.  A project is 
 * only COMPLETE if all its workItems are complete.  
 * 
 * @author: bbenson
 */
public enum ProjectStateType {
    INITIAL, IN_PROCESS, COMPLETED;
}
