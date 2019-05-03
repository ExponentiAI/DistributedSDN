package net.floodlightcontroller.networkcalculus;

import java.util.Collection;

import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFPortStatsRequest.Builder;

import net.floodlightcontroller.core.IOFSwitch;

public class SendPortRequest {
	//指定网络分区交换机的入端口port
	public static int port = 0;
	public SendPortRequest(){
		
	}
	/**
	 * 
	 * @param sw
	 * getEnablePorts() gets all enabled ports
	 * 
	 */
	public void doSendPortRequest(IOFSwitch sw){
		Collection<OFPortDesc> ports = sw.getEnabledPorts();
		for(OFPortDesc pd : ports){
			if(pd.getPortNo().getPortNumber() == port){
				Builder psb = sw.getOFFactory().buildPortStatsRequest();
				psb.setPortNo(pd.getPortNo());
				sw.write(psb.build());
			}
		
		}
	}

}
