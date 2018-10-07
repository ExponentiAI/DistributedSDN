package net.floodlightcontroller.assigntoAP;

import java.util.Vector;

public class APPcaketIn {
	
 	protected String PacketInType;
	protected Vector<Device> vd;
	protected Vector<AccessPoint> ap;
	
	public APPcaketIn(){
		PacketInType = null;
		vd = new Vector<Device>();
		ap = new Vector<AccessPoint>();
	}
	
	public String getPacketInType() {
		return PacketInType;
	}
	public void setPacketInType(String packetInType) {
		PacketInType = packetInType;
	}
	public Vector<Device> getVd() {
		return vd;
	}
	public void setVd(Vector<Device> vd) {
		this.vd = vd;
	}
	public Vector<AccessPoint> getAp() {
		return ap;
	}
	public void setAp(Vector<AccessPoint> ap) {
		this.ap = ap;
	}
	
}
