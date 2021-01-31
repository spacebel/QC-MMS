package be.spacebel.catalog.services;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.HttpMethod;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import be.spacebel.catalog.models.CatalogResponse;
import be.spacebel.catalog.models.SolrCollection;
import be.spacebel.catalog.utils.BundleUtils;
import be.spacebel.catalog.utils.Constants;
import static be.spacebel.catalog.utils.Constants.HTTP_ACCEPT_PARAM;
import be.spacebel.catalog.utils.GeoUtils;
import be.spacebel.catalog.utils.parser.GeoJsonParser;
import be.spacebel.catalog.utils.xml.XpathUtils;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

/**
 * This class is the catalog processor, it receives the search request executes
 * it and returns the atom response
 *
 * @author tth
 *
 */
@Service
public class CatalogHandler {
	
	
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(CatalogHandler.class);

	@Value("${xsl.dir}")
	private String xslDir;
	
	@Value("${server.url}")
	private String serverUrl; 
        
        @Value("${report.template.dir}")
	private String reportTemplateDir;
        
        @Value("${jupyter.iso.report.template.name}")
	private String isoReportTemplateName;
	
	private SolrHandler solrHandler;
	private XMLService xmlService;

	public CatalogHandler(@Autowired SolrHandler solrHandler, @Autowired XMLService xmlService) {
		this.xmlService = xmlService;
		this.solrHandler = solrHandler;
	}


