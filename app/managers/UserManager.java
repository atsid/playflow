package managers;

import dao.Dao;
import models.User;

/**
 * CRUD operations for users.  
 * 
 * @author: bbenson
 */

public class UserManager extends CrudManager<User> {

    /**
     * Constructor 
     * 
     * @param dao
     */
    public UserManager(Dao<User> dao) {
        super(User.class, dao);
    }
}
