package edu.hun.restfulapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParsingFlowInformation {
	static List<Map<String, String>> flowinfos = null;
	 public static FlowRule bulidFlowRule(String hostmac) throws Exception{	
		FlowRule flowrule = new FlowRule();
		flowinfos = JsonTool.jsonStringToList(RestfulTool.getAllFlows());
		System.out.println("flowinfos:  " + flowinfos);
		for(Map<String, String> flow : flowinfos){
			if(flow.containsKey(hostmac)){
				List<Map<String, String>> info = JsonTool.jsonStringToList("[" + flow.get(hostmac) + "]");
				for( Map<String, String> l : info){
					if(l.containsKey("ethSrc"))
						flowrule.setEthSrc(l.get("ethSrc"));
					if(l.containsKey("ethDst"))
						flowrule.setEthDst(l.get("ethDst"));
					if(l.containsKey("ipv4Src"))
						flowrule.setIpv4Src(l.get("ipv4Src"));
					if(l.containsKey("ipv4Dst")) 
						flowrule.setIpv4Dst(l.get("ipv4Dst"));
				}
			}
		}
		System.out.println("flowrule:  " + flowrule.getEthSrc() + "  :  " + flowrule.getEthDst() + "  :  "
		                                                      + flowrule.getIpv4Src() + "  :  " + flowrule.getIpv4Dst());
		return flowrule;
	}
	
}
