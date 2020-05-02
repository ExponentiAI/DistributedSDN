package net.floodlightcontroller.eastwestcommunication;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.qos.logback.classic.Logger;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.Ethernet;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
public class getSwitchService {
	private String strPort;
	private String strMac;
	private String strIp;

	public String getStrPort() {
		return strPort;
	}

	public void setStrPort(String strPort) {
		this.strPort = strPort;
	}

	public String getStrMac() {
		return strMac;
	}

	public void setStrMac(String strMac) {
		this.strMac = strMac;
	}

	public String getStrIp() {
		return strIp;
	}

	public void setStrIp(String strIp) {
		this.strIp = strIp;
	}
    }
	
}
