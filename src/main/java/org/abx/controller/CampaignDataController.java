package org.abx.controller;

import org.abx.model.CampaignData;
import org.abx.controller.util.JsfUtil;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;

@Named("campaignDataController")
@SessionScoped
public class CampaignDataController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CampaignData current;
	private LazyDataModel<CampaignData> items = null;
	@EJB
	private org.abx.controller.CampaignDataFacade ejbFacade;
	
	private int selectedItemIndex;

	public CampaignDataController() {
	}

	public CampaignData getSelected() {
		if (current == null) {
			current = new CampaignData();
			selectedItemIndex = -1;
		}
		return current;
	}

	private CampaignDataFacade getFacade() {
		return ejbFacade;
	}

	public String prepareView() {
		current = (CampaignData) getItems().getRowData();
		return "View";
	}

	 public String prepareList() {
	        recreateModel();
	        return "List";
	 }
	 
	public String prepareCreate() {
		current = new CampaignData();
		return "Create";
	}

	public String create() {
		try {
			getFacade().create(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("CampaignDataCreated"));
			return prepareCreate();
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (CampaignData) getItems().getRowData();
		return "Edit";
	}

	public String update() {
		try {
			getFacade().edit(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("CampaignDataUpdated"));
			return "View";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (CampaignData) getItems().getRowData();
		performDestroy();
		recreateModel();
		return "List";
	}

	public String destroyAndView() {
		performDestroy();
		recreateModel();
		updateCurrentItem();
		if (selectedItemIndex >= 0) {
			return "View";
		} else {
			// all items were removed - go back to list
			recreateModel();
			return "List";
		}
	}

	private void performDestroy() {
		try {
			getFacade().remove(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("CampaignDataDeleted"));
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
		}
	}

	private void updateCurrentItem() {
		int count = getFacade().count();
		if (selectedItemIndex >= count) {
			// selected index cannot be bigger than number of items:
			selectedItemIndex = count - 1;
			// go to previous page if last page disappeared:
			// if (pagination.getPageFirstItem() >= count) {
			// pagination.previousPage();
			// }
		}
		if (selectedItemIndex >= 0) {
			current = getFacade().findRange(
					new int[] { selectedItemIndex, selectedItemIndex + 1 })
					.get(0);
		}
	}

	public LazyDataModel<CampaignData> getItems() {
		if (items == null) {
			items = new LazyDataModel<CampaignData>() {
				private static final long serialVersionUID = 1L;
				
				
				@Override
				public List<CampaignData> load(int first, int pageSize,
						String sortField, SortOrder sortOrder,
						Map<String, Object> filters) {
					List<CampaignData> result = getFacade().getFilterResultList(
							first, pageSize, sortField, sortOrder, filters);
					this.setRowCount(getFacade().getFilterResultCount(sortField, sortOrder,
							filters));
					return result;
				}
			};
			items.setRowCount(getFacade().count());
		}
		return items;
	}

	private void recreateModel() {
		items = null;
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
	}

	public CampaignData getCampaignData(java.lang.Integer id) {
		return ejbFacade.find(id);
	}

	@FacesConverter(forClass = CampaignData.class)
	public static class CampaignDataControllerConverter implements Converter {

		@Override
		public Object getAsObject(FacesContext facesContext,
				UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			CampaignDataController controller = (CampaignDataController) facesContext
					.getApplication()
					.getELResolver()
					.getValue(facesContext.getELContext(), null,
							"campaignDataController");
			return controller.getCampaignData(getKey(value));
		}

		java.lang.Integer getKey(String value) {
			java.lang.Integer key;
			key = Integer.valueOf(value);
			return key;
		}

		String getStringKey(java.lang.Integer value) {
			StringBuilder sb = new StringBuilder();
			sb.append(value);
			return sb.toString();
		}

		@Override
		public String getAsString(FacesContext facesContext,
				UIComponent component, Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof CampaignData) {
				CampaignData o = (CampaignData) object;
				return getStringKey(o.getCampaignDataId());
			} else {
				throw new IllegalArgumentException("object " + object
						+ " is of type " + object.getClass().getName()
						+ "; expected type: " + CampaignData.class.getName());
			}
		}

	}

}
