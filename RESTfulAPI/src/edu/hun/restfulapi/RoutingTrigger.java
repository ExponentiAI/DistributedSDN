package edu.hun.restfulapi;

import java.util.List;
import java.util.Map;

public class RoutingTrigger{
	
	public static void main(String args[]) throws Exception{
		
	 	Map<String, Map<String, String>> hostinfo = TopoParams.getHostInfo();
		System.out.println(hostinfo);
		/*
		 * Add a flow
		 *  "switch":"00:00:00:00:00:00:00:01",
		    "name":"flow_mod_1",
		    "cookie":"0",
		    "priority":"32768",
		    "in_port":"1",
		    "eth_src":"00:00:00:00:00:00:00:01",
		    "eth_dst":"00:00:00:00:00:00:00:02",
		    "active":"true",
		    "actions":"output=flood"
		    return 1:success; 
		    return -1:false
		  	'{"switch":"00:00:00:00:00:00:00:03", "name": "priority":"100", "in_port":"1","eth_src":"00:00:00:00:00:01","eth_dst":"00:00:00:00:00:02", "eth_type":"0x0800", 
		  	"ipv4_src":"10.0.0.7","ipv4_dst":"10.23.0.5","active":"true", "actions":"output=2"}'
		 */
		System.out.println("---------------");
		System.out.println(RestfulTool.addFlow("00:00:00:00:00:00:00:02", "flow_mod_1", "0", "100", "1", "00:00:00:00:00:01", "00:00:00:00:00:02", "0x0800","10.0.0.03", "10.0.0.04","true",  "output="+"2"));
		
		System.out.println("--------FLOWS-------");
		System.out.println(RestfulTool.getAllFlows());
		
		System.out.println("------------Rule------------");
		FlowRule rule = ParsingFlowInformation.bulidFlowRule("c6:96:df:43:bd:0d");
		System.out.println(rule.getEthSrc() + " :  " + rule.getEthDst());
		
		System.out.println("------------Path------------");
		List<ForwardingPathNode> fowardingpath = TopoParams.parsingForwardingPath(rule, hostinfo);
		for( ForwardingPathNode node : fowardingpath){
			System.out.println("switch:  " + node.getSwitchID() + "  inport:  " + node.getIn_Port() + "  outport:  " + node.getOut_Port());
		}
		System.out.println("---------insertflow--------");
		DynamicFlowInserter.insertFlowByFwardingPath(fowardingpath, rule);
	}
}