	public CatalogResponse doSearch(String mimeType, Map<String, String> requestParams, SolrCollection solrCollection) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;
		try {

			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();

			String recordSchema = requestParams.get(Constants.RECORD_SCHEMA_PARAM);
			String schema = null;
			if (StringUtils.endsWithIgnoreCase(recordSchema, "om")
					|| StringUtils.endsWithIgnoreCase(recordSchema, Constants.EOP_21_NS)
					|| StringUtils.endsWithIgnoreCase(recordSchema, "server-choice")) {
				schema = Constants.OM_SCHEMA;
			} else if (StringUtils.endsWithIgnoreCase(recordSchema, "om10")
					|| StringUtils.endsWithIgnoreCase(recordSchema, Constants.EOP_20_NS)) {
				schema = Constants.OM10_SCHEMA;
			}

			if (mimeType.equals(Constants.GEOJSON_MIME_TYPE)) {
				return createGeoJsonResponse4Dataset(results, schema, requestParams);		
			} else if (mimeType.equals(Constants.EOP_GML_MIME_TYPE)) {
				return createOMResponse(results, schema);					
			} else if (mimeType.equals(Constants.EOP_OM_MIME_TYPE)) {
				return createOMResponse(results, Constants.OM_SCHEMA);					
			} else if (mimeType.equals(Constants.EOP_OM10_MIME_TYPE)) {
				return createOMResponse(results, Constants.OM10_SCHEMA);		
			
			} else {
				return new CatalogResponse(null, null, -1);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

	
	public String[] addDatasetQualityInformation(String mimeType, Map<String, String> requestParams, SolrCollection solrCollection, String qualityInfoData) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;		
		String[] error = {"",""};
		
		try {
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String metadata = (String) result.getFieldValue("metadataOrig");
			
			
			JSONObject metadataObj = new JSONObject(metadata);
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
			JSONObject productInfo =  GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation");
			JSONObject qualityInfo =  GeoJsonParser.getGeoJSONObjectProperty(productInfo, "qualityInformation");
			
			if (qualityInfo != null) {
				error[0] = String.valueOf(HttpStatus.SC_CONFLICT); 
				error[1] = "Failed to create, QualityInformation is already exsited.";
				return error;
			} else {
				
				JSONObject qualityObjParent = new JSONObject(qualityInfoData);				
				JSONObject qualityObj =  GeoJsonParser.getGeoJSONObjectProperty(qualityObjParent, "qualityInformation");				
				productInfo.put("qualityInformation", qualityObj);
				metadataObj.remove("productInformation");
				metadataObj.put("productInformation", productInfo);
				String updatedMetadata = metadataObj.toString(2);
				//System.out.println(updatedMetadata);
				solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			}
			
			
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	
	
	public String[] updateDatasetQualityIndicators(String mimeType, Map<String, String> requestParams, SolrCollection solrCollection, String updatedQualityIndicators) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;
		String[] error = {"",""};
				
		try {
			
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String metadata = (String) result.getFieldValue("metadataOrig");
			
			JSONObject metadataObj = new JSONObject(metadata);
			
			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
			JSONObject productInfo =  GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation"); 
			JSONObject qualityInfo =  GeoJsonParser.getGeoJSONObjectProperty(productInfo, "qualityInformation");
			JSONArray qualityIndicators =  GeoJsonParser.getGeoJSONArrayProperty(qualityInfo, "qualityIndicators");
			
			if (qualityIndicators == null) {				 
				error[0] = String.valueOf(HttpStatus.SC_BAD_REQUEST); 
				error[1] = "Failed to update, Quality Indicators is not yet exsited.";
			}
			
			
			if (StringUtils.isEmpty(error[1])) {				
				JSONArray newQualityIndicators = new JSONArray(updatedQualityIndicators);
				
				int count = newQualityIndicators.length();
				
				
				for (int i=0; i< count;i++) {
					JSONObject newObj =  (JSONObject) newQualityIndicators.get(i);
					String isMeasurementOf = GeoJsonParser.getGeoJSONStringProperty(newObj, "isMeasurementOf");
				
					if (!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#degradedDataPercentageMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#degradedAncillaryDataPercentageMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#formatCorrectnessMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#generalQualityMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#geometricQualityMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#radiometricQualityMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#sensorQualityMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#feasibilityControlMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#deliveryControlMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#ordinaryControlMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#detailedControlMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#harmonizationControlMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#ipForLpInformationMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#lpInterpretationMetric") && 
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#lpMetadataControlMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#lpOrdinaryControlMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#lpThematicValidationMetric")) {
							
							error[0] = String.valueOf(HttpStatus.SC_BAD_REQUEST); 
							error[1] = "Failed to update, " + isMeasurementOf + " is an invalid value for isMeasurementOf (in the playload).";
							return error;
							
					}
				}
				
				if (StringUtils.isEmpty(error[1])) {			
					qualityInfo.remove("qualityIndicators");
					qualityInfo.put("qualityIndicators", newQualityIndicators);
				
					String updatedMetadata = metadataObj.toString(2);
					
					solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
				}
			}
			
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	public String[] updateDatasetQualityInformation(String mimeType, Map<String, String> requestParams, SolrCollection solrCollection, String updatedQualityInfo) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;
		String[] error = {"",""};
		
		try {
			
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String metadata = (String) result.getFieldValue("metadataOrig");
			
			JSONObject metadataObj = new JSONObject(metadata);
			
			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
			JSONObject productInfo =  GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation"); 
			JSONObject qualityInfo =  GeoJsonParser.getGeoJSONObjectProperty(productInfo, "qualityInformation");
			
			
			if (qualityInfo == null) {				
				error[0] = String.valueOf(HttpStatus.SC_BAD_REQUEST); 
				error[1] = "Failed to update, QualityInformation is not yet exsited.";
				return error;
			}
			
			
			if (StringUtils.isEmpty(error[1])) {
				qualityInfo =  new JSONObject();		
				JSONObject updateQualitydObj = new JSONObject(updatedQualityInfo);
				for (String key:updateQualitydObj.keySet()) {					
					Object value = GeoJsonParser.getGeoObjectProperty(updateQualitydObj, key);
					qualityInfo.put(key, value);
				}
				
				productInfo.remove("qualityInformation");				
				productInfo.put("qualityInformation", qualityInfo);
				metadataObj.remove("productInformation");
				metadataObj.put("productInformation", productInfo);
				String updatedMetadata = metadataObj.toString(2);
				
				solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			}
			
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	
	public  String[]  addDatasetQualityInformationAttributes(String mimeType, Map<String, String> requestParams, SolrCollection solrCollection, String updatedData) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;
		String errorField = StringUtils.EMPTY;
		String[] error = {"",""};
		
		try {
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
							
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String metadata = (String) result.getFieldValue("metadataOrig");
			
			JSONObject metadataObj = new JSONObject(metadata);
			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
			JSONObject productInfo =  GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation"); 
			JSONObject qualityInfo =  GeoJsonParser.getGeoJSONObjectProperty(productInfo, "qualityInformation");
			
			JSONObject updatedObj = new JSONObject(updatedData);
			for (String key:updatedObj.keySet()) {					
				
				Object value = GeoJsonParser.getGeoObjectProperty(updatedObj, key);				
				if (qualityInfo.has(key)) {					
					errorField = key;
					
					error[0] = String.valueOf(HttpStatus.SC_CONFLICT); 
					error[1] = "Failed to update,  " + errorField + " is already existed. Use PUT method to update.";
					
					break;
				} else {
					qualityInfo.put(key, value);
				}
				
			}
			
			if (StringUtils.isEmpty(errorField)) {
				
				productInfo.remove("qualityInformation");
				productInfo.put("qualityInformation", qualityInfo);
				metadataObj.remove("productInformation");
				metadataObj.put("productInformation", productInfo);
				
				String updatedMetadata = metadataObj.toString(2);			
				solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			}
				
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	public  String[]  addDatasetQualityIndicatorsAttributes(Map<String, String> requestParams, SolrCollection solrCollection, String updatedData) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;		
		String[] error = {"",""};
		try {
			
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String metadata = (String) result.getFieldValue("metadataOrig");
			
			JSONObject metadataObj = new JSONObject(metadata);
			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
			JSONObject productInfo =  GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation"); 
			JSONObject qualityInfo =  GeoJsonParser.getGeoJSONObjectProperty(productInfo, "qualityInformation");
			JSONArray qualityIndicators =  GeoJsonParser.getGeoJSONArrayProperty(qualityInfo, "qualityIndicators");
			
			JSONArray newQualityIndicators = new JSONArray(updatedData);
			
			int count = newQualityIndicators.length();
			
			
			for (int i=0; i< count;i++) {
				JSONObject newObj =  (JSONObject) newQualityIndicators.get(i);				
				String isMeasurementOf = GeoJsonParser.getGeoJSONStringProperty(newObj, "isMeasurementOf");
				
				if (!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#degradedDataPercentageMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#degradedAncillaryDataPercentageMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#formatCorrectnessMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#generalQualityMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#geometricQualityMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#radiometricQualityMetric") && 
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#sensorQualityMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#feasibilityControlMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#deliveryControlMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#ordinaryControlMetric") &&
						!isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#detailedControlMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#harmonizationControlMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#ipForLpInformationMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#lpInterpretationMetric") && 
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#lpMetadataControlMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#lpOrdinaryControlMetric") &&
                                                !isMeasurementOf.equals("http://qcmms.esa.int/quality-indicators/#lpThematicValidationMetric")) {
                                                                       
						error[0] = String.valueOf(HttpStatus.SC_BAD_REQUEST);
						error[1] = "Failed to update, " + isMeasurementOf + " is an invalid value for isMeasurementOf (in the playload).";
				} else {
				
					for (int j=0; j< qualityIndicators.length();j++) {
						JSONObject oldObj =  (JSONObject) qualityIndicators.get(j);					
						String oldIsMeasurementOf = GeoJsonParser.getGeoJSONStringProperty(oldObj, "isMeasurementOf");					
						if (StringUtils.equals(isMeasurementOf, oldIsMeasurementOf)) {
							error[0] = String.valueOf(HttpStatus.SC_CONFLICT); 
							error[1] = "Failed to update,  " + StringUtils.substringAfterLast(oldIsMeasurementOf, "#") + " is already existed. Use PUT method to update.";
						} 
					}
				}
				
				if (StringUtils.isEmpty(error[0])) {
					qualityIndicators.put(newObj);
				} else {
					break;
				}
			}
			
			
			if (StringUtils.isEmpty(error[0])) {
				qualityInfo.remove("qualityIndicators");
				qualityInfo.put("qualityIndicators", qualityIndicators);
				
				String updatedMetadata = metadataObj.toString(2);			
				solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			}
				
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	
	public  String[]  createVia(Map<String, String> requestParams, SolrCollection solrCollection, String viaLinks) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;		
		String[] error = {"",""};
		try {
			
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String productMetadata = (String) result.getFieldValue("metadataOrig");
			
			
			JSONObject productObj = new JSONObject(productMetadata);			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(productObj, "properties");
			JSONObject links =  GeoJsonParser.getGeoJSONObjectProperty(properties, "links"); 
			JSONArray oldVia =  GeoJsonParser.getGeoJSONArrayProperty(links, "via"); 
									
                        JSONArray viaLinksArray = new JSONArray(viaLinks);
			//Object via = GeoJsonParser.getGeoObjectProperty(viaLinksObj, "via");
			//if (via != null) {
				if (oldVia != null) {
					error[0] = String.valueOf(HttpStatus.SC_CONFLICT); 
					error[1] = "Failed to update, via attribute is already existed. Use PUT method to update.";
				} else {
					links.put("via", viaLinksArray);
					String updatedMetadata = productObj.toString(2);			
					solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			
				}
			//}
			
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	public  String[]  addLinksToVia(Map<String, String> requestParams, SolrCollection solrCollection, String imagesMetadata) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;		
		String[] error = {"",""};
		try {
			
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String productMetadata = (String) result.getFieldValue("metadataOrig");
			
			
			JSONObject productObj = new JSONObject(productMetadata);			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(productObj, "properties");
			JSONObject links =  GeoJsonParser.getGeoJSONObjectProperty(properties, "links"); 
			JSONArray oldVias =  GeoJsonParser.getGeoJSONArrayProperty(links, "via"); 
			
			
			if (oldVias == null) {
				error[0] = String.valueOf(HttpStatus.SC_BAD_REQUEST); 
				error[1] = "Failed to update, via attribute is not existed.";
			} else {
				
				JSONArray newVias = new JSONArray(imagesMetadata);
								
				for (int i=0;i<newVias.length();i++) {
					JSONObject via = newVias.getJSONObject(i);
					String href = GeoJsonParser.getGeoJSONStringProperty(via, "href");
					for (int j=0;j<oldVias.length();j++) {
						JSONObject oldVia = oldVias.getJSONObject(j);
						String oldHref = GeoJsonParser.getGeoJSONStringProperty(oldVia, "href");
						if (StringUtils.equals(href, oldHref)) {
							error[0] = String.valueOf(HttpStatus.SC_CONFLICT); 
							error[1] = "File " + href + " has already been associated to the product.";
							break;
						}
					}
					
					if (StringUtils.isEmpty(error[0])) {
						oldVias.put(via);
					} else {
						break;
					}
					
				}
		
			}
			
			String updatedMetadata = productObj.toString(2);			
			solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	public  String[]  updateViaLinks(Map<String, String> requestParams, SolrCollection solrCollection, String imagesMetadata) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;		
		String[] error = {"",""};
		try {
			
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String productMetadata = (String) result.getFieldValue("metadataOrig");
			
			
			JSONObject productObj = new JSONObject(productMetadata);			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(productObj, "properties");
			JSONObject links =  GeoJsonParser.getGeoJSONObjectProperty(properties, "links"); 
			JSONArray oldVias =  GeoJsonParser.getGeoJSONArrayProperty(links, "via"); 
			
			
			if (oldVias == null) {
				error[0] = String.valueOf(HttpStatus.SC_BAD_REQUEST); 
				error[1] = "Failed to update, via attribute is not existed.";
			} else {
				JSONArray newVias = new JSONArray(imagesMetadata);
				links.remove("via");
				links.put("via", newVias);
		
			}
			
			String updatedMetadata = productObj.toString(2);			
			solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	public  String[]  deleteOneViaLink(Map<String, String> requestParams, SolrCollection solrCollection, String viaSeriesId, String viaDatasetId) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;		
		String[] error = {"",""};
		boolean found = false;
		try {
			
			String uri = viaSeriesId + "/datasets/" + viaDatasetId;
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String productMetadata = (String) result.getFieldValue("metadataOrig");
			
			
			JSONObject productObj = new JSONObject(productMetadata);			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(productObj, "properties");
			JSONObject links =  GeoJsonParser.getGeoJSONObjectProperty(properties, "links"); 
			JSONArray vias =  GeoJsonParser.getGeoJSONArrayProperty(links, "via"); 
			
			if (vias == null) {
				error[0] = String.valueOf(HttpStatus.SC_BAD_REQUEST); 
				error[1] = "Failed to update, via attribute is not existed.";
			} else {
				for (int i=0;i<vias.length();i++) {
					JSONObject via = vias.getJSONObject(i);
					String href = GeoJsonParser.getGeoJSONStringProperty(via, "href");
					if (href.endsWith(uri)) {
						vias.remove(i);
						found = true;
						break;
					}
					
				}
			}
			
			if (found) {
				String updatedMetadata = productObj.toString(2);			
				solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
				
			} else {
				error[0] = String.valueOf(HttpStatus.SC_BAD_REQUEST); 
				error[1] = "File " +  uri+ " is not found in via links.";
			}
			
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	public  String[]  deleteDatasetQualityIndicatorsAttribute(String mimeType, Map<String, String> requestParams, SolrCollection solrCollection, String attributeName) throws Exception {
		
		SolrDocumentList results;
		QueryResponse queryResponse;
		String[] error = {"",""};
		try {
			
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String metadata = (String) result.getFieldValue("metadataOrig");
			
			JSONObject metadataObj = new JSONObject(metadata);
			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
			JSONObject productInfo =  GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation"); 
			JSONObject qualityInfo =  GeoJsonParser.getGeoJSONObjectProperty(productInfo, "qualityInformation");
			JSONArray qualityIndicators =  GeoJsonParser.getGeoJSONArrayProperty(qualityInfo, "qualityIndicators");
			
			int count = qualityIndicators.length();
			JSONArray newQualityIndicators = new JSONArray();
			
			for (int i=0; i< count;i++) {
				JSONObject obj =  (JSONObject) qualityIndicators.get(i);				
				String isMeasurementOf = GeoJsonParser.getGeoJSONStringProperty(obj, "isMeasurementOf");
				String s = StringUtils.substringAfterLast(isMeasurementOf, "#");
				if (!s.equals(attributeName)) {
					newQualityIndicators.put(obj);
				} 
			}
			
			qualityInfo.remove("qualityIndicators");				
			qualityInfo.put("qualityIndicators", newQualityIndicators);
			String updatedMetadata = metadataObj.toString(2);			
			solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			
			return error;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
			
	}
	public  String[]  updateDatasetQualityIndicatorsAttribute(String mimeType, Map<String, String> requestParams, SolrCollection solrCollection, String updatedData, String attributeName) throws Exception {

		SolrDocumentList results;
		QueryResponse queryResponse;		
		String[] error = {String.valueOf(HttpStatus.SC_NOT_FOUND) ,"Failed to update, " + attributeName + " is not yet exsited in QualityInformation."};
		try {
			
			
			queryResponse = solrHandler.getMetadata(requestParams, solrCollection);
			results = queryResponse.getResults();
			
			if (results.size() == 0 ) {
				error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
				error[1] = "The product " + requestParams.get(Constants.UID_PARAM) + " of series " + requestParams.get(Constants.PARENT_ID_PARAM) + " not found.";
				return error;
			}
			
			SolrDocument result = results.get(0);
			String metadata = (String) result.getFieldValue("metadataOrig");
			
			JSONObject metadataObj = new JSONObject(metadata);
			
			JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
			JSONObject productInfo =  GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation"); 
			JSONObject qualityInfo =  GeoJsonParser.getGeoJSONObjectProperty(productInfo, "qualityInformation");
			JSONArray qualityIndicators =  GeoJsonParser.getGeoJSONArrayProperty(qualityInfo, "qualityIndicators");
			JSONArray newQualityIndicators = new JSONArray();
			
			
			int count = qualityIndicators.length();			
			for (int i=0; i< count;i++) {
				JSONObject obj =  (JSONObject) qualityIndicators.get(i);				
				String isMeasurementOf = GeoJsonParser.getGeoJSONStringProperty(obj, "isMeasurementOf");				
				if (isMeasurementOf.endsWith(attributeName)) {					
					JSONObject updatedObj = new JSONObject(updatedData);					
					String newIsMeasurementOf = GeoJsonParser.getGeoJSONStringProperty(updatedObj, "isMeasurementOf");					
					if (newIsMeasurementOf.equals(isMeasurementOf)) {						
						newQualityIndicators.put(updatedObj);
						error[0]= "";
						error[1]= "";
					} else {
						error[0] = String.valueOf(HttpStatus.SC_NOT_FOUND); 
						error[1] = "Failed to update, " + newIsMeasurementOf + " is an invalid value for isMeasurementOf (in the playload).";
						return error;
					}					
				} else {
					newQualityIndicators.put(obj);
				}
			}
			
			
			if (StringUtils.isEmpty(error[1])) {				
				qualityInfo.remove("qualityIndicators");				
				qualityInfo.put("qualityIndicators", newQualityIndicators);
				
				
				String updatedMetadata = metadataObj.toString(2);			
				solrHandler.postDatasetMetadata(null, updatedMetadata, Constants.GEOJSON_SCHEMA);
			} 
			
			return error;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("",e);
			throw e;
		}
	}
	
	public void doDelete(String fieldName, String value, SolrCollection solrCollection) throws Exception {
	
		this.solrHandler.deleteFromSolr(fieldName, value, solrCollection);
	
	}

	
	
	private CatalogResponse createGeoJsonResponse4Dataset(SolrDocumentList sorlResponse, String recordSchema, Map<String, String> requestParams) throws Exception {
		
			int size = sorlResponse.size();
			int totalResults = (int) sorlResponse.getNumFound();
			
			JSONArray metadataObjs = new JSONArray();
			String requestURL = requestParams.get(Constants.REQUEST_URL);
			
            log.debug("serverUrl " + serverUrl);			
			String collectionIdPath = StringUtils.substringAfter(requestURL, "series/");
			
						
			String baseHref = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + collectionIdPath;
			
			for (int i=0;i<size;i++) {
				SolrDocument result = sorlResponse.get(i);
				String metadata = (String) result.getFieldValue("metadataOrig");
				
				JSONObject metadataObj = new JSONObject(metadata);
				JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
				String identifier = GeoJsonParser.getGeoJSONStringProperty(properties, "identifier");
                                String parentId = GeoJsonParser.getGeoJSONStringProperty(properties, "parentIdentifier");

                                metadataObj.put("id", (serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + parentId + "/datasets/" + identifier));

                                addQualityReportLinks(properties, (serverUrl + "/" + Constants.SERIES_BASE_PATH), identifier, parentId);			 
                                
//				if (collectionIdPath.endsWith(identifier)) {
//					metadataObj.put("id", baseHref);
//				} else {
//					metadataObj.put("id", baseHref + "/" + identifier);
//				}
				metadataObjs.put(metadataObj);
			}
			
			JSONObject obj = new JSONObject();
			obj.put("type", "FeatureCollection");
			
			
			obj.put("id", baseHref);
			
			
			obj.put("totalResults", totalResults);
			obj.put("itemsPerPage", requestParams.get(Constants.ITEM_PER_PAGE_PARAM));
			obj.put("startIndex", requestParams.get(Constants.START_INDEX_PARAM));
			JSONObject queries = createQuery4GeoJson(requestParams);
			JSONObject properties = createPropertiesGeoJson(totalResults, requestParams);
			obj.put("queries", queries);
			obj.put("properties", properties);
			
			obj.put("features", metadataObjs);
		
			
			return new CatalogResponse(null, obj.toString(2), totalResults);
	}
	
        
	private void addQualityReportLinks(JSONObject properties, String baseHref, String identifier, String parentId) {
		JSONObject links = GeoJsonParser.getGeoJSONObjectProperty(properties, "links");
		if (links != null) {
			JSONArray viaLinks = GeoJsonParser.getGeoJSONArrayProperty(links, "via");
			if (viaLinks != null) {
				boolean addQRLink = false;

				for (int i = 0; i < viaLinks.length(); i++) {
					Iterator<Object> viaLinkIter = viaLinks.iterator();

		             while (viaLinkIter.hasNext()) {
                            Object element = viaLinkIter.next();
                            
                            if (element instanceof JSONObject) {
                                JSONObject viaLink = (JSONObject) element;
                                String type = GeoJsonParser.getGeoJSONStringProperty(viaLink, "type");
                                String title = GeoJsonParser.getGeoJSONStringProperty(viaLink, "title");
                                if (type != null && title != null
                                        && "application/geo+json".equalsIgnoreCase(type)
                                        && "Input data".equalsIgnoreCase(title)) {
                                    addQRLink = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (addQRLink) {
                        // http://qcmms-cat.spacebel.be/eo-catalog/series/EOP:ESA:GR1:UC11D/datasets/IMD_2018_010mD?httpAccept=application/x-ipynb%2Bjson
                        String reportUrl = baseHref + "/" + parentId + "/datasets/" + identifier + "?" + HTTP_ACCEPT_PARAM + "=application/x-ipynb%2Bjson";
                                                                        
                        JSONObject notebookLink = new JSONObject();
                        notebookLink.put("href", reportUrl);
                        notebookLink.put("title", "Online quality report by QCMMS in Jupyter notebook format");
                        notebookLink.put("type", "application/x-ipynb+json");
                        
                        JSONArray qualityReport = new JSONArray();
                        qualityReport.put(notebookLink);
                        
                        links.put("qualityReport", qualityReport);
                        
                        reportUrl = baseHref + "/" + parentId + "/datasets/" + identifier + "?" + HTTP_ACCEPT_PARAM + "=application%2Fvnd.iso.19157-2";
                        JSONObject alternateLink = new JSONObject();
                        alternateLink.put("href", reportUrl);
                        alternateLink.put("title", "Online quality report by QCMMS in ISO 19157-2 format");
                        alternateLink.put("type", "application/vnd.iso.19157-2");
                        
                        JSONArray alternateReport = new JSONArray();
                        alternateReport.put(alternateLink);
                        
                        links.put("alternates", alternateReport);
                    }
                }
            }
        }
		

	
	private CatalogResponse createOMResponse(SolrDocumentList sorlResponse, String recordSchema) throws Exception {

		int numberOfMatches = 1;
		/* The is the request to get the OM metadata of a result that has been returned previously thus, 
		 * the result is there, no need to check if sorlResponse has result or not.
		 * */
		SolrDocument result = sorlResponse.get(0);
		String metadata = (String) result.getFieldValue("metadataOrig");					
		metadata = StringUtils.substringAfter(metadata, "<![CDATA[");
		metadata = StringUtils.substringBeforeLast(metadata, "]]>");		
		
		if (StringUtils.equals(recordSchema, Constants.OM10_SCHEMA)) {
				String xslFile = xslDir + File.separator + Constants.OM11_2_OM10_XSL;
				metadata = xmlService.transformOMMetadata(metadata, xslFile);
		}
		
		Document responseDoc = xmlService.stream2Document(metadata);

		return new CatalogResponse(responseDoc, null, numberOfMatches);
	}

	private JSONObject createPagingLinks4GeoJson(int totalResults, String baseHref, String indexParam, 
			Map<String, String> requestParams) {
		
		JSONObject links = new JSONObject();
		
		JSONArray profiles = createLinkGeoJson(null, "http://www.opengis.net/spec/owc-geojson/1.0/req/core", null);
		links.put("profiles", profiles);
		
		int itemPerPage = Integer.parseInt(requestParams.get(Constants.ITEM_PER_PAGE_PARAM));

                String concat = "?";
		if (baseHref.contains("?")) {
			concat = "&";
		}
                
		if (totalResults > 0 && !requestParams.containsKey(Constants.UID_PARAM)) {

			int numberOfPage = totalResults / itemPerPage;

			if (indexParam.equals(Constants.START_INDEX_PARAM)) {
				int startIndex = Integer.parseInt(requestParams.get(Constants.START_INDEX_PARAM));

				/* create first link */
				//String href = baseHref + "&" + indexParam + "=1";				
                                String href = baseHref + concat + indexParam + "=1";
				JSONArray first = createLinkGeoJson("first results", href, "application/geo+json");
				links.put("first", first);
				
				/* create next link */
				int nextIndex = startIndex + itemPerPage;
				
				if (nextIndex <= totalResults) {
					//href = baseHref + "&" + indexParam + "=" + String.valueOf(nextIndex);
                                        href = baseHref + concat + indexParam + "=" + String.valueOf(nextIndex);
					JSONArray next = createLinkGeoJson("next results", href, "application/geo+json");
					links.put("next", next);
					
				}

				/* create previous link */
				int previousIndex = startIndex - itemPerPage;
				if (previousIndex >= 1 && previousIndex < totalResults) {
					//href = baseHref + "&" + indexParam + "=" + String.valueOf(previousIndex);
                                        href = baseHref + concat + indexParam + "=" + String.valueOf(previousIndex);
					JSONArray previous = createLinkGeoJson("previous results", href, "application/geo+json");
					links.put("previous", previous);
				}

				/* create last link */
				int lastIndex = itemPerPage * (numberOfPage) + 1;				
				if (lastIndex > totalResults) {
					lastIndex = totalResults - itemPerPage + 1;
				}

				//href = baseHref + "&" + indexParam + "=" + String.valueOf(lastIndex);
                                href = baseHref + concat + indexParam + "=" + String.valueOf(lastIndex);
				JSONArray last = createLinkGeoJson("last results", href, "application/geo+json");
				links.put("last", last);
				
			} else {

				int startPage = Integer.parseInt(requestParams.get(Constants.START_PAGE_PARAM));
				if (numberOfPage * itemPerPage < totalResults) {
					numberOfPage++;
				}

				/* create first link */
				/* create first link */
				//String href = baseHref + "&" + indexParam + "=1";
                                String href = baseHref + concat + indexParam + "=1";
				JSONArray first = createLinkGeoJson("first results", href, "application/geo+json");
				links.put("first", first);

				/* create next link */
				int nextPage = startPage + 1;
				if (nextPage <= numberOfPage) {					
					//href = baseHref + "&" + indexParam + "=" + String.valueOf(nextPage);
                                        href = baseHref + concat + indexParam + "=" + String.valueOf(nextPage);
					JSONArray next = createLinkGeoJson("next results", href, "application/geo+json");
					links.put("next", next);
				}

				/* create previous link */
				int previousPage = startPage - 1;
				if (previousPage >= 1) {
					//href = baseHref + "&" + indexParam + "=" + String.valueOf(previousPage);
                                        href = baseHref + concat + indexParam + "=" + String.valueOf(previousPage);
					JSONArray previous = createLinkGeoJson("previous results", href, "application/geo+json");
					links.put("previous", previous);
				}

				//href = baseHref + "&" + indexParam + "=" + String.valueOf(numberOfPage);
                                href = baseHref + concat + indexParam + "=" + String.valueOf(numberOfPage);
				JSONArray last = createLinkGeoJson("last results", href, "application/geo+json");
				links.put("last", last);
			}
		}
		
		String searchBaseHref = StringUtils.substringBeforeLast(baseHref, "/dataset")  + "/description";
		JSONArray search = createLinkGeoJson("search", searchBaseHref, Constants.OSDD_MIME_TYPE);
		links.put("search", search);
		
		return links;

	}
	
	private JSONObject createQuery4GeoJson(Map<String, String> requestParams) {
		Iterator<String> params = requestParams.keySet().iterator();
		
		JSONObject query =  new JSONObject();
		JSONArray request = new JSONArray();
		JSONObject obj =  new JSONObject();
		while (params.hasNext()) {
			String param = params.next();
			String value = requestParams.get(param);
			String osParam = BundleUtils.getEOPParam(param + ".value.description");
			if (osParam != null && !osParam.equals("NA")) {
				obj.put(osParam, value);				
			}
		}
		
		request.put(obj);
		query.put("request", request);
		return query;
	}
	
	private JSONObject createPropertiesGeoJson(int totalResults, Map<String, String> requestParams) {
		

		JSONObject properties =  new JSONObject();
		JSONArray authors = new JSONArray();
		JSONObject author =  new JSONObject();
		
		properties.put("title", BundleUtils.getMessage(BundleUtils.ATOM_TITLE_KEY));
		properties.put("creator", BundleUtils.getMessage(BundleUtils.ATOM_GENERATOR_KEY));
		
		author.put("type", "Agent");
		author.put("name", BundleUtils.getMessage(BundleUtils.ATOM_AUTHOR_NAME_KEY));
		author.put("email",  BundleUtils.getMessage(BundleUtils.ATOM_AUTHOR_EMAIL_NAME_KEY));
		authors.put(author);
		properties.put("authors", authors);
		
		Date currentDate = new Date(); //
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		properties.put("updated", sdf.format(currentDate));
		
		
		properties.put("lang", "en");
		properties.put("rights", BundleUtils.getMessage(BundleUtils.ATOM_RIGHTS_KEY));
		
		String requestURL = requestParams.get(Constants.REQUEST_URL);
		String collectionIdPath = StringUtils.substringAfter(requestURL, "series/");
		
		String baseHref = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + collectionIdPath;
		
		
		String queryString = requestParams.get(Constants.QUERY_STRING);
//		if (queryString != null) {
//			baseHref = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + collectionIdPath + "?" + queryString;
//		}
		

		String queryStringWithoutStartPost;
		if (queryString != null) {
			queryStringWithoutStartPost = queryString.replaceAll("&startRecord=[0-9]*", "")
					.replaceAll("&amp;startRecord=[0-9]*", "").replaceAll("&startPage=[0-9]*", "")
					.replaceAll("&amp;startPage=[0-9]*", "");
			queryStringWithoutStartPost = queryStringWithoutStartPost.replace("&amp;", "&");

			queryStringWithoutStartPost = queryStringWithoutStartPost.replaceAll("startRecord=[0-9]*", "");
			queryStringWithoutStartPost = queryStringWithoutStartPost.replaceAll("startPage=[0-9]*", "");                        
                        
			if (!StringUtils.isEmpty(queryStringWithoutStartPost)) {
				baseHref = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + collectionIdPath + "?" + queryStringWithoutStartPost;	
			}                        
		}                                
		
		String indexParam = Constants.START_INDEX_PARAM;		
		String startIndex = requestParams.get(Constants.START_INDEX_PARAM);		
		
		
		
		if (startIndex == null) {
			indexParam = Constants.START_PAGE_PARAM;		
			startIndex = requestParams.get(Constants.START_PAGE_PARAM);
		}
		
		JSONObject links = createPagingLinks4GeoJson(totalResults, baseHref, indexParam, requestParams);
		properties.put("links", links);
		return properties;
	}
	
	private JSONArray createLinkGeoJson(String title, String href, String type) {
		JSONArray link = new JSONArray();
		JSONObject obj =  new JSONObject();
		obj.put("href", href);
		if (type != null) {
			obj.put("type", type);
		}
		
		if (title != null) {
			obj.put("title", title);
		}
		
		link.put(obj);
		return link;
	}

	public void addRelatedLink(Map<String, String> requestParams, JSONArray viaLinks) {
		for (int j=0;j<viaLinks.length();j++) {
			JSONObject viaLink = viaLinks.getJSONObject(j);
			String href = GeoJsonParser.getGeoJSONStringProperty(viaLink, "href");
			String datasetId= StringUtils.substringAfterLast(href, "/");
			String tmp = StringUtils.substringAfterLast(href, "series/");
			String seriesId = StringUtils.substringBefore(tmp, "/datasets");
			
			String lpParentIdentifier = requestParams.get(Constants.PARENT_ID_PARAM);
			String lpId = requestParams.get(Constants.UID_PARAM);
			
			requestParams.remove(Constants.PARENT_ID_PARAM);
			requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
			requestParams.remove(Constants.UID_PARAM);
			requestParams.put(Constants.UID_PARAM, datasetId);
				
			try {
				
				SolrDocumentList results;
				QueryResponse queryResponse;
				
				queryResponse = solrHandler.getMetadata(requestParams, SolrCollection.DATASET);
				results = queryResponse.getResults();
				
				SolrDocument result = results.get(0);
				String imageProduct = (String) result.getFieldValue("metadataOrig");
					
				JSONObject imageProductObj = new JSONObject(imageProduct);
				
				
				JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(imageProductObj, "properties");
				JSONArray relatedLinks;
				JSONObject links;
				if (properties != null) {
					links =  GeoJsonParser.getGeoJSONObjectProperty(properties, "links");
					if (links != null) {
						relatedLinks =  GeoJsonParser.getGeoJSONArrayProperty(links, "related");
						if (relatedLinks == null) {
							relatedLinks = new JSONArray();
							links.put("related", relatedLinks);
						}
					} else {
						links = new JSONObject();
						properties.put("links", links);
						relatedLinks = new JSONArray();
						links.put("related", relatedLinks);
					}
					
					String relatedHref = serverUrl + "/series/" + lpParentIdentifier + "/datasets/" + lpId;
					JSONObject relatedLink = new JSONObject();
					relatedLink.put("href", relatedHref);
					relatedLink.put("type", "application/geo+json");
					relatedLink.put("title", "Related data");
					relatedLinks.put(relatedLink);
					
					String updatedImageProduct = imageProductObj.toString(2);			
					solrHandler.postDatasetMetadata(null, updatedImageProduct, Constants.GEOJSON_SCHEMA);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	public void removeRelatedLink(Map<String, String> requestParams, JSONArray viaLinks) {
		
		for (int j=0;j<viaLinks.length();j++) {
			JSONObject viaLink = viaLinks.getJSONObject(j);
			String href = GeoJsonParser.getGeoJSONStringProperty(viaLink, "href");
			String datasetId= StringUtils.substringAfterLast(href, "/");
			String tmp = StringUtils.substringAfterLast(href, "series/");
			String seriesId = StringUtils.substringBefore(tmp, "/datasets");
			
			String lpParentIdentifier = requestParams.get(Constants.PARENT_ID_PARAM);
			String lpId = requestParams.get(Constants.UID_PARAM);
			
			String relatedHref = serverUrl + "/series/" + lpParentIdentifier + "/datasets/" + lpId;
			
			requestParams.remove(Constants.PARENT_ID_PARAM);
			requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
			requestParams.remove(Constants.UID_PARAM);
			requestParams.put(Constants.UID_PARAM, datasetId);
				
			try {
				
				SolrDocumentList results;
				QueryResponse queryResponse;
				
				queryResponse = solrHandler.getMetadata(requestParams, SolrCollection.DATASET);
				results = queryResponse.getResults();
				
				SolrDocument result = results.get(0);
				String imageProduct = (String) result.getFieldValue("metadataOrig");
					
				JSONObject imageProductObj = new JSONObject(imageProduct);
				
				
				JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(imageProductObj, "properties");			
				if (properties != null) {
					JSONObject links =  GeoJsonParser.getGeoJSONObjectProperty(properties, "links");
					if (links != null) {
						JSONArray relatedLinks =  GeoJsonParser.getGeoJSONArrayProperty(links, "related");
						if (relatedLinks != null) {
							
							for (int i=0;i<relatedLinks.length();i++) {
								JSONObject relatedLink = relatedLinks.getJSONObject(i);
								String href1 = GeoJsonParser.getGeoJSONStringProperty(relatedLink, "href");
								if (StringUtils.equals(href1, relatedHref)) {
									relatedLinks.remove(i);
								}
							}
							String updatedImageProduct = imageProductObj.toString(2);			
							solrHandler.postDatasetMetadata(null, updatedImageProduct, Constants.GEOJSON_SCHEMA);
						
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
        
       public String buildIsoReport(String seriesId, String datasetId, JSONArray qualityIndicators) throws Exception {
           log.debug("Build ISO report");
            String reportTemplate = reportTemplateDir + "/" + isoReportTemplateName;
            Document isoReportDoc = xmlService.file2Document(reportTemplate);

            String reportUrl = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + seriesId + "/datasets/" + datasetId + "?" + HTTP_ACCEPT_PARAM + "=application/x-ipynb%2Bjson";

            Node onlineRs = XpathUtils.getNodeByXPath(isoReportDoc, "./mdq:DQ_DataQuality/mdq:standaloneQualityReport/mdq:DQ_StandaloneQualityReportInformation/mdq:reportReference/cit:CI_Citation/cit:onlineResource/cit:CI_onlineResource/cit:linkage/igco:CharacterString");
            if (onlineRs != null) {
                onlineRs.setTextContent(reportUrl);
            }
            
            Node protocol = XpathUtils.getNodeByXPath(isoReportDoc, "./mdq:DQ_DataQuality/mdq:standaloneQualityReport/mdq:DQ_StandaloneQualityReportInformation/mdq:reportReference/cit:CI_Citation/cit:onlineResource/cit:CI_onlineResource/cit:protocol/igco:CharacterString");
            if (protocol != null) {
                URI uri = new URI(serverUrl);
                protocol.setTextContent(uri.getScheme());
            }
            
            Map<String, String> valueMap = new HashMap<>();
            Map<String, String> dateTimeMap = new HashMap<>();
            if (qualityIndicators != null) {
                for (int i = 0; i < qualityIndicators.length(); i++) {
                    JSONObject qualityIndicator = (JSONObject) qualityIndicators.get(i);
                    String isMeasurementOf = GeoJsonParser.getGeoJSONStringProperty(qualityIndicator, "isMeasurementOf");
                    String value = "";
                    
                    if (isMeasurementOf.endsWith(Constants.DEGRADED_DATA_PERCENTAGE_METRIC)) {
                        double d = GeoJsonParser.getGeoJSONDoubleProperty(qualityIndicator, "value");
                        value = d + "";
                    } else if (isMeasurementOf.endsWith(Constants.DEGRADED_ANCILLARY_DATA_PERCENTAGE_METRIC)) {
                        double d = GeoJsonParser.getGeoJSONDoubleProperty(qualityIndicator, "value");
                        value = d + "";
                    } else if (isMeasurementOf.endsWith(Constants.FORMAT_CORRECTNESS_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.GENERAL_QUALITY_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.GEOMETRIC_QUALITY_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.RADIOMETRIC_QUALITY_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.SENSOR_QUALITY_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.FEASIBILITY_CONTROL_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.DELIVERY_CONTROL_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.ORDINARY_CONTROL_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.DETAILED_CONTROL_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.HARMONIZATION_CONTROL_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.IP_FOR_LP_INFORMATION_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.LP_INTERPRETATION_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.LP_METADATA_CONTROL_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.LP_ORDINARY_CONTROL_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    } else if (isMeasurementOf.endsWith(Constants.LP_THEMATIC_VALIDATION_METRIC)) {
                        boolean b = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
                        value = Boolean.toString(b);
                    }
                                  
                    String dateTime = GeoJsonParser.getGeoJSONStringProperty(qualityIndicator, "generatedAtTime");
                    
                    log.debug("isMeasurementOf: " + isMeasurementOf);
                    log.debug("value: " + value);
                    log.debug("dateTime: " + dateTime);
                    
                    if(StringUtils.isNotEmpty(isMeasurementOf)){
                        valueMap.put(isMeasurementOf, value);
                        dateTimeMap.put(isMeasurementOf, dateTime);
                    }
                }
            }else{
                log.debug("qualityIndicators is null");
            }
            
            NodeList reportNodes = XpathUtils.getNodesByXPath(isoReportDoc, "./mdq:DQ_DataQuality/mdq:report");
            if (reportNodes != null && reportNodes.getLength() > 0) {
               for (int i = 0; i < reportNodes.getLength(); i++) {
                   Node rNode = reportNodes.item(i);
                   String reportId = XpathUtils.getNodeValueByXPath(rNode, "./*/mdq:measure/mdq:DQ_MeasureReference/mdq:measureIdentification/mcc:MD_Identifier/mcc:code/igco:CharacterString");
                   log.debug("Report Id: " + reportId);
                   boolean noValue = true;

                   if (StringUtils.isNotEmpty(reportId)) {
                       if (valueMap.containsKey(reportId)) {
                           String value = valueMap.get(reportId);
                           if (StringUtils.isNotEmpty(value)) {
                               log.debug("Update report value to " + value);
                               Node resultNode = XpathUtils.getNodeByXPath(rNode, "./*/mdq:result/*/mdq:pass/igco:Boolean");
                               if (resultNode != null) {
                                   resultNode.setTextContent(value);
                               }

                               String date = dateTimeMap.get(reportId);
                               Node dateNode = XpathUtils.getNodeByXPath(rNode, "./*/mdq:result/*/mdq:dateTime/igco:DateTime");
                               if (StringUtils.isNotEmpty(date)) {
                                   if (dateNode != null) {
                                       dateNode.setTextContent(date);
                                   }
                               } else {
                                   if (dateNode != null) {
                                       dateNode.getParentNode().getParentNode().removeChild(dateNode.getParentNode());
                                   }
                               }
                               noValue = false;
                           }
                       }
                   }

                   if (noValue) {
                       log.debug("Remove report value element");
                       Node resultNode = XpathUtils.getNodeByXPath(rNode, "./*/mdq:result");
                       if (resultNode != null) {
                           resultNode.getParentNode().removeChild(resultNode);
                       }

                       log.debug("Add an empty report value element");
                       Node measureNode = XpathUtils.getNodeByXPath(rNode, "./*/mdq:measure");
                       if (measureNode != null) {
                           Element resultElem = isoReportDoc.createElementNS("http://standards.iso.org/iso/19157/-2/mdq/1.0", "mdq:result");
                           resultElem.setAttributeNS("http://standards.iso.org/iso/19115/-3/gco/1.0", "gco:nilReason", "inapplicable");
                           measureNode.getParentNode().appendChild(resultElem);
                       }
                   }
               }
           }
            
            /*
                Clean comments
             */
            NodeList commentNodes = XpathUtils.getNodesByXPath(isoReportDoc, "//comment()");
            if (commentNodes != null) {
                for (int i = 0; i < commentNodes.getLength(); i++) {
                    Node cNode = commentNodes.item(i);
                    if (cNode.getParentNode() != null) {
                        cNode.getParentNode().removeChild(cNode);
                    } else {
                        log.debug("Could not remove the comment because its parent node is null");
                    }
                }
            }
            return xmlService.format(isoReportDoc);
        }
}
