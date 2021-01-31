package be.spacebel.catalog.services;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import be.spacebel.catalog.models.SolrCollection;
import be.spacebel.catalog.utils.BundleUtils;
import be.spacebel.catalog.utils.Constants;
import be.spacebel.catalog.utils.xml.XPathNamespaceContext;
import be.spacebel.catalog.utils.xml.XpathUtils;

@Service
public class OSDDHandler {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(OSDDHandler.class);
	
	@Value("${xsl.dir}")
	private String xslDir;
	
	@Value("${server.url}")
	private String serverUrl;

	private XMLService xmlService;
	private SolrHandler solrHandler;
	private SearchableSeriesService searchableSeriesService;

	
	public OSDDHandler(@Autowired XMLService xmlService, @Autowired SolrHandler solrHandler, @Autowired SearchableSeriesService searchableSeriesService) {
		this.xmlService = xmlService;
		this.solrHandler = solrHandler;
		this.searchableSeriesService = searchableSeriesService;
	}



	/**
	 * Create the OSDD of the input series
	 * @param seriesId identifier of series
	 * @return OSDD of the series
	 * @throws Exception
	 */
	@Cacheable(cacheNames = "osddOfSeries", key = "#seriesId", sync = true)
	public String getOSDDOfSeries(String seriesId, String requestURL) throws Exception {
		log.info("Computing OSDD for series " + seriesId);
		
		if(!searchableSeriesService.getSearchableSeries().contains(seriesId)){
			log.debug("Series " + seriesId + " is not searchable (no data), not returning an OSDD");
			return null;
		}

		Document osddDoc;

		/*
		 * create XPATH
		 */
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("param", Constants.PARAM_NS);
		namespaces.put("os", Constants.OS_NS);
		xpath.setNamespaceContext(new XPathNamespaceContext(namespaces));

		// if (solrHandler.isSearchable(seriesId)) {

		String osddTemplate = xslDir + File.separator + Constants.OSDD_SERIES_TEMPLATE;

		List<String> sensorTypes = solrHandler.getValueOfSolrEnumFieldOfSeries(seriesId, "sensorType");
		String sensorType = null;
		if (sensorTypes.size() > 0) {
			for (String values : sensorTypes) {
				String match = values.split("###")[1];
				if (Integer.parseInt(match) > 0) {
					sensorType = values.split("###")[0].trim().toUpperCase();
				}
			}
		}

		if (sensorType != null) {
			if (sensorType.equals(Constants.OPTICAL_SENSOR_TYPE)) {
				osddTemplate = xslDir + File.separator + Constants.OSDD_OPT_SERIES_TEMPLATE;
			} else if (sensorType.equals(Constants.RADAR_SENSOR_TYPE)) {
				osddTemplate = xslDir + File.separator + Constants.OSDD_SAR_SERIES_TEMPLATE;
			}
		}

		osddDoc = xmlService.file2Document(osddTemplate);

		getParameter4Template(osddDoc, seriesId, sensorType);
		String osdd = xmlService.serializeDOM(osddDoc);

		osdd = StringUtils.replace(osdd, "@SERVER_URL@", serverUrl + "/" + Constants.SERIES_BASE_PATH + "/" +  seriesId);
		osdd = StringUtils.replace(osdd, "@SHORT_NAME@", BundleUtils.getMessage(BundleUtils.OSDD_SHORT_NAME));
		osdd = StringUtils.replace(osdd, "@DESCRIPTION@", BundleUtils.getMessage(BundleUtils.OSDD_DESC));
		osdd = StringUtils.replace(osdd, "@TAG@", BundleUtils.getMessage(BundleUtils.OSDD_TAGS));

		return osdd;
	}




