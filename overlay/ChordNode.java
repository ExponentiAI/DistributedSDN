package chord.net;

public class ChordNode {

	private String nodeId;
	private ChordKey nodeKey;
	private ChordNode predecessor;
	private ChordNode successor;
	private FingerTable fingerTable;
	private Controller controller;
	
	public ChordNode(String nodeId) {
		this.nodeId = nodeId;
		this.nodeKey = new ChordKey(nodeId);
		this.fingerTable = new FingerTable(this);
		this.predecessor = null;
		this.successor = this;
	}
	
	public ChordNode(String nodeId, Controller controller) {
		this.nodeId = nodeId;
		this.nodeKey = new ChordKey(nodeId);
		this.fingerTable = new FingerTable(this);
		this.predecessor = null;
		this.successor = this;
		this.controller = controller;
	}
	
	/**
	 * lookup a successor of given identifier
	 * 
	 * @param identifier		an identifier to lookup
	 * @return 		the successor of given identifier
	 */
	public ChordNode findSuccessor(String identifier) {
		ChordKey key = new ChordKey(identifier);
		return findSuccessor(key);
	}

	/**
	 * lookup a successor of given key
	 * 
	 * @param key		an hash key to lookup
	 * @return 		the successor of given identifier
	 */
	public ChordNode findSuccessor(ChordKey key) {
		if (this == successor) {
			return this;
		}
		if (key.isBetween(this.getNodeKey(), successor.getNodeKey())
				|| key.compareTo(successor.getNodeKey()) == 0) {
			return successor;
		} else {
			ChordNode node = closestPrecedingNode(key);
			if (node == this) {
				return successor.findSuccessor(key);
			}
			return node.findSuccessor(key);
		}
	}
	
	
	/**
	 * lookup the predecessor node of a given key 
	 * @param key		an hash key to lookup
	 * @return		the predecessor of given identifier
	 */
	private ChordNode closestPrecedingNode(ChordKey key) {
		for (int i = Hash.NODE_COUNT - 1; i >= 0; i--) {
			Finger finger = fingerTable.getFinger(i);
			ChordKey fingerKey = finger.getNode().getNodeKey();
			if (fingerKey.isBetween(this.getNodeKey(), key)) {
				return finger.getNode();
			}
		}
		return this;
	}

	/**
	 *  a controller node joins in the Chord ring
	 * 
	 * @param node		a bootstrapping node
	 */
	public void join(ChordNode node) {
		predecessor = null;
		successor = node.findSuccessor(this.getNodeId());
	}

	/**
	 * verifies the successor, and tells the successor about this node. Should
	 * be called periodically.
	 */
	public void stabilize() {
		ChordNode node = successor.getPredecessor();
		if (node != null) {
			ChordKey key = node.getNodeKey();
			if ((this == successor)
					|| key != this.getNodeKey()) {
				node.successor = this;
			}
		}
		successor.notifyPredecessor(this);
	}

	private void notifyPredecessor(ChordNode node) {
		ChordKey key = node.getNodeKey();
		if (predecessor == null
				|| key != predecessor.getNodeKey()) {
			predecessor = node;
		}
	}

	/**
	 * refreshes finger table.
	 */
	public void fixFingers() {
		for (int i = 0; i < Hash.NODE_COUNT; i++) {
			Finger finger = fingerTable.getFinger(i);
			ChordKey key = finger.getStart();
			finger.setNode(findSuccessor(key));
		}
	}
	
	/**
	 * update finger table entries
	 */
	public void updateFingers() {
		for (int i = 0; i < Hash.NODE_COUNT; i++) {
			Finger finger = fingerTable.getFinger(i);
			finger.setNode(successor);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ChordNode[");
		sb.append("ID=" + nodeId);
		sb.append(",KEY=" + nodeKey);
		sb.append("]");
		return sb.toString();
	}

	public void printFingerTable() {
		System.out.println("begin=======================================================");
		System.out.println("FingerTable: " + this);
		System.out.println("-------------------------------------------------------");
		System.out.println("Predecessor: " + predecessor);
		System.out.println("Successor: " + successor);
		System.out.println("-------------------------------------------------------");
		for (int i = 0; i < Hash.NODE_COUNT; i++) {
			Finger finger = fingerTable.getFinger(i);
			System.out.println(finger.getStart() + "\t" + finger.getNode());
		}
		System.out.println("end=======================================================");
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public ChordKey getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(ChordKey nodeKey) {
		this.nodeKey = nodeKey;
	}

	public ChordNode getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(ChordNode predecessor) {
		this.predecessor = predecessor;
	}

	public ChordNode getSuccessor() {
		return successor;
	}

	public void setSuccessor(ChordNode successor) {
		this.successor = successor;
	}

	public FingerTable getFingerTable() {
		return fingerTable;
	}

	public void setFingerTable(FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
}

