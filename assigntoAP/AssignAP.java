package net.floodlightcontroller.assigntoAP;

import java.util.ArrayList;
import java.util.Arrays;
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
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;

public class AssignAP {
	protected static AssignAP aa;
	protected APPcaketIn appi;
	protected int[] X;
	
	public static AssignAP getInstance(){
		if(aa == null){
			aa = new AssignAP();
		}
		return aa;
	}
	
	public void handlePacketIn(IPacket iPacket, AssigntoAP aap){
		String[] subMess;
		appi = new APPcaketIn();
		IPv4 ip = (IPv4)iPacket;
		Data data = (Data) ip.getPayload();
		String mess[] =  new String(data.getData()).split("<>");
		if(mess[0] == "AP_PKT_IN"){
			subMess =  mess[1].split("<><>");
			String subMess1 = subMess[0];
			String subMess2 = subMess[1];
			String[] devices = subMess1.split(" ");
			String[] aps = subMess2.split(" ");
			
			for(int i = 0; i < devices.length; i++){
				appi.getVd().add(devices[i]);
			}
			for(int i = 0; i < aps.length; i++){
				appi.getAp().add(aps[i]);
			}
			
		}
		X = Heuristic.assign(appi.getVd(), appi.getAp());
		
	}
	
	public void doSendPacketOut(AssigntoAP aap, int[] x){
		ILinkDiscoveryService linkService = aap.getLinkService();
		Map<Link, LinkInfo> links = linkService.getLinks();
		for(Link l : links.keySet()){
			IOFSwitch sw = aap.getSwitchService().getSwitch(l.getSrc());
			OFPort port = l.getSrcPort();
			sendPacketOut(sw, port, x);
		}
	}
	
	public void  sendPacketOut(IOFSwitch sw, OFPort port, int[] x){
		byte[] destinationMACAddress={0x00,0x00,0x00,0x00,0x00,0x00};
		byte[] sourceMACAddress={0x00,0x00,0x00,0x00,0x00,0x00};
		Builder pob = sw.getOFFactory().buildPacketOut();
		List<OFAction> actions  = new ArrayList<OFAction>();
		actions.add(sw.getOFFactory().actions().output(port, Integer.MAX_VALUE));
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
		//playload
		StringBuilder sb = new StringBuilder("AP_ASSIGN_RESULT");
		sb.append("<>").append(x);
		String mess = new String(sb);
		Data data = new Data();
		data.setData(mess.getBytes());
		ip.setPayload(data);   
		eth.setPayload(ip);
		pob.setData(eth.serialize());
		sw.write(pob.build());
	}
}
