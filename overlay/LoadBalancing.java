package chord.net;

import java.util.ArrayList;

public class LoadBalancing {
	public static int Threshold = 100;
	public static int MaxBacklog = 100000;
	public static int q = 7;
	public static int P = 11;
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
