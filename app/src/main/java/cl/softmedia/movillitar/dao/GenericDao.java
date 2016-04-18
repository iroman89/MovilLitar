package cl.softmedia.movillitar.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.util.List;

/**
 * Created by iroman on 28/10/2015.
 */
public class GenericDao<T> {

    private Dao oDao;

    public GenericDao(Context mContext, Class<T> oClazz) throws Exception {
        oDao = new DatabaseHelper(mContext).getDao(oClazz);
    }

    public List<T> queryForAll(Where<T, Integer> where) throws Exception {

        QueryBuilder queryBuilder = oDao.queryBuilder();
        queryBuilder.setWhere(where);

        return queryBuilder.query();
    }

    public List<T> queryForAll() throws Exception {

        QueryBuilder queryBuilder = oDao.queryBuilder();

        return queryBuilder.query();
    }

    public List<T> queryForAllOrderBy(String sColumn, boolean asc) throws Exception {

        QueryBuilder queryBuilder = oDao.queryBuilder().orderBy(sColumn, asc);

        return queryBuilder.query();
    }


    public <T> T queryForFirst(Where<T, Integer> where) throws Exception {

        QueryBuilder queryBuilder = oDao.queryBuilder();
        queryBuilder.setWhere(where);

        return (T) queryBuilder.queryForFirst();
    }

    public Where<T, Integer> getStatement() throws Exception {

        return oDao.queryBuilder().where();
    }

    public void update(Where<T, Integer> where, String field, Object value) throws Exception {

        UpdateBuilder<T, Integer> updateBuilder = oDao.updateBuilder();
        updateBuilder.setWhere(where);
        updateBuilder.updateColumnValue(field, value);
        updateBuilder.update();
    }

    public void updateAll(String field, Object value) throws Exception {

        UpdateBuilder<T, Integer> updateBuilder = oDao.updateBuilder();
        updateBuilder.updateColumnValue(field, value);
        updateBuilder.update();
    }

    public void delete(Where<T, Integer> where) throws Exception {

        DeleteBuilder<T, Integer> deleteBuilder = oDao.deleteBuilder();
        deleteBuilder.setWhere(where);
        deleteBuilder.delete();
    }

    public void deleteAll() throws Exception {

        DeleteBuilder<T, Integer> deleteBuilder = oDao.deleteBuilder();
        deleteBuilder.delete();
    }

    public void create(Object object) throws Exception {

        oDao.createOrUpdate(object);
    }

    public void createFromList(List<T> objectList) throws Exception {

        for (Object object : objectList) {
            oDao.create(object);
        }
    }

}
