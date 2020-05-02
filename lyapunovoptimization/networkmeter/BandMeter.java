package net.floodlightcontroller.networkmeter;

import org.projectfloodlight.openflow.protocol.OFFlowStatsRequest.Builder;
import org.projectfloodlight.openflow.types.OFGroup;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TableId;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.util.AppCookie;

public class BandMeter {
	
	
	public BandMeter(){
		
	}
	/**
	 * OFFlowStatsRequest
	 * OFAggregateStatsRequest
	 * @param sw
	 */
	public void doBand(IOFSwitch sw){
		//statsrequest
		Builder request = (Builder)sw.getOFFactory().buildFlowStatsRequest();
		request.setTableId(TableId.ALL);
		request.setOutPort(OFPort.ANY);
		request.setOutGroup(OFGroup.ANY);
		request.setCookie(AppCookie.makeCookie(2, 0));
		sw.write(request.build());
	}
	

}
