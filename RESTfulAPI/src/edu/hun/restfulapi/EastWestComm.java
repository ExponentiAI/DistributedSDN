package edu.hun.restfulapi;

import java.util.List;
import java.util.Map;

public class EastWestComm {
	public static void sendInfo(String hostmac) throws Exception{
//		ArrayList<Map<String, String>> info = new  ArrayList<>();
		FlowRule rule = ParsingFlowInformation.bulidFlowRule(hostmac);
		System.out.println(rule.getEthSrc() + "  :  " + rule.getEthDst() + "  :  " + rule.getIpv4Src() + "  :  " + rule.getIpv4Dst());
		String info = "[{ethSrc=\"" + rule.getEthSrc() + "\"}, {ethDst=\"" + rule.getEthDst() + "\"}, {ipv4Src=\"" + rule.getIpv4Src() + "\"}, {ipv4Dst=\"" + rule.getIpv4Dst() + "\"}]";
		RestfulTool.postFlowInfo(info);
		System.out.println(RestfulTool.postFlowInfo(info));
	}
	
	public static List<Map<String, String>> receiveInfo(String host) throws Exception{
		String flowinfo = RestfulTool.getFlowInfo();
		String str = flowinfo.substring(1, flowinfo.length()-3);
		String[] flowlist = str.split("<>,");
		for(int i = 0; i < flowlist.length; i++){
			List<Map<String, String>> flow = JsonTool.jsonStringToList(flowlist[i]);
			System.out.println(flow.get(0).get("ethSrc"));
			if(flow.get(0).get("ethSrc").equals(host)){
				return flow;
			}
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		EastWestComm.sendInfo("de:a2:c3:f1:8f:4a");
		System.out.println("-------------------------");
		System.out.println(EastWestComm.receiveInfo("de:a2:c3:f1:8f:4a"));
	}
}
