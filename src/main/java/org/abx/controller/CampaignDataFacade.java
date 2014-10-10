/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.abx.controller;



import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.abx.model.CampaignData;


/**
 *
 * @author alex
 */
@Stateless
public class CampaignDataFacade extends AbstractFacade<CampaignData> {
	@PersistenceContext(unitName = "MarketingReportsPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public CampaignDataFacade() {
		super(CampaignData.class);
	}

	@Override
	protected String getIdFieldName() {
		return "campaignDataId";

	}

	

	
}
