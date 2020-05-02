package net.floodlightcontroller.networkmeter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFPacketOut.Builder;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.Link;
import net.floodlightcontroller.linkdiscovery.internal.LinkInfo;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

public class TimeDelayMeter {
	public static byte[] destinationMACAddress={0x00,0x00,0x00,0x00,0x00,0x00};
	public static byte[] sourceMACAddress={0x00,0x00,0x00,0x00,0x00,0x01};
	public TimeDelayMeter(){
		
	}
	
	public boolean isDoingTimeDelay(Ethernet eth){
		byte[] descmac = eth.getDestinationMACAddress().getBytes();
		byte[] srcmac = eth.getSourceMACAddress().getBytes();
		if(descmac.length!=6||srcmac.length!=6)
			return false;
		for(int i=0;i<6;i++){
			if(descmac[i] !=destinationMACAddress[i] ||
					srcmac[i] !=sourceMACAddress[i])
				return false;
		}
		return true;
	}
	public void doTimeDelay(NetworkMeter nm){
		ILinkDiscoveryService linkService = nm.getLinkService();
		Map<Link, LinkInfo> links = linkService.getLinks();
		for(Link l :links.keySet()){
			IOFSwitch fromSw = nm.getSwitchService().getSwitch(l.getSrc());
			IOFSwitch toSw = nm.getSwitchService().getSwitch(l.getDst());
			OFPort inPort = l.getSrcPort();
			OFPort outPort = l.getDstPort();
			sendPacketOut(fromSw,inPort,toSw,outPort);
			sendEchoRequest(fromSw);
			sendEchoRequest(toSw);
		}
	}
	public void  sendPacketOut(IOFSwitch fromSw, OFPort inPort, IOFSwitch toSw, OFPort outPort){
		Builder pob = fromSw.getOFFactory().buildPacketOut();
		List<OFAction> actions  = new ArrayList<OFAction>();
		actions.add(fromSw.getOFFactory().actions().output(inPort, Integer.MAX_VALUE));
		pob.setActions(actions);
		//Ethernet
		Ethernet eth = new Ethernet();
		eth.setDestinationMACAddress(destinationMACAddress);
		eth.setSourceMACAddress(sourceMACAddress);
		eth.setEtherType(EthType.IPv4);
		//IP
		IPv4 ip = new IPv4();
		ip.setSourceAddress(0);
		ip.setDestinationAddress(0);
		ip.setProtocol(IpProtocol.NONE);
		//timestamp
		StringBuilder sb = new StringBuilder(getCurrentTime());
		sb.append("<>").append(fromSw.getId())
			.append("<>").append(inPort.getPortNumber())
			.append("<>").append(toSw.getId())
			.append("<>").append(outPort.getPortNumber());
		String mess = new String(sb);
		Data data = new Data();
		data.setData(mess.getBytes());
		ip.setPayload(data);
		eth.setPayload(ip);
		pob.setData(eth.serialize());
		fromSw.write(pob.build());
		
	}
	public void sendEchoRequest(IOFSwitch sw){
		org.projectfloodlight.openflow.protocol.OFEchoRequest.Builder request 
		= sw.getOFFactory().buildEchoRequest();
		StringBuilder sb = new StringBuilder(getCurrentTime());
		sb.append("<>").append(sw.getId());
		String mess = new String(sb);
		Data data = new Data();
		data.setData(mess.getBytes());
		request.setData(data.serialize());
		sw.write(request.build());
	}
	public String getCurrentTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		return df.format(new Date());
	}
}
