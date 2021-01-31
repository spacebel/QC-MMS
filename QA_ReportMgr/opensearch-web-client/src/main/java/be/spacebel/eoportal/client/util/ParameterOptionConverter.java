/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.spacebel.eoportal.client.util;

import be.spacebel.eoportal.client.business.data.Constants;
import be.spacebel.eoportal.client.business.data.ParameterOption;
import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.commons.lang.StringUtils;

/**
 * This is a converter which is used by Primefaces AutoComplete component
 *
 * @author mng
 */
@FacesConverter("paramOptionConverter")
public class ParameterOptionConverter implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String selectedValue) {
        if (StringUtils.isNotEmpty(selectedValue)) {
            String value = StringUtils.substringAfter(selectedValue, Constants.AUTO_COMPLETE_OPTION_SEPARATOR);
            if(StringUtils.isEmpty(value)){                
                value = selectedValue;
                //System.out.println("MNG NO TOKEN FOUND");
            }
            
            return new ParameterOption(selectedValue, value);
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object obj) {
        if (obj != null && obj instanceof ParameterOption) {
            ParameterOption option = (ParameterOption) obj;
            return option.getValue();
        } else {
            return null;
        }
    }

}
