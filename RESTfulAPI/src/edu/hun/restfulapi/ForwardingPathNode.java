package edu.hun.restfulapi;

public class ForwardingPathNode {
	private String in_Port;
	private String out_Port;
	private String switchID;
	
	public String getIn_Port() {
		return in_Port;
	}
	public void setIn_Port(String in_Port) {
		this.in_Port = in_Port;
	}
	public String getOut_Port() {
		return out_Port;
	}
	public void setOut_Port(String out_Port) {
		this.out_Port = out_Port;
	}
	public String getSwitchID() {
		return switchID;
	}
	public void setSwitchID(String switchID) {
		this.switchID = switchID;
	}
}
