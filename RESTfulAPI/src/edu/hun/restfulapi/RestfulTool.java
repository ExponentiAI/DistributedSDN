package edu.hun.restfulapi;

public class RestfulTool{
	
		//设置控制器的IP地址，以实现控制器的
		public static String ControllerIP = "192.168.43.97";
		/*
		 * statType: aggregate, desc, flow, group, group-desc, group-features, meter, meter-config, 
		 * meter-features, port, port-desc, queue, table, features    
		 */
		public static String getStatOfAllSwitches(String statType){
			String targetURL = "http://"+ControllerIP+":8080/wm/core/switch/all/"+statType+"/json";
			return HttpURLRequest.doGet(targetURL);
		}
		/*
		 * DPID: (XX:XX:XX:XX:XX:XX:XX:XX) 
		 * statType: aggregate, desc, flow, group, group-desc, group-features, meter, meter-config, 
		 * meter-features, port, port-desc, queue, table, features 
		 */
		public static String getStatOfSwitchByDPID(String DPID, String statType){
			String targetURL = "http://"+ControllerIP+":8080/wm/core/switch/"+DPID+"/"+statType+"/json";
			return HttpURLRequest.doGet(targetURL);
	     }
		/*
		 * Get all links
		 */
		public static String getAllLinks(){
			String targetURL = "http://"+ControllerIP+":8080/wm/topology/links/json";
			return HttpURLRequest.doGet(targetURL);
	     }
		/*
		 * Get controller summary
		 */
		
		/**
		 * Get all devices
		 * @return
		 */
		public static String getDevices(){
			String targetURL = "http://"+ControllerIP+":8080/wm/device/";
			return HttpURLRequest.doGet(targetURL);		
		}
		
		public static String getCoSummary(){
			String targetURL = "http://"+ControllerIP+":8080/wm/core/controller/summary/json";
			return HttpURLRequest.doGet(targetURL);
	     }
		/*
		 * Get all switch DPIDs connected to the controller
		 */
		public static String getAllSwitches(){
			String targetURL = "http://"+ControllerIP+":8080/wm/core/controller/switches/json";
			return HttpURLRequest.doGet(targetURL);
	     }
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
		public static int addFlow(String DPID, String name, String table, String priority, String in_port, String eth_src, String eth_dst, String eth_type,String ipv4_src, String ipv4_dst,String active, String actions){
			String targetURL = "http://"+ControllerIP+":8080/wm/staticflowpusher/json";
			String input = "{\"switch\":\""+DPID+"\", \"name\":\""+name+"\", \"cookie\":\""+table+"\", \"priority\":\""+priority+"\", \"in_port\":\""+in_port+"\",\"eth_src\":\""+eth_src+"\",\"eth_dst\":\""+eth_dst+"\",\"eth_type\":\""+eth_type+"\",\"ipv4_src\":\""+ipv4_src+"\",\"ipv4_dst\":\""+ipv4_dst+"\",\"active\":\""+active+"\", \"actions\":\""+actions+"\"}";
			System.out.println("URL:  " + input);
			return HttpURLRequest.doPOST(targetURL, input);
		}     
		
		/**
		 * 
		 * @param srcdpid       the dpid of source switch
		 * @param dstdpid       the dpid of destination switch
		 * @return return the shortest path of the forwarding path
		 */
		public static  String getForwardingPath(String srcdpid, String dstdpid ){
			String targetURL = "http://" + ControllerIP + ":8080/wm/routing/paths/fast/" +  srcdpid + "/" + dstdpid + "/1" + "/json";
			return HttpURLRequest.doGet(targetURL);
		}
		
		/*
		 * Delete a flow
		 * "name":"flow-mod-1"
		 * return 1:success; 
		   return -1:false
		 */
		public static int deleteFlow(String sw){
			String targetURL = "http://"+ControllerIP+":8080/wm/staticflowpusher/json";
			String input = "{\"name\":\""+sw+"\"}";
			return HttpURLRequest.doDelete(targetURL, input);
		}
		/*
		 * List flows
		 * sw: "all" or a DPID
		 */
		public static String listFlows(String sw){
			String targetURL = "http://"+ControllerIP+":8080/wm/staticflowpusher/list/"+sw+"/json";
			return HttpURLRequest.doGet(targetURL);
		}
		/**
		 * get all flow information of the network partition
		 * @return
		 */
		public static String getAllFlows(){
			String targetURL = "http://"+ControllerIP+":8080/wm/networkchord/getentries/json";
			return HttpURLRequest.doGet(targetURL);
		}
		
		/*
		 * Clear flows
		 * sw: "all" or a DPID
		 */
		public static String clearFlows(String sw){
			String targetURL = "http://"+ControllerIP+":8080/wm/staticflowpusher/clear/"+sw+"/json";
			return HttpURLRequest.doGet(targetURL);
		}
}
