package com.aldimbilet.userservice.repo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.aldimbilet.userservice.model.ABUser;

@Repository
public class UserRepository
{
	private EntityManager em;

	private Session session;

	@Autowired
	public UserRepository(EntityManagerFactory factory)
	{
		this.em = factory.createEntityManager();
		this.session = em.unwrap(Session.class);
	}

	public ABUser findByUsername(String username)
	{
		ABUser res = null;
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ABUser> criteria_query = builder.createQuery(ABUser.class);
		Root<ABUser> root = criteria_query.from(ABUser.class);
		criteria_query.where(builder.equal(root.get("username"), username));
		Query<ABUser> query = session.createQuery(criteria_query);
		res = query.getSingleResult();
		return res;
	}

	public boolean save(ABUser user)
	{
		session.beginTransaction();
		boolean res = session.save(user) != null;
		session.getTransaction().commit();
		return res;
	}

	public ABUser findById(Long userId)
	{
		ABUser res = null;
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ABUser> criteria_query = builder.createQuery(ABUser.class);
		Root<ABUser> root = criteria_query.from(ABUser.class);
		criteria_query.where(builder.equal(root.get("id"), userId));
		Query<ABUser> query = session.createQuery(criteria_query);
		res = query.getSingleResult();
		return res;
	}
}
