package be.spacebel.catalog.services;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Properties;

import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.json.JSONArray;
import org.json.JSONObject;

import be.spacebel.catalog.models.SolrCollection;
import be.spacebel.catalog.utils.BundleUtils;
import be.spacebel.catalog.utils.Constants;
import be.spacebel.catalog.utils.GeoUtils;
import be.spacebel.catalog.utils.parser.GeoJsonParser;
import be.spacebel.catalog.utils.xml.XpathUtils;

/**
 * This class is the interface to the SOLR engine. Its provide the operations to
 * insert metadata to the SOLR and retrieve metadata from SOLR
 *
 * @author tth
 *
 */
@Service
public class SolrHandler {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(SolrHandler.class);

	@Value("${xsl.dir}")
	private String xslDir;

	private SolrClient datasetClient;
	private XMLService xmlService;

	public SolrHandler(@Qualifier("dataset") SolrClient datasetClient,
			@Autowired XMLService xmlService) {
		this.datasetClient = datasetClient;
		this.xmlService = xmlService;
	}

	
	
	public QueryResponse getMetadata(Map<String, String> requestParams, SolrCollection solrCollection)
			throws Exception {

		String fQuery = "";
		Iterator<String> params = requestParams.keySet().iterator();
		log.debug("Request parameters: ");
		String delimeter = " AND ";

		while (params.hasNext()) {

			String param = params.next();
			String value = requestParams.get(param);
			log.debug("param: " + param + " ,value: " + value);
			if (!param.equals(Constants.QUERY_PARAM)) {
				if (value != null) {
					//String[] values = value.split(" ");
					//for (int i = 0; i < values.length; i++) {
						
						if (!StringUtils.isEmpty(value)) {
							if (param.equals(Constants.PARENT_ID_PARAM)) {
								String parentIdentifier = StringUtils.replace(value, ":", "\\:");
								parentIdentifier = StringUtils.replace(parentIdentifier, "+", "\\%2B");
								fQuery = fQuery + delimeter + Constants.PARENT_ID_PARAM + ":" + parentIdentifier;
							} else if (param.equals(Constants.UID_PARAM)) {
								String id = StringUtils.replace(value, ":", "\\:");
								id = StringUtils.replace(id, "+", "\\%2B");
								fQuery = fQuery + delimeter + "id:" + id;
							} else if (param.equals(Constants.BBOX_PARAM)) {
								String[] bbox = value.split(",");
								fQuery = fQuery + delimeter + "posList:[" + bbox[1] + "," + bbox[0] + " TO " + bbox[3]
										+ "," + bbox[2] + "]";
							} else if (param.equals(Constants.GEONAME_PARAM)) {
								String geoName = value;
								String radius = requestParams.get(Constants.RADIUS_PARAM);
								if (radius == null) {
									radius = BundleUtils.getResource(BundleUtils.RADIUS_DEFAULT_VALUE);
								}

								String[] bbox = GeoUtils.computeBboxFromGeoName(geoName, radius).split(",");
								fQuery = fQuery + delimeter + "posList:[" + bbox[1] + "," + bbox[0] + " TO " + bbox[3]
										+ "," + bbox[2] + "]";

							} else if (param.equals(Constants.LAT_PARAM)) {
								double lat = Double.parseDouble(value);
								double lon = Double.parseDouble(requestParams.get(Constants.LON_PARAM));

								String radius = requestParams.get(Constants.RADIUS_PARAM);
								if (radius == null) {
									radius = BundleUtils.getResource(BundleUtils.RADIUS_DEFAULT_VALUE);
								}

								String[] bbox = GeoUtils.createBBoxFromPointAndDistance(lon, lat,
										Double.parseDouble(radius) / Constants.KM_TO_M).split(",");

								fQuery = fQuery + delimeter + "posList:[" + bbox[1] + "," + bbox[0] + " TO " + bbox[3]
										+ "," + bbox[2] + "]";

							} else if (param.equals(Constants.CLASSIFIED_AS_PARAM)) {
								String keywordURI = value;
								if (keywordURI.startsWith("http://")) {
									keywordURI = StringUtils.substringAfter(keywordURI, "http://");
								} else if (keywordURI.startsWith("https://")) {
									keywordURI = StringUtils.substringAfter(keywordURI, "https://");
								}

								if (isExactMatched(Constants.CLASSIFIED_AS_PARAM)) {
									fQuery = fQuery + delimeter + "keywordURI:*" + keywordURI;
								} else {
									fQuery = fQuery + delimeter + "keywordURI:" + "*" + keywordURI + "*";
								}

							} else if (param.equals(Constants.START_DATE_PARAM)) {
								String date = value;
								if (!date.contains("T")) {
									date = date + "T00:00:00.000Z";
								} else {
									if (!date.endsWith("Z")) {
										date = date + "Z";
									}
								}
								fQuery = fQuery + delimeter + "endDate:[ " + date + " TO * ]";

							} else if (param.equals(Constants.END_DATE_PARAM)) {
								String date = value;
								if (!date.contains("T")) {
									date = date + "T23:59:59.999Z";
								} else {
									if (!date.endsWith("Z")) {
										date = date + "Z";
									}
								}
								fQuery = fQuery + delimeter + "startDate:[ * TO " + date + " ]";

							} else if (param.equals(Constants.PRODUCT_TYPE_PARAM)) {
								if (value.startsWith("{") && value.endsWith("}")) {
									String producTypeQuery = StringUtils.EMPTY;
									String productTypes = StringUtils
											.substringBefore(StringUtils.substringAfter(value, "{"), "}");
									productTypes = productTypes + ",";
									String[] productTypeArr = productTypes.split(",");

									for (int k = 0; k < productTypeArr.length; k++) {
										String productType = productTypeArr[k].trim();
										if (k == 0) {
											producTypeQuery = Constants.PRODUCT_TYPE_PARAM + ":" + productType + "*";
										} else {
											producTypeQuery = producTypeQuery + " OR " + Constants.PRODUCT_TYPE_PARAM
													+ ":" + productType + "*";
										}

									}
									fQuery = fQuery + delimeter + "(" + producTypeQuery + ")";
								} else {
									fQuery = fQuery + delimeter + Constants.PRODUCT_TYPE_PARAM + ":" + value.trim()
											+ "*";
									;
								}

							} else if (param.equals(Constants.PRODUCTION_STATUS_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.PRODUCTION_STATUS_PARAM, value.trim());
							} else if (param.equals(Constants.ACQ_TYPE_PARAM)) {
								fQuery = fQuery + delimeter + appendParameter(Constants.ACQ_TYPE_PARAM, value.trim());
							} else if (param.equals(Constants.ORBIT_DIRECTION_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.ORBIT_DIRECTION_PARAM, value.trim());
							} else if (param.equals(Constants.ORBIT_NUMBER_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.ORBIT_NUMBER_PARAM, value, "int");
							} else if (param.equals(Constants.TRACK_PARAM)) {
								fQuery = fQuery + delimeter + appendParameter(Constants.TRACK_PARAM, value.trim());

							} else if (param.equals(Constants.FRAME_PARAM)) {
								fQuery = fQuery + delimeter + appendParameter(Constants.FRAME_PARAM, value.trim());

							} else if (param.equals(Constants.SWATH_IDENTIFIER_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.SWATH_IDENTIFIER_PARAM, value.trim());
							} else if (param.equals(Constants.PLATFORM_SERIAL_ID_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.PLATFORM_SERIAL_ID_PARAM, value.trim());
							} else if (param.equals(Constants.CLOUD_COVER_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.CLOUD_COVER_PARAM, value, "int");

							} else if (param.equals(Constants.SNOW_COVER_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.SNOW_COVER_PARAM, value, "int");

							} else if (param.equals(Constants.LOWEST_LOCATION_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.LOWEST_LOCATION_PARAM, value, "double");
							} else if (param.equals(Constants.RESOLUTION_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.RESOLUTION_PARAM, value, "double");
							} else if (param.equals(Constants.DOPPLER_FREQ_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.DOPPLER_FREQ_PARAM, value, "double");	
							} else if (param.equals(Constants.HIGHEST_LOCATION_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.HIGHEST_LOCATION_PARAM, value, "double");

							} else if (param.equals(Constants.PRODUCT_VERSION_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.PRODUCT_VERSION_PARAM, value.trim());

							} else if (param.equals(Constants.PRO_QUAL_STATUS_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.PRO_QUAL_STATUS_PARAM, value.trim());

							} else if (param.equals(Constants.PRO_QUAL_DEG_TAG_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.PRO_QUAL_DEG_TAG_PARAM, value.trim());

							} else if (param.equals(Constants.PROCESSOR_NAME_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.PROCESSOR_NAME_PARAM, value.trim());

							} else if (param.equals(Constants.PROCESSING_CENTER_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.PROCESSING_CENTER_PARAM, value.trim());

							} else if (param.equals(Constants.CREATION_DATE_PARAM)) {
								fQuery = fQuery + delimeter + Constants.CREATION_DATE_PARAM + ":[ " + value.trim() + " TO " + value.trim() + " ]";

							} else if (param.equals(Constants.MODIFICATION_DATE_PARAM)) {
								
								fQuery = fQuery + delimeter + Constants.MODIFICATION_DATE_PARAM + ":[ " + value.trim() + " TO " + value.trim() + " ]";

							} else if (param.equals(Constants.PROCESSING_DATE_PARAM)) {								
								fQuery = fQuery + delimeter + Constants.PROCESSING_DATE_PARAM + ":[ " + value.trim() + " TO " + value.trim() + " ]";	
							} else if (param.equals(Constants.SENSOR_MODE_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.SENSOR_MODE_PARAM, value.trim());

							} else if (param.equals(Constants.SENSOR_TYPE_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.SENSOR_TYPE_PARAM, value.trim());

							} else if (param.equals(Constants.ARCHIVING_CENTER_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.ARCHIVING_CENTER_PARAM, value.trim());

							} else if (param.equals(Constants.PROCESSING_MODE_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.PROCESSING_MODE_PARAM, value.trim());

							} else if (param.equals(Constants.AVAILABILITY_TIME_PARAM)) {
								fQuery = fQuery + delimeter + Constants.AVAILABILITY_TIME_PARAM + ":" + value.trim()
										+ "*";

							} else if (param.equals(Constants.ACQ_STATION_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.ACQ_STATION_PARAM, value.trim());

							} else if (param.equals(Constants.ACQ_SUBTYPE_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.ACQ_SUBTYPE_PARAM, value.trim());

							} else if (param.equals(Constants.START_TIME_ASC_NODE_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.START_TIME_ASC_NODE_PARAM, value, "int");

							} else if (param.equals(Constants.COMP_TIME_ASC_NODE_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.COMP_TIME_ASC_NODE_PARAM, value, "int");
							} else if (param.equals(Constants.ILLU_AZI_ANGLE_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.ILLU_AZI_ANGLE_PARAM, value, "double");

							} else if (param.equals(Constants.ILLU_ZEN_ANGLE_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.ILLU_ZEN_ANGLE_PARAM, value, "double");
							} else if (param.equals(Constants.ILLU_ELE_ANGLE_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.ILLU_ELE_ANGLE_PARAM, value, "double");
							} else if (param.equals(Constants.POLAR_MODE_PARAM)) {
								fQuery = fQuery + delimeter + appendParameter(Constants.POLAR_MODE_PARAM, value.trim());

							} else if (param.equals(Constants.POLAR_CHANNELS_PARAM)) {								
								if (value.trim().contains(", ")) {
									fQuery = fQuery  
											+ parseQuery("polarisationChannels:", value.trim(), true);
								} else {
									
									if (isExactMatched(Constants.POLAR_CHANNELS_PARAM)) {
										fQuery = fQuery + delimeter
												+ "polarisationChannels:" + value.trim();
									} else {
										fQuery = fQuery + delimeter
												+ "polarisationChannels:" + "*" + value.trim() + "*";
									}
								}								
								
							} else if (param.equals(Constants.ANTENNA_LOOK_DIR_PARAM)) {
								fQuery = fQuery + delimeter
										+ appendParameter(Constants.ANTENNA_LOOK_DIR_PARAM, value.trim());

							} else if (param.equals(Constants.MIN_INCI_ANGLE_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.MIN_INCI_ANGLE_PARAM, value, "double");

							} else if (param.equals(Constants.MAX_INCI_ANGLE_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.MAX_INCI_ANGLE_PARAM, value, "double");
							} else if (param.equals(Constants.INCI_ANGLE_PARAM)) {
								fQuery = fQuery + delimeter
										+ parseNumericQuery(Constants.INCI_ANGLE_PARAM, value, "double");
							} else if (param.equals(Constants.SPECIFICATION_TITLE_PARAM)) {
								String degreeValue = requestParams.get(Constants.DEGREE_PARAM);
								
								if (value.equals(Constants.DEGRADED_DATA_PERCENTAGE_METRIC)) {
									fQuery = fQuery + delimeter
											+ parseNumericQuery(Constants.DEGRADED_DATA_PERCENTAGE_METRIC, degreeValue, "int");
									
								} else if (value.equals(Constants.DEGRADED_ANCILLARY_DATA_PERCENTAGE_METRIC)) {
									fQuery = fQuery + delimeter
											+ parseNumericQuery(Constants.DEGRADED_ANCILLARY_DATA_PERCENTAGE_METRIC, degreeValue, "int");
								} else if (value.equals(Constants.FORMAT_CORRECTNESS_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.FORMAT_CORRECTNESS_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.GENERAL_QUALITY_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.GENERAL_QUALITY_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.GEOMETRIC_QUALITY_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.GEOMETRIC_QUALITY_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.RADIOMETRIC_QUALITY_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.RADIOMETRIC_QUALITY_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.SENSOR_QUALITY_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.SENSOR_QUALITY_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.FEASIBILITY_CONTROL_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.FEASIBILITY_CONTROL_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.DELIVERY_CONTROL_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.DELIVERY_CONTROL_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.ORDINARY_CONTROL_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.ORDINARY_CONTROL_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.DETAILED_CONTROL_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.DETAILED_CONTROL_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.HARMONIZATION_CONTROL_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.HARMONIZATION_CONTROL_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.IP_FOR_LP_INFORMATION_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.IP_FOR_LP_INFORMATION_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.LP_INTERPRETATION_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.LP_INTERPRETATION_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.LP_METADATA_CONTROL_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.LP_METADATA_CONTROL_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.LP_ORDINARY_CONTROL_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.LP_ORDINARY_CONTROL_METRIC + ":" + degreeValue.trim().toLowerCase();
								} else if (value.equals(Constants.LP_THEMATIC_VALIDATION_METRIC)) {									
									fQuery = fQuery + delimeter + Constants.LP_THEMATIC_VALIDATION_METRIC + ":" + degreeValue.trim().toLowerCase();
								}
								
								System.out.println("++++++++++++++fQuery: " + fQuery);
							}
							
							
							

						}
					//}
				}
			}
		}

		String queryValue = requestParams.get(Constants.QUERY_PARAM);
		if (queryValue != null) {
			//if (solrCollection.equals(SolrCollection.SERIES)) {
				
				String kwQuery = StringUtils.substringAfter(parseQuery("keyword:", queryValue, isExactMatched(Constants.SUBJECT_PARAM)), delimeter);
				String titleQuery = StringUtils.substringAfter(parseQuery("title:", queryValue, isExactMatched(Constants.TITLE_PARAM)), delimeter);
				String descQuery =StringUtils.substringAfter( parseQuery("description:", queryValue, false), delimeter);
				String orgaQuery = StringUtils.substringAfter(parseQuery("organisation:", queryValue, isExactMatched(Constants.ORGANISATION_NAME_PARAM)), delimeter);
				
				if (requestParams.containsKey(Constants.UID_PARAM) || queryValue.contains(" ")) {
					String str = "((" + descQuery + ") OR (" + titleQuery + ") OR (" + orgaQuery + ") OR (" + kwQuery
							+ "))";
					fQuery = fQuery + delimeter + str;
				} else {
					String id = StringUtils.replace(queryValue, ":", "\\:");
					id = StringUtils.replace(id, "+", "\\%2B");
					String str = "((" + descQuery + ") OR (" + titleQuery + ") OR (" + orgaQuery + ") OR (" + kwQuery
							+ ") OR (id:" + id + "))";
					fQuery = fQuery + delimeter + str;
				}
				// fQuery = fQuery + parseQuery("keyword:", queryValue);
			//}
		}

		/* Parse subject parameter */
		queryValue = requestParams.get(Constants.SUBJECT_PARAM);
		if (queryValue != null) {			
			fQuery = fQuery + parseQuery("keyword:", queryValue, isExactMatched(Constants.SUBJECT_PARAM));
		}

		queryValue = requestParams.get(Constants.GEOMETRY_PARAM);
		if (queryValue != null) {
			String geometry = queryValue;
			fQuery = fQuery + delimeter + "posList:\"Intersects(" + geometry + ")\"";
		}

		/* Parse platform parameter */
		queryValue = requestParams.get(Constants.PLATFORM_PARAM);
		if (queryValue != null) {
			fQuery = fQuery + parseQuery("platform:", queryValue, isExactMatched(Constants.PLATFORM_PARAM));
		}

		/* Parse instrument parameter */
		queryValue = requestParams.get(Constants.INSTRUMENT_PARAM);
		if (queryValue != null) {
			fQuery = fQuery + parseQuery("instrument:", queryValue, isExactMatched(Constants.INSTRUMENT_PARAM));
		}

		/* Parse title parameter */
		queryValue = requestParams.get(Constants.TITLE_PARAM);
		if (queryValue != null) {			
			fQuery = fQuery + parseQuery("title:", queryValue, isExactMatched(Constants.TITLE_PARAM));
		}

		/* Parse organisation parameter */
		queryValue = requestParams.get(Constants.ORGANISATION_NAME_PARAM);
		if (queryValue != null) {			
			fQuery = fQuery + parseQuery("organisation:", queryValue, isExactMatched(Constants.ORGANISATION_NAME_PARAM));
		}

		/* Parse publisher parameter */
		queryValue = requestParams.get(Constants.PUBLISHER_PARAM);
		if (queryValue != null) {			
			fQuery = fQuery + parseQuery("organisation:", queryValue, isExactMatched(Constants.PUBLISHER_PARAM));
		}

		/* Parse nativeProductFormat parameter */
		queryValue = requestParams.get(Constants.NATIVE_PRODUCT_FORMAT_PARAM);
		if (queryValue != null) {
			fQuery = fQuery + parseQuery("nativeProductFormat:", queryValue, isExactMatched(Constants.NATIVE_PRODUCT_FORMAT_PARAM));
		}

		/* Parse processingLevel parameter */
		queryValue = requestParams.get(Constants.PROCESSING_LEVEL_PARAM);
		if (queryValue != null) {
			fQuery = fQuery + parseQuery("processingLevel:", queryValue, isExactMatched(Constants.PROCESSING_LEVEL_PARAM));
		}

		/* Parse useLimitation parameter */
		queryValue = requestParams.get(Constants.USE_LIMITATION);
		if (queryValue != null) {
			fQuery = fQuery + parseQuery("useLimitation:", queryValue, isExactMatched(Constants.USE_LIMITATION));
		}

		/* Parse otherConstraint parameter */
		queryValue = requestParams.get(Constants.OTHER_CONSTRAINT);
		if (queryValue != null) {
			fQuery = fQuery + parseQuery("otherConstraint:", queryValue, isExactMatched(Constants.OTHER_CONSTRAINT));
		}
		
		try {

			String start = "";
			if (requestParams.containsKey(Constants.START_INDEX_PARAM)) {
				start = String.valueOf(Integer.parseInt(requestParams.get(Constants.START_INDEX_PARAM)) - 1);
			} else if (requestParams.containsKey(Constants.START_PAGE_PARAM)) {
				int intemPerPage = Integer.parseInt(requestParams.get(Constants.ITEM_PER_PAGE_PARAM));
				int startPage = Integer.parseInt(requestParams.get(Constants.START_PAGE_PARAM));
				start = String.valueOf((startPage - 1) * intemPerPage);
			}

			HashMap<String, String> paramMap = new HashMap<>();
			
			paramMap.put("q", "*");
			paramMap.put("fq", StringUtils.substringAfter(fQuery, delimeter));
			paramMap.put("sort", "startDate desc");
			paramMap.put("start", start);
			paramMap.put("rows", requestParams.get(Constants.ITEM_PER_PAGE_PARAM));
			NamedList list = new NamedList(paramMap);

			if (solrCollection.equals(SolrCollection.SERIES)) {
				Properties facetMappings = BundleUtils.getFacetMappings();
				list.add("facet", "true");
				list.add("facet.limit", "500");

				for (Object o : facetMappings.keySet()) {
					String facet = (String) o;
					String solrFacet = BundleUtils.getFacetMapping(facet);
					if (solrFacet != null) {
						list.add("facet.field", solrFacet);
					}
				}
			}

			SolrParams params1 = SolrParams.toSolrParams(list);

			SolrClient solrClient = getClient(solrCollection);

			return solrClient.query(params1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error when querying solr", e);
		}
	}

	public void deleteFromSolr(String fieldName, String value, SolrCollection solrCollection) throws Exception {

		SolrClient solrClient = getClient(solrCollection);
		solrClient.deleteByQuery(
				fieldName + ":" + StringUtils.replace(value, ":", "\\:"));
		solrClient.commit();
	}

	/**
	 * Get list of keywords from the metadata
	 *
	 * @param xmlDoc
	 *            metadata
	 */
	private static ArrayList<String> getKeyWords(Document xmlDoc) {
		ArrayList<String> keywordsList = new ArrayList<>();
		List<String> keywords = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gmx:Anchor");

		keywordsList.addAll(keywords);

		keywords = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString");

		keywordsList.addAll(keywords);

		return keywordsList;

	}

	/**
	 * Get list of instrument from the metadata
	 *
	 * @param xmlDoc
	 *            metadata
	 */
	private static ArrayList<String> getInstruments(Document xmlDoc) {
		ArrayList<String> instrumentList = new ArrayList<String>();

		List<String> instruments = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:instrument/gmi:MI_Instrument/gmi:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gmx:Anchor");
		instrumentList.addAll(instruments);

		instruments = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:instrument/gmi:MI_Instrument/gmi:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString");
		instrumentList.addAll(instruments);

		instruments = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:instrument/gmi:MI_Instrument/gmi:citation/gmd:CI_Citation/gmd:title/gmx:Anchor");
		instrumentList.addAll(instruments);
		
		instruments = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:instrument/gmi:MI_Instrument/gmi:citation/gmd:CI_Citation/gmd:title/gco:CharacterString");
		instrumentList.addAll(instruments);

		instruments = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:thesaurusName/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString='Instruments']/gmd:keyword/gco:CharacterString");
		instrumentList.addAll(instruments);
		
		instruments = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:thesaurusName/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString='Instruments']/gmd:keyword/gmx:Anchor");
		instrumentList.addAll(instruments);

		return instrumentList;

	}

