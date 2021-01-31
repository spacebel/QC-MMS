/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.spacebel.eoportal.client.parser;

import be.spacebel.eoportal.client.business.data.Constants;
import be.spacebel.eoportal.client.business.data.CustomJsonNode;
import be.spacebel.eoportal.client.business.data.SearchResultError;
import be.spacebel.eoportal.client.business.data.SearchResultItem;
import be.spacebel.eoportal.client.business.data.SearchResultProperty;
import be.spacebel.eoportal.client.business.data.SearchResultSet;
import be.spacebel.eoportal.client.model.data.Configuration;
import be.spacebel.eoportal.client.util.HTTPInvoker;
import be.spacebel.eoportal.client.util.Utility;
import be.spacebel.opensearch.model.AcquisitionInformation;
import be.spacebel.opensearch.model.AcquisitionParameters;
import be.spacebel.opensearch.model.Feature;
import be.spacebel.opensearch.model.FeatureCollection;
import be.spacebel.opensearch.model.Instrument;
import be.spacebel.opensearch.model.Link;
import be.spacebel.opensearch.model.Platform;
import be.spacebel.opensearch.model.ProductInformation;
import be.spacebel.opensearch.model.Properties_;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author cnl
 */
public class GeoJSONSearchResultParser implements Serializable {

    private static final Logger log = Logger.getLogger(
            GeoJSONSearchResultParser.class);
    private List<String> showFields;
    private Configuration config;

    private final JsonParser jsonParser;

    public GeoJSONSearchResultParser(List<String> showFields,
            Configuration myConfig) {
        this.showFields = showFields;
        this.config = myConfig;
        this.jsonParser = new JsonParser();
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        String out = new Scanner(new URL(
                "http://qcmms-cat.spacebel.be/eo-catalog/series/EOP:SPB:QA_REPORT_TEST_1:S2/datasets?httpAccept=application/geo%2Bjson&startDate=2017-01-01T00:00:00Z&endDate=2019-01-31T00:00:00Z").openStream(),
                "UTF-8").useDelimiter("\\A").next();
        String out2 = new Scanner(new URL(
                "http://qcmms-cat.spacebel.be/eo-catalog/series/EOP:ESA:SCIHUB:S1/datasets?httpAccept=application%2Fgeo%2Bjson&startRecord=&startPage=&maximumRecords=10&startDate=2019-03-26T00%3A00%3A00Z&endDate=2019-03-26T23%3A59%3A59Z&bbox=&name=&lat=&lon=&radius=&uid=&acquisitionType=&instrument=&platform=&orbitNumber=&productionStatus=&sensorType=RADAR&productType=&polarisationChannels=&cloudCover=&snowCover=&sensorMode=&orbitDirection=").openStream(),
                "UTF-8").useDelimiter("\\A").next();

        out = out2;

        //out = new String(Files.readAllBytes(Paths.get("D:/api.txt")));
        GeoJSONSearchResultParser tester = new GeoJSONSearchResultParser(null, null);
        tester.buildResultSetFromJson(out);
    }

    public SearchResultSet buildResultSetFromJson(String source) {
        log.debug("Building Search Result Set From Geo JSON");
        SearchResultSet set = new SearchResultSet();
        // Parsing to FeatureColllection object
        ObjectMapper mapper = createObjectMapper();

        FeatureCollection fc;
        try {
            fc = mapper.readValue(source, FeatureCollection.class);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(
                    GeoJSONSearchResultParser.class.getName()).log(Level.SEVERE,
                    null, ex);
            log.error("Error occured in buildResultSetFromJson");
            SearchResultError error = new SearchResultError();
            error.setErrorCode("500");
            error.setErrorMessage(ex.getMessage());
            set.setSearchResultError(error);
            return set;
        }

        log.debug("Total results:" + fc.getTotalResults());
        set.getAttributes().put("totalResults", String.valueOf(
                fc.getTotalResults()));
        set.getAttributes().put("osSearchType", "Datasets");
        set.getAttributes().put("noScroller", ",no");

        set.getAttributes().put("itemsPerPage", String.valueOf(
                fc.getItemsPerPage()));

        set.setGmlEnveloppe(buildGmlEnveloppe());

        if (fc.getProperties().getLinks() != null) {

            if (fc.getProperties().getLinks().getFirst() != null) {
                log.debug(
                        "First link: " + fc.getProperties().getLinks().getFirst().get(0).getHref());
                set.getAttributes().put(Constants.OS_FIRSTPAGE,
                        fc.getProperties().getLinks().getFirst().get(0).getHref());
            }
            if (fc.getProperties().getLinks().getPrevious() != null) {
                set.getAttributes().put(Constants.OS_PREVIOUSPAGE,
                        fc.getProperties().getLinks().getPrevious().get(0).getHref());
            }
            if (fc.getProperties().getLinks().getNext() != null) {
                set.getAttributes().put(Constants.OS_NEXTPAGE,
                        fc.getProperties().getLinks().getNext().get(0).getHref());
            }
            if (fc.getProperties().getLinks().getLast() != null) {
                set.getAttributes().put(Constants.OS_LASTPAGE,
                        fc.getProperties().getLinks().getLast().get(0).getHref());
            }
            if (fc.getProperties().getLinks().getSearch() != null) {
                for (Link searchLink : fc.getProperties().getLinks().getSearch()) {
                    if (StringUtils.isNotEmpty(searchLink.getHref())
                            && searchLink.getType() != null
                            && "application/opensearchdescription+xml".equalsIgnoreCase(searchLink.getType())) {
                        set.getAttributes().put(Constants.OS_OSDD_URL, searchLink.getHref());
                        break;
                    }
                }
            }
        }

        for (Feature feature : fc.getFeatures()) {
            SearchResultItem searchResultItem = buildFeatureResult(mapper, feature);

            log.debug("Adding search result item");
//            String hasFootPrint = Utility.getPropertyValue(searchResultItem,
//                    "hasFootPrint");
//            if (StringUtils.isNotEmpty(hasFootPrint) && "YES".equals(hasFootPrint)) {
//                searchResultItem.setHasFootprint(true);
//            }
            String imageUrl = "";
            String[] previewImagePropNames = this.config.getPreviewImageOrder().split(
                    ",");
            for (String propName : previewImagePropNames) {
                imageUrl = Utility.getPropertyValue(searchResultItem, propName);
                if (StringUtils.isNotEmpty(imageUrl)) {
                    imageUrl = Utility.validateUrl(imageUrl);
                    searchResultItem.setPreviewUrl(imageUrl);

                    log.debug("Has thumbnail image: " + imageUrl);

                    searchResultItem.setThumbnailDivId(
                            "thumbnailImage" + searchResultItem.getUuid());
                    searchResultItem.setThumbnailDivStyle(
                            "background-image: url(" + "\"" + Utility.escapeSingleQuote(imageUrl) + "\");");
                    searchResultItem.setThumbnailDivClass("fluidgrid-cell-image");

                    break;
                }
            }
            searchResultItem.setThumbnailCellClass("fluidgrid-cell-info");
            if (StringUtils.isEmpty(imageUrl)) {
                int thumbnailService = 0;

                if ("custom".equalsIgnoreCase(
                        this.config.getDataset().getThumbnailService())) {
                    thumbnailService = 2;
                }

                if (StringUtils.isNotEmpty(this.config.getStaticMapServiceUrl())
                        && StringUtils.isNotEmpty(this.config.getStaticMapServiceUniqueKey())) {
                    if ("staticMap".equalsIgnoreCase(
                            this.config.getDataset().getThumbnailService())
                            || "staticMapOnly".equalsIgnoreCase(
                                    this.config.getDataset().getThumbnailService())) {
                        thumbnailService = 1;
                    }
                }

                switch (thumbnailService) {
                    case 0:
                        // not use thumbnail map service
                        log.debug("No thumbnail case.");
                        searchResultItem.setThumbnailDivId(
                                "noImage" + searchResultItem.getUuid());
                        searchResultItem.setThumbnailDivStyle("background: #d3d3d3;");
                        searchResultItem.setThumbnailDivClass("fluidgrid-cell-image");
                        break;
                    case 1:
                        // use mapQuest static map service
                        log.debug("Static map thumbnail case.");
                        imageUrl = Utility.getStaticImageUrl(searchResultItem,
                                this.config.getStaticMapServiceUrl(),
                                this.config.getStaticMapServiceUniqueKey());
                        imageUrl = Utility.validateUrl(imageUrl);

                        searchResultItem.setPreviewUrl(imageUrl);
                        searchResultItem.setThumbnailDivId(
                                "staticMap" + searchResultItem.getUuid());
                        searchResultItem.setThumbnailDivStyle(
                                "background-image: url(" + "\"" + Utility.escapeSingleQuote(imageUrl) + "\");");
                        searchResultItem.setThumbnailDivClass("fluidgrid-cell-image");
                        break;
                    case 2:
                        // use custom wms map service
                        log.debug("Custom WMS map thumbnail case.");
                        searchResultItem.setPreviewUrl("CUSTOMQUICKLOOKMAP");
                        searchResultItem.setThumbnailDivId(
                                "quicklookMap" + searchResultItem.getUuid());
                        searchResultItem.setThumbnailDivStyle("");
                        searchResultItem.setThumbnailCellClass(
                                "fluidgrid-cell-info-wms-custom-map");

                        searchResultItem.setThumbnailDivClass(
                                "fluidgrid-cell-wms-custom-map-quicklook");
                        break;
                }
            } else {
                boolean useStaticMap = false;

                if (StringUtils.isNotEmpty(this.config.getStaticMapServiceUrl())
                        && StringUtils.isNotEmpty(this.config.getStaticMapServiceUniqueKey())) {
                    if (StringUtils.isNotEmpty(this.config.getDataset().getThumbnailService())
                            && "staticMapOnly".equalsIgnoreCase(
                                    this.config.getDataset().getThumbnailService())) {
                        useStaticMap = true;
                    }
                }

                if (useStaticMap) {
                    // use mapQuest static map service
                    log.debug("Use only static map thumbnail case.");
                    imageUrl = Utility.getStaticImageUrl(searchResultItem,
                            this.config.getStaticMapServiceUrl(),
                            this.config.getStaticMapServiceUniqueKey());
                    imageUrl = Utility.validateUrl(imageUrl);

                    searchResultItem.setPreviewUrl(imageUrl);
                    searchResultItem.setThumbnailDivId(
                            "staticMap" + searchResultItem.getUuid());
                    searchResultItem.setThumbnailDivStyle(
                            "background-image: url(" + "\"" + Utility.escapeSingleQuote(imageUrl) + "\");");

                    searchResultItem.setThumbnailDivClass("fluidgrid-cell-image");
                }
            }
            set.addItem(searchResultItem);

        }
        //set.getAttributes().put("nextPage",featureCollection.getTotalResults());
        //JsonParser parser = new JsonParser();
        //JsonElement parsed = parser.parse(source);

        return set;
    }

