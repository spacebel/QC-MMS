package be.spacebel.eoportal.client.util;

import be.spacebel.eoportal.client.business.data.Constants;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class implements XML utilities
 *
 * @author mng
 */
public class XMLUtility implements Serializable {

    private final static Logger log = Logger.getLogger(XMLUtility.class);

    private static final String OS_NAMESPACE_PREFIX = "os";

    public static String getNodeContent(Node n) {
        String returnString = "";
        TransformerFactory transfac = TransformerFactory.newInstance();
        try {
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(n);
            trans.transform(source, result);
            returnString = sw.toString();
        } catch (Exception e) {
            log.error("XMLParserUtils.getNodeContent().error:" + e.getMessage());
        }
        return returnString;
    }

    public static String getNodeAttValue(Node node, String attName) {
        String value = null;
        if (node.getAttributes() != null && node.getAttributes().getNamedItem(attName) != null) {
            value = node.getAttributes().getNamedItem(attName).getNodeValue();
        }
        return value;
    }

    /**
     *
     * @param xmlSource
     * @param schemaLocation
     * @return
     */
    public static Node buildNode(String xmlSource, URL schemaLocation) {
        log.debug("buildNode");
        if (StringUtils.isNotEmpty(xmlSource)) {
            DOMParser parser = new DOMParser();
            try {
                if (schemaLocation != null) {

                    XMLReader validator = XMLReaderFactory.createXMLReader();
                    validator.setFeature("http://xml.org/sax/features/validation",
                            true);
                    validator.setFeature(
                            "http://apache.org/xml/features/validation/schema",
                            true);
                    validator
                            .setProperty(
                                    "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                                    schemaLocation.toExternalForm());
                    // parser.setContentHandler(new IndexationHandler(this));
                    validator.parse(new InputSource(new StringReader(xmlSource)));

                }

                parser.parse(new InputSource(new StringReader(xmlSource)));
            } catch (Exception e) {
                log.error(e);
            }
            Document doc = parser.getDocument();
            Node root = doc.getChildNodes().item(0);
            return root;
        } else {
            return null;
        }

    }

    public static Node buildNode(String xmlSource) {
        return buildNode(xmlSource, null);
    }

    public static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException e) {
            log.error(e);
        }
        return sw.toString();
    }

    public static String getNodeValue(Node node) {
        String value = null;
        try {
            value = node.getFirstChild().getNodeValue();
        } catch (Exception e) {

        }
        return value;
    }

    public static Map<String, String> getNamespaces(Document doc, boolean prefixIsKey)
            throws IOException {
        Map<String, String> namespaces = new HashMap<>();
        NamedNodeMap atts = doc.getDocumentElement().getAttributes();
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); i++) {
                Node node = atts.item(i);
                String attName = node.getNodeName().trim();
                String ns = node.getNodeValue();
                String prefix = "";
                if ("xmlns".equalsIgnoreCase(attName)) {
                    // this is the default namespace
                    if (Constants.OS_NAMESPACE.equalsIgnoreCase(ns)) {
                        prefix = OS_NAMESPACE_PREFIX;
                    } else {
                        log.debug("Unknow prefix for this namespace: " + ns);
                    }
                } else {
                    if (StringUtils.startsWithIgnoreCase(attName, "xmlns:")) {
                        prefix = StringUtils.substringAfter(attName, ":");
                    }
                }
                log.debug(prefix + " = " + ns);
                if (StringUtils.isNotEmpty(ns) && StringUtils.isNotEmpty(prefix)) {
                    if (prefixIsKey) {
                        namespaces.put(prefix, ns);
                    } else {
                        namespaces.put(ns, prefix);
                    }
                }

            }
        }
        return namespaces;
    }   
}
