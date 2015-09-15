package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A user may be a worker at a station or may be an admin or other type of
 * user.  Email is included as a unique user identifier.
 * 
 * @author bryan
 *
 */
@Entity
public class User extends IdModel {

    @Column(unique = true, nullable = false)
    public String email;

    public String firstName;

    public String lastName;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "station_user")
    public List<Station> stations;

    @OneToMany
    @JsonIgnore
    public List<WorkItem> workItems;

}
