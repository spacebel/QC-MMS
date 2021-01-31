package be.spacebel.catalog.utils.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import be.spacebel.catalog.utils.Constants;

public class XpathUtils {
	
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(XpathUtils.class);

	private static Map<String, String> namespaces;

	static {
		namespaces = new ConcurrentHashMap<>();
		namespaces.put("gmd", Constants.GMD_NS);
		namespaces.put("gmi", Constants.GMI_NS);
		namespaces.put("gmx", Constants.GMX_NS);
		namespaces.put("gco", Constants.GCO_NS);
		namespaces.put("gml", Constants.GML_NS);
		namespaces.put("dc", Constants.DC_NS);
		namespaces.put("dct", Constants.DCT_NS);
		namespaces.put("om", Constants.OM_NS);
		namespaces.put("os", Constants.OS_NS);
		namespaces.put("om", Constants.OM_NS);
		namespaces.put("ows", Constants.OWS_NS);
		namespaces.put("param", Constants.PARAM_NS);
		namespaces.put("eop", Constants.EOP_20_NS);
		namespaces.put("opt", Constants.OPT_20_NS);
		namespaces.put("sar", Constants.SAR_20_NS);
		namespaces.put("alt", Constants.ALT_20_NS);
		namespaces.put("atm", Constants.ATM_20_NS);
		namespaces.put("eop21", Constants.EOP_21_NS);
		namespaces.put("opt21", Constants.OPT_21_NS);
		namespaces.put("sar21", Constants.SAR_21_NS);
		namespaces.put("alt21", Constants.ALT_21_NS);
		namespaces.put("atm21", Constants.ATM_21_NS);
		namespaces.put("atom", Constants.ATOM_NS);
		namespaces.put("param", Constants.PARAM_NS);
		namespaces.put("sru", Constants.SRU_NS);
                
                namespaces.put("mdq", "http://standards.iso.org/iso/19157/-2/mdq/1.0");
                namespaces.put("mcc", "http://standards.iso.org/iso/19115/-3/mcc/1.0");
                namespaces.put("cit", "http://standards.iso.org/iso/19115/-3/cit/1.0");
                namespaces.put("igco", "http://standards.iso.org/iso/19115/-3/gco/1.0");

	}

	public static String getNodeValueByXPath(Node node, String strXPATH) {
		try {
			Node result = (Node) getDomXPath(strXPATH).selectSingleNode(node);
			if(result == null){
				return null;
			}
			return result.getTextContent();
		} catch (Exception e) {
			log.trace("Error evaluating XPath expression", e);
			return null;
		}
	}

	public static List<String> getNodesValuesByXPath(Node node, String strXPATH) {
		try {
			List<Node> nodes = getDomXPath(strXPATH).selectNodes(node);
			return nodes.stream().map(Node::getTextContent).collect(Collectors.toList());
		} catch (Exception e) {
			log.trace("Error evaluating XPath expression", e);
			return new ArrayList<>();
		}
	}

	public static Node getNodeByXPath(Node node, String strXPATH) {
		try {
			return (Node) getDomXPath(strXPATH).selectSingleNode(node);
		} catch (Exception e) {
			log.trace("Error evaluating XPath expression", e);
			return null;
		}
	}

	public static NodeList getNodesByXPath(Node node, String strXPATH) {
		try {
			List<Node> nodes = getDomXPath(strXPATH).selectNodes(node);
			return new NodeListAdapter(nodes);
		} catch (Exception e) {
			log.error("Error evaluating XPath expression", e);
			return null;
		}
	}

	public static String getNodeAttributeValueByXPath(Node node, String strXPATH, String attribute) {
		try {
			Element result = (Element) getDomXPath(strXPATH).selectSingleNode(node);
			if (result != null) {
				return result.getAttribute(attribute);
			} else {
				return null;
			}
		} catch (Exception e) {
			log.trace("Error evaluating XPath expression", e);
			return null;
		}
	}

	private static DOMXPath getDomXPath(String strXPATH) throws JaxenException {
		DOMXPath xpath = new DOMXPath(strXPATH);
		xpath.setNamespaceContext(new SimpleNamespaceContext(namespaces));
		return xpath;
	}
}
