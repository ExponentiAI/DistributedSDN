package edu.hun.restfulapi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopoParams {

	public static int numberofswitches = 0;
	public static String Links = null;
	public static ArrayList<String> DPIDs = null;
	public static Map<String, String> SwitchToPorts = null;
	public static Map<String, Map<String, String>> HostMap = null;
	
	public static String initLinks()
	{
		Links = RestfulTool.getAllLinks();
		return Links;
	}
	
	public static int initNumberOfSwitches()
	{
		List<Map<String, String>> switches = null;
		try {
			switches = JsonTool.jsonStringToList(RestfulTool.getAllSwitches());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(switches == null)
			numberofswitches = 0;
		else 
			numberofswitches = switches.size();
		return numberofswitches;
	}
	
	public static ArrayList<String> initDPIDs()
	{
		DPIDs = new ArrayList<String>();
		try {
			List<Map<String, String>> switches = JsonTool.jsonStringToList(RestfulTool.getAllSwitches());
			for(Map<String, String> m : switches)
			{
				DPIDs.add(m.get("switchDPID"));
				System.out.println("DPIDs: " + m.get("switchDPID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DPIDs;
	}
	
	public static Map<String, Map<String, String>> getHostInfo()
	{
		HostMap = new HashMap<String, Map<String, String>>();
		try {
			List<Map<String, String>> devices = JsonTool.jsonStringToList(RestfulTool.getDevices().substring(11, RestfulTool.getDevices().length()-1));
			for(Map<String, String> d : devices)
			{
				if(d.get("ipv4").equals("[]") == true)
					continue;

				Map<String, String> m = new HashMap<String, String>();
				List<Map<String, String>> list = JsonTool.jsonStringToList(d.get("attachmentPoint"));				
				for(Map<String, String> l : list)
				{
					m.put(l.get("switch"), l.get("port"));
					HostMap.put(d.get("mac"), m);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return HostMap;
	}
	
	public static void initSwitchPorts()
	{
		Map<String, String> SwitchToPorts  = new HashMap<String, String>();
		for(int i = 0;i<numberofswitches;i++)
		{
			String ports = String.valueOf(NetTopoMapper.getPortsOfSwitchByDPID(DPIDs.get(i)).size());
			SwitchToPorts.put(DPIDs.get(i), ports);
		}
	}
	
	public  static List<ForwardingPathNode> parsingForwardingPath(FlowRule rule, Map<String, Map<String, String>> hostinfo) throws Exception{
		
		List<ForwardingPathNode> forwardingpath =  new ArrayList<ForwardingPathNode>();
		String eth_src = rule.getEthSrc();
		String eth_dst = rule.getEthDst();
		String srcdpidstr = hostinfo.get("[\"" + eth_src + "\"]").toString();
		System.out.println("src" + srcdpidstr);
		String[] srcdpid = srcdpidstr.substring(1, srcdpidstr.length()-1).split("=");
		String dstdpidstr = hostinfo.get("[\"" + eth_dst + "\"]").toString();
		System.out.println("dst" + dstdpidstr);
		String[] dstdpid = dstdpidstr.substring(1, dstdpidstr.length()-1).split("=");
		System.out.println(srcdpid[0] + " : " + srcdpid[1] + " : " + dstdpid[0] + " : " + dstdpid[1]);
		
		String result = RestfulTool.getForwardingPath(srcdpid[0], dstdpid[0]);
		List<Map<String, String>> path = JsonTool.jsonStringToList(result.substring(11, result.length()-1));
		List<Map<String, String>> path1 = JsonTool.jsonStringToList(path.get(0).get("path"));	
		
		ForwardingPathNode node_head = new ForwardingPathNode();
		node_head.setSwitchID(srcdpid[0]);
		node_head.setIn_Port(srcdpid[1]);
		node_head.setOut_Port(path1.get(0).get("port"));
		forwardingpath.add(node_head);
		
		for(int i = 1; i < path1.size()-1; i=i+2){
			ForwardingPathNode node = new ForwardingPathNode();
			node.setIn_Port(path1.get(i).get("port"));
			node.setSwitchID(path1.get(i).get("dpid"));
			node.setOut_Port(path1.get(i+1).get("port"));
			forwardingpath.add(node);
		}
		
		ForwardingPathNode node_rear = new ForwardingPathNode();
		node_rear.setSwitchID(dstdpid[0]);
		node_rear.setIn_Port(path1.get(path1.size()-1).get("port"));
		node_rear.setOut_Port(dstdpid[1]);
		forwardingpath.add(node_rear);
		
		return forwardingpath;
	}
}
