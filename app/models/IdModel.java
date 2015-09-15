package models;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.avaje.ebean.Model;

/**
 * These objects are soft-deleted so they use an isActive flag.
 * 
 * @author: bbenson
 */
@MappedSuperclass
public abstract class IdModel extends Model {

    @Id
    public Long id;

    @Column(columnDefinition = "boolean default true", nullable = false)
    public Boolean isActive = true;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        IdModel other = (IdModel) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