	/**
	 * Get list of platforms from the metadata
	 *
	 * @param xmlDoc
	 *            metadata
	 */
	private static ArrayList<String> getPlatforms(Document xmlDoc) {
		ArrayList<String> platformList = new ArrayList<>();

		List<String> platforms = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString");

		platformList.addAll(platforms);

		platforms = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:identifier/gmd:MD_Identifier/gmd:code/gmx:Anchor");
		platformList.addAll(platforms);

		platforms = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:thesaurusName/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString='Platforms']/gmd:keyword/gco:CharacterString");
		platformList.addAll(platforms);
		
		platforms = XpathUtils.getNodesValuesByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords[gmd:thesaurusName/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString='Platforms']/gmd:keyword/gmx:Anchor");
		platformList.addAll(platforms);

		return platformList;

	}

	/**
	 * Get list of keywoords from the metadata
	 *
	 * @param xmlDoc
	 *            metadata
	 */
	private static ArrayList<String> getKeyWordURIs(Document xmlDoc, boolean combined) {
		ArrayList<String> keywordURIList = new ArrayList<String>();
		NodeList anchors = XpathUtils.getNodesByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gmx:Anchor");

		for (int i = 0; i < anchors.getLength(); i++) {
			Element anchor = (Element) anchors.item(i);
			String uri = anchor.getAttributeNS(Constants.XLINK_NS, "href");
			String keyword = anchor.getTextContent();
			if (combined) {
				if (!keywordURIList.contains(uri + "#;#" + keyword)) {
					keywordURIList.add(uri + "#;#" + keyword);
				}
			} else {
				if (!keywordURIList.contains(uri)) {
					keywordURIList.add(uri);
				}
			}
		}

		anchors = XpathUtils.getNodesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:identifier/gmd:MD_Identifier/gmd:code/gmx:Anchor");

		for (int i = 0; i < anchors.getLength(); i++) {
			Element anchor = (Element) anchors.item(i);
			String uri = anchor.getAttributeNS(Constants.XLINK_NS, "href");
			String keyword = anchor.getTextContent();
			if (combined) {
				if (!keywordURIList.contains(uri + "#;#" + keyword)) {
					keywordURIList.add(uri + "#;#" + keyword);
				}
			} else {
				if (!keywordURIList.contains(uri)) {
					keywordURIList.add(uri);
				}
			}
		}

		anchors = XpathUtils.getNodesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:instrument/gmi:MI_Instrument/gmi:citation/gmd:CI_Citation/gmd:title/gmx:Anchor");
		for (int i = 0; i < anchors.getLength(); i++) {
			Element anchor = (Element) anchors.item(i);
			String uri = anchor.getAttributeNS(Constants.XLINK_NS, "href");
			String keyword = anchor.getTextContent();
			if (combined) {
				if (!keywordURIList.contains(uri + "#;#" + keyword)) {
					keywordURIList.add(uri + "#;#" + keyword);
				}
			} else {
				if (!keywordURIList.contains(uri)) {
					keywordURIList.add(uri);
				}
			}
		}
		
		anchors = XpathUtils.getNodesByXPath(xmlDoc,
				"//gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:platform/gmi:MI_Platform/gmi:instrument/gmi:MI_Instrument/gmi:citation/gmd:CI_Citation/gmd:identifier/gmd:MD_Identifier/gmd:code/gmx:Anchor");
		for (int i = 0; i < anchors.getLength(); i++) {
			Element anchor = (Element) anchors.item(i);
			String uri = anchor.getAttributeNS(Constants.XLINK_NS, "href");
			String keyword = anchor.getTextContent();
			if (combined) {
				if (!keywordURIList.contains(uri + "#;#" + keyword)) {
					keywordURIList.add(uri + "#;#" + keyword);
				}
			} else {
				if (!keywordURIList.contains(uri)) {
					keywordURIList.add(uri);
				}
			}
		}

		return keywordURIList;

	}

	private static ArrayList<String> geometryInfo(Document xmlDoc) {
		ArrayList<String> geometryInfo = new ArrayList<String>();

		String west = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal");
		String east = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal");
		String south = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude/gco:Decimal");
		String north = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude/gco:Decimal");

		if (west != null && east != null && south != null && north != null) {

			double w = Double.parseDouble(west);
			double e = Double.parseDouble(east);
			double n = Double.parseDouble(north);
			double s = Double.parseDouble(south);
			if ((w == e) && n == s) {
				geometryInfo.add("POINT(" + west + " " + south + ")");
			} else if ((w == e)) {
				geometryInfo.add("LINESTRING(" + west + " " + south + "," + west + " " + north + ")");
			} else if ((n == s)) {
				geometryInfo.add("LINESTRING(" + west + " " + north + "," + east + " " + south + ")");
			} else {
				if (e - w <= 180) {
					geometryInfo.add("POLYGON((" + west + " " + south + "," + east + " " + south + "," + east + " "
							+ north + "," + west + " " + north + "," + west + " " + south + "))");
				} else {
					geometryInfo.add("POLYGON((" + west + " " + south + "," + 0 + " " + south + "," + 0 + " " + north
							+ "," + west + " " + north + "," + west + " " + south + "))");
					geometryInfo.add("POLYGON((" + 0 + " " + south + "," + east + " " + south + "," + east + " " + north
							+ "," + 0 + " " + north + "," + 0 + " " + south + "))");
				}
			}

		} else {
			geometryInfo.add("POLYGON((-180 -90, 0 -90, 0 90, -180 90, -180 -90))");
			geometryInfo.add("POLYGON((0 -90, 180 -90, 180 90, 0 90, 0 -90))");
		}

		return geometryInfo;
	}

	private static String getOriginalGeometryInfo(Document xmlDoc) {
		String polygon = "POLYGON((-180 90,-180 -90,180 -90,180 90,-180 90))";
		String west = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:westBoundLongitude/gco:Decimal");
		String east = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:eastBoundLongitude/gco:Decimal");
		String south = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:southBoundLatitude/gco:Decimal");
		String north = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//gmd:identificationInfo/gmd:MD_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:geographicElement/gmd:EX_GeographicBoundingBox/gmd:northBoundLatitude/gco:Decimal");
		if (west != null && east != null && south != null && north != null) {
			polygon = "POLYGON((" + west + " " + south + "," + east + " " + south + "," + east + " " + north + ","
					+ west + " " + north + "," + west + " " + south + "))";
		}

		return polygon;
	}

	private String appendParameter(String parameter, String value) {
		if (isExactMatched(parameter)) {
			return parameter + ":" + value.trim();
		} else {
			return parameter + ":" + value.trim() + "*";
		}
	}

	
	private String parseQuery(String param, String value, boolean exactMatched) {
		
		String result = StringUtils.EMPTY;
		
		if (exactMatched) {
			String tmp = StringUtils.EMPTY;
			String[] array = value.split(" ");

			for (int i = 0; i < array.length; i++) {
				if (i==0) {
					tmp = array[i];
				} else {
					tmp = tmp + "\\ " + array[i];
				}
			}
			result = result + " AND " + param + tmp;
			result = StringUtils.replace(result, "\"", "");
			
		} else {
			String[] array = value.split(" ");
			boolean isCombinedWord = false;
	
			for (int i = 0; i < array.length; i++) {
				String word = array[i].replaceAll(" ", "");
	
				if (!StringUtils.isEmpty(word)) {
	
					if (isCombinedWord) {
						result = result + "\\ " + array[i];
					} else {
						result = result + " AND " + param + "*" + array[i];
					}
	
					if (StringUtils.startsWith(word, "\"")) {
						isCombinedWord = true;
					} else if (StringUtils.endsWith(word, "\"")) {
						isCombinedWord = false;
						result = result + "*";
					} else if (!isCombinedWord) {
						result = result + "*";
					}
				}
			}
	
			result = StringUtils.replace(result, "\"", "");
			log.debug("+++++++++++++++++++++++++++++++++++++");
			log.debug("parseQuery result: " + result);
			log.debug("+++++++++++++++++++++++++++++++++++++");
		}
		return result;
	}

	private String parseNumericQuery(String param, String value, String type) {
		String result;
		String n1, n2;
		value = StringUtils.deleteWhitespace(value);
		if (value.startsWith("[")) {
			if (value.endsWith("]")) {
				n1 = StringUtils.substringBefore(StringUtils.substringAfter(value, "["), ",");
				n2 = StringUtils.substringBefore(StringUtils.substringAfter(value, ","), "]");
				result = param + ":" + "[" + n1 + " TO " + n2 + "]";
			} else if (value.endsWith("[")) {
				n1 = StringUtils.substringBefore(StringUtils.substringAfter(value, "["), ",");
				n2 = StringUtils.substringBefore(StringUtils.substringAfter(value, ","), "[");

				if (type.equals("int")) {
					result = param + ":" + "[" + n1 + " TO " + (Integer.parseInt(n2) - 1) + "]";
				} else {
					result = param + ":" + "[" + n1 + " TO " + (Double.parseDouble(n2) - 0.0001) + "]";
				}

			} else {
				n1 = StringUtils.substringAfter(value, "[");
				result = param + ":" + "[" + n1 + " TO *]";
			}
		} else if (value.startsWith("]")) {
			if (value.endsWith("]")) {
				n1 = StringUtils.substringBefore(StringUtils.substringAfter(value, "]"), ",");
				n2 = StringUtils.substringBefore(StringUtils.substringAfter(value, ","), "]");

				if (type.equals("int")) {
					result = param + ":" + "[" + (Integer.parseInt(n1) + 1) + " TO " + n2 + "]";
				} else {
					result = param + ":" + "[" + (Double.parseDouble(n1) + 0.0001) + " TO " + n2 + "]";
				}

			} else if (value.endsWith("[")) {
				n1 = StringUtils.substringBefore(StringUtils.substringAfter(value, "]"), ",");
				n2 = StringUtils.substringBefore(StringUtils.substringAfter(value, ","), "[");

				if (type.equals("int")) {
					result = param + ":" + "[" + (Integer.parseInt(n1) + 1) + " TO " + (Integer.parseInt(n2) - 1) + "]";
				} else {
					result = param + ":" + "[" + (Double.parseDouble(n1) + 0.0001) + " TO "
							+ (Double.parseDouble(n2) - 0.0001) + "]";
				}
			} else {
				if (type.equals("int")) {
					n1 = StringUtils.substringAfter(value, "]");
					result = param + ":" + "[" + (Integer.parseInt(n1) + 1) + " TO *]";
				} else {
					n1 = StringUtils.substringAfter(value, "]");
					result = param + ":" + "[" + (Double.parseDouble(n1) + 0.0001) + " TO *]";
				}

			}
		} else if (value.startsWith("{") && value.endsWith("}")) {
			String values = StringUtils.substringBefore(StringUtils.substringAfter(value, "{"), "}");
			if (values.contains(",")) {
				String[] arrValues = values.split(",");
				result = "(";
				for (int i = 0; i < arrValues.length; i++) {
					if (i == (arrValues.length - 1)) {
						result = result + param + ":" + arrValues[i] + ")";
					} else {
						result = result + param + ":" + arrValues[i] + " OR ";
					}
				}
			} else {
				result = param + ":" + values;
			}

		} else {
			if (value.endsWith("]")) {

				if (type.equals("int")) {
					n1 = StringUtils.substringBefore(value, "]");
					result = param + ":" + "[* TO " + Double.parseDouble(n1) + "]";
				} else {
					n1 = StringUtils.substringBefore(value, "]");
					result = param + ":" + "[* TO " + Double.parseDouble(n1) + "]";
				}

			} else if (value.endsWith("[")) {

				if (type.equals("int")) {
					n1 = StringUtils.substringBefore(value, "[");
					result = param + ":" + "[* TO " + (Integer.parseInt(n1) - 1) + "]";
				} else {
					n1 = StringUtils.substringBefore(value, "[");
					result = param + ":" + "[* TO " + (Double.parseDouble(n1) - 0.0001) + "]";
				}

			} else {
				result = param + ":" + value;
			}
		}

		log.debug("+++++++++++++++++++++++++++++++++++++");
		log.debug("parseQuery result: " + result);
		log.debug("+++++++++++++++++++++++++++++++++++++");
		return result;
	}

	public List<String> getList(SolrCollection solrCollection, String fieldName) {

		int count = 0;
		Map<String, String> tmpResults = new HashMap<String, String>();

		String facetCount = BundleUtils.getResource(BundleUtils.FACET_COUNT);
		log.debug("Get list of : " + fieldName);
		try {

			SolrClient solrClient = getClient(solrCollection);
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("q", "*");
			paramMap.put("start", "0");
			paramMap.put("row", "0");
			paramMap.put("facet", "true");
			paramMap.put("facet.limit", facetCount);
			paramMap.put("facet.field", fieldName);

			NamedList list = new NamedList(paramMap);
			SolrParams params1 = SolrParams.toSolrParams(list);
			QueryResponse qResponse = solrClient.query(params1);

			List<FacetField> facetFields = qResponse.getFacetFields();
			List<Count> counts = facetFields.get(0).getValues();

			for (int t = 0; t < counts.size(); t++) {
				Count c = counts.get(t);
				String value = c.getName();
				String match = String.valueOf(c.getCount());                                
				if (!fieldName.equals("originalPlatform") || value.length() > 2) {
					log.debug(value + " matched: " + match);
					count = count + Integer.parseInt(match);
					if (!tmpResults.containsKey(value.toUpperCase())) {
						tmpResults.put(value.toUpperCase(), value + "###" + match);
					} else {
						String s = tmpResults.get(value.toUpperCase());
						value = s.split("###")[0];
						String oldMatch = s.split("###")[1];
						int sum = Integer.parseInt(oldMatch) + Integer.parseInt(match);
						tmpResults.put(value.toUpperCase(), value + "###" + String.valueOf(sum));
					}
				}
			}

		} catch (Exception e) {
			log.error("", e);
		}

		List<String> results = new ArrayList<>(tmpResults.values());
		Collections.sort(results);

		return results;
	}

	/**
	 * Insert the dataset metadata into SORL
	 *
	 * @param metadata
	 *            : metadata to be inserted
	 * @param metadataType
	 *            : metadataType of metadata to be inserted (OM v1.0 or OM v1.1)
	 * @return identifier of the metadata
	 */
	public String[] postDatasetMetadata(String parentId, String metadata, String metadataType) throws Exception {

		ArrayList<SolrInputDocument> docs = new ArrayList<>();
		SolrInputDocument doc = new SolrInputDocument();
		String[] results;
		
		results = createInsertRequestFromGeoJSON(metadata, metadataType, doc);

		
		if (parentId != null) {
			if (!parentId.equals(results[1])) {				
				throw new Exception("The series identifier in the URL (" + parentId + ") is different from the series identifier in the metadata (" + results[1]   + ").");
				 
			}
		}
		docs.add(doc);
		try {
			datasetClient.add(docs);
			datasetClient.commit();
		} catch (SolrServerException | IOException e) {
			throw new Exception("Error when post dataset metadata to solr", e);
		}
		
		return results;
	}

	/**
	 * Create a SOLR request to insert metadata from GeoJSON metadata
	 *
	 * @param metadata
	 *            metadata to be inserted
	 * @param metadataType
	 *            metadataType
	 * @return Array contains SOLR request and id of the metadata
	 * @throws Exception
	 */
	private String[] createInsertRequestFromGeoJSON(String metadata, String metadataType, SolrInputDocument doc)
			throws Exception {

		String[] results = new String[2];		
		
		JSONObject metadataObj = new JSONObject(metadata);
		
		
		JSONObject geometry =  GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "geometry");  
		
		
		JSONObject properties = GeoJsonParser.getGeoJSONObjectProperty(metadataObj, "properties");
		
		
		JSONArray acqInfos = GeoJsonParser.getGeoJSONArrayProperty(properties, "acquisitionInformation");
		JSONObject acqInfo = (JSONObject) acqInfos.get(0);
		JSONObject acqParams =  GeoJsonParser.getGeoJSONObjectProperty(acqInfo, "acquisitionParameters"); 
		JSONObject acqAngles =  GeoJsonParser.getGeoJSONObjectProperty(acqParams, "acquisitionAngles");
		
		JSONObject productInfo =  GeoJsonParser.getGeoJSONObjectProperty(properties, "productInformation"); 
		JSONObject qualityInfo =  GeoJsonParser.getGeoJSONObjectProperty(productInfo, "qualityInformation");
		
		
		/* get parentIdentifier */
		String parentIdentifier =  GeoJsonParser.getGeoJSONStringProperty(properties, "parentIdentifier");		
		if (StringUtils.isNotEmpty(parentIdentifier)) {
			log.debug("parentIdentifier: " + parentIdentifier);
			doc.addField(Constants.PARENT_ID_PARAM, parentIdentifier);
		}
		
		/* get collectionId */
		String collection =  GeoJsonParser.getGeoJSONStringProperty(properties, "collection");
		if (StringUtils.isNotEmpty(collection)) {
			log.debug("collection: " + collection);
			doc.addField(Constants.PARENT_ID_PARAM, collection);
		}
		
				
		/* get identifier */
		String identifier = GeoJsonParser.getGeoJSONStringProperty(properties, "identifier");		
		if (StringUtils.isNotEmpty(identifier)) {
			log.debug("id: " + identifier);
			doc.addField("id", identifier);
			doc.addField("combinedId", parentIdentifier + ":"  + identifier);
		}

		/* get productionStatus */
		String productionStatus  =  GeoJsonParser.getGeoJSONStringProperty(properties, "status");		
		if (StringUtils.isNotEmpty(productionStatus)) {
			log.debug("productionStatus: " + productionStatus);
			doc.addField(Constants.PRODUCTION_STATUS_PARAM, productionStatus);
		}

		
		/* get acquisitionType */
		String acquisitionType =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "acquisitionType");		
		if (StringUtils.isNotEmpty(acquisitionType)) {
			log.debug("acquisitionType: " + acquisitionType);
			doc.addField(Constants.ACQ_TYPE_PARAM, acquisitionType);
		}
		
		/* get wrsLongitude */
		String wrsLongitude =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "wrsLongitude");		
		if (StringUtils.isNotEmpty(wrsLongitude)) {
			log.debug("wrsLongitude: " + wrsLongitude);
			doc.addField(Constants.TRACK_PARAM, wrsLongitude);
		}
		
		/* get wrsLatitude */
		String wrsLatitude =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "wrsLatitude");		
		if (StringUtils.isNotEmpty(wrsLatitude)) {
			log.debug("wrsLatitude: " + wrsLatitude);
			doc.addField(Constants.FRAME_PARAM, wrsLatitude);
		}
		

		/* get acquisitionSubType */
		String acquisitionSubType = GeoJsonParser.getGeoJSONStringProperty(acqParams, "acquisitionSubType");		
		if (StringUtils.isNotEmpty(acquisitionSubType)) {
			log.debug("acquisitionSubType: " + acquisitionSubType);

			doc.addField(Constants.ACQ_SUBTYPE_PARAM, acquisitionSubType);
		}

		/* get orbitNumber */
		int orbitNumber = GeoJsonParser.getGeoJSONIntProperty(acqParams, "orbitNumber");		
		if (orbitNumber > -1) {
			log.debug("orbitNumber: " + orbitNumber);
			doc.addField(Constants.ORBIT_NUMBER_PARAM, orbitNumber);
		}

		/* get lastOrbitNumber */
		int lastOrbitNumber = GeoJsonParser.getGeoJSONIntProperty(acqParams, "lastOrbitNumber");		
		if (lastOrbitNumber > -1) {
			log.debug("lastOrbitNumber: " + lastOrbitNumber);
			doc.addField("lastOrbitNumber", lastOrbitNumber);
		}

		/* get orbitDirection */
		String orbitDirection =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "orbitDirection");		
		if (StringUtils.isNotEmpty(orbitDirection)) {
			log.debug("orbitDirection: " + orbitDirection);
			doc.addField(Constants.ORBIT_DIRECTION_PARAM, orbitDirection);
		}
		
		
		/* get track */
		String track = GeoJsonParser.getGeoJSONStringProperty(acqParams, "wrsLongitudeGrid");				
		if (StringUtils.isNotEmpty(track)) {
			log.debug("track: " + track);
			doc.addField(Constants.TRACK_PARAM, track);
		}

		/* get frame */
		String frame =GeoJsonParser.getGeoJSONStringProperty(acqParams, "wrsLatitudeGrid");				
		if (StringUtils.isNotEmpty(frame)) {
			log.debug("frame: " + frame);
			doc.addField(Constants.FRAME_PARAM, frame);
		}

		/* get swathIdentifier */
		String swathIdentifier = GeoJsonParser.getGeoJSONStringProperty(acqParams, "swathIdentifier");
		if (StringUtils.isNotEmpty(swathIdentifier)) {
			log.debug("swathIdentifier: " + swathIdentifier);
			doc.addField(Constants.SWATH_IDENTIFIER_PARAM, swathIdentifier);
		}

		/* get cloudCover */
		double cloudCover =  GeoJsonParser.getGeoJSONDoubleProperty(productInfo, "cloudCover");
		if (cloudCover > -1.0) {
			log.debug("cloudCover: " + cloudCover);
			doc.addField(Constants.CLOUD_COVER_PARAM, cloudCover);
		}

		/* get snowCover */
		double snowCover =  GeoJsonParser.getGeoJSONDoubleProperty(productInfo, "snowCover");		
		if (snowCover > -1.0) {
			log.debug("snowCover: " + snowCover);
			doc.addField(Constants.SNOW_COVER_PARAM, snowCover);
		}

		
		/* get lowestLocation */
		String lowestLocation =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "lowestLocation");
		if (StringUtils.isNotEmpty(lowestLocation)) {
			log.debug("lowestLocation: " + lowestLocation);
			doc.addField(Constants.LOWEST_LOCATION_PARAM, lowestLocation);
		}

		/* get highestLocation */
		String highestLocation = GeoJsonParser.getGeoJSONStringProperty(acqParams, "highestLocation");
				
		if (StringUtils.isNotEmpty(highestLocation)) {
			log.debug("highestLocation: " + highestLocation);
			doc.addField(Constants.HIGHEST_LOCATION_PARAM, highestLocation);
		}

		/* get acquisitionStation */
		String acquisitionStation = GeoJsonParser.getGeoJSONStringProperty(acqParams, "acquisitionStation");		
		if (StringUtils.isNotEmpty(acquisitionStation)) {
			log.debug("acquisitionStation: " + acquisitionStation);
			doc.addField(Constants.ACQ_STATION_PARAM, acquisitionStation);
		}

		/* get productQualityStatus */
		String qualityStatus = GeoJsonParser.getGeoJSONStringProperty(qualityInfo, "qualityStatus");
		if (StringUtils.isNotEmpty(qualityStatus)) {
			log.debug("productQualityStatus: " + qualityStatus);

			doc.addField(Constants.PRO_QUAL_STATUS_PARAM, qualityStatus);
		}

		/* get productQualityDegradation */
		double qualityDegradation = GeoJsonParser.getGeoJSONDoubleProperty(qualityInfo, "qualityDegradation");
		if (qualityDegradation > -1.0) {
			log.debug("productQualityDegradation: " + qualityDegradation);

			doc.addField(Constants.PRO_QUAL_DEG, qualityDegradation);
		}

		/* get productQualityDegradationTag */
		String qualityDegradationTag = GeoJsonParser.getGeoJSONStringProperty(qualityInfo, "qualityDegradationTag");
		if (StringUtils.isNotEmpty(qualityDegradationTag)) {
			log.debug("productQualityDegradationTag: " + qualityDegradationTag);

			doc.addField(Constants.PRO_QUAL_DEG_TAG_PARAM, qualityDegradationTag);
		}
		
		/* get qualityIndicators */
		JSONArray qualityIndicators = GeoJsonParser.getGeoJSONArrayProperty(qualityInfo, "qualityIndicators");
		if (qualityIndicators != null) {
			for(int i=0; i<qualityIndicators.length(); i++) {			
				JSONObject qualityIndicator = (JSONObject) qualityIndicators.get(i);
				String isMeasurementOf = GeoJsonParser.getGeoJSONStringProperty(qualityIndicator, "isMeasurementOf");
				
				if (isMeasurementOf.endsWith(Constants.DEGRADED_DATA_PERCENTAGE_METRIC)) {
					double value = GeoJsonParser.getGeoJSONDoubleProperty(qualityIndicator, "value");
					//System.out.println(Constants.DEGRADED_DATA_PERCENTAGE_METRIC + ": " + value);
					log.debug(Constants.DEGRADED_DATA_PERCENTAGE_METRIC + ": " + value);
					doc.addField(Constants.DEGRADED_DATA_PERCENTAGE_METRIC, value);
				} else if (isMeasurementOf.endsWith(Constants.DEGRADED_ANCILLARY_DATA_PERCENTAGE_METRIC)) {
					double value = GeoJsonParser.getGeoJSONDoubleProperty(qualityIndicator, "value");
					//System.out.println(Constants.DEGRADED_ANCILLARY_DATA_PERCENTAGE_METRIC + ": " + value);
					log.debug(Constants.DEGRADED_ANCILLARY_DATA_PERCENTAGE_METRIC + ": " + value);
					doc.addField(Constants.DEGRADED_ANCILLARY_DATA_PERCENTAGE_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.FORMAT_CORRECTNESS_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.FORMAT_CORRECTNESS_METRIC + ": " + value);
					log.debug(Constants.FORMAT_CORRECTNESS_METRIC + ": " + value);
					doc.addField(Constants.FORMAT_CORRECTNESS_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.GENERAL_QUALITY_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.GENERAL_QUALITY_METRIC + ": " + value);
					log.debug(Constants.GENERAL_QUALITY_METRIC + ": " + value);
					doc.addField(Constants.GENERAL_QUALITY_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.GEOMETRIC_QUALITY_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.GEOMETRIC_QUALITY_METRIC + ": " + value);
					log.debug(Constants.GEOMETRIC_QUALITY_METRIC + ": " + value);
					doc.addField(Constants.GEOMETRIC_QUALITY_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.RADIOMETRIC_QUALITY_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.RADIOMETRIC_QUALITY_METRIC + ": " + value);
					log.debug(Constants.RADIOMETRIC_QUALITY_METRIC + ": " + value);
					doc.addField(Constants.RADIOMETRIC_QUALITY_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.SENSOR_QUALITY_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.SENSOR_QUALITY_METRIC + ": " + value);
					log.debug(Constants.SENSOR_QUALITY_METRIC + ": " + value);
					doc.addField(Constants.SENSOR_QUALITY_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.FEASIBILITY_CONTROL_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.FEASIBILITY_CONTROL_METRIC + ": " + value);
					log.debug(Constants.FEASIBILITY_CONTROL_METRIC + ": " + value);
					doc.addField(Constants.FEASIBILITY_CONTROL_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.DELIVERY_CONTROL_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.DELIVERY_CONTROL_METRIC + ": " + value);
					log.debug(Constants.DELIVERY_CONTROL_METRIC + ": " + value);
					doc.addField(Constants.DELIVERY_CONTROL_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.ORDINARY_CONTROL_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.ORDINARY_CONTROL_METRIC + ": " + value);
					log.debug(Constants.ORDINARY_CONTROL_METRIC + ": " + value);
					doc.addField(Constants.ORDINARY_CONTROL_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.DETAILED_CONTROL_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.DETAILED_CONTROL_METRIC + ": " + value);
					log.debug(Constants.DETAILED_CONTROL_METRIC + ": " + value);
					doc.addField(Constants.DETAILED_CONTROL_METRIC, value);
					
				} else if (isMeasurementOf.endsWith(Constants.HARMONIZATION_CONTROL_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.HARMONIZATION_CONTROL_METRIC + ": " + value);
					log.debug(Constants.HARMONIZATION_CONTROL_METRIC + ": " + value);
					doc.addField(Constants.HARMONIZATION_CONTROL_METRIC, value);					
                                        
				}else if (isMeasurementOf.endsWith(Constants.IP_FOR_LP_INFORMATION_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.IP_FOR_LP_INFORMATION_METRIC + ": " + value);
					log.debug(Constants.IP_FOR_LP_INFORMATION_METRIC + ": " + value);
					doc.addField(Constants.IP_FOR_LP_INFORMATION_METRIC, value);	
                                        
				}else if (isMeasurementOf.endsWith(Constants.LP_INTERPRETATION_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.LP_INTERPRETATION_METRIC + ": " + value);
					log.debug(Constants.LP_INTERPRETATION_METRIC + ": " + value);
					doc.addField(Constants.LP_INTERPRETATION_METRIC, value);
                                        
				}else if (isMeasurementOf.endsWith(Constants.LP_METADATA_CONTROL_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.LP_METADATA_CONTROL_METRIC + ": " + value);
					log.debug(Constants.LP_METADATA_CONTROL_METRIC + ": " + value);
					doc.addField(Constants.LP_METADATA_CONTROL_METRIC, value);
                                        
				}else if (isMeasurementOf.endsWith(Constants.LP_ORDINARY_CONTROL_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.LP_ORDINARY_CONTROL_METRIC + ": " + value);
					log.debug(Constants.LP_ORDINARY_CONTROL_METRIC + ": " + value);
					doc.addField(Constants.LP_ORDINARY_CONTROL_METRIC, value);					
                                        
				}else if (isMeasurementOf.endsWith(Constants.LP_THEMATIC_VALIDATION_METRIC)) {
					boolean value = GeoJsonParser.getGeoJSONBooleanProperty(qualityIndicator, "value");
					//System.out.println(Constants.LP_THEMATIC_VALIDATION_METRIC + ": " + value);
					log.debug(Constants.LP_THEMATIC_VALIDATION_METRIC + ": " + value);
					doc.addField(Constants.LP_THEMATIC_VALIDATION_METRIC, value);					
				}
				
				
				//System.out.println("i: " + i);
				//System.out.println("isMeasurementOf: " + isMeasurementOf);
			}
		}
		
		/* get processorName */
		String processorName = GeoJsonParser.getGeoJSONStringProperty(productInfo, "processorName");
		if (StringUtils.isNotEmpty(processorName)) {
			log.debug("processorName: " + processorName);
			doc.addField(Constants.PROCESSOR_NAME_PARAM, processorName);
		}

		/* get productVersion */
		String productVersion =  GeoJsonParser.getGeoJSONStringProperty(productInfo, "productVersion");
		
		if (StringUtils.isNotEmpty(productVersion)) {
			log.debug("productVersion: " + productVersion);
			doc.addField(Constants.PRODUCT_VERSION_PARAM, productVersion);
		}
		
		/* get processingCenter */
		String processingCenter = GeoJsonParser.getGeoJSONStringProperty(productInfo, "processingCenter");		
		if (StringUtils.isNotEmpty(processingCenter)) {
			log.debug("processingCenter: " + processingCenter);
			doc.addField(Constants.PROCESSING_CENTER_PARAM, processingCenter);
		}

		/* get compositeType */
		String compositeType = GeoJsonParser.getGeoJSONStringProperty(productInfo, "compositeType");
		if (StringUtils.isNotEmpty(compositeType)) {
			log.debug("compositeType: " + compositeType);
			doc.addField(Constants.COMPOSITE_TYPE_PARAM, compositeType);
		}
		
		/* get processingDate */
		String processingDate = GeoJsonParser.getGeoJSONStringProperty(productInfo, "processingDate");
		if (StringUtils.isNotEmpty(processingDate)) {
			log.debug("processingDate: " + processingDate);
			doc.addField(Constants.PROCESSING_DATE_PARAM, processingDate);
		}

		/* get processingMode */
		String processingMode = GeoJsonParser.getGeoJSONStringProperty(productInfo, "processingMode");
		if (StringUtils.isNotEmpty(processingMode)) {
			log.debug("processingMode: " + processingMode);
			doc.addField(Constants.PROCESSING_MODE_PARAM, processingMode);
		}
		
		/* get productType */
		String productType =  GeoJsonParser.getGeoJSONStringProperty(productInfo, "productType");
		if (StringUtils.isNotEmpty(productType)) {
			log.debug("productType: " + productType);
			doc.addField(Constants.PRODUCT_TYPE_PARAM, productType);
		}
		
		/* get processingLevel */
		String processingLevel = GeoJsonParser.getGeoJSONStringProperty(productInfo, "processingLevel");
		if (StringUtils.isNotEmpty(processingLevel)) {
			log.debug("processingLevel: " + processingLevel);
			doc.addField(Constants.PROCESSING_LEVEL_PARAM, processingLevel);
		}
		
		/* get sensorMode */
		String sensorMode =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "operationalMode");
		if (StringUtils.isNotEmpty(sensorMode)) {
			log.debug("sensorMode: " + sensorMode);
			doc.addField(Constants.SENSOR_MODE_PARAM, sensorMode);
		}

		/* get resolution */
		double resolution =  GeoJsonParser.getGeoJSONDoubleProperty(acqParams, "resolution");
		if (resolution > -1.0) {
			log.debug("resolution: " + resolution);
			doc.addField(Constants.RESOLUTION_PARAM, resolution);
		}
		
		
		/* get availabilityTime */
		String availabilityTime = GeoJsonParser.getGeoJSONStringProperty(productInfo, "availabilityTime");
		if (StringUtils.isNotEmpty(availabilityTime)) {
			log.debug("availabilityTime: " + availabilityTime);
			doc.addField(Constants.AVAILABILITY_TIME_PARAM, availabilityTime);
		}

		/* get startTimeFromAscendingNode */
		int startTimeFromAscNode = GeoJsonParser.getGeoJSONIntProperty(acqParams, "startTimeFromAscendingNode");
		if (startTimeFromAscNode > - 1) {
			log.debug("startTimeFromAscNode: " + startTimeFromAscNode);
			doc.addField(Constants.START_TIME_ASC_NODE_PARAM, startTimeFromAscNode);
		}

		/* get completionTimeFromAscendingNode */
		int compTimeFromAscNode = GeoJsonParser.getGeoJSONIntProperty(acqParams, "completionTimeFromAscendingNode");
		if (compTimeFromAscNode > - 1) {
			log.debug("completionTimeFromAscendingNode: " + compTimeFromAscNode);

			doc.addField(Constants.COMP_TIME_ASC_NODE_PARAM, compTimeFromAscNode);
		}

		/* get illuminationAzimuthAngle */
		double illuAzimuthAngle = GeoJsonParser.getGeoJSONDoubleProperty(acqAngles, "illuminationAzimuthAngle");
		if (illuAzimuthAngle > -1.0) {
			log.debug("illuminationAzimuthAngle: " + illuAzimuthAngle);
			doc.addField(Constants.ILLU_AZI_ANGLE_PARAM, illuAzimuthAngle);
		}

		/* get illuminationZenithAngle */
		double illuZenithAngle = GeoJsonParser.getGeoJSONDoubleProperty(acqAngles, "illuminationZenithAngle");
		if (illuZenithAngle > -1.0) {
			log.debug("illuminationZenithAngle: " + illuZenithAngle);
			doc.addField(Constants.ILLU_ZEN_ANGLE_PARAM, illuZenithAngle);
		}

		/* get illuminationElevationAngle */
		double illuElevationAngle = GeoJsonParser.getGeoJSONDoubleProperty(acqAngles, "illuminationElevationAngle");
		if (illuElevationAngle > -1.0) {
			log.debug("illuminationElevationAngle: " + illuElevationAngle);
			doc.addField(Constants.ILLU_ELE_ANGLE_PARAM, illuElevationAngle);
		}
		
		
		/* get minimumIncidenceAngle */
		double minIncidenceAngle = GeoJsonParser.getGeoJSONDoubleProperty(acqAngles, "minimumIncidenceAngle");
		if (minIncidenceAngle > -1.0) {
			log.debug("minimumIncidenceAngle: " + minIncidenceAngle);
			doc.addField(Constants.MIN_INCI_ANGLE_PARAM, minIncidenceAngle);
		}

		/* get maximumIncidenceAngle */
		double maxIncidenceAngle = GeoJsonParser.getGeoJSONDoubleProperty(acqAngles, "maximumIncidenceAngle");
		if (maxIncidenceAngle > -1.0) {
			log.debug("maximumIncidenceAngle: " + maxIncidenceAngle);
			doc.addField(Constants.MAX_INCI_ANGLE_PARAM, maxIncidenceAngle);
		}
		
		
		/* get incidenceAngleVariation */
		double incidenceAngleVariation = GeoJsonParser.getGeoJSONDoubleProperty(acqAngles, "incidenceAngleVariation");
		
		if (incidenceAngleVariation > -1.0) {
			log.debug("incidenceAngleVariation: " + incidenceAngleVariation);
			doc.addField(Constants.INCI_ANGLE_PARAM, incidenceAngleVariation);
		}
		
		/* get polarisationMode */
		String polarisationMode =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "polarisationMode");
		if (StringUtils.isNotEmpty(polarisationMode)) {
			log.debug("polarisationMode: " + polarisationMode);
			doc.addField(Constants.POLAR_MODE_PARAM, polarisationMode);
		}

		/* get polarisationChannels */
		String polarisationChannels =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "polarisationChannels");
		if (StringUtils.isNotEmpty(polarisationChannels)) {
			log.debug("polarisationChannels: " + polarisationChannels);
			doc.addField(Constants.POLAR_CHANNELS_PARAM, polarisationChannels);
		}

		/* get antennaLookDirection */
		String antennaLookDirection = GeoJsonParser.getGeoJSONStringProperty(acqParams, "antennaLookDirection");
		if (StringUtils.isNotEmpty(antennaLookDirection)) {
			log.debug("antennaLookDirection: " + antennaLookDirection);
			doc.addField(Constants.ANTENNA_LOOK_DIR_PARAM, antennaLookDirection);
		}
		
		
		/* get dopplerFrequency */
		double dopplerFrequency = GeoJsonParser.getGeoJSONDoubleProperty(acqParams, "dopplerFrequency");
		if (dopplerFrequency > -1.0) {
			log.debug("dopplerFrequency: " + dopplerFrequency);
			doc.addField(Constants.DOPPLER_FREQ_PARAM, dopplerFrequency);
		}

		
		/* get date */
		String date = GeoJsonParser.getGeoJSONStringProperty(properties, "date");
		if (StringUtils.isNotEmpty(date)) {
			log.debug("date: " + date);
			
			String startDate =  StringUtils.substringBefore(date, "/");
			String endDate =  StringUtils.substringAfter(date, "/");
			
			if (StringUtils.isNotEmpty(startDate)) {
				log.debug("startDate: " + startDate);
				doc.addField(Constants.START_DATE_PARAM, startDate);
			}

			/* get endDate */			
			if (StringUtils.isNotEmpty(endDate.trim())) {
				log.debug("endDate: " + endDate);
				doc.addField(Constants.END_DATE_PARAM, endDate);
			} else {				
				endDate = "9999-01-01T23:59:59.999Z";
                                doc.addField(Constants.END_DATE_PARAM, endDate);
			}

			
		} else {
			
			/* get startDate */
			String startDate =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "beginningDateTime");
			if (StringUtils.isNotEmpty(startDate)) {
				log.debug("startDate: " + startDate);
				doc.addField(Constants.START_DATE_PARAM, startDate);
			}

			/* get endDate */
			String endDate =  GeoJsonParser.getGeoJSONStringProperty(acqParams, "endingDateTime");
			if (StringUtils.isNotEmpty(endDate)) {
				log.debug("endDate: " + endDate);
				doc.addField(Constants.END_DATE_PARAM, endDate);
			}

			
		}
		
		/* get doi 
		String doi = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//*[local-name() = 'metaDataProperty']/*[local-name() = 'EarthObservationMetaData']/*[local-name() = 'doi']");
		if (StringUtils.isNotEmpty(doi)) {
			log.debug("doi: " + doi);
			doc.addField(Constants.DOI_PARAM, doi);
		}
		*/
		
		/*
		String nativeProductFormat = XpathUtils.getNodeValueByXPath(xmlDoc,
				"//*[local-name() = 'metaDataProperty']/*[local-name() = 'EarthObservationMetaData']//*[local-name() = 'ProcessingInformation']//*[local-name() = 'nativeProductFormat']");
		if (StringUtils.isNotEmpty(nativeProductFormat)) {
			log.debug("nativeProductFormat: " + nativeProductFormat);
			doc.addField(Constants.NATIVE_PRODUCT_FORMAT_PARAM, nativeProductFormat);
		}
		
		*/
		
		
		
		String wkt = getWKT(geometry);
		
		/* get GML */
		if (StringUtils.isNotEmpty(wkt)) {
			doc.addField("posList", wkt);

		}
		
		doc.addField("originalPosList", wkt);
		
		
		for (int i=0; i< acqInfos.length();i++) {
			
			JSONObject acqInfo1 = (JSONObject) acqInfos.get(i);
			
			JSONObject platformInfo = GeoJsonParser.getGeoJSONObjectProperty(acqInfo1, "platform");
			JSONObject instrumentInfo = GeoJsonParser.getGeoJSONObjectProperty(acqInfo1, "instrument");
			
			/* get platform */
			String platform = GeoJsonParser.getGeoJSONStringProperty(platformInfo, "platformShortName");
			if (StringUtils.isNotEmpty(platform)) {
				log.debug("platform: " + platform);
				doc.addField(Constants.PLATFORM_PARAM, platform);
			}

			/* get serialIdentifier */
			String serialIdentifier =  GeoJsonParser.getGeoJSONStringProperty(platformInfo, "platformSerialIdentifier");
			if (StringUtils.isNotEmpty(serialIdentifier)) {
				log.debug("serialIdentifier: " + serialIdentifier);
				doc.addField(Constants.PLATFORM_SERIAL_ID_PARAM, serialIdentifier);
			}
			
			/* get orbitType */
			String orbitType = GeoJsonParser.getGeoJSONStringProperty(platformInfo, "orbitType");		
			if (StringUtils.isNotEmpty(orbitType)) {
				log.debug("orbitType: " + orbitType);
				doc.addField(Constants.ORBIT_TYPE_PARAM, orbitType);
			}
			
			/* get instrument */				
			String instrument = GeoJsonParser.getGeoJSONStringProperty(instrumentInfo, "instrumentShortName");
			
			if (StringUtils.isNotEmpty(instrument)) {
				log.debug("instrument: " + instrument);
				doc.addField(Constants.INSTRUMENT_PARAM, instrument);
			}
			
			/* get sensor */
			String sensorType =  GeoJsonParser.getGeoJSONStringProperty(instrumentInfo, "sensorType");		
			if (StringUtils.isNotEmpty(sensorType)) {
				log.debug("sensor: " + sensorType);
				doc.addField(Constants.SENSOR_TYPE_PARAM, sensorType);
			}
		}
		
		
		/* Attribute for land product */
		
		/* get description */
		String title = GeoJsonParser.getGeoJSONStringProperty(properties, "title");
		if (StringUtils.isNotEmpty(title)) {
			log.debug("title: " + title);
			doc.addField("title", title);
		}
		
		/* get description */
		String description = GeoJsonParser.getGeoJSONStringProperty(properties, "abstract");
		if (StringUtils.isNotEmpty(description)) {
			log.debug("description: " + description);
			doc.addField("description", description);
		}
		
		/* keyword */
		ArrayList<String> keywords = getKeyWordsFromJson(properties);
		for (String keyword : keywords) {
			log.debug("keyword: " + keyword);
			doc.addField("keyword", keyword);
		}
		
		/* keyword URI */
		ArrayList<String> keywordURIs =  getKeyWordsURIFromJson(properties, false);
		for (String keywordURI : keywordURIs) {
			log.debug("keywordURI: " + keywordURI);
			doc.addField("keywordURI", keywordURI);
		}

		ArrayList<String> combinedKeywordURIs = getKeyWordsURIFromJson(properties, true);
		for (String combinedKeywordURI : combinedKeywordURIs) {
			log.debug("combined keyword URI : " + combinedKeywordURI);
			doc.addField("combinedKeywordURI", combinedKeywordURI);
		}
		
		
		/* organisation */
		ArrayList<String> organisations = getOrganisationFromJson(properties);
		for (String organisation : organisations) {
			log.debug("organisation: " + organisation);
			doc.addField("organisation", organisation);
		}
		
		doc.addField("original", "geojson");

		doc.addField("metadataOrig",  metadata);
		
		results[0] = identifier;
		results[1] = parentIdentifier;
		return results;
		
	}
	
	
	private String getWKT(JSONObject geometry) {
		String wkt = "";
		
		String geometryType = GeoJsonParser.getGeoJSONStringProperty(geometry, "type");
		JSONArray coordinates = GeoJsonParser.getGeoJSONArrayProperty(geometry, "coordinates");
		
		if (geometryType.equals("Polygon")) {
			
			JSONArray polygon = (JSONArray) coordinates.get(0);			
			
			
			for (int i=0;i<polygon.length();i++) {				
				JSONArray point = (JSONArray) polygon.get(i);
				double x = point.getDouble(0);
				double y = point.getDouble(1);				
				if (i == 0) {
					wkt = wkt +  x + " " + y;
				} else {
					wkt = wkt + "," + x + " " + y;
				}
				
			}
			
			wkt = "POLYGON((" + wkt + "))";
		} else if (geometryType.equals("MultiPolygon")) {
			for (int i=0;i<coordinates.length();i++) {	
				JSONArray polygon = (JSONArray) ((JSONArray) coordinates.get(i)).get(0);
				
				String p = StringUtils.EMPTY;
				for (int j=0;j<polygon.length();j++) {					
					JSONArray point = (JSONArray) polygon.get(j);
					
					double x = point.getDouble(0);
					double y = point.getDouble(1);				
					if (j == 0) {
						p = p +  x + " " + y;
					} else {
						p = p + "," + x + " " + y;
					}
				}
				
				if (i == 0) {
					wkt = wkt + "((" + p + "))";
				} else {
					wkt = wkt + ", ((" + p + "))";
				}
			
			}
			
			wkt = "MULTIPOLYGON (" + wkt + ")";
			
		} else if (geometryType.equals("Point")) {
			wkt = "POINT (" + coordinates.getDouble(0) + " " +  coordinates.getDouble(1) + ")";
		} else if (geometryType.equals("MultiPoint")) {
			
			for (int i=0;i<coordinates.length();i++) {		
				JSONArray point = (JSONArray) coordinates.get(i);
				
				double x = point.getDouble(0);
				double y = point.getDouble(1);				
				if (i == 0) {
					wkt = wkt +  x + " " + y;
				} else {
					wkt = wkt + "," + x + " " + y;
				}
				
			}
			
			wkt = "MULTIPOINT (" + wkt + ")";
			
		} else if (geometryType.equals("LineString")) {
			for (int i=0;i<coordinates.length();i++) {		
				JSONArray point = (JSONArray) coordinates.get(i);
				
				double x = point.getDouble(0);
				double y = point.getDouble(1);				
				if (i == 0) {
					wkt = wkt +  x + " " + y;
				} else {
					wkt = wkt + "," + x + " " + y;
				}
				
			}
			
			wkt = "LINESTRING (" + wkt + ")";
		
		} else if (geometryType.equals("MultiLineString")) {
			for (int i=0;i<coordinates.length();i++) {	
				JSONArray lineString = (JSONArray) coordinates.get(i);
				String ls = StringUtils.EMPTY;
				for (int j=0;j<lineString.length();j++) {					
					JSONArray point = (JSONArray) lineString.get(j);
					
					double x = point.getDouble(0);
					double y = point.getDouble(1);				
					if (j == 0) {
						ls = ls +  x + " " + y;
					} else {
						ls = ls + "," + x + " " + y;
					}
				}
				
				if (i == 0) {
					wkt = wkt + "(" + ls + ")";
				} else {
					wkt = wkt + ", (" + ls + ")";
				}
				
			}
			
			wkt = "MULTILINESTRING  (" + wkt + ")";
		}
		
		
		
		return wkt;
	}
	
	
	
	public boolean isSorlFieldHasValue(String seriesId, String fieldName) {
		boolean hasValue = false;
		List<String> values = getValueOfSolrFieldOfSeries(seriesId, fieldName);
		if (values.size() > 0) {
			hasValue = true;
		}
		return hasValue;
	}
	
	
	/**
	 * Get list of keywords from the metadata
	 *
	 * @param properties
	 *            metadata
	 */
	private ArrayList<String> getKeyWordsFromJson(JSONObject properties) {
		
		ArrayList<String> keywordsList = new ArrayList<>();
		JSONArray categories = GeoJsonParser.getGeoJSONArrayProperty(properties, "categories");
		
		if (categories != null) {
			for (int i=0;i<categories.length();i++) {
				JSONObject category =  (JSONObject) categories.get(i);
				String label = GeoJsonParser.getGeoJSONStringProperty(category, "label");
				keywordsList.add(label);
			}
		}
		
		JSONArray subjects = GeoJsonParser.getGeoJSONArrayProperty(properties, "subject");
		
		if (subjects != null) {
			for (int i=0;i<subjects.length();i++) {
				JSONObject subject = (JSONObject) subjects.get(i);
				String label = GeoJsonParser.getGeoJSONStringProperty(subject, "label");
				keywordsList.add(label);
			}
		}
		
		JSONArray keywords = GeoJsonParser.getGeoJSONArrayProperty(properties, "keyword");
		if (keywords != null) {
			for (int i=0;i<keywords.length();i++) {
				String keyword = (String) keywords.get(i);
				keywordsList.add(keyword);
			}
		}
		return keywordsList;

	}
	
	/**
	 * Get list of organisation from the metadata
	 *
	 * @param properties
	 *            metadata
	 */
	private ArrayList<String> getOrganisationFromJson(JSONObject properties) {
		
		ArrayList<String> organisations = new ArrayList<>();
		JSONArray qualifiedAttributions = GeoJsonParser.getGeoJSONArrayProperty(properties, "qualifiedAttribution");
		
		if (qualifiedAttributions != null) {
			for (int i=0;i<qualifiedAttributions.length();i++) {
				JSONObject qualifiedAttribution =  (JSONObject) qualifiedAttributions.get(i);
				JSONArray agents = GeoJsonParser.getGeoJSONArrayProperty(qualifiedAttribution, "agent");
				if (agents != null) {
					for (int j=0;j<agents.length();j++) {
						JSONObject agent =  (JSONObject) agents.get(j);
						String name = GeoJsonParser.getGeoJSONStringProperty(agent, "name");
						if (!organisations.contains(name)) {
							organisations.add(name);
						}
						
					}
				}
			}
		}
		
		return organisations;

	}
	
	/**
	 * Get list of keywords URI from the metadata
	 *
	 * @param properties
	 *            metadata
	 */
	private ArrayList<String> getKeyWordsURIFromJson(JSONObject properties, boolean combined) {
		
		ArrayList<String> keywordURIList = new ArrayList<String>();
		JSONArray categories = GeoJsonParser.getGeoJSONArrayProperty(properties, "categories");
		
		if (categories != null) {
			for (int i=0;i<categories.length();i++) {
				JSONObject category =  (JSONObject) categories.get(i);
				String label = GeoJsonParser.getGeoJSONStringProperty(category, "label");
				String term = GeoJsonParser.getGeoJSONStringProperty(category, "term");
				if (combined) {
					if (!keywordURIList.contains(term + "#;#" + label)) {
						keywordURIList.add(term + "#;#" + label);
					}
				} else {
					if (!keywordURIList.contains(term)) {
						keywordURIList.add(term);
					}
				}
			}
		}
		
		JSONArray subjects = GeoJsonParser.getGeoJSONArrayProperty(properties, "subject");
		
		if (subjects != null) {
			for (int i=0;i<subjects.length();i++) {
				JSONObject subject = (JSONObject) subjects.get(i);
				String label = GeoJsonParser.getGeoJSONStringProperty(subject, "label");
				String term = GeoJsonParser.getGeoJSONStringProperty(subject, "term");
				if (combined) {
					if (!keywordURIList.contains(term + "#;#" + label)) {
						keywordURIList.add(term + "#;#" + label);
					}
				} else {
					if (!keywordURIList.contains(term)) {
						keywordURIList.add(term);
					}
				}
			}
		}
		
		return keywordURIList;

	}
	
	
	
	public List<String> getValueOfSolrEnumFieldOfSeries(String seriesId, String fieldName) {

		Map<String, String> tmpResults = new HashMap<String, String>();

		String facetCount = BundleUtils.getResource(BundleUtils.FACET_COUNT);
		String fq = "parentIdentifier:" + StringUtils.replace(seriesId, ":", "\\:");;
		log.debug("Get list of : " + fieldName);
		
		
		try {
			SolrClient solrClient = getClient(SolrCollection.DATASET);
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("q", "*");
			paramMap.put("fq", fq);
			paramMap.put("start", "0");
			paramMap.put("rows", "0");
			paramMap.put("facet", "true");
			paramMap.put("facet.limit", facetCount);
			paramMap.put("facet.field", fieldName);

			NamedList list = new NamedList(paramMap);
			SolrParams params1 = SolrParams.toSolrParams(list);
			QueryResponse qResponse = solrClient.query(params1);

			List<FacetField> facetFields = qResponse.getFacetFields();
			List<Count> counts = facetFields.get(0).getValues();
			
			for (Count c : counts) {
				String value = c.getName();
				long match = c.getCount();
				tmpResults.put(value, value + "###" + match);
			}
		} catch (Exception e) {
			log.error("", e);
		}

		List<String> results = new ArrayList<>(tmpResults.values());
		Collections.sort(results);
		return results;
	}

	public List<String> getValueOfSolrFieldOfSeries(String seriesId, String fieldName) {

		Map<String, String> tmpResults = new HashMap<String, String>();
		String fq = "parentIdentifier:" + seriesId + " AND " + fieldName + ":*";

		log.debug("Get list of : " + fieldName);
		try {

			SolrClient solrClient = getClient(SolrCollection.DATASET);
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("q", "*");
			paramMap.put("fq", fq);
			paramMap.put("start", "0");
			paramMap.put("rows", "0");

			NamedList list = new NamedList(paramMap);
			SolrParams params1 = SolrParams.toSolrParams(list);
			QueryResponse qResponse = solrClient.query(params1);
			String match = String.valueOf(qResponse.getResults().getNumFound());
			if (qResponse.getResults().getNumFound() > 0) {
				tmpResults.put(match, match + "###" + match);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> results = new ArrayList<String>(tmpResults.values());
		Collections.sort(results);
		return results;
	}

	public String[]  getMinMaxOf(String seriesId, String solrField) {
		String[] results = new String[] {"", ""};
		try {
			
			HashMap<String, String> paramMap = new HashMap<String, String>();					
			String fq = "parentIdentifier:" + StringUtils.replace(seriesId, ":", "\\:");	
			paramMap.put("q", "*");
			paramMap.put("fq", fq);
			paramMap.put("start", "0");
			paramMap.put("rows", "0");
			paramMap.put("stats", "true");
			paramMap.put("stats.field", "startDate");
					
			NamedList list = new NamedList(paramMap);
			SolrParams params = SolrParams.toSolrParams(list);
			QueryResponse qResponse = datasetClient.query(params);
			
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	        String maxStartDate = formatter.format((Date) qResponse.getFieldStatsInfo().get("startDate").getMax());
	        
	        String minStartDate = formatter.format((Date) qResponse.getFieldStatsInfo().get("startDate").getMin());	        
	        results[0]=minStartDate;
	        results[1]=maxStartDate;
	        
		} catch (Exception e) {
			log.error("", e);
		}
		return results;
		
	}
	
	public HashMap<String, List<String>> getSolrFieldOfSeries(String seriesId, HashMap<String, String> eo2SolrMappings,
			HashMap<String, String> eo2SolrMappingsEnum) {

		HashMap<String, List<String>> map = new HashMap<String, List<String>>();

		for (String key : eo2SolrMappings.keySet()) {
			
			
			
			String type = BundleUtils.getTypeOfEOPParam(key);
			
			
			String solrField = eo2SolrMappings.get(key);
			List<String> fieldValues;

			if (eo2SolrMappingsEnum.containsKey(key)) {
				fieldValues = getValueOfSolrEnumFieldOfSeries(seriesId, solrField);
				map.put(solrField, fieldValues);
			} else {
				Map<String, String> tmpResults = new HashMap<String, String>();
				try {
					HashMap<String, String> paramMap = new HashMap<String, String>();					
					String fq = "parentIdentifier:" + StringUtils.replace(seriesId, ":", "\\:") + " AND ";
					if (type.equals("integer") || type.equals("double")) {						
						fq = fq + solrField + ":[* TO *]";
					} else {
						fq = fq + solrField + ":*";
					}
						
					paramMap.put("q", "*");
					paramMap.put("fq", fq);
					paramMap.put("start", "0");
					paramMap.put("rows", "0");
					NamedList list = new NamedList(paramMap);
					SolrParams params = SolrParams.toSolrParams(list);
					QueryResponse qResponse = datasetClient.query(params);

					long match = qResponse.getResults().getNumFound();					
					if (match > 0) {
						tmpResults.put(String.valueOf(match), String.valueOf(match) + "###" + String.valueOf(match));
					}

				} catch (Exception e) {
					log.error("", e);
				}

				fieldValues = new ArrayList<>(tmpResults.values());
				Collections.sort(fieldValues);
				map.put(solrField, fieldValues);
			}
		}

		return map;
	}

	private SolrClient getClient(SolrCollection solrCollection) {
		return this.datasetClient;
	}

	private boolean isExactMatched(String param) {

		String exactMatchedValue = BundleUtils.getEOPParam(param + ".value.exact.matched");
		boolean exactMatched = false;
		if (exactMatchedValue != null) {
			if (exactMatchedValue.trim().equals("true")) {
				exactMatched = true;
			}
		}
		
		return exactMatched;
	}

}
