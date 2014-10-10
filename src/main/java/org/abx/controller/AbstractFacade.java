/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.abx.controller;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;


import org.primefaces.model.SortOrder;

/**
 *
 * @author alex
 */
public abstract class AbstractFacade<T> {
	private Class<T> entityClass;

	public AbstractFacade(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract EntityManager getEntityManager();

	public void create(T entity) {
		getEntityManager().persist(entity);
	}

	public void edit(T entity) {
		getEntityManager().merge(entity);
	}

	public void remove(T entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
	}

	public T find(Object id) {
		return getEntityManager().find(entityClass, id);
	}

	public List<T> findAll() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);

		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	public List<T> findRange(int[] range) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);

		javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
		cq.select(rt);
		
		javax.persistence.TypedQuery<T> q = getEntityManager().createQuery(cq);
		q.setMaxResults(range[1] - range[0] + 1);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}

	public int count() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);

		javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
		
		CriteriaQuery<Long> cql = cb.createQuery(Long.class);
		cql.select(cb.count(rt));

		javax.persistence.TypedQuery<Long> q = getEntityManager().createQuery(cql);
		return q.getSingleResult().intValue();
	}

	protected String getIdFieldName() {
		return null;

	}

	protected Path<?> getPath(String field, Root<T> site) {
		// sort
		Path<?> path = null;
		if (field == null) {
			path = site.get(this.getIdFieldName());
		} else {
			path = site.get(field);
		}
		return path;
	}

	protected Path<String> getStringPath(String field, Root<T> site) {
		// sort
		Path<String> path = null;
		if (field == null) {
			path = site.get(this.getIdFieldName());
		} else {
			path = site.get(field);
		}
		return path;
	}
	
	public List<T> getFilterResultList(int first, int pageSize,
			String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		List<T> all = new ArrayList<T>();
		all.addAll(this.getItemsByFilter(first, pageSize, sortField, sortOrder,
				filters));
		return all;
	}

	public int getFilterResultCount(String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		return this.getItemsByFilter(-1, -1, null, null, filters).size();
	}
	
	private Collection<T> getItemsByFilter(int first, int pageSize,
			String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> q = cb.createQuery(entityClass);
		Root<T> rt = q.from(entityClass);
		q.select(rt);

		Path<?> path = getPath(sortField, rt);
		if (sortOrder == null) {
			// just don't sort
		} else if (sortOrder.equals(SortOrder.ASCENDING)) {
			q.orderBy(cb.asc(path));
		} else if (sortOrder.equals(SortOrder.DESCENDING)) {
			q.orderBy(cb.asc(path));
		} else if (sortOrder.equals(SortOrder.UNSORTED)) {
			// just don't sort
		} else {
			// just don't sort
		}

		// filter
		Predicate filterCondition = cb.conjunction();
		for (Map.Entry<String, Object> filter : filters.entrySet()) {
			if (!filter.getValue().equals("")) {
				// try as string using like
				Path<String> pathFilter = getStringPath(filter.getKey(), rt);
				if (pathFilter != null) {
					filterCondition = cb.and(filterCondition,
							cb.like(pathFilter, "%" + filter.getValue() + "%"));
				} else {
					// try as non-string using equal
					Path<?> pathFilterNonString = getPath(filter.getKey(), rt);
					filterCondition = cb.and(filterCondition,
							cb.equal(pathFilterNonString, filter.getValue()));
				}
			}
		}
		q.where(filterCondition);

		// pagination
		TypedQuery<T> tq = getEntityManager().createQuery(q);
		if (pageSize >= 0) {
			tq.setMaxResults(pageSize);
		}
		if (first >= 0) {
			tq.setFirstResult(first);
		}
		return tq.getResultList();
	}
}
