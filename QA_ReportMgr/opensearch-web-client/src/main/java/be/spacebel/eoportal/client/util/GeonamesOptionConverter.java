/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.spacebel.eoportal.client.util;

import be.spacebel.eoportal.client.business.data.Constants;
import be.spacebel.eoportal.client.business.data.GeonamesOption;
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
@FacesConverter("geonamesOptionConverter")
public class GeonamesOptionConverter implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String selectedValue) {
        if (StringUtils.isNotEmpty(selectedValue)) {
            return toGeonamesOption(selectedValue);
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object obj) {
        if (obj != null && obj instanceof GeonamesOption) {
            return toString((GeonamesOption) obj);
        } else {
            return null;
        }
    }

    private GeonamesOption toGeonamesOption(String strOption) {
        GeonamesOption geoOption = new GeonamesOption();
        if (StringUtils.isNotEmpty(strOption)) {
            String[] options = strOption.split(Constants.AUTO_COMPLETE_OPTION_SEPARATOR);
            if (options != null && options.length == 6) {
                geoOption.setName(options[0]);
                geoOption.setLatitude(Double.parseDouble(options[1]));
                geoOption.setLongitude(Double.parseDouble(options[2]));
                geoOption.setCountryCode(options[3]);
                geoOption.setCountryName(options[4]);
                geoOption.setBbox(StringUtils.trimToNull(options[5]));
            } else {
                //System.out.println("strOption = " + strOption);
            }
        }
        return geoOption;
    }

    private String toString(GeonamesOption option) {
        StringBuilder sb = new StringBuilder();
        sb.append(option.getName());
        sb.append(Constants.AUTO_COMPLETE_OPTION_SEPARATOR);
        sb.append(option.getLatitude());
        sb.append(Constants.AUTO_COMPLETE_OPTION_SEPARATOR);
        sb.append(option.getLongitude());
        sb.append(Constants.AUTO_COMPLETE_OPTION_SEPARATOR);
        sb.append(option.getCountryCode());
        sb.append(Constants.AUTO_COMPLETE_OPTION_SEPARATOR);
        sb.append(option.getCountryName());
        sb.append(Constants.AUTO_COMPLETE_OPTION_SEPARATOR);
        if (StringUtils.isNotEmpty(option.getBbox())) {
            sb.append(option.getBbox());
        } else {
            sb.append(Constants.NO_BBOX);
        }

        return sb.toString();
    }
}
