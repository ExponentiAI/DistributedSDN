package net.floodlightcontroller.networkcalculus;

import java.util.Collection;

import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFPortStatsRequest.Builder;

import net.floodlightcontroller.core.IOFSwitch;

public class PacketLoss {
	public PacketLoss(){
	
	}
	/**
	 * 
	 * @param sw
	 * getEnablePorts() 获取所有能用的端口
	 * 
	 * 
	 * getOFFactory().buildPortStatsRequest()
	 * setPortNo()��ָ���������Ķ˿ڷ������������ݰ�  
	 */
	public void doPacketLoss(IOFSwitch sw){
		Collection<OFPortDesc> ports = sw.getEnabledPorts();
		for(OFPortDesc pd : ports){
			Builder psb = sw.getOFFactory().buildPortStatsRequest();
			psb.setPortNo(pd.getPortNo());//
			sw.write(psb.build());
		}
	}

}
