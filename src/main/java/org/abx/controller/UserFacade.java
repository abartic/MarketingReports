/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.abx.controller;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.abx.model.User;



/**
 *
 * @author alex
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {
    @PersistenceContext(unitName = "MarketingReportsPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }
    
    @Override
	protected String getIdFieldName() {
		return "username";

	}
    
    public Boolean checkCredentials(String userName, String password) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);

		Root<User> rt = cq.from(User.class);
		cq.select(rt);
		
		Predicate filterCondition = cb.conjunction();
		filterCondition = cb.and(filterCondition, cb.equal(rt.get("username"), userName));
		filterCondition = cb.and(filterCondition, cb.equal(rt.get("password"), password));
		
		cq.where(filterCondition);
		
		return getEntityManager().createQuery(cq).getResultList().isEmpty() == false;
	}
}
