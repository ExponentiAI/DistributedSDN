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
	 * 存放获取得到的MAC地址与对应端口号
	 */
	public static List<Property> lProperties = new ArrayList<Property>();
	/**
	 * 存放获取得到的MAC地址与IP地址
	 */
	public static HashMap<String, String> map = new HashMap<String, String>();

	/**
	 * 创建SNMP管理对象
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
				// 设置安全模式
				USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
				SecurityModels.getInstance().addSecurityModel(usm);
			}
			// 开始监听消息
			transport.listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取交换机端口号与MAC地址
	 * @param pdu 报文对象
	 * @param address 交换机IP地址与端口号
	 * @param port 交换机接外网端口号
	 * @return 对象集合
	 * @throws Exception
	 */
	public List<Property> getMacAndPortOnSwitch(PDU pdu, String address, String port) throws Exception {
		Address targetAddress = GenericAddress.parse(address); // 生成目标地址对象
		Target target = null;
		if (version == SnmpConstants.version3) {
			// 添加用户
			snmp.getUSM().addUser(new OctetString("MD5DES"), new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
					new OctetString("MD5DESUserAuthPassword"), PrivDES.ID, new OctetString("MD5DESUserPrivPassword")));
			target = new UserTarget();
			// 设置安全级别
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
		// 目标对象相关设置
		target.setAddress(targetAddress);
		target.setRetries(5);
		target.setTimeout(1000);

		// 发送报文 并且接受响应
		ResponseEvent response = snmp.send(pdu, target);
		
		// 处理响应
		/*System.out.println("Synchronize(同步) message(消息) from(来自) " + response.getPeerAddress() + "\r\n"
				+ "request(发送的请求):" + response.getRequest() + "\r\n" + "response(返回的响应):" + response.getResponse());*/
		PDU pduresult = response.getResponse();// 获取响应报文数据
		Vector<? extends VariableBinding> vector = pduresult.getVariableBindings();
		VariableBinding strPort = vector.get(0);// 获取端口号
		VariableBinding strMac = vector.get(1);// 获取MAC

		// 判断如果返回值是物理地址MAC,则进行递归获取数据
		if (strMac.getVariable().toString().contains(":")) {
			//配置多个外网端口时使用
			String[] array = port.split(",");
			List<String> list = Arrays.asList(array);
			if(!list.contains(strPort.getVariable().toString())){
				Property property = new Property();
				property.setStrPort(strPort.getVariable().toString());
				property.setStrMac(strMac.getVariable().toString());
				lProperties.add(property); // 添加属性对象数据到list集合
				//System.out.println("端口号：" + strPort.getVariable() + "; MAC地址：" + strMac.getVariable());
			}
			
			pdu = new PDU();
			OID oPort = new OID(strPort.getOid().toString()); // 获取端口
			OID oMac = new OID(strMac.getOid().toString()); // 获取端口对应的MAC
			// 设置要获取的对象ID，这个OID代表远程计算机的名称
			pdu.add(new VariableBinding(oPort));
			pdu.add(new VariableBinding(oMac));
			// 设置报文类型
			pdu.setType(PDU.GETNEXT);
			getMacAndPortOnSwitch(pdu, address, port);
		}
		return lProperties;
	}
	
	/**
	 * 获取MAC对应的IP地址信息
	 * @param ipoid 查询IP地址OID
	 * @param pdu 报文对象
	 * @param address 交换机IP地址
	 * @return 集合
	 * @throws Exception
	 */
	public HashMap<String, String> getIpOnSwitch(String ipoid, PDU pdu, String address) throws Exception {
		Address targetAddress = GenericAddress.parse(address); // 生成目标地址对象
		Target target = null;
		if (version == SnmpConstants.version3) {
			// 添加用户
			snmp.getUSM().addUser(new OctetString("MD5DES"), new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
					new OctetString("MD5DESUserAuthPassword"), PrivDES.ID, new OctetString("MD5DESUserPrivPassword")));
			target = new UserTarget();
			// 设置安全级别
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
		// 目标对象相关设置
		target.setAddress(targetAddress);
		target.setRetries(5);
		target.setTimeout(1000);

		// 发送报文 并且接受响应
		ResponseEvent response = snmp.send(pdu, target);
		PDU pduresult = response.getResponse();// 获取响应报文数据
		Vector<? extends VariableBinding> vector = pduresult.getVariableBindings();
		VariableBinding strIP = vector.get(0);// 获取IP

		// 判断如果返回值是IP地址,则进行递归获取数据
		if (strIP.getVariable().toString().contains(":")) {
			String ip = strIP.getOid().toString().substring(ipoid.length()+1, strIP.getOid().toString().length());
			map.put(strIP.getVariable().toString(), ip);
			
			pdu = new PDU();
			OID oIP = new OID(strIP.getOid().toString()); // 获取端口对应的MAC
			// 设置要获取的对象ID，这个OID代表远程计算机的名称
			pdu.add(new VariableBinding(oIP));
			// 设置报文类型
			pdu.setType(PDU.GETNEXT);
			getIpOnSwitch(ipoid, pdu, address);
		}
		return map;
	}
	
	/**
	 * 测试Main
	 * @param args
	 */
	public static void main(String[] args) {
		Snmp4jManager manager = new Snmp4jManager(SnmpConstants.version2c);
		PDU pdu = new PDU();	// 构造报文
		/**
		 * .1.3.6.1.2.1.17.4.3.1.1	//获取连接交换机设备MAC地址(根据MAC大小排序,从小到大)
		 * 1.3.6.1.2.1.3.1.1.2.1.1	//获取IP地址OID
		 * .1.3.6.1.2.1.17.4.3.1.2	//当前MAC地址端口
		 */
		OID oPort = new OID(".1.3.6.1.2.1.17.4.3.1.2");	//获取端口
		OID oMac = new OID(".1.3.6.1.2.1.17.4.3.1.1");	//获取端口对应的MAC
		// 设置要获取的对象ID，这个OID代表远程计算机的名称
		pdu.add(new VariableBinding(oPort));
		pdu.add(new VariableBinding(oMac));
		pdu.setType(PDU.GETNEXT);	// 设置报文类型
		
		PDU pdu2 = new PDU();	// 构造报文
		OID oIp = new OID("1.3.6.1.2.1.3.1.1.2.1.1.");	//获取IP
		pdu2.add(new VariableBinding(oIp));
		pdu2.setType(PDU.GETNEXT);	// 设置报文类型
		
		try {
			manager.getMacAndPortOnSwitch(pdu, "udp:192.168.33.166/161", "45");// 192.168.33.166 思科交换机
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
