package chord.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Controller extends Thread{
	
	private String controllerId;
	private int controllerMum;
	private ChordNode chordnode;
	private List<ChordKey> devices;
	private SupervisedTable supervisedTable;
	
	public Controller(String controllerName){
		this.controllerId = controllerName;
		this.chordnode = new ChordNode(controllerName, this);
		this.devices = new ArrayList<ChordKey>();
		this.supervisedTable = new SupervisedTable();
	}
	
	/**
	 * controller node join the Chord ring
	 * @param startNode  the bootstrapping node that used to establish the Chord ring
	 */
	public void jionChord(ChordNode startNode){
		this.chordnode.join(startNode);
		ChordNode preceding = this.chordnode.getSuccessor().getPredecessor();
		this.chordnode.stabilize();
		if(preceding == null) {
			this.chordnode.getSuccessor().stabilize();
		} else {
			preceding.stabilize();
		}
		this.chordnode.fixFingers();
	}
	
	/**
	 * controller node leave the Chord ring,
	 * @param leaveNode	
	 */
	public void leaveChordRing(){
		
		ChordNode successor = this.getChordnode().getSuccessor();
		ChordNode predecessor = this.getChordnode().getPredecessor();
		successor.setPredecessor(predecessor);
		predecessor.setSuccessor(successor);
		
		Map<ChordKey, Entry> succtable = successor.getController().getSupervisedTable().getTable();
		Map<ChordKey, Entry> localtable = this.getSupervisedTable().getTable();
		if(localtable.keySet().size() != 0){
			for (ChordKey k : localtable.keySet()) {
				succtable.put(k, localtable.get(k));
			}
		}
	
		this.getChordnode().setSuccessor(null);
		this.getChordnode().setPredecessor(null);
		successor.fixFingers();	
	}
	
	/**
	 * add a controller node to the Chord ring
	 * @param c		the boot node
	 */
	public void joinChordRing(Controller c){
		
		this.jionChord(c.getChordnode());
		ChordNode successor = this.getChordnode().getSuccessor();
		Map<ChordKey, Entry> succtable = successor.getController().getSupervisedTable().getTable();
		Map<ChordKey, Entry> localtable = this.getSupervisedTable().getTable();
		for (ChordKey key : succtable.keySet()) {
			if(key.compareTo(this.getChordnode().getNodeKey()) <= 0){
				localtable.put(key, succtable.get(key));
				succtable.remove(key);
			}
		}
	}
	
	/**
	 *    update the supervised table of supervisory controller when a mobile device come to connect to a controller,
	 * 	if  the supervisory controller contains this mobile device, update the entry in the supervisory table, otherwise,
	 * 	add and initialize an entry.  
	 * @param chordKey  	the chordkey of mobile device
	 * @param user	 	the mane or address
	 * @param currentController 	the ip or mac address of current associated controller
	 */
	public void updateSupervisedTable(Controller supervisoryController, ChordKey chordKey, String user, String currentController){
		if(supervisoryController.isContainsChordKey(chordKey)){
			supervisoryController.getSupervisedTable().updateEntry(currentController, chordKey);
		}else{
			devices.add(chordKey);
			supervisoryController.getSupervisedTable().addEntry(chordKey, user, currentController);
		}
	}
	
	/**
	 * find the given mobile device's supervisory controller
	 * @param device   the mobile device whose supervisory controller you want to locate
	 * @return  the supervisory controller of device
	 */
	public ChordNode findSupervisoryController(ChordKey device){	
		return this.getChordnode().findSuccessor(device);	
	}
	
	/**
	 *  Determine if the controller node contains the  device
	 * @param device 
	 * @return
	 */
	public  boolean isContainsChordKey(ChordKey device){
		return this.devices.contains(device);
	}
	
	public String getControllerId() {
		return controllerId;
	}

	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}
	
	public int getControllerMum() {
		return controllerMum;
	}

	public void setControllerMum(int controllerMum) {
		this.controllerMum = controllerMum;
	}

	public ChordNode getChordnode() {
		return chordnode;
	}

	public void setChordnode(ChordNode chordnode) {
		this.chordnode = chordnode;
	}

	public List<ChordKey> getDevices() {
		return devices;
	}

	public void setDevices(List<ChordKey> devices) {
		this.devices = devices;
	}

	public SupervisedTable getSupervisedTable() {
		return supervisedTable;
	}

	public void setSupervisedTable(SupervisedTable supervisedTable) {
		this.supervisedTable = supervisedTable;
	}
}
