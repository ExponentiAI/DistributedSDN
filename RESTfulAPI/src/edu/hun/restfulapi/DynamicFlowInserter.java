package edu.hun.restfulapi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class DynamicFlowInserter {
	
	public static ArrayList<String> getAllFlows(){
		ArrayList<String> flows = new ArrayList<String>();
		String out = RestfulTool.listFlows("all");
		JSONObject jsonObject = JSONObject.fromObject(out); 
		for (Iterator<?> iter = jsonObject.keys(); iter.hasNext();)
        {
            String key = (String) iter.next();
            String value = jsonObject.get(key).toString();
            //JSONObject jsonObject1 = JSONObject.fromObject(value); 
            JSONArray arry = JSONArray.fromObject(value);
            for (Iterator<?> iter1 = arry.iterator(); iter1.hasNext();)
            {
            	JSONObject aa = (JSONObject) iter1.next();
            	for(Iterator<?> iter2 = aa.keys(); iter2.hasNext();)
            	{
            		String id = (String) iter2.next();
            		flows.add(id);
            	}
            }	
        }
		return flows;
	}
	/*
	 * Adding a flow rule in the specified switches, 
	 * generating a new flow entry through flow information and the specified switch forwarding port
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
	public static void addFlowByRules(FlowRule flowrule, ForwardingPathNode node){
		
		String in_port = node.getIn_Port();
		String out_port = node.getOut_Port();
		String switchdpid = node.getSwitchID();
		String eth_src = flowrule.getEthSrc();
		String eth_dst = flowrule.getEthDst();
		String ipv4_src = flowrule.getIpv4Src();
		String ipv4_dst = flowrule.getIpv4Dst();
		RestfulTool.addFlow(switchdpid, "flow_mod_" + in_port, "0", "100", in_port, eth_src, eth_dst, "0x0800", ipv4_src, ipv4_dst, "true", "output="+out_port);
		RestfulTool.addFlow(switchdpid, "flow_mod_" + out_port, "0", "100", out_port, eth_dst, eth_src, "0x0800", ipv4_dst, ipv4_src, "true", "output="+in_port);
	}
	/*
	 * The flow table is sent in sequence according to 
	 * the forwarding path node of the new network partition where the mobile terminal is located.
	 */
	public static void insertFlowByFwardingPath(List<ForwardingPathNode> forwardingpath, FlowRule flowrule){
		for(int i = 0; i < forwardingpath.size(); i++){
			addFlowByRules(flowrule, forwardingpath.get(i));
		}
	}
}
