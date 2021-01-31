package be.spacebel.catalog.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.Logger;
import org.apache.xml.serializer.DOMSerializer;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service
public class XMLService {

	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(XMLService.class);

	private ThreadLocal<DocumentBuilder> documentBuilder = new ThreadLocal<DocumentBuilder>(){
		@Override
		protected DocumentBuilder initialValue() {
			DocumentBuilderFactory builderFactory;
			builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setNamespaceAware(true);
			builderFactory.setValidating(false);

			try {
				return builderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
		}
	};

	private ThreadLocal<TransformerFactory> transformerFactory = new ThreadLocal<TransformerFactory>() {
		@Override
		protected TransformerFactory initialValue() {
			return TransformerFactory.newInstance();
		}
	};


	public Document createEmptyDocument() {
		return documentBuilder.get().newDocument();
	}

	public String serializeDOM(Document xmlDoc) throws Exception {
		StringWriter stringWriter = new StringWriter();
		Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
		props.setProperty(OutputKeys.INDENT, "yes");
		Serializer serializer = SerializerFactory.getSerializer(props);
		serializer.setWriter(stringWriter);
		DOMSerializer domSerializer = serializer.asDOMSerializer();
		domSerializer.serialize(xmlDoc);
		return stringWriter.toString();
	}

	public void serialize(Node node, File out) throws IOException, TransformerException {
		try(FileOutputStream os = new FileOutputStream(out)){
			serialize(node, os);
		}
	}

	private void serialize(Node node, OutputStream os) throws TransformerException {
		this.transformerFactory.get()
				.newTransformer()
				.transform(new DOMSource(node), new StreamResult(os));
	}



	public Document file2Document(String fileName) throws Exception {
		Document doc;
		File f = new File(fileName);
		doc = documentBuilder.get().parse(f);
		return doc;
	}


	public Document stream2Document(String xmlStream) {
		log.debug("stream2Document invoked");
		Document doc = null;
		try {
			if ((xmlStream != null) && (xmlStream.length() > 0)) {
				StringReader stringReader = new StringReader(xmlStream);
				doc = documentBuilder.get().parse(new InputSource(stringReader));
			} else { // null or empty string
				doc = this.createEmptyDocument();
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return doc;
	}



	public String transformOMMetadata(String metadata, String xslFile) throws Exception {
		Document xmlDoc = stream2Document(metadata);
		File xslResponseFile = new File(xslFile);
		Document respDoc = createEmptyDocument();
		DOMResult respDOMesult = new DOMResult(respDoc);
		transformDOM2DOM(new DOMSource(xmlDoc), new StreamSource(xslResponseFile), respDOMesult);
		return serializeDOM(respDoc);

	}

	public String transformAtom2OMMetadata(String metadata, String xslFile, String parentId) throws Exception {

		Document xmlDoc = stream2Document(metadata);
		File xslResponseFile = new File(xslFile);
		Document respDoc = createEmptyDocument();
		DOMResult respDOMesult = new DOMResult(respDoc);

		Vector<String[]> xslParams = new Vector<>();
		String[] parentIdParam = { "parentId", parentId };
		xslParams.add(parentIdParam);

		transformDOM2DOMWithParam(new DOMSource(xmlDoc), new StreamSource(xslResponseFile), xslParams,
				respDOMesult);
		return serializeDOM(respDoc);
	}


	public void transformDOM2DOM(DOMSource domSource, StreamSource xslStreamSource, DOMResult domResult) {

		try {
			Transformer transformer = getTransformer(xslStreamSource);
			transformer.transform(domSource, domResult);
		} catch (Exception e) {
			log.error("", e);
		}
	}
        
        public String format(Document xmlDoc) {
            log.debug("Format XML document with inden = 4");
            try {
                /* 
                 Remove whitespaces outside tags
                 */

                xmlDoc.normalize();

                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", xmlDoc, XPathConstants.NODESET);

                for (int i = 0; i < nodeList.getLength(); ++i) {
                    Node node = nodeList.item(i);
                    node.getParentNode().removeChild(node);
                }

                Transformer transformer = transformerFactory.get().newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(4));

                // Return pretty print xml string
                StringWriter stringWriter = new StringWriter();
                transformer.transform(new DOMSource(xmlDoc), new StreamResult(stringWriter));
                return stringWriter.toString();
            } catch (XPathExpressionException | DOMException | IllegalArgumentException | TransformerException e) {
                String errorMsg = "Error happens when formatting the XML document.";
                log.error(errorMsg + ":" + e.getMessage());
                return null;
            }
        }


	private void transformDOM2DOMWithParam(DOMSource domSource, StreamSource xslStreamSource, Collection<String[]> param,
										   DOMResult domResult) throws Exception {
		Transformer transformer = getTransformer(xslStreamSource);
		if (!param.isEmpty()) {
			for (String[] paramStr : param) {
				// Get the name / value pair
				String paramName = paramStr[0];
				String paramValue = paramStr[1];
				// Set parameter to the transformer
				transformer.setParameter(paramName, paramValue);
			}
		}
		transformer.transform(domSource, domResult);

	}


	private Transformer getTransformer(StreamSource xslStreamSource) throws TransformerConfigurationException {
		if (xslStreamSource != null) {
			return transformerFactory.get().newTransformer(xslStreamSource);
		} else {
			return transformerFactory.get().newTransformer();
		}
	}
}
