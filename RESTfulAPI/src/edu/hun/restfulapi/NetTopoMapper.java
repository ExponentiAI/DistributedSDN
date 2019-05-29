package edu.hun.restfulapi;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class NetTopoMapper {
	/*
	 * Get ports of a switch by DPID
	 */
	public static ArrayList<String> getPortsOfSwitchByDPID(String DPID)
	{
		ArrayList<String> Ports = new ArrayList<String>();
		try {
			String out = RestfulTool.getStatOfSwitchByDPID(DPID, "port");
			JSONObject jo = JSONObject.fromObject(out);
			JSONArray arry = jo.getJSONArray("port");
			List<Map<String, String>> portinfo = JsonTool.jsonStringToList(arry.toString());
			for(Map<String, String> p : portinfo)
			{
				if(p.get("portNumber").equals("65534") == false && p.get("portNumber").equals("local") == false)
					Ports.add(p.get("portNumber"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Ports;
	}	
}
