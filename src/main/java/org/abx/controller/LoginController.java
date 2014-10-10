package org.abx.controller;

import org.abx.model.User;

import java.io.Serializable;




import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.faces.application.FacesMessage;


import javax.faces.context.FacesContext;

@Named("loginController")
@SessionScoped
public class LoginController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User sessionUser;

	public LoginController() {

	}

	public User getSessionUser() {
		if (sessionUser == null) {
			sessionUser = new User();

		}
		return sessionUser;
	}

	
	@Inject
	HttpServletRequest request;

	public String login() {
		FacesContext context = FacesContext.getCurrentInstance();
			
		try {

			if (request == null)
				return "login";
			else if (request.getUserPrincipal() != null)
				request.logout();
			
			request.login(getSessionUser().getUsername(), getSessionUser().getPassword());
			if (request.isUserInRole("admin") == true) {
				return "campaignData/List?faces-redirect=true";
			} else {
				context.addMessage(null, new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "User not allowed.",
						"User belong to a group that is not allowed."));
				return "login";
			}

		} catch (ServletException e) {

			context.addMessage(null, new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.getMessage(),
					e.getMessage()));
			return "login";
		}

	}

}
