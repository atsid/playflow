package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * A WorkItem is any item that can be processed to completion at a
 * set of stations.  It has an audit history of state transitions. It also
 * has an assignee that is assigned to complete work at a given station.
 * 
 * @author: bbenson
 */
@Entity
public class WorkItem extends IdModel {

    @Column(nullable = false)
    public String name;

    public String description;

    @ManyToOne
    @Column(nullable = false)
    public WorkItemStateType state;

    @OneToMany(cascade = CascadeType.ALL)
    public List<WorkItemStateTransition> history;

    @ManyToOne
    @Column(nullable = false)
    public Project project;

    @ManyToOne
    public Station station;

    @ManyToOne
    public User assignee;

}
