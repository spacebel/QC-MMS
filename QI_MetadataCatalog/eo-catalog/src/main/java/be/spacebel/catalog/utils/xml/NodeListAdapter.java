package be.spacebel.catalog.utils.xml;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListAdapter implements NodeList {

	private List<Node> nodes;

	public NodeListAdapter(List<Node> nodes) {
		this.nodes = nodes;
	}

	@Override
	public Node item(int index) {
		return nodes.get(index);
	}

	@Override
	public int getLength() {
		return nodes.size();
	}
}
