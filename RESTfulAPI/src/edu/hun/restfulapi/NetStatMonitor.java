package edu.hun.restfulapi;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class NetStatMonitor {

	/*
	 * Get receiveBytes of a switch
	 */
	public static float getReceiveBytesOfSwitch(String DPID, String Port)
	{	
		float rbytes = 0;
		try {
			String out = RestfulTool.getStatOfSwitchByDPID(DPID, "port");
			JSONObject jo = JSONObject.fromObject(out);
			JSONArray arry = jo.getJSONArray("port");
			List<Map<String, String>> portinfo = JsonTool.jsonStringToList(arry.toString());
			for(Map<String, String> p : portinfo)
			{
				if(p.get("portNumber").equals(Port) == true)
				{
					rbytes = Float.parseFloat(p.get("receiveBytes"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rbytes;
	}
	/*
	 * Get transmitBytes of a switch
	 */
	public static float getTransmitBytesOfSwitch(String DPID, String Port)
	{	
		float rbytes = 0;
		try {
			String out = RestfulTool.getStatOfSwitchByDPID(DPID, "port");
			JSONObject jo = JSONObject.fromObject(out);
			JSONArray arry = jo.getJSONArray("port");
			List<Map<String, String>> portinfo = JsonTool.jsonStringToList(arry.toString());
			for(Map<String, String> p : portinfo)
			{
				if(p.get("portNumber").equals(Port) == true)
				{
					rbytes = Float.parseFloat(p.get("transmitBytes"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rbytes;
	}
}
