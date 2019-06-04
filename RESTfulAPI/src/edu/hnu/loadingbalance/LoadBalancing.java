package edu.hnu.loadingbalance;

public class LoadBalancing {
	public static int Threshold = 100;
	public static int MaxBacklog = 100000;
	public static int q = 7;
	public static int P = 11;
	/**
	 * According to the length of the mobile information table and the network calculation result, 
	 * the double hash is implemented to implement dynamic mapping between the mobile device 
	 * and the controller.
	 * @param key  Mobile terminal's mobile ID, generally use a unique MAC address.
	 * @param associatedcontroller the associated controller of the current mobile device
	 * @return
	 */
	public static ChordNode DoubleHashing(String key, Controller associatedcontroller){
		ChordKey m = new ChordKey(key);
		ChordNode succNode = associatedcontroller.findSupervisoryController(m);
	 	if(succNode.getController().getSupervisedTable().getTable().size() > Threshold && NetworkCurve.getNetworkCurve() > MaxBacklog){
	 		for(int j = 1; ; j++){
	 			int key1 = j*(q - (succNode.getController().getControllerMum() % q));
	 			ChordNode backsuccNode = succNode;
	 			for(int k = 1; k <= key1; k++){
	 				backsuccNode = backsuccNode.getSuccessor();
	 			}
	 			if(backsuccNode.getController().getSupervisedTable().getTable().size() < 100){
	 				return backsuccNode;
	 			}
	 		}
	 	}
		return succNode;
	}
}
