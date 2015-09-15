package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Factory has an assembly line which is a set of stations in which to 
 * process workItems.
 * 
 * @author: bbenson
 */
@Entity
public class Factory extends IdModel {

    @Column(unique = true, nullable = false)
    public String name;

    public String description;

    @JsonIgnore
    @OneToMany
    public List<Station> assemblyLine;
}
