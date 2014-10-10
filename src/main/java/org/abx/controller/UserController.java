package org.abx.controller;

import org.abx.model.User;
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

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User current;
	private LazyDataModel<User> items = null;
	@EJB
	private org.abx.controller.UserFacade ejbFacade;

	public UserController() {
	}

	public User getSelected() {
		if (current == null) {
			current = new User();

		}
		return current;
	}

	private UserFacade getFacade() {
		return ejbFacade;
	}

	public String prepareList() {
		recreateModel();
		return "List";
	}

	public String prepareView() {
		current = (User) getItems().getRowData();
		return "View";
	}

	public String prepareCreate() {
		current = new User();

		return "Create";
	}

	public String create() {
		try {
			getFacade().create(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("UserCreated"));
			return prepareCreate();
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (User) getItems().getRowData();
		return "Edit";
	}

	public String update() {
		try {
			
			if (current.getTextPassword1().isEmpty() != false)
			getFacade().edit(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("UserUpdated"));
			return "View";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (User) getItems().getRowData();
		performDestroy();
		recreateModel();
		return "List";
	}

	public String destroyAndView() {
		performDestroy();
		recreateModel();
		updateCurrentItem();
		recreateModel();
		return "List";
	}

	private void performDestroy() {
		try {
			getFacade().remove(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("Bundle")
					.getString("UserDeleted"));
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("Bundle")
					.getString("PersistenceErrorOccured"));
		}
	}

	private void updateCurrentItem() {
//		int count = getFacade().count();
//		if (selectedItemIndex >= count) {
//			// selected index cannot be bigger than number of items:
//			selectedItemIndex = count - 1;
//			// go to previous page if last page disappeared:
//
//		}
//		if (selectedItemIndex >= 0) {
//			current = getFacade().findRange(
//					new int[] { selectedItemIndex, selectedItemIndex + 1 })
//					.get(0);
//		}
	}

	public LazyDataModel<User> getItems() {
		if (items == null) {
			items = new LazyDataModel<User>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<User> load(int first, int pageSize,
						String sortField, SortOrder sortOrder,
						Map<String, Object> filters) {
					List<User> result = getFacade().getFilterResultList(first,
							pageSize, sortField, sortOrder, filters);
					this.setRowCount(getFacade().getFilterResultCount(
							sortField, sortOrder, filters));
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

	public String next() {
		recreateModel();
		return "List";
	}

	public String previous() {
		recreateModel();
		return "List";
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
	}

	public User getUser(java.lang.Integer id) {
		return ejbFacade.find(id);
	}

	@FacesConverter(forClass = User.class)
	public static class UserControllerConverter implements Converter {

		@Override
		public Object getAsObject(FacesContext facesContext,
				UIComponent component, String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			UserController controller = (UserController) facesContext
					.getApplication()
					.getELResolver()
					.getValue(facesContext.getELContext(), null,
							"userController");
			return controller.getUser(getKey(value));
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
			if (object instanceof User) {
				User o = (User) object;
				return o.getUsername();
			} else {
				throw new IllegalArgumentException("object " + object
						+ " is of type " + object.getClass().getName()
						+ "; expected type: " + User.class.getName());
			}
		}

	}

}
