package be.spacebel.catalog.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import be.spacebel.catalog.models.CatalogResponse;
import be.spacebel.catalog.models.SolrCollection;
import be.spacebel.catalog.services.CatalogHandler;
import be.spacebel.catalog.services.MetadataHandler;
import be.spacebel.catalog.services.OSDDHandler;
import be.spacebel.catalog.services.SolrHandler;
import be.spacebel.catalog.utils.BundleUtils;
import be.spacebel.catalog.utils.Constants;
import be.spacebel.catalog.utils.FileUtils;
import be.spacebel.catalog.utils.parser.GeoJsonParser;
import be.spacebel.catalog.services.XMLService;
import be.spacebel.catalog.utils.xml.XpathUtils;
import be.spacebel.catalog.utils.zip.ZipUtils;
import org.json.JSONException;

@Controller
@Path("/")
public class CatalogController {
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(CatalogController.class);
	
	
	@Value("${xsl.dir}")
	private String xslDir;
	
	@Value("${repository.dir}")
	private String repoDir;
	
	@Value("${server.url}")
	private String serverUrl; 
	
//        @Value("${jupyter.notebook.template.location}")
//	private String reportTemplateLocation;                 
        
        @Value("${report.template.dir}")
	private String reportTemplateDir;

        @Value("${jupyter.default_report.template.name}")
	private String defaultReportTemplateName; 
        
        @Value("${jupyter.report.template.name.pattern}")
	private String reportTemplateNamePattern; 
        
        @Value("${jupyter.notebook.template.product.id.token}")
	private String reportTemplateProductIdToken; 
        
        @Value("${jupyter.notebook.template.parent.id.token}")
	private String reportTemplateParentIdToken; 
        
	private SolrHandler solrHandler;
	
	private CatalogHandler catHandler;
	
	private OSDDHandler osddHandler;
	
	private MetadataHandler metadataHandler;

	private XMLService xmlService;

	@Autowired
	public CatalogController(SolrHandler solrHandler, CatalogHandler catalogHandler,
			OSDDHandler osddHandler, MetadataHandler metadataHandler, XMLService xmlService) {
		this.solrHandler = solrHandler;
		this.catHandler = catalogHandler;
		this.osddHandler = osddHandler;		
		this.metadataHandler = metadataHandler;
		this.xmlService = xmlService;
	}