	/**
	 * Create the OSDD default OSDD of the catalog
	 *
	 * @return OSDD of the catalog
	 * @throws Exception
	 */
	@Cacheable(cacheNames = "defaultOSDD", sync = true)
	public String getDefaultOSDD(String requestURL) throws Exception {

		log.info("Computing catalog default OSDD");
		
		
		Document osddDoc;

		osddDoc = xmlService.file2Document(xslDir + File.separator + Constants.OSDD_TEMPLATE);
		/*
		 * create XPATH
		 */
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("param", Constants.PARAM_NS);
		namespaces.put("os", Constants.OS_NS);
		xpath.setNamespaceContext(new XPathNamespaceContext(namespaces));

		HashMap<String, String> eo2SolrMappingsEnum = BundleUtils.getEo2SolrMappings4Series();

		/* get url template for series */
		Node url4Series = XpathUtils.getNodeByXPath(osddDoc, "os:OpenSearchDescription/os:Url[@rel='collection']");

		if (url4Series != null) {
			for (String key:eo2SolrMappingsEnum.keySet()) {
				String value = eo2SolrMappingsEnum.get(key);
				List<String> paramValues = solrHandler.getList(SolrCollection.SERIES,
						value);
				Collections.sort(paramValues);
	
	
				if (paramValues.size() > 0) {
					Element parameter = osddDoc.createElementNS(Constants.PARAM_NS,
							"param:Parameter");
					parameter.setAttribute("name", key);
					parameter.setAttribute("value", "{eo:" + key +"}");
	
					for (String values : paramValues) {
						String paramValue  = values.split("###")[0];
						String match = values.split("###")[1];
						if (paramValue != null && StringUtils.isNotEmpty(paramValue) && Integer.parseInt(match) > 0) {
							if (paramValue.length() > 1) {
	
								Element option = osddDoc.createElementNS(Constants.PARAM_NS,
										"param:Option");
								option.setAttribute("label", paramValue + " (" + match + ")");
								option.setAttribute("value", paramValue);
								parameter.appendChild(option);
							}
						}
					}
					url4Series.appendChild(parameter);
				}
	
			}
		}

		/* get url template for dataset */
		Element url4Dataset = (Element) XpathUtils.getNodeByXPath(osddDoc,
				"os:OpenSearchDescription/os:Url[@rel='results']");

		String template;

		String original = BundleUtils.getResource(BundleUtils.DATSET_ORIGINAL_KEY);

		
		if (original == null || StringUtils.equals(original, "om")) {			
			template = "@SERVER_URL@/series/{eo:parentIdentifier}/datasets?httpAccept=application/geo%2Bjson" +
					"&startRecord={os:startIndex?}&startPage={os:startPage?}&maximumRecords={os:count?}&startDate={time:start?}&endDate={time:end?}&subject={dc:subject?}"  +
					"&type={dc:type?}&bbox={geo:box?}&name={geo:name?}&lat={geo:lat?}&lon={geo:lon?}&radius={geo:radius?}&uid={geo:uid?}&geometry={geo:geometry?}" +
					getParameter4Template() + "&specificationTitle={eo:specificationTitle?}&degree={eo:degree?}";
			url4Dataset.setAttribute("template", template);
		}

		List<String> parentIds = solrHandler.getList(SolrCollection.DATASET, Constants.PARENT_ID_PARAM);
		Collections.sort(parentIds);
		if (parentIds.size() > 0) {
			Element parameter = osddDoc.createElementNS(Constants.PARAM_NS, "param:Parameter");
			parameter.setAttribute("name", "parentIdentifier");
			parameter.setAttribute("value", "{eo:parentIdentifier}");

			for (String values : parentIds) {
				String parentId = values.split("###")[0];
				String match = values.split("###")[1];
				if (Integer.parseInt(match) > 0) {
					Element option = osddDoc.createElementNS(Constants.PARAM_NS, "param:Option");
					option.setAttribute("label", parentId);
					option.setAttribute("value", parentId);
					parameter.appendChild(option);
				}
			}
			url4Dataset.appendChild(parameter);
		}

		String osdd = xmlService.serializeDOM(osddDoc);
		osdd = StringUtils.replace(osdd, "@SERVER_URL@", serverUrl);
		osdd = StringUtils.replace(osdd, "@SHORT_NAME@", BundleUtils.getMessage(BundleUtils.OSDD_SHORT_NAME));
		osdd = StringUtils.replace(osdd, "@DESCRIPTION@", BundleUtils.getMessage(BundleUtils.OSDD_DESC));
		osdd = StringUtils.replace(osdd, "@TAG@", BundleUtils.getMessage(BundleUtils.OSDD_TAGS));
		return osdd;

	}


