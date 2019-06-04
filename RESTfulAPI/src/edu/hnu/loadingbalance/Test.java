package edu.hnu.loadingbalance;

import java.util.ArrayList;
import java.util.List;

import edu.hnu.loadingbalance.ChordNode;

public class Test { 

	public static void main(String[] args) throws Exception{
		
		//test Chord algorithm
		List<Controller> controllerSet = new ArrayList<>();
		Controller c1 = new Controller("192.168.1.1");
		Controller c2 = new Controller("192.168.1.13");
		Controller c3 = new Controller("192.168.1.34");
		Controller c4 = new Controller("192.168.1.45");
		Controller c5 = new Controller("192.168.1.56");
		Controller c6 = new Controller("192.168.1.66");

		controllerSet.add(c1);
		controllerSet.add(c2);
		controllerSet.add(c3);
		controllerSet.add(c4);
		controllerSet.add(c5);
		controllerSet.add(c6);
		
		System.out.println("=============Start Chord============");
		
		Controller N0 = controllerSet.get(0);
		for(int i = 0; i < controllerSet.size(); i++) {			
			Controller controllerNode = controllerSet.get(i);
			controllerNode.jionChord(N0.getChordnode());
		}//Chord ring is established.
		
		//show the result of Chord ring
		for(int j=0; j < controllerSet.size() ; j++) {
			ChordNode node = controllerSet.get(j).getChordnode();
			ChordNode p = node.getPredecessor();
			ChordNode s = node.getSuccessor();
			if(s != null) {
				System.out.println(node.getNodeId()+"->Successor->"+s);
			}
			if(p != null) {
				System.out.println(node.getNodeId()+"->Predecessor->"+p);
			}
		}
		
		//mobile device join Chord ring, 
		String mobileId = "192.168.1.20";
		ChordKey m = new ChordKey(mobileId);  //map the address into Chord ring
		
		//1. mobile device connect to controller c(1)
		ChordNode succNode1 = c1.findSupervisoryController(m);
		System.out.println("the ip address of supervised controller: " + succNode1.getNodeId() + " Current associated controller:  " + c1.getControllerId());
		Controller controller1 = succNode1.getController();
		controller1.updateSupervisedTable(controller1, m, mobileId, c1.getControllerId());
		Entry entry1 = controller1.getSupervisedTable().getTable().get(m);
		System.out.println("User:   " + entry1.getUser() + "   Current:   " + entry1.getCurrent() + "   Previous:   " + entry1.getPrevious() + "   Time:   " + entry1.getTime());
		
		//2. after 3 seconds, mobile device move to another area and connect to controller c(2)
		Thread.sleep(3000);
		ChordNode succNode2 = c2.findSupervisoryController(m);
		System.out.println("move to another area, the ip address of supervised controller: " + succNode2.getNodeId() + " Current associated controller:  " + c3.getControllerId());
		Controller controller2 = succNode2.getController();
		controller2.updateSupervisedTable(controller2, m, mobileId, c2.getControllerId());
		Entry entry2 = controller2.getSupervisedTable().getTable().get(m);
		System.out.println("User:   " + entry2.getUser() + "   Current:   " + entry2.getCurrent() + "   Previous:   " + entry2.getPrevious() + "   Time:   " + entry2.getTime());
		
		//3. controller node join Chord ring
		Controller controller4 = new Controller("192.168.1.30");
		controller4.joinChordRing(N0);
		System.out.println("add controller, the supervised controller of mobile: " + c3.findSupervisoryController(m).getNodeId());
		System.out.println("add controller, the ip address of successor node: " + controller4.getChordnode().getSuccessor().getNodeId());
		Entry entry4 = controller4.getSupervisedTable().getTable().get(m);
		System.out.println("User:   " + entry4.getUser() + "   Current:   " + entry4.getCurrent() + "   Previous:   " + entry4.getPrevious() + "   Time:   " + entry4.getTime());
		
		//4. controller nodes leave Chord ring
		c3.leaveChordRing();
		controller4.leaveChordRing();
		c4.leaveChordRing();
		ChordNode succNode3 = c5.findSupervisoryController(m);
		System.out.println("delete controller, the supervised controller of mobile: " + c5.findSupervisoryController(m).getNodeId());
		System.out.println("delete controller, the ip address of successor node: " + succNode3.getSuccessor().getNodeId());
		Controller controller3 = succNode3.getController();
		Entry entry3 = controller3.getSupervisedTable().getTable().get(m);
		System.out.println("User:   " + entry3.getUser() + "   Current:   " + entry3.getCurrent() + "   Previous:   " + entry3.getPrevious() + "   Time:   " + entry3.getTime());
	}
}

