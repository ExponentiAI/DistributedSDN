package edu.hun.restfulapi;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class MonitorTopology implements Runnable{
	
	public  Map<String, Map<String, String>> historydevices =  TopoParams.getHostInfo();
	public  Map<String, Map<String, String>> currentdevices = null;
	
	@Override
	public void run() {
		System.out.println("start monitortopo......");
		while(true){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentdevices	= TopoParams.getHostInfo();
			Set<String> currentkey = currentdevices.keySet();
			for( String key : currentkey){
				if(!historydevices.containsKey(key)){
					try {
						FlowRule rule = ParsingFlowInformation.bulidFlowRule("0e:04:75:a3:48:b0");
						Map<String, Map<String, String>> hostinfo = TopoParams.getHostInfo();
						List<ForwardingPathNode> fowardingpath = TopoParams.parsingForwardingPath(rule, hostinfo);
						DynamicFlowInserter.insertFlowByFwardingPath(fowardingpath, rule);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}  
	}
}
