package be.spacebel.catalog.utils.xml;

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

import org.apache.commons.lang.StringUtils;

public class XPathNamespaceContext implements NamespaceContext {
	private Map<String, String> namespaces = null;

	public XPathNamespaceContext(Map<String, String> namespaces) {
		super();
		this.namespaces = namespaces;
	}

	public String getNamespaceURI(String prefix) {
		if (prefix == null)
			throw new IllegalArgumentException("The prefix cannot be null.");
		else
			return namespaces.get(prefix);
	}

	public String getPrefix(String namespace) {
		if (namespace == null)
			throw new IllegalArgumentException("The namespace uri cannot be null.");
		else {
			Iterator<String> prefixes = namespaces.keySet().iterator();
			while (prefixes.hasNext()) {
				String strPrefix = prefixes.next();
				if (StringUtils.equalsIgnoreCase(namespace, namespaces.get(strPrefix))) {
					return strPrefix;
				}
			}
			throw new IllegalArgumentException("The namespace uri cannot be null.");
		}
	}

	public Iterator<String> getPrefixes(String namespace) {
		return null;
	}
}
