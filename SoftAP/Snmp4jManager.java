package net.floodlightcontroller.eastwestcommunication;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.introspector.Property;

public class Snmp4jManager {
	public Snmp snmp = null;
	public int version;
	/**
	 * ��Ż�ȡ�õ���MAC��ַ���Ӧ�˿ں�
	 */
	public static List<Property> lProperties = new ArrayList<Property>();
	/**
	 * ��Ż�ȡ�õ���MAC��ַ��IP��ַ
	 */
	public static HashMap<String, String> map = new HashMap<String, String>();

	/**
	 * ����SNMP�������
	 * 
	 * @param version
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Snmp4jManager(int version) {
		try {
			this.version = version;
			TransportMapping transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);
			if (version == SnmpConstants.version3) {
				// ���ð�ȫģʽ
				USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
				SecurityModels.getInstance().addSecurityModel(usm);
			}
			// ��ʼ������Ϣ
			transport.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�������˿ں���MAC��ַ
	 * @param pdu ���Ķ���
	 * @param address ������IP��ַ��˿ں�
	 * @param port �������������˿ں�
	 * @return ���󼯺�
	 * @throws Exception
	 */
	public List<Property> getMacAndPortOnSwitch(PDU pdu, String address, String port) throws Exception {
		Address targetAddress = GenericAddress.parse(address); // ����Ŀ���ַ����
		Target target = null;
		if (version == SnmpConstants.version3) {
			// ����û�
			snmp.getUSM().addUser(new OctetString("MD5DES"), new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
					new OctetString("MD5DESUserAuthPassword"), PrivDES.ID, new OctetString("MD5DESUserPrivPassword")));
			target = new UserTarget();
			// ���ð�ȫ����
			((UserTarget) target).setSecurityLevel(SecurityLevel.AUTH_PRIV);
			((UserTarget) target).setSecurityName(new OctetString("MD5DES"));
			target.setVersion(SnmpConstants.version3);
		} else {
			target = new CommunityTarget();
			if (version == SnmpConstants.version1) {
				target.setVersion(SnmpConstants.version1);
				((CommunityTarget) target).setCommunity(new OctetString("public"));
			} else {
				target.setVersion(SnmpConstants.version2c);
				((CommunityTarget) target).setCommunity(new OctetString("public"));
			}

		}
		// Ŀ������������
		target.setAddress(targetAddress);
		target.setRetries(5);
		target.setTimeout(1000);

		// ���ͱ��� ���ҽ�����Ӧ
		ResponseEvent response = snmp.send(pdu, target);
		
		// ������Ӧ
		/*System.out.println("Synchronize(ͬ��) message(��Ϣ) from(����) " + response.getPeerAddress() + "\r\n"
				+ "request(���͵�����):" + response.getRequest() + "\r\n" + "response(���ص���Ӧ):" + response.getResponse());*/
		PDU pduresult = response.getResponse();// ��ȡ��Ӧ��������
		Vector<? extends VariableBinding> vector = pduresult.getVariableBindings();
		VariableBinding strPort = vector.get(0);// ��ȡ�˿ں�
		VariableBinding strMac = vector.get(1);// ��ȡMAC

