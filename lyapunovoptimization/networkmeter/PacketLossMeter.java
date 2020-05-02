package net.floodlightcontroller.networkmeter;

import java.util.Collection;

import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFPortStatsRequest.Builder;

import net.floodlightcontroller.core.IOFSwitch;

public class PacketLossMeter {
	public PacketLossMeter(){
		
	}
	public void doPacketLoss(IOFSwitch sw){
		Collection<OFPortDesc> ports = sw.getEnabledPorts();
		for(OFPortDesc pd : ports){
			Builder psb = sw.getOFFactory().buildPortStatsRequest();
			psb.setPortNo(pd.getPortNo());
			sw.write(psb.build());
		}
	}

}
