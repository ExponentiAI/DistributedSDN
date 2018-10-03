package net.floodlightcontroller.flow;

import java.util.ArrayList;
import java.util.List;


import org.projectfloodlight.openflow.protocol.OFFlowStatsEntry;
import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;


import net.floodlightcontroller.core.IOFSwitchBackend;
import net.floodlightcontroller.core.internal.OFSwitch;

public class PacketProcess {
	
	protected static PacketProcess pp;
	
	protected List<EntryInfo> entryInfos;
	protected List<EntryInfo> storeInfos;
	
	public PacketProcess(){
		entryInfos = new ArrayList<EntryInfo>();
		storeInfos = new ArrayList<EntryInfo>();
	}
	
	public static synchronized PacketProcess getInstance(){
		if(pp == null){
			pp = new PacketProcess();
		}
		return pp;
	}

	public void handleFlowStatsReply(OFFlowStatsReply reply, IOFSwitchBackend sw) {
		
		OFSwitch toSw  = null;
		MacAddress srcMac = null, dstMac = null;
		IPv4Address srcIp = null, dstIp = null;
		TransportPort tcpSrc = null, tcpDst = null;
		OFPort inPort = null;
		EthType ethtype = null;
		
		System.out.println("process packet...");
		
		toSw = (OFSwitch)sw;
		List<OFFlowStatsEntry> entries = reply.getEntries();
		System.out.println("size of entryInfos:" + "----" + entryInfos.size() + "----");
		for(OFFlowStatsEntry e : entries){
				
			inPort = e.getMatch().get(MatchField.IN_PORT);
			
			if(inPort == null){//to controller
				inPort = OFPort.ALL;
			}
			
			srcMac = e.getMatch().get(MatchField.ETH_SRC);
			dstMac = e.getMatch().get(MatchField.ETH_DST);
			srcIp = e.getMatch().get(MatchField.IPV4_SRC);
			dstIp = e.getMatch().get(MatchField.IPV4_DST);
			tcpSrc = e.getMatch().get(MatchField.TCP_SRC);
			tcpDst = e.getMatch().get(MatchField.TCP_DST);
			ethtype = e.getMatch().get(MatchField.ETH_TYPE);
			
			if(inPort==OFPort.ALL){
				continue;
			}
			EntryInfo ei = new EntryInfo();
			
			
			ei.setToSw(toSw);
			ei.setInPort(inPort);
			ei.setSrcMac(srcMac);
			ei.setDstMac(dstMac);
			ei.setSrcIp(srcIp);
			ei.setDstIp(dstIp);
			ei.setTcpSrc(tcpSrc);
			ei.setTcpDst(tcpDst);
			ei.setEthtype(ethtype);
			
			entryInfos.add(ei);
			System.out.println("count:....." + entryInfos.size());
		}		
	}
	
//	public void storeEntryInfo(EntryInfo ei){
//		if(entryInfos.size()==0)//first time
//			entryInfos.add(ei);
//		
//		entryInfos.add(ei);
//
//	}
	
	public void showEntry(){
		System.out.println("--------" + entryInfos.size());
		for(EntryInfo e : entryInfos){
			if(e != null){
				System.out.println( "SwitchId:" + e.getToSw().getId() + "  " +
									"in_port:" + e.getInPort().getPortNumber() + "  " +
									"eth_src:" + e.getSrcMac() + "  " + "eth_dst:" + e.getDstMac() + "  " +
									"ipv4_src:" + e.getSrcIp() + "  " + "ipv4_dst:" + e.getDstIp() + "  " +
									"EthType:" + e.getEthtype());
			}	
		}
	}
	
	public void clearEntryInfo(){
		storeInfos.clear();
		for(EntryInfo e : entryInfos){
			storeInfos.add(e);
		}
		entryInfos.clear();
	}
}