    public List<String> getShowFields() {
        return showFields;
    }

    public void setShowFields(List<String> showFields) {
        this.showFields = showFields;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    private SearchResultItem buildFeatureResult(ObjectMapper mapper, Feature feature) {
        SearchResultItem searchResultItem = new SearchResultItem(Constants.DATASET_SEARCH);
        Properties_ props = feature.getProperties();
        // log.debug(searchResultItem.getUuid());
        String productId = null;
        String parentId = null;
        if (props.getIdentifier() != null) {
            productId = props.getIdentifier();
            searchResultItem.setProductId(productId);
            searchResultItem.setShowProductId(showField("identifier", productId));
        }

        if (props.getParentIdentifier() != null) {
            searchResultItem.addProperty("parentId", new SearchResultProperty(
                    this.showFields, "parentId",
                    props.getParentIdentifier()));
            parentId = props.getParentIdentifier();
            searchResultItem.setParentId(parentId);
            searchResultItem.setShowParentId(showField("parentId", parentId));
            log.debug("ParentId:" + parentId);
        }

        if (props.getCollection() != null) {
            searchResultItem.addProperty("collection", new SearchResultProperty(
                    this.showFields, "collection",
                    props.getCollection()));
        }

        if (feature.getId() != null) {
            if (config.getDataset().getListMoreOptions() != null
                    && config.getDataset().getListMoreOptions().contains("id")) {
                searchResultItem.addProperty("productIdLink",
                        new SearchResultProperty(feature.getId()));
            }
        }

        /*
        searchResultItem.addProperty("hasEOPMetadata",
                new SearchResultProperty(
                        "YES"));
         */
        // set this property to "true" if the metadata contains both properties of collections and products
        searchResultItem.setEoProductAndCollection(true);

        searchResultItem.addProperty("presentImageInOrder",
                new SearchResultProperty(
                        "mediaQuicklook,image,mediaThumbnail,preview,icon"));
        searchResultItem.addProperty("osSearchType", new SearchResultProperty(Constants.DATASET_SEARCH));

        try {
            if (feature.getBbox() != null
                    && feature.getBbox().size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (BigDecimal b : feature.getBbox()) {
                    sb.append(b).append(" ");
                }

                String bbox = StringUtils.trimToNull(sb.toString());
                if (StringUtils.isNotEmpty(bbox)) {
                    searchResultItem.addProperty("georssBox", new SearchResultProperty(bbox));
                }
            }

            if (feature.getGeometry() != null) {

                String geometry = mapper.writeValueAsString(feature.getGeometry());
                log.debug("geometry = " + geometry);
                getGeometry(searchResultItem, geometry);
            }

            if (props.any().get("categories") != null) {
                String categories = mapper.writeValueAsString(props.any().get("categories"));
                if (StringUtils.isNotEmpty(categories)) {
                    getCategories(searchResultItem, categories);
                }
            }

            if (props.any().get("qualifiedAttribution") != null) {
                String contacts = mapper.writeValueAsString(props.any().get("qualifiedAttribution"));
                if (StringUtils.isNotEmpty(contacts)) {
                    getContact(searchResultItem, contacts);
                }
            }

            if (props.getAdditionalAttributes() != null) {
//                String addAttrs = mapper.writeValueAsString(props.getAdditionalAttributes());
//                parseAdditionalAttributes(searchResultItem, addAttrs);
                searchResultItem.setAdditionalAttributes(jsonToHtml(props.getAdditionalAttributes(), 0, false));
            }
        } catch (JsonProcessingException jEx) {
            log.debug("Error while writing json object to String " + jEx);
        }

        if (props.getAbstract() != null) {
            searchResultItem.addProperty("abstract", new SearchResultProperty(
                    this.showFields, "abstract",
                    props.getAbstract()));

        }

        if (props.getTitle() != null) {
            searchResultItem.addProperty("title", new SearchResultProperty(
                    this.showFields, "title",
                    props.getTitle()));

        }
        if (props.getDate() != null) {
            searchResultItem.addProperty("startDate", new SearchResultProperty(
                    this.showFields, "startDate", StringUtils.substringBefore(
                            props.getDate(), "/")));

            searchResultItem.addProperty("endDate", new SearchResultProperty(
                    this.showFields, "endDate",
                    StringUtils.substringAfter(
                            props.getDate(), "/")));

        }
        if (props.getUpdated() != null) {
            searchResultItem.addProperty("updatedDate", new SearchResultProperty(
                    this.showFields, "updatedDate", props.getUpdated().format(
                            DateTimeFormatter.ISO_DATE)));

        }
        if (props.getStatus() != null) {
            searchResultItem.addProperty("status", new SearchResultProperty(
                    this.showFields, "status", props.getStatus()));
        }
        if (props.getDoi() != null) {
            searchResultItem.addProperty("doi", new SearchResultProperty(
                    this.showFields, "doi", props.getDoi()));
        }
        if (props.getKind() != null) {
            searchResultItem.addProperty("productKind", new SearchResultProperty(
                    this.showFields, "productKind", props.getKind()));
        }

        // Platform
        List<AcquisitionInformation> acq = props.getAcquisitionInformation();
        log.debug("Acquisition Information is available");
        if (acq != null && acq.get(0) != null && acq.get(0).getPlatform() != null) {
            log.debug("Platform information found");

            SearchResultProperty group = searchResultItem
                    .getProperties()
                    .get("eopPlatform");
            if (group == null) {
                group = new SearchResultProperty();
                searchResultItem.getProperties().put("eopPlatform", group);
            }
            Map<String, SearchResultProperty> groupMap = new HashMap<>();
            group.getGroups().add(groupMap);

            Platform platform = acq.get(0).getPlatform();
            if (platform.getPlatformShortName() != null) {
                groupMap.put("shortName", new SearchResultProperty(platform.getPlatformShortName()));
                searchResultItem.addProperty("soShortName", new SearchResultProperty(
                        this.showFields, "soShortName",
                        platform.getPlatformShortName()));
            }
            if (platform.getPlatformSerialIdentifier() != null) {
                groupMap.put("serialIdentifier", new SearchResultProperty(platform.getPlatformSerialIdentifier()));
                searchResultItem.addProperty("soSerialIdentifier",
                        new SearchResultProperty(this.showFields, "soSerialIdentifier",
                                platform.getPlatformSerialIdentifier()));
            }
            if (platform.getOrbitType() != null) {
                groupMap.put("orbitType", new SearchResultProperty(platform.getOrbitType()));
                searchResultItem.addProperty("soOrbitType", new SearchResultProperty(
                        this.showFields, "soOrbittype",
                        platform.getOrbitType()));
            }
        }
        // instru
        if (acq != null && acq.get(0) != null && acq.get(0).getInstrument() != null) {
            log.debug("Instrument part found");
            SearchResultProperty group = searchResultItem
                    .getProperties()
                    .get("instrument");
            if (group == null) {
                group = new SearchResultProperty();
                searchResultItem.getProperties().put("instrument", group);
            }
            Map<String, SearchResultProperty> groupMap = new HashMap<>();
            group.getGroups().add(groupMap);

            Instrument instru = acq.get(0).getInstrument();
            if (instru.getInstrumentShortName() != null) {
                groupMap.put("shortName", new SearchResultProperty(instru.getInstrumentShortName()));
                searchResultItem.addProperty("soInstrumentShortName",
                        new SearchResultProperty(this.showFields,
                                "soInstrumentShortName",
                                instru.getInstrumentShortName()));
            }
            if (instru.getSensorType() != null) {
                groupMap.put("sensorType", new SearchResultProperty(instru.getSensorType()));
                searchResultItem.addProperty("sensorType",
                        new SearchResultProperty(this.showFields, "sensorType",
                                instru.getSensorType()));

            }
            if (instru.getDescription() != null) {
                groupMap.put("description", new SearchResultProperty(instru.getDescription()));
            }
            if (instru.getType() != null) {
                groupMap.put("type", new SearchResultProperty(instru.getType()));
            }
        }
        // acquisition parameters
        if (acq != null && acq.get(0) != null && acq.get(0).getAcquisitionParameters() != null) {
            log.debug("Acquisition Parameters parsing");
            AcquisitionParameters acqparams = acq.get(0).getAcquisitionParameters();
            boolean hasParam = false;
            if (acqparams.getAcquisitionAngles() != null) {
                if (acqparams.getAcquisitionAngles().getAcrossTrackIncidenceAngle() != null) {
                    searchResultItem.addProperty("acrossTrackIncidenceAngle", new SearchResultProperty(
                            this.showFields, "acrossTrackIncidenceAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getAcrossTrackIncidenceAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getAlongTrackIncidenceAngle() != null) {
                    searchResultItem.addProperty("alongTrackIncidenceAngle", new SearchResultProperty(
                            this.showFields, "alongTrackIncidenceAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getAlongTrackIncidenceAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getIlluminationAzimuthAngle() != null) {
                    searchResultItem.addProperty("illuminationAzimuthAngle", new SearchResultProperty(
                            this.showFields, "illuminationAzimuthAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getIlluminationAzimuthAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getIlluminationElevationAngle() != null) {
                    searchResultItem.addProperty("illuminationElevationAngle", new SearchResultProperty(
                            this.showFields, "illuminationElevationAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getIlluminationElevationAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getIlluminationZenithAngle() != null) {
                    searchResultItem.addProperty("instrumentZenithAngle", new SearchResultProperty(
                            this.showFields, "instrumentZenithAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getIlluminationZenithAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getIncidenceAngle() != null) {
                    searchResultItem.addProperty("incidenceAngle", new SearchResultProperty(
                            this.showFields, "incidenceAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getIncidenceAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getIncidenceAngleVariation() != null) {
                    searchResultItem.addProperty("incidenceAngleVariation", new SearchResultProperty(
                            this.showFields, "incidenceAngleVariation",
                            String.valueOf(acqparams.getAcquisitionAngles().getIncidenceAngleVariation())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getInstrumentAzimuthAngle() != null) {
                    searchResultItem.addProperty("instrumentAzimuthAngle", new SearchResultProperty(
                            this.showFields, "instrumentAzimuthAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getInstrumentAzimuthAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getInstrumentElevationAngle() != null) {
                    searchResultItem.addProperty("instrumentElevationAngle", new SearchResultProperty(
                            this.showFields, "instrumentElevationAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getInstrumentElevationAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getInstrumentZenithAngle() != null) {
                    searchResultItem.addProperty("instrumentZenithAngle", new SearchResultProperty(
                            this.showFields, "instrumentZenithAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getInstrumentZenithAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getMaximumIncidenceAngle() != null) {
                    searchResultItem.addProperty("maximumIncidenceAngle", new SearchResultProperty(
                            this.showFields, "maximumIncidenceAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getMaximumIncidenceAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getMinimumIncidenceAngle() != null) {
                    searchResultItem.addProperty("minimumIncidenceAngle", new SearchResultProperty(
                            this.showFields, "minimumIncidenceAngle",
                            String.valueOf(acqparams.getAcquisitionAngles().getMinimumIncidenceAngle())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getPitch() != null) {
                    searchResultItem.addProperty("pitch", new SearchResultProperty(
                            this.showFields, "pitch",
                            String.valueOf(acqparams.getAcquisitionAngles().getPitch())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getRoll() != null) {
                    searchResultItem.addProperty("roll", new SearchResultProperty(
                            this.showFields, "roll",
                            String.valueOf(acqparams.getAcquisitionAngles().getRoll())));
                    hasParam = true;
                }
                if (acqparams.getAcquisitionAngles().getYaw() != null) {
                    searchResultItem.addProperty("yaw", new SearchResultProperty(
                            this.showFields, "yaw",
                            String.valueOf(acqparams.getAcquisitionAngles().getYaw())));
                    hasParam = true;
                }
            }

            if (acqparams.getAcquisitionStation() != null) {
                searchResultItem.addProperty("acquisitionStation", new SearchResultProperty(
                        this.showFields, "acquisitionStation",
                        acqparams.getAcquisitionStation()));
                hasParam = true;
            }
            if (acqparams.getAcquisitionType() != null) {
                searchResultItem.addProperty("acquisitionType", new SearchResultProperty(
                        this.showFields, "acquisitionType",
                        acqparams.getAcquisitionType()));
                hasParam = true;
            }
            if (acqparams.getAcquisitionSubType() != null) {
                searchResultItem.addProperty("acquisitionSubType", new SearchResultProperty(
                        this.showFields, "acquisitionSubType",
                        acqparams.getAcquisitionSubType()));
                hasParam = true;
            }
            if (acqparams.getAntennaLookDirection() != null) {
                searchResultItem.addProperty("antennaLookDirection", new SearchResultProperty(
                        this.showFields, "antennaLookDirection",
                        acqparams.getAntennaLookDirection()));
                hasParam = true;
            }
            if (acqparams.getAscendingNodeDate() != null) {
                searchResultItem.addProperty("ascendingNodeDate",
                        new SearchResultProperty(this.showFields, "ascendingNodeDate",
                                acqparams.getAscendingNodeDate().format(
                                        DateTimeFormatter.ISO_DATE)));
                hasParam = true;
            }
            if (acqparams.getAscendingNodeLongitude() != null) {
                searchResultItem.addProperty("ascendingNodeLongitude",
                        new SearchResultProperty(this.showFields,
                                "ascendingNodeLongitude",
                                acqparams.getAscendingNodeLongitude().toString()));
                hasParam = true;
            }
            if (acqparams.getCompletionTimeFromAscendingNode() != null) {
                searchResultItem.addProperty("completionTimeFromAscendingNode",
                        new SearchResultProperty(this.showFields,
                                "completionTimeFromAscendingNode",
                                acqparams.getCompletionTimeFromAscendingNode().toString()));
                hasParam = true;
            }
            if (acqparams.getCycleNumber() != null) {
                searchResultItem.addProperty("cycleNumber", new SearchResultProperty(
                        this.showFields, "cycleNumber",
                        String.valueOf(acqparams.getCycleNumber())));
                hasParam = true;
            }
            if (acqparams.getGroundTrackUncertainty() != null) {
                searchResultItem.addProperty("groundTrackUncertainty", new SearchResultProperty(
                        this.showFields, "groundTrackUncertainty",
                        String.valueOf(acqparams.getGroundTrackUncertainty())));
                hasParam = true;
            }
            if (acqparams.getHighestLocation() != null) {
                searchResultItem.addProperty("highestLocation", new SearchResultProperty(
                        this.showFields, "highestLocation",
                        String.valueOf(acqparams.getHighestLocation())));
                hasParam = true;
            }
            if (acqparams.getLastOrbitDirection() != null) {
                searchResultItem.addProperty("lastOrbitDirection", new SearchResultProperty(
                        this.showFields, "lastOrbitDirection",
                        String.valueOf(acqparams.getLastOrbitDirection())));
                hasParam = true;
            }
            if (acqparams.getLastOrbitNumber() != null) {
                searchResultItem.addProperty("lastOrbitNumber",
                        new SearchResultProperty(this.showFields, "lastOrbitNumber",
                                String.valueOf(acqparams.getLastOrbitNumber())));
                hasParam = true;
            }
            if (acqparams.getLocationUnit() != null) {
                searchResultItem.addProperty("locationUnit",
                        new SearchResultProperty(this.showFields, "locationUnit",
                                String.valueOf(acqparams.getLocationUnit())));
                hasParam = true;
            }
            if (acqparams.getLowestLocation() != null) {
                searchResultItem.addProperty("lowestLocation",
                        new SearchResultProperty(this.showFields, "lowestLocation",
                                String.valueOf(acqparams.getLowestLocation())));
                hasParam = true;
            }
            if (acqparams.getOperationalMode() != null) {
                searchResultItem.addProperty("operationalMode",
                        new SearchResultProperty(this.showFields, "operationalMode",
                                String.valueOf(acqparams.getOperationalMode())));
                hasParam = true;
            }
            if (acqparams.getOrbitDirection() != null) {
                searchResultItem.addProperty("orbitDirection",
                        new SearchResultProperty(this.showFields, "orbitDirection",
                                acqparams.getOrbitDirection()));
                hasParam = true;
            }
            if (acqparams.getOrbitDuration() != null) {
                searchResultItem.addProperty("orbitDuration",
                        new SearchResultProperty(this.showFields, "orbitDuration",
                                acqparams.getOrbitDuration().toString()));
                hasParam = true;
            }
            if (acqparams.getOrbitNumber() != null) {
                searchResultItem.addProperty("orbitNumber", new SearchResultProperty(
                        this.showFields, "orbitNumber",
                        String.valueOf(acqparams.getOrbitNumber())));
                hasParam = true;
            }
            if (acqparams.getRelativeOrbitNumber() != null) {
                searchResultItem.addProperty("relativeOrbitNumber", new SearchResultProperty(
                        this.showFields, "relativeOrbitNumber",
                        String.valueOf(acqparams.getRelativeOrbitNumber())));
                hasParam = true;
            }
            if (acqparams.getStartTimeFromAscendingNode() != null) {
                searchResultItem.addProperty("startTimeFromAscendingNode",
                        new SearchResultProperty(this.showFields,
                                "startTimeFromAscendingNode",
                                acqparams.getStartTimeFromAscendingNode().toString()));
                hasParam = true;
            }
            if (acqparams.getTileId() != null) {
                searchResultItem.addProperty("tileId", new SearchResultProperty(
                        this.showFields, "tileId",
                        String.valueOf(acqparams.getTileId())));
                hasParam = true;
            }
            if (acqparams.getWrsLongitude() != null) {
                searchResultItem.addProperty("wrsLongitudeGrid",
                        new SearchResultProperty(this.showFields, "wrsLongitudeGrid",
                                acqparams.getWrsLongitude()));
                hasParam = true;
            }
            if (acqparams.getWrsLatitude() != null) {
                searchResultItem.addProperty("wrsLatitudeGrid",
                        new SearchResultProperty(this.showFields, "wrsLatitudeGrid",
                                acqparams.getWrsLatitude()));
                hasParam = true;
            }
            if (acqparams.getBeginningDateTime() != null) {
                searchResultItem.addProperty("beginningDateTime",
                        new SearchResultProperty(this.showFields, "beginningDateTime",
                                String.valueOf(acqparams.getBeginningDateTime())));
                hasParam = true;
            }
            if (acqparams.getEndingDateTime() != null) {
                searchResultItem.addProperty("endingDateTime",
                        new SearchResultProperty(this.showFields, "endingDateTime",
                                String.valueOf(acqparams.getEndingDateTime())));
                hasParam = true;
            }

            searchResultItem.setHasAcquisitionParameter(hasParam);
        }
        // product information
        if (props.getProductInformation() != null) {
            ProductInformation info = props.getProductInformation();
            if (info.getArchivingCenter() != null) {
                searchResultItem.addProperty("piArchivingCenter",
                        new SearchResultProperty(info.getArchivingCenter()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getArchivingDate() != null) {
                searchResultItem.addProperty("piArchivingDate",
                        new SearchResultProperty(info.getArchivingDate().toString()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getAvailabilityTime() != null) {
                searchResultItem.addProperty("piAvailabilityTime",
                        new SearchResultProperty(info.getAvailabilityTime().toString()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getCloudCover() != null) {
                searchResultItem.addProperty("piCloudCover",
                        new SearchResultProperty(String.valueOf(info.getCloudCover())));
                searchResultItem.setHasProductInfo(true);

                searchResultItem.addProperty("cloudCoverPercentage",
                        new SearchResultProperty(this.showFields, "cloudCoverPercentage",
                                info.getCloudCover().toString()));
            }
            if (info.getCompositeType() != null) {
                searchResultItem.addProperty("piCompositeType",
                        new SearchResultProperty(String.valueOf(info.getCompositeType())));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getFormat() != null) {
                searchResultItem.addProperty("piFormat",
                        new SearchResultProperty(String.valueOf(info.getFormat())));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProcessingCenter() != null) {
                searchResultItem.addProperty("piProcessingCenter",
                        new SearchResultProperty(String.valueOf(info.getProcessingCenter())));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProcessingDate() != null) {
                searchResultItem.addProperty("piProcessingDate",
                        new SearchResultProperty(info.getProcessingDate().toString()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProcessingLevel() != null) {
                searchResultItem.addProperty("piProcessingLevel",
                        new SearchResultProperty(info.getProcessingLevel()));
                searchResultItem.setHasProductInfo(true);

                searchResultItem.addProperty("level",
                        new SearchResultProperty(this.showFields, "level",
                                info.getProcessingLevel()));
            }
            if (info.getProcessingMethod() != null) {
                searchResultItem.addProperty("piProcessingMethod",
                        new SearchResultProperty(info.getProcessingMethod()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProcessingMethodVersion() != null) {
                searchResultItem.addProperty("piProcessingMethodVersion",
                        new SearchResultProperty(info.getProcessingMethodVersion()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProcessingMode() != null) {
                searchResultItem.addProperty("piProcessingMode",
                        new SearchResultProperty(info.getProcessingMode()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProcessorName() != null) {
                searchResultItem.addProperty("piProcessorName",
                        new SearchResultProperty(info.getProcessorName()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProcessorVersion() != null) {
                searchResultItem.addProperty("piProcessorVersion",
                        new SearchResultProperty(info.getProcessorVersion()));
                searchResultItem.setHasProductInfo(true);

                searchResultItem.addProperty("processorVersion",
                        new SearchResultProperty(this.showFields, "processorVersion",
                                info.getProcessorVersion()));
            }
            if (info.getProductContentsType() != null) {
                searchResultItem.addProperty("piProductContentsType",
                        new SearchResultProperty(info.getProductContentsType()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProductGroupId() != null) {
                searchResultItem.addProperty("piProductGroupId",
                        new SearchResultProperty(String.valueOf(info.getProductGroupId())));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProductType() != null) {
                searchResultItem.addProperty("productType",
                        new SearchResultProperty(this.showFields, "productType",
                                info.getProductType()));
                searchResultItem.addProperty("piProductType",
                        new SearchResultProperty(info.getProductType()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getProductVersion() != null) {
                searchResultItem.addProperty("piProductVersion",
                        new SearchResultProperty(info.getProductVersion()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getQualityInformation() != null) {
                if (info.getQualityInformation().getQualityDegradation() != null) {
                    searchResultItem.addProperty("piQualityDegradation",
                            new SearchResultProperty(String.valueOf(info.getQualityInformation().getQualityDegradation())));
                    searchResultItem.setHasProductInfo(true);
                }
                if (info.getQualityInformation().getQualityDegradationQuotationMode() != null) {
                    searchResultItem.addProperty("piQualityDegradationQuotationMode",
                            new SearchResultProperty(String.valueOf(info.getQualityInformation().getQualityDegradationQuotationMode())));
                    searchResultItem.setHasProductInfo(true);
                }
                if (info.getQualityInformation().getQualityDegradationTag() != null) {
                    searchResultItem.addProperty("piQualityDegradationTag",
                            new SearchResultProperty(String.valueOf(info.getQualityInformation().getQualityDegradationTag())));
                    searchResultItem.setHasProductInfo(true);
                }
                if (info.getQualityInformation().getQualityStatus() != null) {
                    searchResultItem.addProperty("piQualityStatus",
                            new SearchResultProperty(String.valueOf(info.getQualityInformation().getQualityStatus())));
                    searchResultItem.setHasProductInfo(true);
                }
                if (info.getQualityInformation().getQualityIndicators() != null) {
                    searchResultItem.setQualityIndicators(info.getQualityInformation().getQualityIndicators());
//                    for (QualityIndicator qli : info.getQualityInformation().getQualityIndicators()) {
//                        if (qli.any() != null) {
//                            for (Map.Entry<String, Object> entry : qli.any().entrySet()) {
//                                String key = entry.getKey();
//                                Object value = entry.getValue();
//                                System.out.println(key + ": " + value);
//                            }
//                        }
//                    }
                }

            }
            if (info.getReferenceSystemIdentifier() != null) {
                searchResultItem.addProperty("piReferenceSystemIdentifier",
                        new SearchResultProperty(info.getReferenceSystemIdentifier()));
                searchResultItem.setHasProductInfo(true);
                searchResultItem.addProperty("productReferenceSystemIdentifier",
                        new SearchResultProperty(this.showFields,
                                "productReferenceSystemIdentifier",
                                info.getReferenceSystemIdentifier()));

            }
            if (info.getSize() != null) {
                searchResultItem.addProperty("piSize",
                        new SearchResultProperty(String.valueOf(info.getSize())));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getSnowCover() != null) {
                searchResultItem.addProperty("piSnowCover",
                        new SearchResultProperty(String.valueOf(info.getSnowCover())));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getStatusDetail() != null) {
                searchResultItem.addProperty("piStatusDetail",
                        new SearchResultProperty(info.getStatusDetail()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getStatusSubType() != null) {
                searchResultItem.addProperty("piStatusSubType",
                        new SearchResultProperty(info.getStatusSubType()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getTimeliness() != null) {
                searchResultItem.addProperty("piTimeliness",
                        new SearchResultProperty(info.getTimeliness()));
                searchResultItem.setHasProductInfo(true);
            }
            if (info.getType() != null) {
                searchResultItem.addProperty("piType",
                        new SearchResultProperty(info.getType()));
                searchResultItem.setHasProductInfo(true);
            }
        }

        if (props.getLinks() != null) {
            log.debug("Links");

            // preview
            if (props.getLinks().getPreviews() != null) {
                log.debug("preview");
                String href = props.getLinks().getPreviews().get(0).getHref();
                // According to os-response.xsl this is the icon property
                searchResultItem.addProperty("icon",
                        new SearchResultProperty(this.showFields, "image",
                                href));
                // According to eop-om-10-metadata.xsl, this is the image property (i believe this one is useless)
                searchResultItem.addProperty("image",
                        new SearchResultProperty(this.showFields, "image",
                                href));

            }

            if (props.getLinks().getData() != null) {
                log.debug("Found link data");
                // According to eop-om-metadata.xsl this is productFileUrl
                String href = props.getLinks().getData().get(0).getHref();
                searchResultItem.addProperty("productFileUrl",
                        new SearchResultProperty(this.showFields, "productFileUrl",
                                href));
                // According to os-result.xsl
                searchResultItem.addProperty("onlineResource",
                        new SearchResultProperty(this.showFields, "onlineResource",
                                href));
            }
            // Todo check the mime type
            if (props.getLinks().getSearch() != null
                    && props.getLinks().getSearch().size() > 0
                    && props.getLinks().getSearch().get(0).getHref() != null) {
                log.debug(
                        "Found searchDatasetURL " + props.getLinks().getSearch().get(0).getHref());
                searchResultItem.addProperty("searchDatasetURL",
                        new SearchResultProperty(
                                props.getLinks().getSearch().get(0).getHref()));
                searchResultItem.getProperties().get("searchDatasetURL").setShow(true);
            }

            if (props.getLinks().getAlternates() != null) {
                log.debug("found alternates");
                for (Link alternate : props.getLinks().getAlternates()) {
                    log.debug("Found alternate links ");
                    if (alternate.getHref() != null) {
                        SearchResultProperty group = searchResultItem.getProperties()
                                .get("alternateLinks");
                        if (group == null) {
                            group = new SearchResultProperty();
                            searchResultItem.getProperties().put("alternateLinks", group);
                            log.debug("adding alternate links");
                        }
                        Map<String, SearchResultProperty> groupMap = new HashMap<>();
                        groupMap.put("menuLabel",
                                new SearchResultProperty("Metadata download"));
                        groupMap.put("menuIcon",
                                new SearchResultProperty("fa fa-fw fa-save"));
                        groupMap.put("link", new SearchResultProperty(alternate.getHref()));
                        groupMap.put("type", new SearchResultProperty(alternate.getType()));
                        groupMap.put("title", new SearchResultProperty(
                                alternate.getTitle()));
                        switch (alternate.getType()) {
                            case "application/atom+xml":
                                groupMap.put("label", new SearchResultProperty("Atom"));
                                break;
                            case "text/html":
                                groupMap.put("label", new SearchResultProperty("HTML"));
                                break;
                            case "application/rss+xml":
                                groupMap.put("label", new SearchResultProperty("RSS"));
                                break;
                            case "application/geo+json":
                                groupMap.put("label", new SearchResultProperty("JSON"));
                                break;
                            case "Atom":
                                groupMap.put("label", new SearchResultProperty(""));
                                break;
                            case "application/vnd.iso.19157-2":
                                groupMap.put("label", new SearchResultProperty(alternate.getType()));
                                if (StringUtils.isEmpty(searchResultItem.getIsoReportLink())) {
                                    searchResultItem.setIsoReportLink(alternate.getHref());
                                }
                                break;
                            default:
                                //System.out.println("Case default ");
                                groupMap.put("label", new SearchResultProperty(alternate.getType()));
                                break;
                        }
                        log.debug(
                                "Found alternate entry :" + alternate.getHref() + " " + alternate.getType());
                        //groupMap.put("label", new SearchResultProperty());
                        group.getGroups().add(groupMap);
                        group.setShow(showField("alternateLinks",
                                groupMap.size() > 0 ? "has child" : ""));
                    }
                }
            }

            if (props.getLinks().getVia() != null) {
                log.debug("found via");
                for (Link via : props.getLinks().getVia()) {
                    log.debug("Found via links ");
                    if (via.getHref() != null) {
                        SearchResultProperty group = searchResultItem.getProperties().get(
                                "viaLinks");
                        if (group == null) {
                            group = new SearchResultProperty();
                            searchResultItem.getProperties().put("viaLinks", group);
                            log.debug("adding viaLinks ");
                        }
                        Map<String, SearchResultProperty> groupMap = new HashMap<>();
                        groupMap.put("menuLabel",
                                new SearchResultProperty("Metadata download"));
                        groupMap.put("menuIcon",
                                new SearchResultProperty("fa fa-fw fa-save"));
                        groupMap.put("link", new SearchResultProperty(via.getHref()));
                        groupMap.put("type", new SearchResultProperty(via.getType()));
                        groupMap.put("title", new SearchResultProperty(
                                via.getTitle()));
                        switch (via.getType()) {
                            case "application/atom+xml":
                                groupMap.put("label", new SearchResultProperty("Atom"));
                                break;
                            case "text/html":
                                groupMap.put("label", new SearchResultProperty("HTML"));
                                break;
                            case "application/rss+xml":
                                groupMap.put("label", new SearchResultProperty("RSS"));
                                break;
                            case "application/geo+json":
                                groupMap.put("label", new SearchResultProperty("JSON"));
                                if (searchResultItem.getViaLinks() == null) {
                                    searchResultItem.setViaLinks(new ArrayList<>());
                                }
                                searchResultItem.getViaLinks().add(via.getHref());

                                if (via.getTitle() != null
                                        && "Input data".equalsIgnoreCase(via.getTitle())
                                        && StringUtils.isEmpty(searchResultItem.getRelatedProductsLink())) {
                                    log.debug("Related products link: " + via.getHref());
                                    searchResultItem.setRelatedProductsLink(via.getHref());
                                }
                                break;
                            case "Atom":
                                groupMap.put("label", new SearchResultProperty(""));
                                break;
                        }
                        log.debug(
                                "Found via entry :" + via.getHref() + " " + via.getType());
                        //groupMap.put("label", new SearchResultProperty());
                        group.getGroups().add(groupMap);
                        group.setShow(showField("viaLinks",
                                groupMap.size() > 0 ? "has child" : ""));
                    }
                }
            }

            if (props.getLinks().getQualityReport() != null) {
                log.debug("found quality report");
                for (Link qReport : props.getLinks().getQualityReport()) {
                    log.debug("Found quality report links ");
                    if (qReport.getHref() != null) {
                        if (StringUtils.isEmpty(searchResultItem.getJupyterNotebookReportLink())
                                && qReport.getType() != null && qReport.getType().equalsIgnoreCase("application/x-ipynb+json")) {
                            searchResultItem.setJupyterNotebookReportLink(qReport.getHref());
                        }

                        SearchResultProperty group = searchResultItem.getProperties().get(
                                "qualityReportLinks");
                        if (group == null) {
                            group = new SearchResultProperty();
                            searchResultItem.getProperties().put("qualityReportLinks", group);
                            log.debug("adding qualityReportLinks ");
                        }
                        Map<String, SearchResultProperty> groupMap = new HashMap<>();
                        groupMap.put("menuLabel",
                                new SearchResultProperty("Quality Report"));
                        if (qReport.getHref().startsWith("https://colab")) {
                            groupMap.put("menuIcon",
                                    new SearchResultProperty("colab-quality-report"));
                        } else {
                            groupMap.put("menuIcon",
                                    new SearchResultProperty("fa fa-fw fa-save"));
                        }

                        groupMap.put("link", new SearchResultProperty(qReport.getHref()));
                        groupMap.put("type", new SearchResultProperty(qReport.getType()));
                        groupMap.put("title", new SearchResultProperty(
                                qReport.getTitle()));
                        //System.out.println("QualityReport type " + qReport.getType());
                        switch (qReport.getType()) {
                            case "application/atom+xml":
                                groupMap.put("label", new SearchResultProperty("Atom"));
                                break;
                            case "text/html":
                                groupMap.put("label", new SearchResultProperty("HTML"));
                                break;
                            case "application/rss+xml":
                                groupMap.put("label", new SearchResultProperty("RSS"));
                                break;
                            case "application/geo+json":
                                groupMap.put("label", new SearchResultProperty("JSON"));
                                if (searchResultItem.getViaLinks() == null) {
                                    searchResultItem.setViaLinks(new ArrayList<>());
                                }
                                break;
                            case "Atom":
                                groupMap.put("label", new SearchResultProperty(""));
                                break;
                            default:
                                //System.out.println("Case default ");
                                groupMap.put("label", new SearchResultProperty(qReport.getType()));
                                break;
                        }
                        log.debug(
                                "Found quality report entry :" + qReport.getHref() + " " + qReport.getType());
                        //groupMap.put("label", new SearchResultProperty());
                        group.getGroups().add(groupMap);
                        group.setShow(showField("qualityReport",
                                groupMap.size() > 0 ? "has child" : ""));
                    }
                }
            }
        }

        /*
            Build voila report Url for the products
         */
        if (StringUtils.isNotEmpty(searchResultItem.getRelatedProductsLink())
                && StringUtils.isNotEmpty(searchResultItem.getJupyterNotebookReportLink())
                && StringUtils.isNotEmpty(parentId) && StringUtils.isNotEmpty(productId)
                && StringUtils.isNotEmpty(config.getVoilaReportsDir())
                //&& StringUtils.isNotEmpty(config.getVoilaReportTemplate())
                && StringUtils.isNotEmpty(config.getVoilaReportUrl())) {

            String reportFileName = getVoilaReportFileName(parentId, productId);
            log.debug("Voila report file name: " + reportFileName);
            log.debug("Voila report directory: " + config.getVoilaReportsDir());
            log.debug("Fresh report: " + config.isFreshReport());

            if (reportFileName != null) {
                // Check if the report is already existed
                File reportFile = new File(config.getVoilaReportsDir() + "/" + reportFileName);

                if (!reportFile.getParentFile().exists()) {
                    reportFile.getParentFile().mkdirs();
                }

                boolean hasReportFile = true;

//                File reportTemplateFile = null;
                // if the report does not exist yet, create it from the report template
                if (config.isFreshReport() || !reportFile.exists()) {
                    try {
                        Map<String, String> details = new HashMap<>();
                        log.debug("Downloading Jupyter notebook report from the URL " + searchResultItem.getJupyterNotebookReportLink());

                        String reportContent = HTTPInvoker.invokeGET(searchResultItem.getJupyterNotebookReportLink(), details);
                        if (details.get(Constants.HTTP_GET_DETAILS_ERROR_CODE) != null) {
                            log.debug(String.format("Errors while downloading Jupyter notebook report from the URL %s: %s",
                                    searchResultItem.getJupyterNotebookReportLink(), details.get(Constants.HTTP_GET_DETAILS_ERROR_MSG)));
                            hasReportFile = false;
                        } else {
                            Files.write(Paths.get(reportFile.getAbsolutePath()), reportContent.getBytes(StandardCharsets.UTF_8));
                        }
                    } catch (IOException e) {
                        hasReportFile = false;
                        log.debug(String.format("Errors while downloading Jupyter notebook report from the URL %s: %s",
                                searchResultItem.getJupyterNotebookReportLink(), e.getMessage()));
                    }
//                    if (StringUtils.startsWithIgnoreCase(config.getVoilaReportTemplate(), "http")) {
//                        log.debug("Load voila report template remotely via HTTP");
//                        
//                        try {
//                            Map<String, String> details = new HashMap<>();
//                            String templateContent = HTTPInvoker.invokeGET(config.getVoilaReportTemplate(), details);
//                            if (details.get(Constants.HTTP_GET_DETAILS_ERROR_CODE) != null) {
//                                log.debug("Errors while downloading voila template via HTTP " + details.get(Constants.HTTP_GET_DETAILS_ERROR_MSG));
//                            } else {
//                                Files.write(Paths.get(reportFile.getAbsolutePath()), templateContent.getBytes(StandardCharsets.UTF_8));
//                                reportTemplateFile = new File(reportFile.getAbsolutePath());
//                            }
//                        } catch (IOException e) {
//                            log.debug("Errors while downloading voila template via HTTP " + e);
//                        }
//
////                        try {
////                            int connectionTimeout = 3 * 60 * 1000;
////                            FileUtils.copyURLToFile(new URL(config.getVoilaReportTemplate()), reportFile, connectionTimeout, connectionTimeout);
////                            reportTemplateFile = new File(reportFile.getAbsolutePath());
////                        } catch (IOException ex) {
////                            log.debug("Errors while downloading voila template via HTTP " + ex);
////                        }
//                    } else {
//                        log.debug("Load voila report template from local file");
//                        reportTemplateFile = new File(config.getVoilaReportTemplate());
//                    }

//                    if (reportTemplateFile != null && reportTemplateFile.exists()) {
//
//                        try {
//                            try (Stream<String> lines = Files.lines(reportTemplateFile.toPath())) {
//                                final String beingReplacedParentId = parentId;
//                                final String beingReplacedProductId = productId;
//
//                                List<String> replaced = lines.map(line -> line
//                                        .replaceAll(config.getVoilaReportParentIdToBeReplaced(), beingReplacedParentId)
//                                        .replaceAll(config.getVoilaReportProductIdToBeReplaced(), beingReplacedProductId)).collect(Collectors.toList());
//                                Files.write(reportFile.toPath(), replaced);
//
//                                log.debug("Created voila report file: " + reportFileName);
//                            }
//                        } catch (IOException e) {
//                            hasReportFile = false;
//                            log.error("Creating voila report file errors: " + e);
//                        }
//                    }
                }
                if (hasReportFile) {
                    String reportUrl = config.getVoilaReportUrl() + "/" + reportFileName;
                    log.debug("Voila report Url: " + reportUrl);
                    searchResultItem.setVoilaReportLink(reportUrl);
                }
            }
        }
        return searchResultItem;
    }

    /**
     * Build a SearchResultItem from a JSON String
     *
     * @param source     
     * @return
     */
    public SearchResultItem buildResultFromJson(String source) {
        log.debug("Building Search Result Set From Geo JSON");
        // Parsing to FeatureColllection object
        ObjectMapper mapper = createObjectMapper();

        Feature feat;
        try {
            feat = mapper.readValue(source, Feature.class);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(
                    GeoJSONSearchResultParser.class.getName()).log(Level.SEVERE,
                    null, ex);
            log.error("Error occured in buildResultSetFromJson");
            return null;
        }
        return buildFeatureResult(mapper, feat);
    }

    /**
     * Check if the field will be shown. The field show only if it is checked in
     * the preferences and its value is not empty
     *
     * @param fieldName
     * @param value
     * @return
     */
    private boolean showField(String fieldName, String value) {
        boolean show = false;
        if (StringUtils.isNotEmpty(value)) {
            if (this.showFields != null) {
                if (this.showFields.contains(fieldName)) {
                    show = true;
                }
            }
        }
        return show;
    }

    private void getCategories(SearchResultItem searchResultItem, String categories) {
        log.debug("Parse categories: " + categories);

        try (JsonReader reader = new JsonReader(new StringReader(categories))) {
            //reader.setLenient(true);            

            /*
                parse json
             */
            JsonToken nextToken = reader.peek();

            while (reader.peek() != JsonToken.END_DOCUMENT) {
                if (JsonToken.BEGIN_ARRAY.equals(nextToken)) {
                    jsonBegin(reader);
                    while (reader.hasNext()) {
                        readCategories(reader, searchResultItem);
                    }
                    jsonEnd(reader);
                } else if (JsonToken.BEGIN_OBJECT.equals(nextToken)) {
                    readCategories(reader, searchResultItem);
                }
            }
        } catch (IOException e) {
            log.debug("Error while parsing categories " + e);
        } catch (Exception e) {
            log.debug("Error while parsing categories " + e);
        }
    }

    private void readCategories(JsonReader reader,
            SearchResultItem searchResultItem) throws IOException {
        jsonBegin(reader);
        SearchResultProperty group = searchResultItem
                .getProperties()
                .get("categories");
        if (group == null) {
            group = new SearchResultProperty();
            searchResultItem.getProperties().put("categories", group);
        }
        Map<String, SearchResultProperty> groupMap = new HashMap<>();
        group.getGroups().add(groupMap);

        boolean showCategories = showField("categories",
                groupMap.size() > 0 ? "has child" : "");
        log.debug("Show categories: " + showCategories);
        group.setShow(showCategories);
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("term") || name.equals("label")) {
                groupMap.put(name, new SearchResultProperty(reader.nextString()));
            } else {
                reader.skipValue();
            }
        }
        jsonEnd(reader);
    }

    private void getContact(SearchResultItem searchResultItem, String contacts) {
        log.debug("Parse contact: " + contacts);

        try (JsonReader reader = new JsonReader(new StringReader(contacts))) {
            SearchResultProperty group = searchResultItem
                    .getProperties()
                    .get("contact");
            if (group == null) {
                group = new SearchResultProperty();
                searchResultItem.getProperties().put("contact", group);
            }
            Map<String, SearchResultProperty> groupMap = new HashMap<>();
            group.getGroups().add(groupMap);

            boolean showContact = showField("contact",
                    groupMap.size() > 0 ? "has child" : "");
            log.debug("Show contact: " + showContact);
            group.setShow(showContact);

            while (reader.peek() != JsonToken.END_DOCUMENT) {
                //reader.setLenient(true);
                jsonBegin(reader);
                /*
                parse json
                 */
                while (reader.hasNext()) {
                    jsonBegin(reader);
                    while (reader.hasNext()) {
                        if (JsonToken.NAME.equals(reader.peek())) {
                            String name = reader.nextName();
                            switch (name) {
                                case "agent":
                                    jsonBegin(reader);
                                    while (reader.hasNext()) {
                                        jsonBegin(reader);
                                        while (reader.hasNext()) {
                                            if (JsonToken.NAME.equals(reader.peek())) {
                                                String agentName = reader.nextName();
                                                switch (agentName) {
                                                    case "hasAddress":
                                                        jsonBegin(reader);
                                                        while (reader.hasNext()) {
                                                            if (JsonToken.NAME.equals(reader.peek())) {
                                                                String adrName = reader.nextName();
                                                                switch (adrName) {
                                                                    case "postal-code":
                                                                        groupMap.put("postalCode", new SearchResultProperty(reader.nextString()));
                                                                        break;
                                                                    case "street-address":
                                                                        groupMap.put("street", new SearchResultProperty(reader.nextString()));
                                                                        break;
                                                                    case "locality":
                                                                        groupMap.put("locality", new SearchResultProperty(reader.nextString()));
                                                                        break;
                                                                    case "country-name":
                                                                        groupMap.put("country", new SearchResultProperty(reader.nextString()));
                                                                        break;
                                                                    default:
                                                                        reader.skipValue();
                                                                        break;
                                                                }
                                                            } else {
                                                                reader.skipValue();
                                                            }
                                                        }
                                                        jsonEnd(reader);
                                                        break;
                                                    case "phone":
                                                        groupMap.put("phone", new SearchResultProperty(reader.nextString()));
                                                        break;
                                                    case "name":
                                                        groupMap.put("organisationName", new SearchResultProperty(reader.nextString()));
                                                        break;
                                                    case "type":
                                                        groupMap.put("orgType", new SearchResultProperty(reader.nextString()));
                                                        break;
                                                    case "uri":
                                                        groupMap.put("onlineRsLinkage", new SearchResultProperty(reader.nextString()));
                                                        break;
                                                    case "email":
                                                        groupMap.put("electronicMail", new SearchResultProperty(reader.nextString()));
                                                        break;
                                                    default:
                                                        reader.skipValue();
                                                        break;
                                                }
                                            } else {
                                                reader.skipValue();
                                            }
                                        }
                                        jsonEnd(reader);
                                    }
                                    jsonEnd(reader);
                                    break;
                                case "role":
                                    groupMap.put("role", new SearchResultProperty(reader.nextString()));
                                    break;
                                case "type":
                                    groupMap.put("type", new SearchResultProperty(reader.nextString()));
                                    break;
                                default:
                                    reader.skipValue();
                                    break;
                            }
                        } else {
                            reader.skipValue();
                        }
                    }
                    jsonEnd(reader);
                }
                jsonEnd(reader);
            }
        } catch (IOException e) {
            log.debug("Error while parsing contact " + e);
        } catch (Exception e) {
            log.debug("Error while parsing contact " + e);
        }
    }

    private void getGeometry(SearchResultItem searchResultItem, String geometry) {

        StringBuilder sb = new StringBuilder();
        String type = "Polygon";
        try (JsonReader reader = new JsonReader(new StringReader(geometry))) {
            while (reader.peek() != JsonToken.END_DOCUMENT) {
                jsonBegin(reader);
                while (reader.hasNext()) {
                    if (JsonToken.NAME.equals(reader.peek())) {
                        String name = reader.nextName();
                        switch (name) {
                            case "coordinates":
                                jsonBegin(reader);
                                while (reader.hasNext()) {
                                    jsonBegin(reader);
                                    while (reader.hasNext()) {
                                        jsonBegin(reader);
                                        if (JsonToken.NUMBER.equals(reader.peek())) {
                                            sb.append(reader.nextDouble()).append(" ");
                                        } else {
                                            reader.skipValue();
                                        }
                                        jsonBegin(reader);
                                    }
                                    jsonEnd(reader);
                                }
                                jsonEnd(reader);
                                break;
                            case "type":
                                type = reader.nextString();
                                break;
                            default:
                                reader.skipValue();
                                break;
                        }
                    } else {
                        reader.skipValue();
                    }
                }
                jsonEnd(reader);
            }
        } catch (IOException e) {
            log.debug("Error while parsing geometry " + e);
        } catch (Exception e) {
            log.debug("Error while parsing geometry " + e);
        }

        String coordinates = StringUtils.trimToNull(sb.toString());
        if (StringUtils.isNotEmpty(coordinates)) {
            if ("Polygon".equalsIgnoreCase(type)) {
                searchResultItem.addProperty("georssPolygon1", new SearchResultProperty(coordinates));
                searchResultItem.setGmlNode(buildGmlFeature(coordinates));
                searchResultItem.setHasFootprint(true);
            }
        }
        //System.out.println("MNGMNG " + searchResultItem.getProperties().get("georssPolygon1"));
    }

    private void jsonBegin(JsonReader reader) throws IOException {
        if (JsonToken.BEGIN_ARRAY.equals(reader.peek())) {
            reader.beginArray();
        } else if (JsonToken.BEGIN_OBJECT.equals(reader.peek())) {
            reader.beginObject();
        }
    }

    private void jsonEnd(JsonReader reader) throws IOException {
        if (JsonToken.END_ARRAY.equals(reader.peek())) {
            reader.endArray();
        } else if (JsonToken.END_OBJECT.equals(reader.peek())) {
            reader.endObject();
        }
    }

    private String buildGmlEnveloppe() {
        return ("<wfs:FeatureCollection xmlns:wfs=\"http://www.opengis.net/wfs\"  "
                + "     xmlns:gml=\"http://www.opengis.net/gml\"\n"
                + "     id=\"featCollection\">\n"
                + "     <gml:boundedBy>\n"
                + "          <gml:Envelope srsName=\"EPSG:4326\">\n"
                + "               <gml:lowerCorner>-90 -180</gml:lowerCorner>\n"
                + "               <gml:upperCorner>90 180</gml:upperCorner>\n"
                + "          </gml:Envelope>\n"
                + "     </gml:boundedBy>\n"
                + "  <gml:featureMembers>\n"
                + "  </gml:featureMembers>\n"
                + "</wfs:FeatureCollection>");
    }

    private String buildGmlFeature(String posList) {
        return ("<Feature xmlns=\"http://www.esa.int/xml/schemas/mass/serviceresult\" xmlns:gml=\"http://www.opengis.net/gml\" gml:id=\"GML_IDENTIFIER\">\n"
                + " <id>GML_IDENTIFIER</id>\n"
                + " <geometry>\n"
                + " <Polygon xmlns=\"http://www.opengis.net/gml\" srsName=\"EPSG:4326\">\n"
                + "     <exterior>\n"
                + "         <LinearRing>\n"
                + "	<posList>" + Utility.validateCoordinates(Utility.reverseCoordinates(posList)) + "</posList>"
                + "         </LinearRing>\n"
                + "     </exterior>\n"
                + " </Polygon>\n"
                + "</geometry>\n"
                + "</Feature>");
    }

    public String jsonToHtml(String key, Object jsonValue, int indent) {
        JsonNode jsonNodeRoot;

        if (jsonValue instanceof JsonNode) {
            //System.out.println("MNG This is JSON NODE 111 " + jsonValue);
            jsonNodeRoot = ((JsonNode) jsonValue);
        } else {
            jsonNodeRoot = toJsonNode(jsonValue);
        }

        if (jsonNodeRoot != null) {
            CustomJsonNode customJsonNode = new CustomJsonNode();
            customJsonNode.setKey(key);
            customJsonNode.setIndent(indent);

            parseJson(jsonNodeRoot, customJsonNode);

            //log.debug("GSON to Debug - > " + customJsonNode.toDebug());
            //log.debug("GSON to HTML - > " + customJsonNode.toHTML());
            return customJsonNode.toHTML();
        }
        return null;
    }

    public String jsonToHtml(Object jsonObject, int indent, boolean collapse) {
        //System.out.println(jsonObject);
        JsonNode jsonNodeRoot;

        if (jsonObject instanceof JsonNode) {
            jsonNodeRoot = ((JsonNode) jsonObject);
        } else {
            jsonNodeRoot = toJsonNode(jsonObject);
        }

        if (jsonNodeRoot != null) {
            CustomJsonNode customJsonNode = new CustomJsonNode();
            customJsonNode.setIndent(indent);

            parseJson(jsonNodeRoot, customJsonNode);

            //log.debug("GSON to Debug - > " + customJsonNode.toDebug());
            //log.debug("GSON to HTML - > " + customJsonNode.toHTML());
            return customJsonNode.toHTML();
        }

        return null;
    }

    private void parseJson(JsonNode jsonNode, CustomJsonNode customJsonNode) {

        if (jsonNode.isObject()) {
            customJsonNode.setType(CustomJsonNode.TYPE.OBJECT);

            Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();

                CustomJsonNode child = new CustomJsonNode();
                child.setKey(key);

                customJsonNode.addChild(child);

                if (field.getValue().isValueNode()) {
                    child.setType(CustomJsonNode.TYPE.VALUE);
                    child.setValue(field.getValue().asText());
                } else {
                    parseJson(field.getValue(), child);
                }
            }
        } else {
            if (jsonNode.isArray()) {
                customJsonNode.setType(CustomJsonNode.TYPE.ARRAY);
                for (JsonNode childNode : jsonNode) {
                    CustomJsonNode child = new CustomJsonNode();
                    customJsonNode.addChild(child);

                    if (childNode.isValueNode()) {
                        child.setType(CustomJsonNode.TYPE.VALUE);
                        child.setValue(childNode.asText());
                    }

                    if (childNode.isObject()) {
                        child.setType(CustomJsonNode.TYPE.OBJECT);
                        parseJson(childNode, child);
                    }

                    if (childNode.isArray()) {
                        child.setType(CustomJsonNode.TYPE.ARRAY);
                        parseJson(childNode, child);
                    }
                }
            } else {
                if (jsonNode.isValueNode()) {
                    customJsonNode.setType(CustomJsonNode.TYPE.VALUE);
                    customJsonNode.setValue(jsonNode.asText());
                } else {
                    log.debug("Node type: " + jsonNode.getNodeType());
                }
            }
        }
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.registerModule(new JavaTimeModule()); // new module, NOT JSR310Module
        return mapper;
    }

    private JsonNode toJsonNode(Object jsonObject) {
        try {
            ObjectMapper mapper = createObjectMapper();
            String jsonStr = mapper.writeValueAsString(jsonObject);
            return mapper.readTree(jsonStr);
        } catch (JsonProcessingException jEx) {
            log.debug("Error while parsing json object: " + jEx);
            return null;
        } catch (IOException e) {
            log.debug("Error while parsing json object: " + e);
            return null;
        }
    }

    private String getVoilaReportFileName(String parentId, String productId) {
        if (config.getVoilaReportFileNamePattern() != null
                && parentId != null
                && productId != null) {
            return config.getVoilaReportFileNamePattern()
                    .replaceAll("\\{ParentIdentifier\\}", idToFileName(parentId))
                    .replaceAll("\\{ProductIdentifier\\}", idToFileName(productId));
        }
        return null;
    }

    private String idToFileName(String id) {
        return id.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
