package com.aldimbilet.activityservice.repo;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.aldimbilet.activityservice.model.Activity;

@Repository
public class ActivityRepository
{
	private EntityManager em;

	private Session session;

	@Autowired
	public ActivityRepository(EntityManagerFactory factory)
	{
		this.em = factory.createEntityManager();
		this.session = em.unwrap(Session.class);
	}

	public boolean save(Activity activity)
	{
		session.beginTransaction();
		boolean res = session.save(activity) != null;
		session.getTransaction().commit();
		return res;
	}

	public Activity findById(Long userId)
	{
		Activity res = null;
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Activity> criteria_query = builder.createQuery(Activity.class);
		Root<Activity> root = criteria_query.from(Activity.class);
		criteria_query.where(builder.equal(root.get("id"), userId));
		Query<Activity> query = session.createQuery(criteria_query);
		res = query.getSingleResult();
		return res;
	}

	public List<Activity> getAll()
	{
		List<Activity> res = null;
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Activity> criteria_query = builder.createQuery(Activity.class);
		Query<Activity> query = session.createQuery(criteria_query);
		res = query.getResultList();
		return res;
	}

	public int getCount()
	{
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Activity> root = query.from(Activity.class);
		query.select(builder.count(root));
		Long res = session.createQuery(query).getSingleResult();
		return res.intValue();
	}
}
