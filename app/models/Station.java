package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A station has workers that process the workItems at the station.  
 * It also has a 'next' station that indicates which station should process 
 * workItems after the current station (this ordering is not enforced -- 
 * the 'next' service call can choose to ignore this information).  
 * A station can be the first (only one first station) or last (there might
 * be several last stations if workflow branches).
 * 
 * @author: bbenson
 */
@Entity
public class Station extends IdModel {

    @OneToMany
    @JsonIgnore
    public List<WorkItem> workItems;

    @Column(nullable = false)
    public String name;

    public String description;

    @ManyToOne
    public Station next;

    @Column(columnDefinition = "boolean default false")
    public Boolean isLast = false;

    @Column(columnDefinition = "boolean default false")
    public Boolean isFirst = false;

    @ManyToOne
    @Column(nullable = false)
    public Factory factory;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "station_user")
    public List<User> workers;

}
