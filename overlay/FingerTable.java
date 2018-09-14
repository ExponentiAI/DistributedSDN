package chord.net;


public class FingerTable {

	Finger[] fingers;

	public FingerTable(ChordNode node) {
		this.fingers = new Finger[Hash.NODE_COUNT];
		for (int i = 0; i < fingers.length; i++) {
			ChordKey start = node.getNodeKey().createStartKey(i);
			fingers[i] = new Finger(start, node);
		}
	}

	public Finger getFinger(int i) {
		return fingers[i];
	}

}
