package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A WorkItemStateTransition is used to track an audit history of the workItem.
 * The "transitioner" is the admin user or other that instigates the service 
 * call that transitions the workItem.  A comment field collects any notes
 * the transitioner or worker may have had in processing the work item.
 * 
 * @author: bbenson
 */
@Entity
public class WorkItemStateTransition extends IdModel {

    public WorkItemStateTransition(User fromAssignee, User toAssignee, Station fromStation, Station toStation,
            WorkItemStateType fromState, WorkItemStateType toState, User transitioner, String comment) {
        this.fromAssignee = fromAssignee;
        this.toAssignee = toAssignee;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.fromState = fromState;
        this.toState = toState;
        this.transitioner = transitioner;
        this.comment = comment;
        this.transitionDate = new Date();
    }

    @ManyToOne
    @JsonIgnore
    @Column(nullable = false)
    public WorkItem workItem;

    @ManyToOne
    public Station fromStation;

    @ManyToOne
    public Station toStation;

    @ManyToOne
    @Column(nullable = false)
    public WorkItemStateType fromState;

    @ManyToOne
    @Column(nullable = false)
    public WorkItemStateType toState;

    @ManyToOne
    public User fromAssignee;

    @ManyToOne
    public User toAssignee;

    // the transitioner is the user that initiated this transition (null if the
    // system initiated the transition)
    @ManyToOne
    public User transitioner;

    @Column(nullable = false)
    public Date transitionDate;

    // e.g. why was the item transitioned?
    public String comment;

}
