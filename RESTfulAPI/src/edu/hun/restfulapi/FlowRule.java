package edu.hun.restfulapi;

public class FlowRule {
	
	private String ethSrc;
	private String ethDst;
	private String ipv4Src;
	private String ipv4Dst;

	public String getEthSrc() {
		return ethSrc;
	}
	public void setEthSrc(String ethSrc) {
		this.ethSrc = ethSrc;
	}
	public String getEthDst() {
		return ethDst;
	}
	public void setEthDst(String ethDst) {
		this.ethDst = ethDst;
	}
	public String getIpv4Src() {
		return ipv4Src;
	}
	public void setIpv4Src(String ipv4Src) {
		this.ipv4Src = ipv4Src;
	}
	public String getIpv4Dst() {
		return ipv4Dst;
	}
	public void setIpv4Dst(String ipv4Dst) {
		this.ipv4Dst = ipv4Dst;
	}
}