	private void getParameter4Template(Document osddDoc, String seriesId, String sensorType) throws Exception {

		String result = "";

		/*
		 * create XPATH
		 */
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("param", Constants.PARAM_NS);
		namespaces.put("os", Constants.OS_NS);
		xpath.setNamespaceContext(new XPathNamespaceContext(namespaces));

		Element url = (Element) XpathUtils.getNodeByXPath(osddDoc, "os:OpenSearchDescription/os:Url[@rel='results']");
		String template = url.getAttribute("template");
		String type = url.getAttribute("type");

		HashMap<String, String> eo2SolrMappings = BundleUtils.getEo2SolrMappings(false);
		HashMap<String, String> eo2SolrMappingsEnum = BundleUtils.getEo2SolrMappings(true);
		eo2SolrMappings.putAll(eo2SolrMappingsEnum);

		HashMap<String, List<String>> map = solrHandler.getSolrFieldOfSeries(seriesId, eo2SolrMappings, eo2SolrMappingsEnum);

		for (String key : eo2SolrMappings.keySet()) {

			
			String solrField = eo2SolrMappings.get(key);
			String eoParam = BundleUtils.getEOPParam(key + ".value.description");			
			List<String> fieldValues = map.get(solrField);
			
			/*
			 * List<String> fieldValues;
			 *
			 * if (eo2SolrMappingsEnum.containsKey(key)) { fieldValues =
			 * solrHandler4Dataset.getValueOfSolrEnumFieldOfSeries(seriesId, solrField); }
			 * else { fieldValues =
			 * solrHandler4Dataset.getValueOfSolrFieldOfSeries1(seriesId, solrField); }
			 */

			if (fieldValues.size() > 0) {
				ArrayList<String> possibleValues = new ArrayList<String>();

				for (String fieldValue : fieldValues) {
					String match = fieldValue.split("###")[1];
					if (Integer.parseInt(match) > 0) {
						possibleValues.add(fieldValue.split("###")[0].trim().toLowerCase());
					}
				}

				if (key.equals("sensorType") && sensorType != null
						&& (sensorType.equalsIgnoreCase("OPTICAL") || sensorType.equalsIgnoreCase("RADAR")
						|| sensorType.equalsIgnoreCase("ATMOSPHERIC") || sensorType.equalsIgnoreCase("LIMB")
						|| sensorType.equalsIgnoreCase("ALTIMETRIC"))) {
					result = result + "&" + key + "=" + sensorType;
					Node parameter = XpathUtils.getNodeByXPath(url, "param:Parameter[@name='sensorType']");
					if (parameter != null) {
						url.removeChild(parameter);
					}
				} else {
					Node parameter = XpathUtils.getNodeByXPath(url, "param:Parameter[@name='" + key + "']");
					if (possibleValues.size() > 0 ) {
						result = result + "&" + key + "={" + eoParam + "?}";
						if (parameter != null) {
							if (eo2SolrMappingsEnum.containsKey(key)) {
								NodeList options = XpathUtils.getNodesByXPath(parameter, "param:Option");
								if (options.getLength() > 0) {
									for (int i = 0; i < options.getLength(); i++) {
										Element option = (Element) options.item(i);
										String optionValue = option.getAttribute("value").toLowerCase();
										if (!possibleValues.contains(optionValue)) {
											parameter.removeChild(option);
										}
									}
								}
							}
						} else {
							if (eo2SolrMappingsEnum.containsKey(key)) {

								Element paramEle = osddDoc.createElementNS(Constants.PARAM_NS, "param:Parameter");
								paramEle.setAttribute("name", key);
								paramEle.setAttribute("value", "{eo:" + key + "}");
								for (String s : possibleValues) {
									Element optionEle = osddDoc.createElementNS(Constants.PARAM_NS, "param:Option");
									optionEle.setAttribute("label", s.toUpperCase());
									optionEle.setAttribute("value", s.toUpperCase());
									paramEle.appendChild(optionEle);
								}
								url.appendChild(paramEle);
							}
						}

					} else {
						if (parameter != null) {
							url.removeChild(parameter);
						}
					}
				}
			} else {
				Node parameter = XpathUtils.getNodeByXPath(url, "param:Parameter[@name='" + key + "']");
				if (parameter != null) {
					url.removeChild(parameter);
				}
			}
		}

		Element pidParamElement = osddDoc.createElementNS(Constants.PARAM_NS, "param:Parameter");
		pidParamElement.setAttribute("name", "parentIdentifier");
		pidParamElement.setAttribute("value", "{eo:parentIdentifier}");

		Element pidOptionElement = osddDoc.createElementNS(Constants.PARAM_NS, "param:Option");
		pidOptionElement.setAttribute("label", seriesId);
		pidOptionElement.setAttribute("value", seriesId);
		pidParamElement.appendChild(pidOptionElement);
		url.appendChild(pidParamElement);

		String[] startDates = solrHandler.getMinMaxOf(seriesId, "startDate");
		String minstartDate = startDates[0];
		String maxstartDate = startDates[1];
		
		Element sDateParamElement = (Element) XpathUtils.getNodeByXPath(url, "param:Parameter[@name='startDate']");
		if (sDateParamElement == null) {
			sDateParamElement = osddDoc.createElementNS(Constants.PARAM_NS, "param:Parameter");
			sDateParamElement.setAttribute("name", "startDate");
			sDateParamElement.setAttribute("value", "{time:start}");
			sDateParamElement.setAttribute("pattern", "^[0-9]{4}-[0-9]{2}-[0-9]{2}(T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\\\.[0-9]{0,3})?(Z)$)?");
			url.appendChild(sDateParamElement);
		}
		
		
		if (!minstartDate.isEmpty()) {
			sDateParamElement.setAttribute("minInclusive", minstartDate + "T00:00:00.000Z");
		}
		
		Element eDateParamElement = (Element) XpathUtils.getNodeByXPath(url, "param:Parameter[@name='endDate']");
		if (eDateParamElement == null) {
			eDateParamElement = osddDoc.createElementNS(Constants.PARAM_NS, "param:Parameter");
			eDateParamElement.setAttribute("name", "endDate");
			eDateParamElement.setAttribute("value", "{time:end}");
			eDateParamElement.setAttribute("pattern", "^[0-9]{4}-[0-9]{2}-[0-9]{2}(T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\\\.[0-9]{0,3})?(Z)$)?");
			url.appendChild(sDateParamElement);
			
		}
		
		if (!maxstartDate.isEmpty()) {
			eDateParamElement.setAttribute("maxInclusive", maxstartDate + "T23:59:59.999Z");
		}
				
		template = template + result + "&specificationTitle={eo:specificationTitle?}&degree={eo:degree?}"; 
		if (type.equals(Constants.ATOM_MIME_TYPE)) { 
			template = template + "&recordSchema={sru:recordSchema?}";
		}  
		url.setAttribute("template", template);
		
		
	}

	private String getParameter4Template() {

		String result = StringUtils.EMPTY;
		HashMap<String, String> eo2SolrMappings = BundleUtils.getEo2SolrMappings(false);

		for (String key : eo2SolrMappings.keySet()) {
			String solrField = eo2SolrMappings.get(key);
			String eoParam = BundleUtils.getEOPParam(key + ".value.description");
			boolean isSupported = this.solrHandler.isSorlFieldHasValue("*", solrField);

			if (isSupported) {
				result = result + "&" + key + "={" + eoParam + "?}";
			}
		}

		return result;

	}


}