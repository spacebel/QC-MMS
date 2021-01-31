package be.spacebel.catalog.services;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import be.spacebel.catalog.models.SolrCollection;
import be.spacebel.catalog.utils.Constants;

@Service
public class SearchableSeriesService {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(SearchableSeriesService.class);

    private SolrHandler solrHandler;

    public SearchableSeriesService(@Autowired  SolrHandler solrHandler) {
        this.solrHandler = solrHandler;
    }

    @Cacheable(cacheNames = "searchableSeries", sync = true)
    public Collection<String> getSearchableSeries(){
        log.info("Reloading searchable series");
        Collection<String> searchableSeries = new ArrayList<>();
        for(String serie : this.solrHandler.getList(SolrCollection.DATASET, Constants.PARENT_ID_PARAM)){
            String serieId = serie.split("###")[0];
            String match = serie.split("###")[1];
            if (Integer.parseInt(match) > 0){
                searchableSeries.add(serieId);
            }
        }
        return searchableSeries;
    }
}
