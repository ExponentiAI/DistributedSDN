package net.floodlightcontroller.flow;

import org.projectfloodlight.openflow.types.DatapathId;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;

public class NetworkFlowThread extends Thread{
	protected NetworkFlow nc;
	
	public NetworkFlowThread(NetworkFlow nc){
		this.nc = nc;
	}
	
	public void run(){
		while(true){
			try {
				sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("------Thread Start-------");
			IOFSwitchService switchService = nc.getSwitchService();
			for(DatapathId d : switchService.getAllSwitchDpids()){
				IOFSwitch sw = switchService.getSwitch(d);
				if(sw == null){
					System.out.println("switch is null...");
					continue;
				}
				nc.getSendEntry().doSendEntry(sw);
				
			}
			PacketProcess pp = PacketProcess.getInstance();
			pp.showEntry();
			pp.clearEntryInfo();
		}
	}
}
