package net.floodlightcontroller.networkmeter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.projectfloodlight.openflow.protocol.OFActionType;
import org.projectfloodlight.openflow.protocol.OFEchoReply;
import org.projectfloodlight.openflow.protocol.OFFlowStatsEntry;
import org.projectfloodlight.openflow.protocol.OFFlowStatsReply;
import org.projectfloodlight.openflow.protocol.OFPortDesc;
import org.projectfloodlight.openflow.protocol.OFPortFeatures;
import org.projectfloodlight.openflow.protocol.OFPortStatsEntry;
import org.projectfloodlight.openflow.protocol.OFPortStatsReply;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.instruction.OFInstruction;
import org.projectfloodlight.openflow.protocol.instruction.OFInstructionApplyActions;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.IOFSwitchBackend;
import net.floodlightcontroller.core.internal.OFSwitch;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.Link;
import net.floodlightcontroller.linkdiscovery.internal.LinkInfo;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;

public class NetworkStore {
	protected static NetworkStore ns;
	//现在----历史
	protected  List<LinkDataInfo> currentLinkStatus;
	protected List<LinkDataInfo> historyLinkStatus;
	protected List<LinkTimeInfo> linkTimeStatus;
	public NetworkStore(){
		currentLinkStatus = new ArrayList<LinkDataInfo>();
		historyLinkStatus = new ArrayList<LinkDataInfo>();
		linkTimeStatus = new ArrayList<LinkTimeInfo>();
	}
	/**
	 * 单例模式
	 * @return
	 */
	public static synchronized NetworkStore getInstance(){
		if(ns==null){
			ns = new NetworkStore();
		}
		return ns;
		
	}
	public void handlePacketIn(IPacket iPacket, ILinkDiscoveryService linkService){
		IPv4 ip = (IPv4)iPacket;
		Data data = (Data)ip.getPayload();
		String mess[] = new String(data.getData()).split("<>");
		if(mess.length !=5){
			Log.error("length is not 5!");
			return;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		Date currentTime = new Date();
		long allTime = 0;
		try {
			Date sendTime = df.parse(mess[0]);
			allTime = currentTime.getTime()-sendTime.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<Link, LinkInfo> links = linkService.getLinks();
		for(Link l:links.keySet()){
			if(l.getSrc().equals(DatapathId.of(mess[1]))&&
					l.getSrcPort().getPortNumber()== Integer.parseInt(mess[2])&&
					l.getDst().equals(DatapathId.of(mess[3]))&&
					l.getDstPort().getPortNumber()==Integer.parseInt(mess[4])){
				LinkTimeInfo ltf = new LinkTimeInfo();
				ltf.setL(l);
				ltf.setAllTime(allTime);
				linkTimeStatus.add(ltf);
				break;
			}
			
		}
	}
	
	public void handleEchoReply(OFEchoReply reply){
		if(reply.getData().length<=0)
			return;
		String[]  data = new String(reply.getData()).split("<>");
		if(data.length!=2){
				Log.error("length is not 2!");
				return;
			}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		Date currentTime = new Date();
		long Time = 0;
		try {
			Date sendTime = df.parse(data[0]);
			Time = currentTime.getTime()-sendTime.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//更新记录
		for( LinkTimeInfo lti:linkTimeStatus){
			if(lti.getL().getSrc().equals(DatapathId.of(data[1])))
				lti.setCtossw(Time/2);
			if(lti.getL().getDst().equals(DatapathId.of(data[1])))
				lti.setCtodsw(Time/2);
			if(lti.getCtodsw()!=-1&&lti.getCtossw()!=-1){
				long delay = lti.getAllTime()-lti.getCtodsw()-lti.getCtossw();
				lti.setDelay(delay>=0?delay:0);
				Log.info("时延："+lti.getDelay());
			}
				
		}
		
	}
	public void handlePortStatsReply(OFPortStatsReply reply, IOFSwitchBackend sw){
		List<OFPortStatsEntry> entries = reply.getEntries();
		for(OFPortStatsEntry e:entries){
//			System.out.println("----"+e.getRxDropped());
//			e.getRxErrors();
//			e.getTxDropped();
//			e.getTxErrors();
		}
	}
	
	
	/**
	 * 处理FlowStataReply消息
	 * @param reply
	 * @param sw
	 */
	public void handleFlowStatsReply(OFFlowStatsReply reply , IOFSwitchBackend sw){
		 OFSwitch fromSw, toSw=null;
		 OFPort inPort, outPort=null;
		 long byteCount, maxBand, currentBand=0;
		 fromSw = toSw =(OFSwitch)sw;
		 List<OFFlowStatsEntry> entries = reply.getEntries();
		for(OFFlowStatsEntry e:entries){
			byteCount = e.getByteCount().getValue();
			inPort = e.getMatch().get(MatchField.IN_PORT);
			if(inPort == null){//to controller
				inPort = OFPort.ALL;
			}
			//得到outPort
			List<OFInstruction> instruction = e.getInstructions();
			for(OFInstruction i : instruction){
				if(i instanceof OFInstructionApplyActions){
					List<OFAction> action = ((OFInstructionApplyActions) i).getActions();
					for(OFAction a: action){
						if(a.getType() == OFActionType.OUTPUT){
							outPort = ((OFActionOutput)a).getPort();
							break;
						}
					}
				}else
					continue;
			}
			//默认的流表项不需要存储
			if(inPort==OFPort.ALL || outPort.getPortNumber()<1){
				continue;
			}
			//构造链路信息对象
			maxBand=calculateMaxBand(fromSw,toSw,inPort,outPort);
			LinkDataInfo ldi = new LinkDataInfo();
			ldi.setFromSw(fromSw);
			ldi.setToSw(toSw);
			ldi.setInPort(inPort);
			ldi.setOutPort(outPort);
			ldi.setMaxBand(maxBand);
			ldi.setByteCount(byteCount);
			//存储
			storeLinkStatus(ldi);
		}
		
	}
	//存储
	public void storeLinkStatus(LinkDataInfo ldi){
		if(currentLinkStatus.size()==0)//first time
			currentLinkStatus.add(ldi);
		else{
			for(LinkDataInfo l : currentLinkStatus){
				if(l.getFromSw().getId()==ldi.getFromSw().getId()&&
						l.getToSw().getId()==ldi.getToSw().getId()&&
						l.getInPort().getPortNumber()==ldi.getInPort().getPortNumber()&&
						l.getOutPort().getPortNumber()==ldi.getOutPort().getPortNumber()){
					l.setByteCount(l.getByteCount()+ldi.getByteCount());
				}
			}
			//new 
			currentLinkStatus.add(ldi);
		}
	}
	public void nextMeterBegin(){
		historyLinkStatus.clear();
		for(LinkDataInfo l:currentLinkStatus){
			historyLinkStatus.add(l);
		}
		currentLinkStatus.clear();
		linkTimeStatus.clear();
	}
	
	//计算链路的最大带宽
	public long calculateMaxBand(OFSwitch fromSw,OFSwitch toSw,OFPort inPort,OFPort outPort){
		long fromBand=0,toBand=0;
		//inport
		OFPortDesc inPortDesc = fromSw.getPort(inPort);
		Set<OFPortFeatures> infeatures = inPortDesc.getAdvertised();
		//?
		for(OFPortFeatures f: infeatures){
			fromBand = f.getPortSpeed().getSpeedBps();//bps
			if(fromBand>0)
				break;
		}
		//outPort
		OFPortDesc outPortDesc = toSw.getPort(outPort);
		Set<OFPortFeatures> outfeatures = outPortDesc.getAdvertised();
		//?
		for(OFPortFeatures f: outfeatures){
			toBand = f.getPortSpeed().getSpeedBps();//bps
			if(toBand>0)
				break;
		}
		return (fromBand>=toBand?toBand:fromBand);
	}
	//计算当前带宽，输出
	public void calCurrentBand() {
		for(LinkDataInfo h:historyLinkStatus)
			for(LinkDataInfo c:currentLinkStatus){
				if(h.getFromSw().getId()==c.getFromSw().getId()&&
						h.getToSw().getId()==c.getToSw().getId()&&
						h.getInPort().getPortNumber()==c.getInPort().getPortNumber()&&
						h.getOutPort().getPortNumber()==c.getOutPort().getPortNumber()){
					long speed = (c.getByteCount()-h.getByteCount())/1;
					float band = (float) (speed*1.0/c.maxBand);
//					System.out.println("currentspeed:"+speed+"Bps"+"currentBand:"+band*100+"%");
				}
			}
		
	}
	
	
	
}
class LinkDataInfo{
	protected OFSwitch fromSw;
	protected OFSwitch toSw;
	protected OFPort inPort;
	protected OFPort outPort;
	protected long byteCount;
	protected long maxBand;
	protected long currentBand;
	public OFSwitch getFromSw() {
		return fromSw;
	}
	public void setFromSw(OFSwitch fromSw) {
		this.fromSw = fromSw;
	}
	public OFSwitch getToSw() {
		return toSw;
	}
	public void setToSw(OFSwitch toSw) {
		this.toSw = toSw;
	}
	public OFPort getInPort() {
		return inPort;
	}
	public void setInPort(OFPort inPort) {
		this.inPort = inPort;
	}
	public OFPort getOutPort() {
		return outPort;
	}
	public void setOutPort(OFPort outPort) {
		this.outPort = outPort;
	}
	public long getByteCount() {
		return byteCount;
	}
	public void setByteCount(long byteCount) {
		this.byteCount = byteCount;
	}
	public long getMaxBand() {
		return maxBand;
	}
	public void setMaxBand(long maxBand) {
		this.maxBand = maxBand;
	}
	public long getCurrentBand() {
		return currentBand;
	}
	public void setCurrentBand(long currentBand) {
		this.currentBand = currentBand;
	}
	
	
}
class LinkTimeInfo{
	protected Link l;
	protected long allTime = -1;
	protected long ctossw = -1;
	protected long ctodsw = -1;
	protected long delay = -1;
	public Link getL() {
		return l;
	}
	public void setL(Link l) {
		this.l = l;
	}
	public long getAllTime() {
		return allTime;
	}
	public void setAllTime(long allTime) {
		this.allTime = allTime;
	}
	public long getCtossw() {
		return ctossw;
	}
	public void setCtossw(long time) {
		this.ctossw = time;
	}
	public long getCtodsw() {
		return ctodsw;
	}
	public void setCtodsw(long ctodsw) {
		this.ctodsw = ctodsw;
	}
	public long getDelay() {
		return delay;
	}
	public void setDelay(long m) {
		this.delay = m;
	}
	
	
}
