package net.floodlightcontroller.networkcalculus;

import org.projectfloodlight.openflow.types.DatapathId;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.networkmeterlog.Log;

public class NetworkCalculusThread extends Thread {
	
	protected NetworkCalculus nm;
	public static byte[] switchID = {0x00,0x00,0x00,0x00,0x00,0x00};
	
	public NetworkCalculusThread(NetworkCalculus nm){
		this.nm=nm;
	}
	
	public void run(){
		while(true){
			System.out.println("start thread...");
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			NetworkStore ns =NetworkStore.getInstance(); 
			//指定边界OpenFlow交换机ID，并统计如入端口的流量
			IOFSwitchService switchService = nm.getSwitchService();
			for(DatapathId d : switchService.getAllSwitchDpids()){
				IOFSwitch sw = switchService.getSwitch(d);
				if(sw == null){
					Log.warn("sw is null");
					continue;
				}
				if(sw.getId().getBytes().toString() == switchID.toString()){
					nm.sendPortRequest.doSendPortRequest(sw);
				}
			}
		}	
	}
}
