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
	 * ��ȡMAC��˿ں�
	 * @param portOID ��ȡ�˿ں�OID(��ͬ�豸OID��ͬ)
	 * @param macOID ��ȡ�豸�µ�PC��MAC
	 * @param udpURL ���������������ַ(IP+�˿ں�)
	 * <li>���ӣ�"udp:192.168.33.166/161" //���θ�ʽ
	 * @param InternetPort
	 * <li>���ӣ�"45,46" //�����Ӷ�������˿�ʱ,���ö��ŷֿ�
	 * @return ���󼯺�
	 */
	public static List<Property> getPcMac(String portOID, String macOID, String udpURL, String InternetPort){
		List<Property> lProperties = new ArrayList<Property>();
		Snmp4jManager manager = new Snmp4jManager(SnmpConstants.version2c);
		PDU pdu = new PDU();	// ���챨��
		OID oPort = new OID(portOID);	//��ȡ�˿�
		OID oMac = new OID(macOID);	//��ȡ�˿ڶ�Ӧ��MAC
		// ����Ҫ��ȡ�Ķ���ID�����OID����Զ�̼����������
		pdu.add(new VariableBinding(oPort));
		pdu.add(new VariableBinding(oMac));
		pdu.setType(PDU.GETNEXT);	// ���ñ�������
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
	 * ��ȡPC��IP
	 * @param ipOID ��ȡIP��ַ
	 * @param udpURL ���������������ַ(IP+�˿ں�)
	 * <li>���ӣ�"udp:192.168.33.166/161" //���θ�ʽ
	 * @return 
	 */
	public static HashMap<String, String> getPcIp(String ipOID, String udpURL){
		HashMap<String, String> map = new HashMap<String, String>();
		Snmp4jManager manager = new Snmp4jManager(SnmpConstants.version2c);
		PDU pdu = new PDU();	// ���챨��
		OID oIp = new OID(ipOID);	//��ȡIP
		// ����Ҫ��ȡ�Ķ���ID�����OID����Զ�̼����������
		pdu.add(new VariableBinding(oIp));
		pdu.setType(PDU.GETNEXT);	// ���ñ�������
		try {
			// 192.168.33.166 ˼�ƽ�����
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
