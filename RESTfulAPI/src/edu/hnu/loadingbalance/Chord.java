package edu.hnu.loadingbalance;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.hnu.loadingbalance.ChordNode;

public class Chord {

	List<ChordNode> nodeList = new ArrayList<ChordNode>();
	SortedMap<ChordKey, ChordNode> sortedNodeMap = new TreeMap<ChordKey, ChordNode>();
	Object[] sortedKeyArray;
	int nodeCount;
	
	public List<ChordNode> getNodeList() {
		return nodeList;
	}
	
	public SortedMap<ChordKey, ChordNode> getSortedNodeMap() {
		return sortedNodeMap;
	}

	public int getNodeCount() {
		return nodeCount;
	}

	public void createNode(String nodeId) throws ChordException {
		ChordNode node = new ChordNode(nodeId);
		if (sortedNodeMap.get(node.getNodeKey()) != null ) {
			return;
		}		
		nodeList.add(node);
		nodeCount = nodeList.size();		
		sortedNodeMap.put(node.getNodeKey(), node);
	}

	public ChordNode getNode(int i) {
		return (ChordNode) nodeList.get(i);
	}

	public ChordNode getSortedNode(int i) {
		if (sortedKeyArray == null) {
			sortedKeyArray = sortedNodeMap.keySet().toArray();
		}
		return (ChordNode) sortedNodeMap.get(sortedKeyArray[i]);
	}

	public void addNode(ChordNode node) {
		nodeList.add(node);
		nodeCount = nodeList.size();		
		sortedNodeMap.put(node.getNodeKey(), node);
	}
}
