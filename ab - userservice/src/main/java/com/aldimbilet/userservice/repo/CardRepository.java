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
import com.aldimbilet.userservice.model.CardInfo;

@Repository
public class CardRepository
{
	private EntityManager em;

	private Session session;

	@Autowired
	public CardRepository(EntityManagerFactory factory)
	{
		this.em = factory.createEntityManager();
		this.session = em.unwrap(Session.class);
	}

	public CardInfo findByUserId(Long userId)
	{
		CardInfo res = null;
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<CardInfo> criteria_query = builder.createQuery(CardInfo.class);
		Root<CardInfo> root = criteria_query.from(CardInfo.class);
		criteria_query.where(builder.equal(root.get("userId"), userId));
		Query<CardInfo> query = session.createQuery(criteria_query);
		res = query.getSingleResult();
		return res;
	}

	public boolean save(CardInfo cardInfo)
	{
		session.beginTransaction();
		boolean res = session.save(cardInfo) != null;
		session.getTransaction().commit();
		return res;
	}
}
