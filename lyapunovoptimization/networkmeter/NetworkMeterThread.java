package net.floodlightcontroller.networkmeter;

import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;

public class NetworkMeterThread extends Thread {
	protected NetworkMeter nm;

	
	public NetworkMeterThread(NetworkMeter nm){
		this.nm=nm;
	}
	
	public void run(){
		while(true){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//clear
			NetworkStore ns =NetworkStore.getInstance();
			ns.calCurrentBand();
			ns.nextMeterBegin();
			// get all switch
			IOFSwitchService switchService = nm.getSwitchService();
			for(DatapathId d : switchService.getAllSwitchDpids()){
				IOFSwitch sw = switchService.getSwitch(d);
				if(sw ==null){
					Log.warn("sw is null");
					continue;
				}
				//TODO
				nm.getBandMeter().doBand(sw);
				nm.getPacketLossMeter().doPacketLoss(sw);
				
			}
			nm.getTimeDelayMeter().doTimeDelay(nm);
		}
		
	}

}
