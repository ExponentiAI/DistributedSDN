package net.floodlightcontroller.flow;

import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;

import net.floodlightcontroller.core.internal.OFSwitch;

public class EntryInfo {
	protected OFPort inPort = null;
	protected OFSwitch toSw = null;
	
	protected MacAddress srcMac = null; 
	protected MacAddress dstMac = null;
	protected IPv4Address srcIp = null;
	protected IPv4Address dstIp = null;
	protected TransportPort tcpSrc = null;
	protected TransportPort tcpDst = null;
	protected EthType ethtype = null;
	
	
	public MacAddress getSrcMac() {
		return srcMac;
	}
	public void setSrcMac(MacAddress srcMac) {
		this.srcMac = srcMac;
	}
	public MacAddress getDstMac() {
		return dstMac;
	}
	public void setDstMac(MacAddress dstMac) {
		this.dstMac = dstMac;
	}
	public IPv4Address getSrcIp() {
		return srcIp;
	}
	public void setSrcIp(IPv4Address srcIp) {
		this.srcIp = srcIp;
	}
	public IPv4Address getDstIp() {
		return dstIp;
	}
	public void setDstIp(IPv4Address dstIp) {
		this.dstIp = dstIp;
	}
	public TransportPort getTcpSrc() {
		return tcpSrc;
	}
	public void setTcpSrc(TransportPort tcpSrc) {
		this.tcpSrc = tcpSrc;
	}
	public TransportPort getTcpDst() {
		return tcpDst;
	}
	public void setTcpDst(TransportPort tcpDst) {
		this.tcpDst = tcpDst;
	}
	public OFPort getInPort() {
		return inPort;
	}
	public void setInPort(OFPort inPort) {
		this.inPort = inPort;
	}
	public OFSwitch getToSw() {
		return toSw;
	}
	public void setToSw(OFSwitch toSw) {
		this.toSw = toSw;
	}
	public EthType getEthtype() {
		return ethtype;
	}
	public void setEthtype(EthType ethtype) {
		this.ethtype = ethtype;
	}
	
}
