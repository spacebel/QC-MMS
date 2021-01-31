package be.spacebel.eoportal.client.business.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.primefaces.extensions.model.fluidgrid.FluidGridItem;

/**
 * This class represents a list of attributes of a search results item
 *
 * @author mng
 */
public class SearchResultProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    private String value;
    private boolean show;
    private List<String> values = new ArrayList<>();
    private List<Map<String, SearchResultProperty>> groups = new ArrayList<>();

    public SearchResultProperty() {
    }

    public SearchResultProperty(String value) {
        this.value = value;
    }

  public SearchResultProperty(List<String> showFields, String field, String value) {
    this(value);
    this.show=false;
    if (StringUtils.isNotEmpty(value)) {
      if (showFields != null) {
        if (showFields.contains(field)) {
          this.show = true;
        }
      }
    }
  }

    @Override
    public String toString() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Map<String, SearchResultProperty>> getGroups() {
        return groups;
    }

    public void setGroups(List<Map<String, SearchResultProperty>> groups) {
        this.groups = groups;
    }

    public List<FluidGridItem> getFluidGridItems(String key) {
        List<FluidGridItem> fluidItems = new ArrayList<>();
        if (this.groups != null) {
            for (Map<String, SearchResultProperty> group : this.groups) {
                SearchResultProperty groupProp = group.get(key);
                if (groupProp != null) {
                    fluidItems.add(new FluidGridItem(groupProp));
                }
            }
        }
        return fluidItems;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

}
