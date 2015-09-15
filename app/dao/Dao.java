package dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;

import models.IdModel;

/**
 * This is an Ebean Facade.  It keeps Ebean calls out of manager
 * code so that this code can be unit tested.  It also makes
 * managers less dependent on a particular ORM.
 * 
 * @author: bbenson
 */

public class Dao<M extends IdModel> {
    private final Class<M> modelClass;

    public Dao(Class<M> clazz) {
        modelClass = clazz;
    }

    public List<M> filter(Map<String, Object> filterParams) {
        Query<M> q = Ebean.createQuery(modelClass);
        ExpressionList<M> expressions = q.where();
        for (Entry<String, Object> param : filterParams.entrySet()) {
            expressions.add(Expr.eq(param.getKey(), param.getValue()));
        }
        return expressions.findList();
    }

    public M read(Long id) {
        M model = Ebean.find(modelClass).where().idEq(id).findUnique();
        return model;
    }

    public M create(M model) {
        Ebean.save(model);
        return model;
    }

    public M update(M model) {
        Ebean.update(model);
        return model;
    }

    public void delete(M model) {
        Ebean.delete(model);
    }
}