    @GET
    @Path("/isAlive")
    public Response livenessTest() throws IOException {
            ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK);
            return responseBuilder.build();
    }

    
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getLandingPage() throws IOException {
    	String s = "";
		try {
			s = FileUtils.readFile(xslDir + "/landingPage.json");
			s = StringUtils.replace(s, "SERVER_URL", serverUrl);
			JSONObject metadataObj = new JSONObject(s);
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(metadataObj.toString(2));		
    		responseBuilder.header("Access-Control-Allow-Origin", "*");
    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
    		return responseBuilder.build();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();				
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(null);		
    		responseBuilder.header("Access-Control-Allow-Origin", "*");
    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
    		return responseBuilder.build();
		}
    }
    
	
	@POST
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response postQualityInformation(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		String metadata;
		try {
			
			Map<String, String> requestParams = getRequestParameters(httpRequest);
			requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
			requestParams.put(Constants.UID_PARAM, datasetId);
			requestParams.put(Constants.START_INDEX_PARAM, "1");
			requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
			
			metadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);			
			String[] error = catHandler.addDatasetQualityInformation(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET, metadata);
			
			if (!StringUtils.isEmpty(error[0])) {
				return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
						"request");
			}
			
		} catch (Exception e) {
			log.error("", e);			
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_CREATED).entity("Quality Information has been created.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@OPTIONS
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response optionsQualityInformation(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(null);		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@PUT
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response putQualityInformation(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		String metadata;
		try {
			
			Map<String, String> requestParams = getRequestParameters(httpRequest);
			requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
			requestParams.put(Constants.UID_PARAM, datasetId);
			requestParams.put(Constants.START_INDEX_PARAM, "1");
			requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
			
			metadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);			
			String error[] = catHandler.updateDatasetQualityInformation(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET, metadata);
			
			if (!StringUtils.isEmpty(error[0])) {
				return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
						"request");
			}
			
		} catch (Exception e) {
			log.error("", e);			
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity("Quality Information has been updated.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
//        @GET
//	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation")	
//	/**
//	 * HTTP get handler, process the get datasets of series requests
//	 */
//	public Response getQualityReport(@Context HttpServletRequest httpRequest, 
//                @HeaderParam("Accept") String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId) {
//
//            Map<String, String> requestParams = getRequestParameters(httpRequest);
//            String mimeType = "application/x-ipynb+json";
//            if (requestParams.containsKey(Constants.HTTP_ACCEPT_PARAM)) {
//                log.debug("In case of Query parameter.");
//                String httpAccept = requestParams.get(Constants.HTTP_ACCEPT_PARAM);
//                if(StringUtils.isNotEmpty(httpAccept)){
//                    mimeType = httpAccept;
//                }                
//            } else {
//                if (StringUtils.isNotEmpty(contentNegotiation)
//                        && BundleUtils.getResourceExtensions().containsValue(contentNegotiation)) {
//                    log.debug("In case of Content negotiation.");
//                    mimeType = contentNegotiation;
//                }
//            }
//
//            if (!"application/x-ipynb+json".equalsIgnoreCase(mimeType)) {
//                String message = BundleUtils.getMessage(BundleUtils.INVALID_MIMETYPE_ERROR);
//                message = StringUtils.replace(message, "{MIME_TYPE}", mimeType);
//                return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE,
//                        Constants.HTTP_ACCEPT_PARAM);
//            }
//                        
//            try {
//                // check existing of the dataset                
//                requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
//                requestParams.put(Constants.UID_PARAM, datasetId);
//
//                requestParams.put(Constants.HTTP_ACCEPT_PARAM, Constants.GEOJSON_MIME_TYPE);
//
//                requestParams.put(Constants.START_INDEX_PARAM, "1");
//                requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
//                requestParams.put(Constants.TYPE_PARAM, "dataset");
//                String requestURL = removeLastSlash(httpRequest.getRequestURL().toString());
//                requestParams.put(Constants.REQUEST_URL, requestURL);
//                requestParams.put(Constants.QUERY_STRING, httpRequest.getQueryString());
//
//                CatalogResponse searchResponse = catHandler.doSearch(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET);
//
//                if (searchResponse.getNumberOfMatch() <= 0) {
//
//                    String message = BundleUtils.getMessage(BundleUtils.DATASET_ID_NOT_FOUND_ERROR);
//                    message = StringUtils.replace(message, "{VALUE}", datasetId);
//
//                    return createErroResponse4Json(message, HttpStatus.SC_NOT_FOUND, "uid");
//                } else {
//                    try {
//                        Map<String, String> details = new HashMap<>();
//                        String templateContent = HTTPInvoker.invokeGET(reportTemplateLocation, details);
//                        if (details.get(HTTPInvoker.HTTP_GET_DETAILS_ERROR_CODE) != null || StringUtils.isEmpty(templateContent)) {
//                            log.error("Errors while downloading voila template via HTTP " + details.get(HTTPInvoker.HTTP_GET_DETAILS_ERROR_MSG));
//                            String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
//                            return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
//                        }
//
//                        templateContent = templateContent.replaceAll(reportTemplateParentIdToken, seriesId);
//                        templateContent = templateContent.replaceAll(reportTemplateProductIdToken, datasetId);
//
//                        ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(templateContent);
//
//                        responseBuilder.header("Content-Type", "application/x-ipynb+json; charset=UTF-8");
//                        responseBuilder.header("Access-Control-Allow-Origin", "*");
//                        responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
//                        responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
//                        return responseBuilder.build();
//
//                    } catch (IOException e) {
//                        log.error("Errors ", e);
//                        String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
//                        return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
//                    }
//                }
//            } catch (Exception e) {
//                log.error("Errors ", e);
//                String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
//                return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
//            }                      
//        }
        
	@POST
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response postQualityIndicators(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		String metadata;
		try {
				
				Map<String, String> requestParams = getRequestParameters(httpRequest);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				requestParams.put(Constants.UID_PARAM, datasetId);
				requestParams.put(Constants.START_INDEX_PARAM, "1");
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
				
				metadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);
				
				String error[] = catHandler.addDatasetQualityInformationAttributes(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET, metadata);
				
				if (!StringUtils.isEmpty(error[0])) {
					return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
							"request");
				}
				
		} catch (Exception e) {
			log.error("", e);
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_CREATED).entity("QualityIndicators has been created.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@OPTIONS
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response optionsQualityIndicators(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(null);		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@POST
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation/qualityIndicators")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response postQualityIndicatorsSubAttribute(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		String metadata;
		try {
				
				Map<String, String> requestParams = getRequestParameters(httpRequest);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				requestParams.put(Constants.UID_PARAM, datasetId);
				requestParams.put(Constants.START_INDEX_PARAM, "1");
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
				
				metadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);
				
				String[] error = catHandler.addDatasetQualityIndicatorsAttributes(requestParams, SolrCollection.DATASET, metadata);
				
				if (!StringUtils.isEmpty(error[0])) {
					return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
							"request");
				}
				
				
		} catch (Exception e) {
			log.error("", e);
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_CREATED).entity("QualityIndicators child elements has been created.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@OPTIONS
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation/qualityIndicators")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response optionsQualityIndicatorsSubAttribute(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(null);		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@PUT
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation/qualityIndicators")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response putQualityIndicators(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		String metadata;
		try {
			
			Map<String, String> requestParams = getRequestParameters(httpRequest);
			requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
			requestParams.put(Constants.UID_PARAM, datasetId);
			requestParams.put(Constants.START_INDEX_PARAM, "1");
			requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
			
			metadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);			
			String error[] = catHandler.updateDatasetQualityIndicators(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET, metadata);
			
			if (!StringUtils.isEmpty(error[0])) {
				return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
						"request");
			}	
			
		} catch (Exception e) {
			log.error("", e);			
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity("Quality Indicators has been updated.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@PUT
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation/qualityIndicators/{attributeName}")
	@Consumes("application/geo+json")
	/**
	 * Update qualityIndicators attribute
	 * 
	 */
	public Response putQualityIndicatorsSubAttribute(@Context HttpServletRequest httpRequest, @HeaderParam("Accept") String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, @PathParam("attributeName") String attributeName, InputStream uploadedFileStream) {
		
		String metadata;
		try {
			
			
				
				Map<String, String> requestParams = getRequestParameters(httpRequest);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				requestParams.put(Constants.UID_PARAM, datasetId);
				requestParams.put(Constants.START_INDEX_PARAM, "1");
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
				
				metadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);
				
				
				String error[] = catHandler.updateDatasetQualityIndicatorsAttribute(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET, metadata, attributeName);
				
				if (!StringUtils.isEmpty(error[0])) {
					return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
							"request");
				}	
			
			
		} catch (Exception e) {
			log.error("", e);			
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(attributeName + " has been updated.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	
	@DELETE
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation/qualityIndicators/{attributeName}")
	/**
	 * Delete qualityIndicators attribute
	 *
	 **/	 
	public Response deleteQualityIndicatorsSubAttribute(@Context HttpServletRequest httpRequest, @HeaderParam("Accept") String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, @PathParam("attributeName") String attributeName) {

		try {
			
			Map<String, String> requestParams = getRequestParameters(httpRequest);
			requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
			requestParams.put(Constants.UID_PARAM, datasetId);
			requestParams.put(Constants.START_INDEX_PARAM, "1");
			requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
			
			String error[] = catHandler.deleteDatasetQualityIndicatorsAttribute(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET, attributeName);
			
			if (!StringUtils.isEmpty(error[0])) {
				return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
						"request");
			}
			
		} catch (Exception e) {
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);

		}
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NO_CONTENT).entity(null);				
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		return responseBuilder.build();
	}
	
	@OPTIONS
	@Path("/series/{seriesId}/datasets/{datasetId}/productInformation/qualityInformation/qualityIndicators/{attributeName}")
	/**
	 * Delete qualityIndicators attribute
	 *
	 **/	 
	public Response optionsQualityIndicatorsSubAttribute(@Context HttpServletRequest httpRequest, @HeaderParam("Accept") String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, @PathParam("attributeName") String attributeName) {
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(null);		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
		
	}
	
	
	@POST
	@Path("/series/{seriesId}/datasets/{datasetId}/links")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response createVia(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")
                String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		String viaLinks;
		try {
				
				Map<String, String> requestParams = getRequestParameters(httpRequest);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				requestParams.put(Constants.UID_PARAM, datasetId);
				requestParams.put(Constants.START_INDEX_PARAM, "1");
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
				
				viaLinks = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);
				
				String[] error = catHandler.createVia(requestParams, SolrCollection.DATASET, viaLinks);
				
				if (!StringUtils.isEmpty(error[0])) {
					return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
							"request");
				}
				
				
		} catch (Exception e) {
			log.error("", e);
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_CREATED).entity("Via child element has been created.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@POST
	@Path("/series/{seriesId}/datasets/{datasetId}/links/via")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response addLinksVia(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		String imagesMetadata;
		try {
				
				Map<String, String> requestParams = getRequestParameters(httpRequest);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				requestParams.put(Constants.UID_PARAM, datasetId);
				requestParams.put(Constants.START_INDEX_PARAM, "1");
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
				
				imagesMetadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);
				
				JSONArray viaLinks = new JSONArray(imagesMetadata);				
				String errorLink = validateViaLinks(viaLinks, httpRequest);
				if (StringUtils.isNotEmpty(errorLink)) {						
					ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NOT_FOUND)
							.entity("Dataset not found for " + errorLink);
					responseBuilder.header("Access-Control-Allow-Origin", "*");
					responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");								
					return responseBuilder.build();
				}
	
				String[] error = catHandler.addLinksToVia(requestParams, SolrCollection.DATASET, imagesMetadata);
								
				if (!StringUtils.isEmpty(error[0])) {
					return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
							"request");
				} else {
					if (viaLinks != null) {
						catHandler.addRelatedLink(requestParams, viaLinks);
					}
					
				}
				
				
		} catch (Exception e) {
			log.error("", e);
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_CREATED).entity("Child links have been added to via.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@PUT
	@Path("/series/{seriesId}/datasets/{datasetId}/links/via")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response updateViaLink(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		String imagesMetadata;
		try {
				
				Map<String, String> requestParams = getRequestParameters(httpRequest);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				requestParams.put(Constants.UID_PARAM, datasetId);
				requestParams.put(Constants.START_INDEX_PARAM, "1");
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
				
				imagesMetadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);
				
				JSONArray viaLinks = new JSONArray(imagesMetadata);				
				validateViaLinks(viaLinks, httpRequest);
				
				String errorLink = validateViaLinks(viaLinks, httpRequest);
				if (StringUtils.isNotEmpty(errorLink)) {						
					ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NOT_FOUND)
							.entity("Dataset not found for " + errorLink);
					responseBuilder.header("Access-Control-Allow-Origin", "*");
					responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");								
					return responseBuilder.build();
				}
				
				String[] error = catHandler.updateViaLinks(requestParams, SolrCollection.DATASET, imagesMetadata);
				
				if (!StringUtils.isEmpty(error[0])) {
					return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
							"request");
				}
				
				
		} catch (Exception e) {
			log.error("", e);
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity("Via links have been updated.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@DELETE
	@Path("/series/{seriesId}/datasets/{datasetId}/links/via/{viaSeriesId}/datasets/{viaDatasetId}")
	@Consumes("application/geo+json")
	/**
	 * HTTP update an existed dataset
	 * dataset metadata
	 */
	public Response deleteAViaLik(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, @PathParam("viaSeriesId") String viaSeriesId,  @PathParam("viaDatasetId") String viaDatasetId) {
		
		
		try {
				
				Map<String, String> requestParams = getRequestParameters(httpRequest);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				requestParams.put(Constants.UID_PARAM, datasetId);
				requestParams.put(Constants.START_INDEX_PARAM, "1");
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
				
				String[] error = catHandler.deleteOneViaLink(requestParams, SolrCollection.DATASET, viaSeriesId, viaDatasetId);
				
				if (!StringUtils.isEmpty(error[0])) {
					return createErroResponse4Json(error[1], Integer.parseInt(error[0]),
							"request");
				}
				
				
		} catch (Exception e) {
			log.error("", e);
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}
		
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NO_CONTENT).entity("Via links have been updated.");		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@OPTIONS
	@Path("/datasets")
	@Consumes("application/geo+json")
	/**
	 * HTTP post handler for request whose content type is application/geo+json (post) a single
	 * dataset metadata
	 */
	public Response postGeoJSONDatasetMimeType(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, InputStream uploadedFileStream) {
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NO_CONTENT).entity(null);		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	
	}
	
	@POST
	@Path("/datasets")
	@Consumes("application/geo+json")
	/**
	 * HTTP post handler for request whose content type is application/geo+json (post) a single
	 * dataset metadata
	 */   
	public Response postDatasetGeoJsonMimeType(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, InputStream uploadedFileStream) {		
		return processPostGeoJSONDataset(null, httpRequest, uploadedFileStream);	
	}
	
	@POST
	@Path("/series/{seriesId}/datasets")
	@Consumes("application/geo+json")
	/**
	 * HTTP get handler, process the get datasets of series requests
	 */
	public Response postDatasetGeoJsonMimeType(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, InputStream uploadedFileStream) {
		
		return processPostGeoJSONDataset(seriesId, httpRequest, uploadedFileStream);
	
	}
	
	@PUT
	@Path("/series/{seriesId}/datasets/{datasetId}")
	@Consumes("application/geo+json")
	/**
	 * HTTP get handler, process the get datasets of series requests
	 */
	public Response putDatasetGeoJsonMimeType(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId, InputStream uploadedFileStream) {
		
		return processPutGeoJSONDataset(seriesId, datasetId, httpRequest, uploadedFileStream);
	}
	
	private Response processPostGeoJSONDataset(String seriesId, HttpServletRequest httpRequest, InputStream uploadedFileStream) {
		
		log.debug("Dataset with GeoJSON file .............");
		
		String[] result;
		String datasetId  = null;
		try {			
			String metadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);
			datasetId = getIdentifierFromGeoJsonMetadata(metadata);
			
			if (datasetId != null) {
				
				Map<String, String> requestParams = getRequestParameters(httpRequest);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				requestParams.put(Constants.UID_PARAM, datasetId);
				requestParams.put(Constants.START_INDEX_PARAM, "1");
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
				requestParams.put(Constants.TYPE_PARAM, "dataset");
				String requestURL = removeLastSlash(httpRequest.getRequestURL().toString());
				requestParams.put(Constants.REQUEST_URL, requestURL);
				requestParams.put(Constants.QUERY_STRING, httpRequest.getQueryString());
				
				CatalogResponse searchResponse = catHandler.doSearch(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET);
				
				if (searchResponse.getNumberOfMatch() != 0) {
					ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_CONFLICT)
							.entity("Dataset " + datasetId + " is . Use PUT method to update.");
				
					String location = StringUtils.EMPTY;		
					location = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + seriesId + "/datasets/" + datasetId;				
					responseBuilder.header("Location", location);
					responseBuilder.header("Access-Control-Allow-Origin", "*");
					responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
							
					return responseBuilder.build();
				} else {
					
					
					JSONObject metadataObj = new JSONObject(metadata);
					JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");					
					String errorLink  = StringUtils.EMPTY;
					JSONArray viaLinks = null;
					if (properties != null) {
						JSONObject links =  GeoJsonParser.getGeoJSONObjectProperty(properties, "links");
						if (links != null) {
							viaLinks =  GeoJsonParser.getGeoJSONArrayProperty(links, "via");					
							errorLink = validateViaLinks(viaLinks, httpRequest);
						}
					}
					
					if (StringUtils.isNotEmpty(errorLink)) {						
						ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NOT_FOUND)
								.entity("Dataset not found for " + errorLink);
						responseBuilder.header("Access-Control-Allow-Origin", "*");
						responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");								
						return responseBuilder.build();
					}
					
					
					result = solrHandler.postDatasetMetadata(seriesId, metadata, Constants.GEOJSON_SCHEMA);
					
					if (viaLinks != null) {
						catHandler.addRelatedLink(requestParams, viaLinks);
					}
					
					
					datasetId = result[0];
					seriesId = result[1];
					ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_CREATED)
								.entity("Dataset " + datasetId + " has been inserted.");
							
					String location = StringUtils.EMPTY;		
					location = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + seriesId + "/datasets/" + datasetId;
					
					responseBuilder.header("Location", location);
					responseBuilder.header("Access-Control-Allow-Origin", "*");
					responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
							
					return responseBuilder.build();
				}
				
			} else {
				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_BAD_REQUEST)
						.entity("The metadata does not contain dataset identifier.");	
				responseBuilder.header("Access-Control-Allow-Origin", "*");
				responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
				return responseBuilder.build();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String message =  e.getMessage(); //BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);			
			return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, "seriesId");
		}		
	}
	
	private Response processPutGeoJSONDataset(String seriesId, String datasetId, HttpServletRequest httpRequest, InputStream uploadedFileStream) {

		log.debug("Dataset with GeoJSON file .............");
		
		try {
			
			Map<String, String> requestParams = getRequestParameters(httpRequest);
			requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
			requestParams.put(Constants.UID_PARAM, datasetId);
			requestParams.put(Constants.START_INDEX_PARAM, "1");
			requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
			requestParams.put(Constants.TYPE_PARAM, "dataset");
			String requestURL = removeLastSlash(httpRequest.getRequestURL().toString());
			requestParams.put(Constants.REQUEST_URL, requestURL);
			requestParams.put(Constants.QUERY_STRING, httpRequest.getQueryString());
			
			CatalogResponse searchResponse = catHandler.doSearch(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET);
			
			if (searchResponse.getNumberOfMatch() == 0) {
				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NOT_FOUND)
						.entity("Dataset " + datasetId + " not found.");
			
				String location = StringUtils.EMPTY;		
				location = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + seriesId + "/datasets/" + datasetId;				
				responseBuilder.header("Location", location);
				responseBuilder.header("Access-Control-Allow-Origin", "*");
				responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
						
				return responseBuilder.build();
			} else {
				String metadata = metadataHandler.getMetataAsStringFromInputStream(uploadedFileStream);
				
				JSONObject metadataObj = new JSONObject(metadata);
				JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
				
				String errorLink  = StringUtils.EMPTY;
				if (properties != null) {
					JSONObject links =  GeoJsonParser.getGeoJSONObjectProperty(properties, "links");
					if (links != null) {
						JSONArray viaLinks =  GeoJsonParser.getGeoJSONArrayProperty(links, "via");					
						errorLink = validateViaLinks(viaLinks, httpRequest);
					}					
				}
				
				if (StringUtils.isNotEmpty(errorLink)) {
					ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NOT_FOUND)
							.entity("Dataset not found for " + errorLink);
				
					String location = StringUtils.EMPTY;		
					location = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + seriesId + "/datasets/" + datasetId;				
					responseBuilder.header("Location", location);
					responseBuilder.header("Access-Control-Allow-Origin", "*");
					responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
							
					return responseBuilder.build();
				}
				solrHandler.postDatasetMetadata(seriesId, metadata, Constants.GEOJSON_SCHEMA);
				
				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK)
						.entity("Dataset " + datasetId + " has been updated.");
			
				String location = StringUtils.EMPTY;		
				location = serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" + seriesId + "/datasets/" + datasetId;
				
				responseBuilder.header("Location", location);
				responseBuilder.header("Access-Control-Allow-Origin", "*");
				responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
						
				return responseBuilder.build();
			} 
			
			
		} catch (Exception e) {
			e.printStackTrace();
			String message =  e.getMessage(); //BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);			
			return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, "seriesId");
		}
		
		
	}
	
	
	
	@GET
	@Path("/description")
	/**
	 * HTTP get handler, process the get OSDD requests
	 */
	public Response getOSDD(@Context HttpServletRequest httpRequest,  @HeaderParam("Accept") String contentNegotiation) {
		
		
		Map<String, String> requestParams = getRequestParameters(httpRequest);
		String mimeType = requestParams.get(Constants.HTTP_ACCEPT_PARAM);
		
		if (mimeType == null) {
			mimeType = contentNegotiation;
		}
		
		
		if (mimeType == null) {
			mimeType = Constants.OPENAPI_MIME_TYPE;
		}
		
		if (mimeType.equals(Constants.OSDD_MIME_TYPE)) {					
			try {
				String requestURL = removeLastSlash(httpRequest.getRequestURL().toString());
				String osdd = osddHandler.getDefaultOSDD(requestURL);
				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(osdd);
				responseBuilder.header("Allow", "POST,GET,HEAD,OPTIONS,DELETE");
				responseBuilder.header("Access-Control-Allow-Origin", "*");
				responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
				responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
				responseBuilder.header("Content-Type", Constants.OSDD_MIME_TYPE + "; charset=UTF-8");
				return responseBuilder.build();
			} catch (Exception e) {
				log.error("", e);
				String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
				return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
			}
			
		} else {
			
			String s = "";
			try {
				s = FileUtils.readFile(xslDir + "/openAPI.json");
				s = StringUtils.replace(s, "SERVER_URL", serverUrl);
				JSONObject metadataObj = new JSONObject(s);
				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(metadataObj.toString(2));
				
	    		responseBuilder.header("Access-Control-Allow-Origin", "*");
	    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
	    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
	    		responseBuilder.header("Content-Type", Constants.GEOJSON_MIME_TYPE + "; charset=UTF-8");
	    		return responseBuilder.build();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(null);		
	    		responseBuilder.header("Access-Control-Allow-Origin", "*");
	    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
	    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
	    		return responseBuilder.build();
			}
			
			
		}
	}
	
	@OPTIONS
	@Path("/description")
	/**
	 * HTTP get handler, process the get OSDD requests
	 */
	public Response optionOSDD(@Context HttpServletRequest httpRequest) {

		try {
			
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(null);
			responseBuilder.header("Allow", "POST,GET,HEAD,OPTIONS,DELETE");
			responseBuilder.header("Access-Control-Allow-Origin", "*");
			responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
			responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
			responseBuilder.header("Content-Type", Constants.OSDD_MIME_TYPE + "; charset=UTF-8");
			return responseBuilder.build();
		} catch (Exception e) {
			log.error("", e);
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
		}

	}
	
	@GET
    @Path("/conformance")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getConformance() throws IOException {
    	String s = "";
		try {
			s = FileUtils.readFile(xslDir + "/conformance.json");
			JSONObject metadataObj = new JSONObject(s);
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(metadataObj.toString(2));		
    		responseBuilder.header("Access-Control-Allow-Origin", "*");
    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
    		return responseBuilder.build();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();				
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(null);		
    		responseBuilder.header("Access-Control-Allow-Origin", "*");
    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
    		return responseBuilder.build();
		}
    }
    
    @GET
    @Path("/collections")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCollections() throws IOException {
    	String s = "";
		try {
			s = FileUtils.readFile(xslDir + "/collections.json");
			s = StringUtils.replace(s, "SERVER_URL", serverUrl);
			JSONObject metadataObj = new JSONObject(s);
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(metadataObj.toString(2));		
    		responseBuilder.header("Access-Control-Allow-Origin", "*");
    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
    		return responseBuilder.build();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();				
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(null);		
    		responseBuilder.header("Access-Control-Allow-Origin", "*");
    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
    		return responseBuilder.build();
		}
    }
    
    @GET
    @Path("/collections/{collectionId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCollection(@PathParam("collectionId") String collectionId) throws IOException {
    	String s = "";
		try {
			s = FileUtils.readFile(xslDir + "/collections-"  + collectionId +  ".json");
			s = StringUtils.replace(s, "SERVER_URL", serverUrl);
			JSONObject metadataObj = new JSONObject(s);
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(metadataObj.toString(2));		
    		responseBuilder.header("Access-Control-Allow-Origin", "*");
    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
    		return responseBuilder.build();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();				
			ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(null);		
    		responseBuilder.header("Access-Control-Allow-Origin", "*");
    		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
    		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
    		return responseBuilder.build();
		}
    }
	@GET
	@Path("/series/{seriesId}/description")
	/**
	 * HTTP get handler, process the get OSDD requests
	 */
	public Response getOSDDOfSeries(@Context HttpServletRequest httpRequest, @PathParam("seriesId") String seriesId) {
		
		log.debug("Get OSDD of series seriesId=" + seriesId + " .............");

		try {
			String requestURL = removeLastSlash(httpRequest.getRequestURL().toString());
			String osdd = osddHandler.getOSDDOfSeries(seriesId, requestURL);

			if (osdd == null) {
				String message = BundleUtils.getMessage(BundleUtils.SERIES_UNSEARCHABLE_ERROR);
				message = StringUtils.replace(message, "{VALUE}", seriesId);
				return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_NOT_FOUND, "uid");
			} else {

				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(osdd);
				responseBuilder.header("Access-Control-Allow-Origin", "*");
				responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
				responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
				responseBuilder.header("Content-Type", Constants.OSDD_MIME_TYPE + "; charset=UTF-8");
				return responseBuilder.build();
			}

		} catch (Exception e) {
			if (!StringUtils.isEmpty(e.getMessage())) {
				String message = e.getMessage();
				return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR,
						"parentIdentifier");
			} else {
				String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
				return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR,
						null);
			}

		}
	}
	
	@OPTIONS
	@Path("/series/{seriesId}/description")
	/**
	 * HTTP get handler, process the get OSDD requests
	 */
	public Response optionOSDDOfSeries(@PathParam("seriesId") String seriesId) {
		
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_OK).entity(null);		
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();	

	}

	@GET
	@Path("/series/{seriesId}/datasets")
	/**
	 * HTTP get handler, process the get datasets of series requests
	 */
	public Response getDatasets(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId) {

		log.debug("Get datasets of series seriesId " + seriesId + " .............");
		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		Response res = processDatasetSearch(httpRequest, contentNegotiation, requestParams, false, Constants.HTTP_GET, StringUtils.EMPTY);		
		return res;
	}

	@GET
	@Path("/series/{seriesId}/datasets.{resourceExt}")
	/**
	 * HTTP get handler, process the get datasets of series requests with resource
	 * extension
	 */
	public Response getDatasetsWithRsExt(@Context HttpServletRequest httpRequest, @HeaderParam("Accept") String contentNegotiation, @PathParam("seriesId") String seriesId,
			@PathParam("resourceExt") String resourceExt) {
		log.debug("Get datasets of series seriesId " + seriesId + ", resourceExt= " + resourceExt + " ............."
				+ " .............");

		if (resourceExt != null && resourceExt.contains(".")) {
			resourceExt = getRealResourceExt(resourceExt);
		}

		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);

		if (!resourceExt.equalsIgnoreCase("atom")) {
			return createErroResponse(resourceExt + " is not a valid resource extension for this request. ",
					Constants.ERROR_400_TEMPLATE, HttpStatus.SC_BAD_REQUEST, "request");
		}
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, false, Constants.HTTP_GET, resourceExt);
	}

	@GET
	@Path("/series/{seriesId}/datasets/{datasetId}")
	/**
	 * HTTP get handler, process the get datasets of series requests
	 */
	public Response getDataset(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId, @PathParam("datasetId") String datasetId) {
		log.debug("Get datasets of series seriesId=" + seriesId + ", datasetId= " + datasetId + " .............");

		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		requestParams.put(Constants.UID_PARAM, datasetId);
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, true, Constants.HTTP_GET, StringUtils.EMPTY);
	}

	@GET
	@Path("/series/{seriesId}/datasets/{datasetId}.{resourceExt}")
	/**
	 * HTTP get handler, process the get datasets of series requests with resource
	 * extension
	 */
	public Response getDatasetWithRsExt(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId,
			@PathParam("datasetId") String datasetId, @PathParam("resourceExt") String resourceExt) {
		log.debug("Get datasets of series seriesId=" + seriesId + ", datasetId= " + datasetId + " ............."
				+ ", resourceExt= " + resourceExt + " .............");
		Map<String, String> requestParams = getRequestParameters(httpRequest);

		datasetId = datasetId + getIdPartInResourceExt(resourceExt);

		if (resourceExt != null && resourceExt.contains(".")) {
			resourceExt = getRealResourceExt(resourceExt);
		}

		String recordSchema = BundleUtils.getResourceExtensions().get(resourceExt + ".recordSchema");
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		requestParams.put(Constants.UID_PARAM, datasetId);

		if (recordSchema != null) {
			requestParams.put(Constants.RECORD_SCHEMA_PARAM, recordSchema);
		}
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, true, Constants.HTTP_GET, resourceExt);
	}

	
	@HEAD
	@Path("/series/{seriesId}/datasets")
	/**
	 * HTTP HEAD handler, process the get one series requests
	 */
	public Response getDatasets4Head(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId) {
		log.debug("Get (with HEAD protocol) dataset of series seriesId=" + seriesId + " .............");
		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, true, Constants.HTTP_HEAD, StringUtils.EMPTY);
	}

	@HEAD
	@Path("/series/{seriesId}/datasets.{resourceExt}")
	/**
	 * HTTP HEAD handler, process the get one series requests
	 */
	public Response getDatasets4HeadWithRsExt(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId,
			@PathParam("resourceExt") String resourceExt) {
		log.debug("Get (with HEAD protocol) datasets of series seriesId " + seriesId + ", resourceExt= " + resourceExt
				+ " ............." + " .............");

		if (resourceExt != null && resourceExt.contains(".")) {
			resourceExt = getRealResourceExt(resourceExt);
		}

		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);

		if (!resourceExt.equalsIgnoreCase("atom")) {
			return createErroResponse(resourceExt + " is not a valid resource extension for this request. ",
					Constants.ERROR_400_TEMPLATE, HttpStatus.SC_BAD_REQUEST, "request");
		}
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, false, Constants.HTTP_HEAD, resourceExt);
	}

	@HEAD
	@Path("/series/{seriesId}/datasets/{datasetsId}")
	/**
	 * HTTP HEAD handler, process the get one series requests
	 */
	public Response getDataset4Head(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId,
			@PathParam("datasetsId") String datasetsId) {
		log.debug("Get (with HEAD protocol) datasets of series seriesId= " + seriesId + ", datasetsId= " + datasetsId
				+ ".............");
		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		requestParams.put(Constants.UID_PARAM, datasetsId);
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, true, Constants.HTTP_HEAD, StringUtils.EMPTY);
	}

	@HEAD
	@Path("/series/{seriesId}/datasets/{datasetId}.{resourceExt}")
	/**
	 * HTTP HEAD handler, process the get one series requests
	 */
	public Response getDataset4HeadWithRsExt(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId,
			@PathParam("datasetId") String datasetId, @PathParam("resourceExt") String resourceExt) {
		log.debug("Get (with HEAD protocol) datasets of series seriesId= " + ", datasetId= " + datasetId
				+ " ............." + ", resourceExt= " + resourceExt + " .............");
		Map<String, String> requestParams = getRequestParameters(httpRequest);

		datasetId = datasetId + getIdPartInResourceExt(resourceExt);

		if (resourceExt != null && resourceExt.contains(".")) {
			resourceExt = getRealResourceExt(resourceExt);
		}

		String recordSchema = BundleUtils.getResourceExtensions().get(resourceExt + ".recordSchema");

		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		requestParams.put(Constants.UID_PARAM, datasetId);

		if (recordSchema != null) {
			requestParams.put(Constants.RECORD_SCHEMA_PARAM, recordSchema);
		}
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, true, Constants.HTTP_HEAD, StringUtils.EMPTY);
	}
	

	@OPTIONS
	@Path("/series/{seriesId}/datasets")
	/**
	 * HTTP OPTIONS handler, process the get one series requests
	 */
	public Response getDatasets4Options(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId) {
		
		log.debug("Get (with OPTIONS protocol) datasets of series seriesId " + seriesId + " .............");
		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		Response res = processDatasetSearch(httpRequest, contentNegotiation, requestParams, false, Constants.HTTP_OPTIONS, StringUtils.EMPTY);		
		return res;
		
	}

	@OPTIONS
	@Path("/series/{seriesId}/datasets.{resourceExt}")
	/**
	 * HTTP OPTIONS handler, process the get one series requests
	 */
	public Response getDatasets4OptionsWithRsExt(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId,
			@PathParam("resourceExt") String resourceExt) {
		log.debug("Get (with OPTIONS protocol) datasets of series seriesId " + seriesId + ", resourceExt= "
				+ resourceExt + " ............." + " .............");

		if (resourceExt != null && resourceExt.contains(".")) {
			resourceExt = getRealResourceExt(resourceExt);
		}

		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);

		if (!resourceExt.equalsIgnoreCase("atom")) {
			ResponseBuilder responseBuilder;
			responseBuilder = Response.status(HttpStatus.SC_BAD_REQUEST).entity(null);
			return responseBuilder.build();
		}
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, false, Constants.HTTP_OPTIONS, resourceExt);
	}

	@OPTIONS
	@Path("/series/{seriesId}/datasets/{datasetsId}")
	/**
	 * HTTP OPTIONS handler, process the get one series requests
	 */
	public Response getDataset4Options(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId,
			@PathParam("datasetsId") String datasetsId) {
		log.debug("Get (with OPTIONS protocol) datasets of series seriesId= " + seriesId + ", datasetsId= " + datasetsId
				+ ".............");
		Map<String, String> requestParams = getRequestParameters(httpRequest);
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		requestParams.put(Constants.UID_PARAM, datasetsId);
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, true, Constants.HTTP_OPTIONS, StringUtils.EMPTY);
	}

	@OPTIONS
	@Path("/series/{seriesId}/datasets/{datasetId}.{resourceExt}")
	/**
	 * HTTP OPTIONS handler, process the get one series requests
	 */
	public Response getDataset4OptionsWithRsExt(@Context HttpServletRequest httpRequest, @HeaderParam("Accept")	String contentNegotiation, @PathParam("seriesId") String seriesId,
			@PathParam("datasetId") String datasetId, @PathParam("resourceExt") String resourceExt) {
		log.debug("Get (with OPTIONS protocol) datasets of series seriesId= " + ", datasetId= " + datasetId
				+ " ............." + ", resourceExt= " + resourceExt + " .............");
		Map<String, String> requestParams = getRequestParameters(httpRequest);

		datasetId = datasetId + getIdPartInResourceExt(resourceExt);

		if (resourceExt != null && resourceExt.contains(".")) {
			resourceExt = getRealResourceExt(resourceExt);
		}

		String recordSchema = BundleUtils.getResourceExtensions().get(resourceExt + ".recordSchema");
		requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
		requestParams.put(Constants.UID_PARAM, datasetId);

		if (recordSchema != null) {
			requestParams.put(Constants.RECORD_SCHEMA_PARAM, recordSchema);
		}
		return processDatasetSearch(httpRequest, contentNegotiation, requestParams, true, Constants.HTTP_OPTIONS, resourceExt);
	}

	/**
	 * Get all the parameters in the http request
	 *
	 * @return map that contains the http parameters
	 */
	private Map<String, String> getRequestParameters(HttpServletRequest httpRequest) {

		Map<String, String> params = new HashMap<String, String>();
		Enumeration<String> parameters = httpRequest.getParameterNames();
		if (parameters != null) {
			while (parameters.hasMoreElements()) {
				String key = parameters.nextElement();
				// log.debug("key = " + key);
				if (!params.containsKey(key)) {
					String value = httpRequest.getParameter(key);
					// log.debug("value = " + value);
					if (StringUtils.isEmpty(value)) {
						value = httpRequest.getParameter("amp;" + key);
					}

					if (StringUtils.isNotEmpty(value)) {
						key = key.replaceAll("amp;", "");
						params.put(key, value);
					}
				}
			}
		} else {
			log.debug("NO PARAMS.");
		}
		return params;
	}
	
	
	@DELETE
	@Path("/series/{seriesId}/datasets")
	/**
	 * HTTP get handler, process the delete requests that delete all datasets that
	 * belong to a series
	 *
	 * @param seriesId
	 *            : identifier of the series to be deleted
	 * @return http response
	 */
	public Response deleteDatasets(@PathParam("seriesId") String seriesId) {

		try {

			log.debug("Delete dataset series seriesId = " + seriesId + " .............");
			catHandler.doDelete(Constants.PARENT_ID_PARAM, seriesId, SolrCollection.DATASET);
		} catch (Exception e) {
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);

		}
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NO_CONTENT).entity(null);
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
		responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");		
		return responseBuilder.build();
	}

	@DELETE
	@Path("/series/{seriesId}/datasets/{datasetsId}.{resourceExt}")
	/**
	 * HTTP get handler, process the get datasets of series requests with resource
	 * extension
	 */
	public Response deleteDatasetsWithExt(@PathParam("seriesId") String seriesId,
			@PathParam("datasetsId") String datasetsId, @PathParam("resourceExt") String resourceExt) {

		try {

			log.debug("Delete dataset series seriesId = " + seriesId + "dataset series datasetsId = " + datasetsId
					+ "resourceExt= " + resourceExt + " .............");

			datasetsId = datasetsId + getIdPartInResourceExt(resourceExt);

			catHandler.doDelete("id", datasetsId, SolrCollection.DATASET);
		} catch (Exception e) {
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);

		}
		ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NO_CONTENT).entity(null);
		return responseBuilder.build();
	}

	@DELETE
	@Path("/series/{seriesId}/datasets/{datasetsId}")
	/**
	 * HTTP get handler, process the delete one datasets requests
	 *
	 * @param seriesId
	 *            : identifier of the series that the dataset to be deleted belongs
	 *            to
	 * @param datasetsId
	 *            : identifier of the datasets to be deleted
	 * @return http response
	 */
	public Response deleteDatasets(@PathParam("seriesId") String seriesId, @PathParam("datasetsId") String datasetsId) {
			
		try {

			log.debug("Delete dataset series seriesId = " + seriesId + "dataset series datasetsId = " + datasetsId
					+ " .............");
			
			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
			requestParams.put(Constants.UID_PARAM, datasetsId);
			requestParams.put(Constants.START_INDEX_PARAM, "1");
			requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
			
				
			QueryResponse queryResponse = solrHandler.getMetadata(requestParams, SolrCollection.DATASET);
			SolrDocumentList results = queryResponse.getResults();
			
			if (results.isEmpty()) {
				String message = BundleUtils.getMessage(BundleUtils.DATASET_ID_NOT_FOUND_ERROR);
				message = StringUtils.replace(message, "{VALUE}", requestParams.get(Constants.UID_PARAM));
				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NOT_FOUND).entity(message);		
				responseBuilder.header("Access-Control-Allow-Origin", "*");
				responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
				return responseBuilder.build();
			} else {
				catHandler.doDelete("id", datasetsId, SolrCollection.DATASET);
				ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_NO_CONTENT).entity(null);		
				responseBuilder.header("Access-Control-Allow-Origin", "*");
				responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
				return responseBuilder.build();
			}
			
			
		} catch (Exception e) {
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);

		}
		
		
	}

	private String removeLastSlash(String url) {
		while (StringUtils.endsWith(url, "/")) {
			url = StringUtils.removeEnd(url, "/");
		}
		return url;
	}

	
	private Response processDatasetSearch(HttpServletRequest httpRequest, String contentNegotiation, Map<String, String> requestParams, boolean isPresent, String operation,
			String resourceExt) {
		try {

			String mimeType = getMediaType(contentNegotiation, requestParams, resourceExt);                       
                        String originalMimeType = mimeType;
                        
                        if(mimeType.equals(Constants.JUPYTER_NOTEBOOK_MIME_TYPE) 
                                || mimeType.equals(Constants.ISO_19157_2_MIME_TYPE)){
                            mimeType = Constants.GEOJSON_MIME_TYPE;
                        }
			requestParams.put(Constants.HTTP_ACCEPT_PARAM, mimeType);
                        
			String validateResult = validateHttpParameters(requestParams);

			if (!validateResult.equals("OK")) {                            
				String message = BundleUtils.getMessage(BundleUtils.INVALID_PARAM_ERROR);
				message = StringUtils.replace(message, "{PARAMETER_NAME}", validateResult);
				if (mimeType.equals(Constants.GEOJSON_MIME_TYPE)) {
					return createErroResponse4Json(message, HttpStatus.SC_BAD_REQUEST,
							validateResult);
				} else {
					return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_BAD_REQUEST,
							validateResult);
				}
				
			}
                        
			validateResult = validateHttpParameterValues(requestParams);
                        
			if (!validateResult.equals("OK")) {                                
				String paramValue = requestParams.get(validateResult);

				if (paramValue != null) {
					String message = BundleUtils.getMessage(BundleUtils.INVALID_PARAM_VALUE_ERROR);
					message = StringUtils.replace(message, "{PARAMETER_NAME}", validateResult);
					message = StringUtils.replace(message, "{VALUE}", paramValue);
					
					if (mimeType.equals(Constants.GEOJSON_MIME_TYPE)) {
						return createErroResponse4Json(message, HttpStatus.SC_BAD_REQUEST,
								validateResult);
					} else {
						return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_BAD_REQUEST,
								validateResult);
					}
					
				} else {
					String message = BundleUtils.getMessage(BundleUtils.MISSING_PARAM_VALUE_ERROR);
					message = StringUtils.replace(message, "{PARAMETER_NAME}", validateResult);
					return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_BAD_REQUEST,
							validateResult);
				}
			}

			/*
			 * mimeType = requestParams.get(Constants.HTTP_ACCEPT_PARAM); if (mimeType ==
			 * null) { if (StringUtils.isNotEmpty(contentNegotiation) &&
			 * (contentNegotiation.equals(Constants.ATOM_MIME_TYPE) || contentNegotiation
			 * .equals(Constants.EOP_OM10_MIME_TYPE) || contentNegotiation
			 * .equals(Constants.EOP_OM_MIME_TYPE))) { mimeType = contentNegotiation; } else
			 * { mimeType = Constants.ATOM_MIME_TYPE; } }
			 */
                        
			if ((mimeType.equals(Constants.EOP_GML_MIME_TYPE) 
                                || mimeType.equals(Constants.EOP_OM10_MIME_TYPE)
				|| mimeType.equals(Constants.ISO_19139_2_MIME_TYPE)) && !isPresent) {
                            
				String message = BundleUtils.getMessage(BundleUtils.INVALID_MIMETYPE_ERROR);
				message = StringUtils.replace(message, "{MIME_TYPE}", mimeType);
				return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE,
						Constants.HTTP_ACCEPT_PARAM);
			}

			requestParams.put(Constants.TYPE_PARAM, "dataset");

			if (!requestParams.containsKey(Constants.START_INDEX_PARAM)
					&& !requestParams.containsKey(Constants.START_PAGE_PARAM)) {
				requestParams.put(Constants.START_INDEX_PARAM, Constants.DEFAULT_START_INDEX);
			}

			if (!requestParams.containsKey(Constants.ITEM_PER_PAGE_PARAM)) {
				requestParams.put(Constants.ITEM_PER_PAGE_PARAM, Constants.DEFAULT_ITEM_PER_PAGE);
			}

			String requestURL = removeLastSlash(httpRequest.getRequestURL().toString());
			requestParams.put(Constants.REQUEST_URL, requestURL);
			requestParams.put(Constants.QUERY_STRING, httpRequest.getQueryString());


			CatalogResponse searchResponse = catHandler.doSearch(mimeType, requestParams, SolrCollection.DATASET);
			if (searchResponse.getNumberOfMatch() == 0 && isPresent) {

				if (operation.equals(Constants.HTTP_OPTIONS)) {
					ResponseBuilder responseBuilder;
					responseBuilder = Response.status(HttpStatus.SC_NOT_FOUND).entity(null);
					return responseBuilder.build();
				} else {
					String message = BundleUtils.getMessage(BundleUtils.DATASET_ID_NOT_FOUND_ERROR);
					message = StringUtils.replace(message, "{VALUE}", requestParams.get(Constants.UID_PARAM));
					
					if (mimeType.equals(Constants.GEOJSON_MIME_TYPE)) {
						return createErroResponse4Json(message, HttpStatus.SC_NOT_FOUND,
								"uid");
					} else {
						return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_NOT_FOUND, "uid");
					}
				}

			} else if (searchResponse.getNumberOfMatch() == -1) {

				String message = BundleUtils.getMessage(BundleUtils.INVALID_MIMETYPE_ERROR);
				message = StringUtils.replace(message, "{MIME_TYPE}", mimeType);
				return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE,
						Constants.HTTP_ACCEPT_PARAM);

			} else {
				
				String response;
                                switch (originalMimeType) {
                                    case Constants.GEOJSON_MIME_TYPE:
                                        response = searchResponse.getGeoJsonResponse();
                                        break;
                                    case Constants.JUPYTER_NOTEBOOK_MIME_TYPE:
//                                        Map<String, String> details = new HashMap<>();
//                                        String templateContent = HTTPInvoker.invokeGET(reportTemplateLocation, details);
//                                        if (details.get(HTTPInvoker.HTTP_GET_DETAILS_ERROR_CODE) != null || StringUtils.isEmpty(templateContent)) {
//                                            log.error("Errors while downloading voila template via HTTP " + details.get(HTTPInvoker.HTTP_GET_DETAILS_ERROR_MSG));
//                                            String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
//                                            return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);
//                                        }
                                        
                                        /*
                                            Check if this is a land product (qualityReport is existing)
                                        */
                                        boolean validResp = false;
                                        
                                        JSONObject metadataObj = new JSONObject(searchResponse.getGeoJsonResponse());
                                        JSONArray features = GeoJsonParser.getGeoJSONArrayProperty(metadataObj, "features");
                                        if (features != null && !features.isEmpty()) {
                                            JSONObject featureObj = (JSONObject) features.get(0);
                                            JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(featureObj, "properties");
                                            if(properties != null){
                                                JSONObject links = GeoJsonParser.getGeoJSONObjectProperty(properties, "links");
                                                if (links != null) {                
                                                    JSONArray qualityReportLinks = GeoJsonParser.getGeoJSONArrayProperty(links, "qualityReport");
                                                    if (qualityReportLinks != null) {                    
                                                        for (int i = 0; i < qualityReportLinks.length(); i++) {
                                                            Iterator<Object> qrLinkIter = qualityReportLinks.iterator();

                                                            while (qrLinkIter.hasNext()) {
                                                                Object element = qrLinkIter.next();

                                                                if (element instanceof JSONObject) {
                                                                    JSONObject qrLink = (JSONObject) element;
                                                                    String type = GeoJsonParser.getGeoJSONStringProperty(qrLink, "type");                                                                    
                                                                    if (type != null
                                                                            && Constants.JUPYTER_NOTEBOOK_MIME_TYPE.equalsIgnoreCase(type)) {
                                                                        validResp = true;
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }                                                
                                            }
                                        }
                                        
                                        if(validResp){
                                            String seriesId = requestParams.get(Constants.PARENT_ID_PARAM);
                                            String datasetId = requestParams.get(Constants.UID_PARAM);

                                            response = buildJupyterReport(seriesId, datasetId);
                                            break;
                                        }else{
                                            String message = BundleUtils.getMessage(BundleUtils.INVALID_MIMETYPE_ERROR);
                                            message = StringUtils.replace(message, "{MIME_TYPE}", originalMimeType);
                                            return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE,
                                                            Constants.HTTP_ACCEPT_PARAM);
                                        }
                                        
                                    case Constants.ISO_19157_2_MIME_TYPE:
                                        
                                        
                                        /*
                                            Check if this is a land product (qualityReport is existing)
                                        */
                                        validResp = false;
                                        
                                        metadataObj = new JSONObject(searchResponse.getGeoJsonResponse());
                                        features = GeoJsonParser.getGeoJSONArrayProperty(metadataObj, "features");
                                        if (features != null && !features.isEmpty()) {
                                            JSONObject featureObj = (JSONObject) features.get(0);
                                            JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(featureObj, "properties");
                                            if(properties != null){
                                                JSONObject links = GeoJsonParser.getGeoJSONObjectProperty(properties, "links");
                                                if (links != null) {                
                                                    JSONArray alternateLinks = GeoJsonParser.getGeoJSONArrayProperty(links, "alternates");
                                                    if (alternateLinks != null) {                    
                                                        for (int i = 0; i < alternateLinks.length(); i++) {
                                                            Iterator<Object> altLinkIter = alternateLinks.iterator();

                                                            while (altLinkIter.hasNext()) {
                                                                Object element = altLinkIter.next();

                                                                if (element instanceof JSONObject) {
                                                                    JSONObject altLink = (JSONObject) element;
                                                                    String type = GeoJsonParser.getGeoJSONStringProperty(altLink, "type");                                                                    
                                                                    if (type != null
                                                                            && Constants.ISO_19157_2_MIME_TYPE.equalsIgnoreCase(type)) {
                                                                        validResp = true;
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }                                                
                                            }
                                        }
                                        
                                        if(validResp){
                                            //metadataObj = new JSONObject(searchResponse.getGeoJsonResponse());
                                            //log.debug("metadata = " + searchResponse.getGeoJsonResponse());                                                                                

                                            features = GeoJsonParser.getGeoJSONArrayProperty(metadataObj, "features");
                                            JSONArray qiArray = null;
                                            if (features != null && !features.isEmpty()) {
                                                JSONObject featureObj = (JSONObject) features.get(0);
                                                JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(featureObj, "properties");
                                                log.debug("properties = " + properties.toString(2));

                                                JSONObject pInfoObj = GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation");

                                                if (pInfoObj != null) {
                                                    log.debug("productInformation = " + pInfoObj.toString(2));
                                                    JSONObject qInfoObj = GeoJsonParser.getGeoJSONObjectProperty(pInfoObj, "qualityInformation");
                                                    if (qInfoObj != null) {
                                                        log.debug("qualityInformation = " + qInfoObj.toString(2));
                                                        qiArray = GeoJsonParser.getGeoJSONArrayProperty(qInfoObj, "qualityIndicators");
                                                    }
                                                }
                                            }					

                                            String seriesId = requestParams.get(Constants.PARENT_ID_PARAM);
                                            String datasetId = requestParams.get(Constants.UID_PARAM);
                                            response = catHandler.buildIsoReport(seriesId, datasetId, qiArray);
                                            break;
                                        }else{
                                             String message = BundleUtils.getMessage(BundleUtils.INVALID_MIMETYPE_ERROR);
                                            message = StringUtils.replace(message, "{MIME_TYPE}", originalMimeType);
                                            return createErroResponse(message, Constants.ERROR_400_TEMPLATE, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE,
                                                            Constants.HTTP_ACCEPT_PARAM);
                                        }                                        
                                        
                                    default:
                                        response = xmlService.serializeDOM(searchResponse.getResponseDoc());
                                        break;

                                }				
				
				ResponseBuilder responseBuilder;

				if (operation.equals(Constants.HTTP_OPTIONS)) {
					responseBuilder = Response.status(HttpStatus.SC_OK).entity(null);
					responseBuilder.header("Allow", "POST,GET,HEAD,OPTIONS,DELETE");
				} else {
					responseBuilder = Response.status(HttpStatus.SC_OK).entity(response);
				}

				if (StringUtils.equals(Constants.EOP_OM_MIME_TYPE, mimeType)
						|| StringUtils.equals(Constants.EOP_OM10_MIME_TYPE, mimeType)) {
					originalMimeType = Constants.EOP_GML_MIME_TYPE;
				}
												
                                if (Constants.JUPYTER_NOTEBOOK_MIME_TYPE.equalsIgnoreCase(originalMimeType)) {
                                    responseBuilder.header("Content-Disposition", "attachment; filename=" + requestParams.get(Constants.UID_PARAM) + ".ipynb;");
                                }
                                
                                if (Constants.ISO_19157_2_MIME_TYPE.equalsIgnoreCase(originalMimeType)) {
                                    responseBuilder.header("Content-Type", "application/xml; charset=UTF-8");
                                }else{
                                    responseBuilder.header("Content-Type", originalMimeType + "; charset=UTF-8");
                                }
                                
				responseBuilder.header("Access-Control-Allow-Origin", "*");
				responseBuilder.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");		
				responseBuilder.header("Access-Control-Allow-Headers", "X-PINGOTHER, Content-Type");
				return responseBuilder.build();
			}
		} catch (Exception e) {
                    e.printStackTrace();
			String message = BundleUtils.getMessage(BundleUtils.INTERNAL_SERVER_ERROR);
			return createErroResponse(message, Constants.ERROR_500_TEMPLATE, HttpStatus.SC_INTERNAL_SERVER_ERROR, null);

		}
	}

	/**
	 * Create error message
	 *
	 * @param errorMsg
	 *            : initial error message
	 * @param errorTemplate
	 *            : OWS exception template template
	 * @return HTTP response with error
	 */
	private Response createErroResponse(String errorMsg, String errorTemplate, int httpErrorCode, String locator) {
		String msg = errorMsg;
		try {

			Document error = xmlService.file2Document(xslDir + File.separator + errorTemplate);

			msg = xmlService.serializeDOM(error);
			if (Constants.ERROR_400_TEMPLATE.equals(errorTemplate)) {
				msg = StringUtils.replace(msg, "@LOCATOR@", locator);
				msg = StringUtils.replace(msg, "@ERROR_MESSAGE@", errorMsg);
			}
		} catch (Exception e1) {
			log.error("", e1);

		}
		ResponseBuilder responseBuilder = Response.status(httpErrorCode).entity(msg);
		responseBuilder.header("Content-Type", Constants.TEXT_XML_MIME_TYPE + "; charset=UTF-8");
		return responseBuilder.build();
	}
	
	private Response createErroResponse4Json(String errorMsg, int httpErrorCode, String locator) {
		String msg = errorMsg;
		try {

			JSONObject response = new JSONObject();
			JSONObject error = new JSONObject();
			JSONArray errors = new JSONArray();
			JSONObject e = new JSONObject();
			e.put("location", locator);
			errors.put(e);
			error.put("errors", errors);
			error.put("code", httpErrorCode);
			error.put("message", errorMsg);
			response.put("error", error);
			msg = response.toString(2);	
			
		} catch (Exception e1) {
			log.error("", e1);

		}
		ResponseBuilder responseBuilder = Response.status(httpErrorCode).entity(msg);
		responseBuilder.header("Content-Type", Constants.GEOJSON_MIME_TYPE + "; charset=UTF-8");
		return responseBuilder.build();
	}

	/**
	 * Validate inputed http parameters are valid opensearch parameter
	 * 
	 * @param requestParams
	 * @return if all paramters are valid.
	 */
	private String validateHttpParameters(Map<String, String> requestParams) {
		String result = "OK";
		Iterator<String> params = requestParams.keySet().iterator();

		while (params.hasNext()) {
			String param = params.next();
			String osParam = BundleUtils.getEOPParam(param + ".value.description");
			if (osParam == null) {
				return param;
			}
		}
		return result;
	}

	/**
	 * Validate values of inputed http parameters
	 * 
	 * @param requestParams
	 * @return @return OK if all paramters' value are valid. Otherwise the invalid
	 *         parameter is returned.
	 */
	private String validateHttpParameterValues(Map<String, String> requestParams) {
		String result = "OK";
		Iterator<String> params = requestParams.keySet().iterator();
		while (params.hasNext()) {
			String param = params.next();
			String paramType = BundleUtils.getEOPParam(param + ".value.type");
			String value = requestParams.get(param);
			value = StringUtils.deleteWhitespace(value);
			if (StringUtils.endsWithIgnoreCase(paramType, "integer")) {
				try {
					String regEx = BundleUtils.getParam("integer.regex");
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(value);
					if (m != null) {
						if (!m.matches()) {
							return param;
						}
					}
					// Integer.parseInt(value);
				} catch (Exception e) {
					return param;
				}
			} else if (StringUtils.endsWithIgnoreCase(paramType, "integerNonNegative")) {
				try {
					String regEx = BundleUtils.getParam("integerNonNegative.regex");
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(value);
					if (m != null) {
						if (!m.matches()) {
							return param;
						}
					}
					// Integer.parseInt(value);
				} catch (Exception e) {
					return param;
				}
			} else if (StringUtils.endsWithIgnoreCase(paramType, "double")) {

				try {
					String regEx = BundleUtils.getParam("double.regex");
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(value);
					if (m != null) {
						if (!m.matches()) {
							return param;
						}
					}
					// Integer.parseInt(value);
				} catch (Exception e) {
					return param;
				}

			} else if (StringUtils.endsWithIgnoreCase(paramType, "enumeration")) {
				String[] values = BundleUtils.getEOPParam(param + ".value.enumeration").split(",");

				boolean found = false;
				for (int i = 0; i < values.length; i++) {
					if (StringUtils.endsWithIgnoreCase(values[i].trim(), value.trim())) {
						found = true;
					}
				}
				if (!found) {
					return param;
				}
			} else if (StringUtils.endsWithIgnoreCase(paramType, "date")) {
				try {
					String regEx = BundleUtils.getParam("date.regex");
					Pattern p = Pattern.compile(regEx);
					Matcher m = p.matcher(value);
					if (m != null) {
						if (!m.matches()) {
							return param;
						}
					}
					// Integer.parseInt(value);
				} catch (Exception e) {
					return param;
				}
			}

			if (param.equals(Constants.LAT_PARAM)) {
				double lat = Double.parseDouble(value);

				if (lat < -90.0 || lat > 90.0) {
					return Constants.LAT_PARAM;
				}

				if (requestParams.get(Constants.LON_PARAM) == null) {
					return Constants.LON_PARAM;
				}
			}

			if (param.equals(Constants.LON_PARAM)) {

				double lon = Double.parseDouble(value);

				if (lon < -180.0 || lon > 180.0) {
					return Constants.LON_PARAM;
				}

				if (requestParams.get(Constants.LAT_PARAM) == null) {
					return Constants.LAT_PARAM;
				}
			}
			
			if (param.equals(Constants.SPECIFICATION_TITLE_PARAM)) {
				if (!requestParams.containsKey(Constants.DEGREE_PARAM)) {
					return Constants.DEGREE_PARAM;
				}
			}
			
			if (param.equals(Constants.DEGREE_PARAM)) {
				if (!requestParams.containsKey(Constants.SPECIFICATION_TITLE_PARAM)) {
					return Constants.SPECIFICATION_TITLE_PARAM;
				}
			}
			

			if (param.equals(Constants.PRODUCT_TYPE_PARAM)) {
				if ((value.startsWith("{") && !value.endsWith("}"))
						|| (!value.startsWith("{") && value.endsWith("}"))) {
					return Constants.PRODUCT_TYPE_PARAM;
				} else {
					String productTypes = StringUtils.substringBefore(StringUtils.substringAfter(value, "{"), "}")
							.trim();
					if (productTypes.startsWith(",") || productTypes.endsWith(",")) {
						return Constants.PRODUCT_TYPE_PARAM;
					} else {
						if (productTypes.contains(",")) {
							String[] productTypeArr = productTypes.split(",");
							for (int k = 0; k < productTypeArr.length; k++) {
								if (StringUtils.isEmpty(productTypeArr[k])) {
									return Constants.PRODUCT_TYPE_PARAM;
								}
							}
						}
					}
				}

			}
		}
		return result;
	}

	
	private String getRealResourceExt(String resourceExt) {
		return StringUtils.substringAfterLast(resourceExt, ".");
	}

	private String getMediaType(String contentNegotiation, Map<String, String> parameters, String resourceExt) {

		if (parameters.containsKey(Constants.HTTP_ACCEPT_PARAM)) {
			log.debug("In case of Query parameter.");
			return parameters.get(Constants.HTTP_ACCEPT_PARAM);
		}

		// Resource extension: if it is provided and supported
		if (StringUtils.isNotEmpty(resourceExt) && BundleUtils.getResourceExtensions().containsKey(resourceExt)) {
			log.debug("In case of Resource extension.");
			return BundleUtils.getResourceExtensions().get(resourceExt);
		}

		if (StringUtils.isNotEmpty(contentNegotiation)
				&& BundleUtils.getResourceExtensions().containsValue(contentNegotiation)) {
			log.debug("In case of Content negotiation.");
			return contentNegotiation;
		}

		return BundleUtils.getResource(BundleUtils.DEFAULT_MIMETYPE);
	}

	private String getIdPartInResourceExt(String resourceExt) {
		if (StringUtils.isNotEmpty(resourceExt) && BundleUtils.getResourceExtensions().containsKey(resourceExt)) {
			return "";
		} else {
			String rsc = StringUtils.substringAfterLast(resourceExt, ".");
			if (StringUtils.isNotEmpty(rsc) && BundleUtils.getResourceExtensions().containsKey(rsc)) {
				return "." + StringUtils.substringBeforeLast(resourceExt, ".");
			} else {
				return "." + resourceExt;
			}
		}
	}
	
	
	
	private String getIdentifierFromGeoJsonMetadata(String metadata)
			throws Exception {
				
		
		String identifier = null;
		JSONObject metadataObj = new JSONObject(metadata);
		JSONObject properties = (JSONObject) metadataObj.get("properties");
		
		if (properties!= null) {
			identifier = (String) properties.get("identifier");		
		}
		
		return identifier; 
	}
	
	private String validateViaLinks(JSONArray viaLinks, HttpServletRequest httpRequest) {
		
		String errorLink = StringUtils.EMPTY;
		
	
		if (viaLinks != null ) {
			
			
			
			Map<String, String> requestParams = getRequestParameters(httpRequest);			
			requestParams.put(Constants.START_INDEX_PARAM, "1");
			requestParams.put(Constants.ITEM_PER_PAGE_PARAM, "1");
			requestParams.put(Constants.TYPE_PARAM, "dataset");
			String requestURL = removeLastSlash(httpRequest.getRequestURL().toString());
			requestParams.put(Constants.REQUEST_URL, requestURL);
			requestParams.put(Constants.QUERY_STRING, httpRequest.getQueryString());
			
			for (int j=0;j<viaLinks.length();j++) {
				JSONObject viaLink = viaLinks.getJSONObject(j);
				String href = GeoJsonParser.getGeoJSONStringProperty(viaLink, "href");
				String datasetId= StringUtils.substringAfterLast(href, "/");
				String tmp = StringUtils.substringAfterLast(href, "series/");
				String seriesId = StringUtils.substringBefore(tmp, "/datasets");
				
				requestParams.remove(Constants.PARENT_ID_PARAM);
				requestParams.put(Constants.PARENT_ID_PARAM, seriesId);
				//requestParams.remove(Constants.UID_PARAM);
				//requestParams.put(Constants.UID_PARAM, datasetId);
				
				CatalogResponse searchResponse;
				try {
					searchResponse = catHandler.doSearch(Constants.GEOJSON_MIME_TYPE, requestParams, SolrCollection.DATASET);
					
                                        // MNG: According to Marc, we don't need to check the existing of the dataset
//					if (searchResponse.getNumberOfMatch() == 0) {				
//						errorLink = href;
//						break;
//					}
					
				} catch (Exception e) {
                                    errorLink = href;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                                
                                if(StringUtils.isNotEmpty(errorLink)){
                                    break;
                                }
			}
			
		}
		return errorLink;
	}
        
        private String buildJupyterReport(String seriesId, String datasetId) throws IOException {
            log.debug(String.format("Build Jupyter report of %s / %s", seriesId, datasetId));
            try{
                String pId = seriesId.replaceAll("[^a-zA-Z0-9_-]", "_");
                String templatePath = reportTemplateDir + "/" + reportTemplateNamePattern.replaceAll("\\{ParentIdentifierToBeReplaced\\}", pId);
                log.debug("template path: " + templatePath);
                File templateFile = new File(templatePath);

                String templateContent;
                if (templateFile.exists()) {
                    templateContent = FileUtils.readFile(templatePath);
                } else {
                    templatePath = reportTemplateDir + "/" + defaultReportTemplateName;
                    templateFile = new File(templatePath);
                    if(templateFile.exists()){
                        templateContent = FileUtils.readFile(templatePath);
                    }else{
                        throw new IOException(String.format("Jupyter notebook report template %s does not exist",templatePath));
                    }                
                }

                templateContent = templateContent.replaceAll(reportTemplateParentIdToken, seriesId);
                templateContent = templateContent.replaceAll(reportTemplateProductIdToken, datasetId);

                try{
                    // format JSON
                    JSONObject tempObj = new JSONObject(templateContent);
                    templateContent = tempObj.toString(2);
                }catch(JSONException e){                    
                }
                return templateContent;
            }catch(IOException e){
                log.debug(e);
                throw e;
            }
        }
                
	
	@PostConstruct
	public void postConstruct() {
		new File(repoDir).mkdirs();
	}

}
