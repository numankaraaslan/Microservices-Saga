package com.aldimbilet.userservice.repo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.aldimbilet.userservice.model.Role;

@Repository
public class RoleRepository
{
	private EntityManager em;

	private Session session;

	@Autowired
	public RoleRepository(EntityManagerFactory factory)
	{
		this.em = factory.createEntityManager();
		this.session = em.unwrap(Session.class);
	}

	public boolean save(Role role)
	{
		session.beginTransaction();
		boolean res = session.save(role) != null;
		session.getTransaction().commit();
		return res;
	}
}