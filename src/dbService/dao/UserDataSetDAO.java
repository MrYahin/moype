package dbService.dao;

import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import base.dataSets.UserDataSet;
import org.hibernate.HibernateException;

import java.util.List;

public class UserDataSetDAO {

	private Session session;
    
	public UserDataSetDAO(Session session) {
        this.session = session;
    }	
	
    public void save(UserDataSet dataSet) {
        session.save(dataSet);
        session.close();
    }

    public UserDataSet read(long id) {
        return (UserDataSet) session.load(UserDataSet.class, id);
    }

    public UserDataSet readByName(String username) throws HibernateException {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet)criteria.add(Restrictions.eq("username", username)).uniqueResult();
    }	
	
    @SuppressWarnings("unchecked")
    public List<UserDataSet> readAll() {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (List<UserDataSet>) criteria.list();
    }    
    
}
