package managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.Dao;
import exceptions.NotFoundException;
import models.IdModel;

/**
 * Base CRUD Manager.  Uses soft delete (isActive flag).
 * List method only returns active objects.
 * 
 * @author: bbenson
 */

public class CrudManager<M extends IdModel> {
    private final Class<M> modelClass;
    private final Dao<M> dao;

    /**
     * Constructor
     * 
     * @param clazz
     * @param dao
     */
    public CrudManager(Class<M> clazz, Dao<M> dao) {
        this.modelClass = clazz;
        this.dao = dao;
    }

    /**
     * List all objects that are active
     * 
     * @return list of all active objects
     */
    public List<M> list() {
        Map<String, Object> filterParams = new HashMap<String, Object>();
        filterParams.put("is_active", true);
        return dao.filter(filterParams);

    }

    /**
     * read a single object
     * 
     * @param id
     * @return object found
     */
    public M read(Long id) {
        M model = dao.read(id);
        if (model == null) {
            throw new NotFoundException(modelClass + ":" + id + " not found");
        }

        return model;
    }

    /**
     * create an object
     * 
     * @param model
     * @return created object
     */
    public M create(M model) {
        dao.create(model);
        return model;
    }

    /**
     * Update an object
     * 
     * @param model
     * @return updated object
     */
    public M update(M model) {
        dao.update(model);
        return model;
    }

    /**
     * Soft delete an Object by making it inactive.
     * 
     * @param id
     */
    public void delete(Long id) {
        M model = this.read(id);
        model.isActive = false;
        dao.update(model);
    }
}