		// �ж��������ֵ�������ַMAC,����еݹ��ȡ����
		if (strMac.getVariable().toString().contains(":")) {
			//���ö�������˿�ʱʹ��
			String[] array = port.split(",");
			List<String> list = Arrays.asList(array);
			if(!list.contains(strPort.getVariable().toString())){
				Property property = new Property();
				property.setStrPort(strPort.getVariable().toString());
				property.setStrMac(strMac.getVariable().toString());
				lProperties.add(property); // ������Զ������ݵ�list����
				//System.out.println("�˿ںţ�" + strPort.getVariable() + "; MAC��ַ��" + strMac.getVariable());
			}
			
			pdu = new PDU();
			OID oPort = new OID(strPort.getOid().toString()); // ��ȡ�˿�
			OID oMac = new OID(strMac.getOid().toString()); // ��ȡ�˿ڶ�Ӧ��MAC
			// ����Ҫ��ȡ�Ķ���ID�����OID����Զ�̼����������
			pdu.add(new VariableBinding(oPort));
			pdu.add(new VariableBinding(oMac));
			// ���ñ�������
			pdu.setType(PDU.GETNEXT);
			getMacAndPortOnSwitch(pdu, address, port);
		}
		return lProperties;
	}
	
	/**
	 * ��ȡMAC��Ӧ��IP��ַ��Ϣ
	 * @param ipoid ��ѯIP��ַOID
	 * @param pdu ���Ķ���
	 * @param address ������IP��ַ
	 * @return ����
	 * @throws Exception
	 */
	public HashMap<String, String> getIpOnSwitch(String ipoid, PDU pdu, String address) throws Exception {
		Address targetAddress = GenericAddress.parse(address); // ����Ŀ���ַ����
		Target target = null;
		if (version == SnmpConstants.version3) {
			// ����û�
			snmp.getUSM().addUser(new OctetString("MD5DES"), new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
					new OctetString("MD5DESUserAuthPassword"), PrivDES.ID, new OctetString("MD5DESUserPrivPassword")));
			target = new UserTarget();
			// ���ð�ȫ����
			((UserTarget) target).setSecurityLevel(SecurityLevel.AUTH_PRIV);
			((UserTarget) target).setSecurityName(new OctetString("MD5DES"));
			target.setVersion(SnmpConstants.version3);
		} else {
			target = new CommunityTarget();
			if (version == SnmpConstants.version1) {
				target.setVersion(SnmpConstants.version1);
				((CommunityTarget) target).setCommunity(new OctetString("public"));
			} else {
				target.setVersion(SnmpConstants.version2c);
				((CommunityTarget) target).setCommunity(new OctetString("public"));
			}

		}
		// Ŀ������������
		target.setAddress(targetAddress);
		target.setRetries(5);
		target.setTimeout(1000);

		// ���ͱ��� ���ҽ�����Ӧ
		ResponseEvent response = snmp.send(pdu, target);
		PDU pduresult = response.getResponse();// ��ȡ��Ӧ��������
		Vector<? extends VariableBinding> vector = pduresult.getVariableBindings();
		VariableBinding strIP = vector.get(0);// ��ȡIP

		// �ж��������ֵ��IP��ַ,����еݹ��ȡ����
		if (strIP.getVariable().toString().contains(":")) {
			String ip = strIP.getOid().toString().substring(ipoid.length()+1, strIP.getOid().toString().length());
			map.put(strIP.getVariable().toString(), ip);
			
			pdu = new PDU();
			OID oIP = new OID(strIP.getOid().toString()); // ��ȡ�˿ڶ�Ӧ��MAC
			// ����Ҫ��ȡ�Ķ���ID�����OID����Զ�̼����������
			pdu.add(new VariableBinding(oIP));
			// ���ñ�������
			pdu.setType(PDU.GETNEXT);
			getIpOnSwitch(ipoid, pdu, address);
		}
		return map;
	}
	
	/**
	 * ����Main
	 * @param args
	 */
	public static void main(String[] args) {
		Snmp4jManager manager = new Snmp4jManager(SnmpConstants.version2c);
		PDU pdu = new PDU();	// ���챨��
		/**
		 * .1.3.6.1.2.1.17.4.3.1.1	//��ȡ���ӽ������豸MAC��ַ(����MAC��С����,��С����)
		 * 1.3.6.1.2.1.3.1.1.2.1.1	//��ȡIP��ַOID
		 * .1.3.6.1.2.1.17.4.3.1.2	//��ǰMAC��ַ�˿�
		 */
		OID oPort = new OID(".1.3.6.1.2.1.17.4.3.1.2");	//��ȡ�˿�
		OID oMac = new OID(".1.3.6.1.2.1.17.4.3.1.1");	//��ȡ�˿ڶ�Ӧ��MAC
		// ����Ҫ��ȡ�Ķ���ID�����OID����Զ�̼����������
		pdu.add(new VariableBinding(oPort));
		pdu.add(new VariableBinding(oMac));
		pdu.setType(PDU.GETNEXT);	// ���ñ�������
		
		PDU pdu2 = new PDU();	// ���챨��
		OID oIp = new OID("1.3.6.1.2.1.3.1.1.2.1.1.");	//��ȡIP
		pdu2.add(new VariableBinding(oIp));
		pdu2.setType(PDU.GETNEXT);	// ���ñ�������
		
		try {
			manager.getMacAndPortOnSwitch(pdu, "udp:192.168.33.166/161", "45");// 192.168.33.166 ˼�ƽ�����
			manager.getIpOnSwitch(oIp.toString(), pdu2, "udp:192.168.33.166/161");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (Property pro : lProperties) {
			System.out.println("Port:"+pro.getStrPort()+";Mac:"+pro.getStrMac()+";IP:"+map.get(pro.getStrMac()));
		}
		
		System.out.println(map.size());
	}
}
