package be.spacebel.eoportal.client.model.data;

import be.spacebel.eoportal.client.business.data.SearchResultItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.extensions.model.fluidgrid.FluidGridItem;

/**
 * This class represents a DataModel which is used to display the search results
 *
 * @author mng
 */
public class DataModel implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<SearchResultItem> items;
    private List<FluidGridItem> fluidItems;
    private String gmlFeatures;

    public DataModel() {
        this.items = new ArrayList<>();
    }

    public DataModel(List<SearchResultItem> items) {
        this.items = items;
        this.fluidItems = new ArrayList<>();
        for (SearchResultItem i : this.items) {
            this.fluidItems.add(new FluidGridItem(i));
        }
    }

    public List<SearchResultItem> getItems() {
        return items;
    }

    public List<FluidGridItem> getFluidItems() {
        return this.fluidItems;
    }

    public void setItems(List<SearchResultItem> items) {
        this.items = items;
    }

    public int getItemCount() {
        if (this.items != null) {
            return this.items.size();
        } else {
            return 0;
        }
    }

    public String getGmlFeatures() {
        return gmlFeatures;
    }

    public void setGmlFeatures(String gmlFeatures) {
        this.gmlFeatures = gmlFeatures;
    }
}
