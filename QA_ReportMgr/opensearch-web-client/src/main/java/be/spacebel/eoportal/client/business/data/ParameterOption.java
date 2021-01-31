package be.spacebel.eoportal.client.business.data;

import java.io.Serializable;

/**
 * This class represents an option of OpenSearch parameter extension
 *
 * @author mng
 */
public class ParameterOption implements Serializable {

    private String value;
    private String label;

    public ParameterOption() {
    }

    public ParameterOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
