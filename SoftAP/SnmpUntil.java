package net.floodlightcontroller.eastwestcommunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.snmp4j.PDU;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public class SnmpUntil {
	
	/**
	 * 获取MAC与端口号
	 * @param portOID 获取端口号OID(不同设备OID则不同)
	 * @param macOID 获取设备下的PC机MAC
	 * @param udpURL 交换机发送请求地址(IP+端口号)
	 * <li>例子："udp:192.168.33.166/161" //传参格式
	 * @param InternetPort
	 * <li>例子："45,46" //当连接多个外网端口时,则用逗号分开
	 * @return 对象集合
	 */
	public static List<Property> getPcMac(String portOID, String macOID, String udpURL, String InternetPort){
		List<Property> lProperties = new ArrayList<Property>();
		Snmp4jManager manager = new Snmp4jManager(SnmpConstants.version2c);
		PDU pdu = new PDU();	// 构造报文
		OID oPort = new OID(portOID);	//获取端口
		OID oMac = new OID(macOID);	//获取端口对应的MAC
		// 设置要获取的对象ID，这个OID代表远程计算机的名称
		pdu.add(new VariableBinding(oPort));
		pdu.add(new VariableBinding(oMac));
		pdu.setType(PDU.GETNEXT);	// 设置报文类型
		try {
			// 192.168.33.166
			lProperties = manager.getMacAndPortOnSwitch(pdu, udpURL, InternetPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lProperties;
	}
	
	/**
	 * 获取PC机IP
	 * @param ipOID 获取IP地址
	 * @param udpURL 交换机发送请求地址(IP+端口号)
	 * <li>例子："udp:192.168.33.166/161" //传参格式
	 * @return 
	 */
	public static HashMap<String, String> getPcIp(String ipOID, String udpURL){
		HashMap<String, String> map = new HashMap<String, String>();
		Snmp4jManager manager = new Snmp4jManager(SnmpConstants.version2c);
		PDU pdu = new PDU();	// 构造报文
		OID oIp = new OID(ipOID);	//获取IP
		// 设置要获取的对象ID，这个OID代表远程计算机的名称
		pdu.add(new VariableBinding(oIp));
		pdu.setType(PDU.GETNEXT);	// 设置报文类型
		try {
			// 192.168.33.166 思科交换机
			map = manager.getIpOnSwitch(ipOID, pdu, udpURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		SnmpUntil su = new SnmpUntil();
		List<Property> lp = su.getPcMac(".1.3.6.1.2.1.17.4.3.1.2", ".1.3.6.1.2.1.17.4.3.1.1", "udp:192.168.33.166/161", "45");
		HashMap<String, String> map = su.getPcIp("1.3.6.1.2.1.3.1.1.2.1.1", "udp:192.168.33.166/161");
		for (Property pro : lp) {
			System.out.println("Port:"+pro.getStrPort()+";----Mac:"+pro.getStrMac()+";----IP:"+map.get(pro.getStrMac()));
		}
	}
}
