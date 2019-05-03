package net.floodlightcontroller.eastwestcommunication;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.projectfloodlight.openflow.protocol.OFPacketOut.Builder;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.networkmeterlog.Log;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;

public class SendPacketOutMessage {
	
	//指定Packet-Out数据包的源MAC地址和目的MAC地址,以及目的主机的IP地址
	public static byte[] destinationMACAddress = {0x00,0x00,0x00,0x00,0x00,0x00};
	public static byte[] sourceMACAddress = {0x00,0x00,0x00,0x00,0x00,0x00};
	public static String sourceIpAddress = "10.0.0.1";
	//指定网络分区边缘的连接其他网络分区的交换机ID以及端口号
    public static byte[] switchID = {0x00,0x00,0x00,0x00,0x00,0x00};
    public static int portNum = 0;
	
	public SendPacketOutMessage(){	
	}
	
	public boolean isDoingSendPacket(Ethernet eth){
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
	
	/**
	 * 
	 * @param nm
	 * getSwitchService()方法拿到交换机信息，getSwitch()方法是获取指定链路接口的交换机
	 * getSrcPort()方法是获取指定链路的端口
	 */
	public void doSendPacketOutMessage(EastWestCommunication nm){
		IOFSwitch fromSw = null;
		OFPort fromPort = null;	
		//找到指定的交换机机器端口
		IOFSwitchService switchService = nm.getSwitchService();
		for(DatapathId d : switchService.getAllSwitchDpids()){
			IOFSwitch sw = switchService.getSwitch(d);
			if(sw ==null){
				Log.warn("sw is null");
				continue;
			}
			if(sw.getId().getBytes().toString() == switchID.toString()){
				fromSw = sw;
				for( OFPort p : sw.getEnabledPortNumbers()){
					if(p.getPortNumber() == portNum)
						fromPort = p;
				}
			}
		}
		sendPacketOut(fromSw,fromPort);
	}
	
	public void  sendPacketOut(IOFSwitch fromSw, OFPort inPort){
		
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
		ip.setSourceAddress(GetIpAddress());
		ip.setDestinationAddress(sourceIpAddress);
		ip.setProtocol(IpProtocol.NONE);
		
		//timestamp 可以在此添加解析流表的信息
		StringBuilder sb = new StringBuilder(getCurrentTime());
		sb.append("<>").append(fromSw.getId());
		
		//数据包每层进行封装操作
		String mess = new String(sb);
		Data data = new Data();
		data.setData(mess.getBytes());
		ip.setPayload(data);   
		eth.setPayload(ip);
		pob.setData(eth.serialize());
		fromSw.write(pob.build());
		
	}

	public String getCurrentTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		return df.format(new Date());
	}
	
	public String GetIpAddress(){
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip.getHostAddress();
	}
	
	public byte[] GetMAC(){
		InetAddress ia;
        byte[] mac = null;
      try {
          ia = InetAddress.getLocalHost();
          mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();  
      } catch (Exception e) {
          e.printStackTrace();
      }
      return mac;
	}
}
