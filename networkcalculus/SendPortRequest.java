package net.floodlightcontroller.networkcalculus;

import java.util.Collection;

import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFPortStatsRequest.Builder;

import net.floodlightcontroller.core.IOFSwitch;

public class SendPortRequest {
	
	public static int port = 2;
	public SendPortRequest(){
		
	}
	/**
	 * 
	 * @param sw
	 * getEnablePorts() get所有端口
	 * 
	 */
	public void doSendPortRequest(IOFSwitch sw){
		Collection<OFPortDesc> ports = sw.getEnabledPorts();
		for(OFPortDesc pd : ports){
			if(pd.getPortNo().getPortNumber()== port){
				Builder psb = sw.getOFFactory().buildPortStatsRequest();
				psb.setPortNo(pd.getPortNo());
				sw.write(psb.build());
			}
		}
	}
}
