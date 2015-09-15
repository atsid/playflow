package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A project is a collection of workItems.  
 * @author: bbenson
 */
@Entity
public class Project extends IdModel {

    @Column(unique = true, nullable = false)
    public String name;

    public String description;

    @Transient
    public ProjectStateType state;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    public List<WorkItem> workItems;

    /**
     * Instead of storing project state, it is calculated based on the
     * collective states of its workItems.  If any one workItem is
     * in an INITIAL state, the entire project is considered to be INITIAL.
     * If there aren't workItems in an INITIAL state, but there are
     * are workItems in other non-complete states, the entire project is
     * considered to be IN_PROCESS.  A project is only COMPLETED if all its 
     * workItems are completed.  
     * 
     * @return Project state
     */
    public ProjectStateType getState() {
        boolean hasIncomplete = false;
        if (this.workItems.isEmpty()) {
            return ProjectStateType.INITIAL;
        }
        for (WorkItem workItem : workItems) {
            if (workItem.state.equals(WorkItemStateType.INITIAL)) {
                return ProjectStateType.INITIAL;
            } else if (!workItem.state.equals(WorkItemStateType.COMPLETED)) {
                hasIncomplete = true;
            }
        }
        return hasIncomplete ? ProjectStateType.IN_PROCESS : ProjectStateType.COMPLETED;
    }
}
