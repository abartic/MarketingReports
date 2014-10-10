package org.abx.controller.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("org.abx.controller.util.NegateBooleanConverter")
public class NegateBooleanConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		return Boolean.parseBoolean(value) == false;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {

		return value.equals(true) ? Boolean.FALSE.toString() : Boolean.TRUE.toString();
	}

}
