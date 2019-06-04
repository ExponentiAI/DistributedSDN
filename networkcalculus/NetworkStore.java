package net.floodlightcontroller.networkcalculus;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.projectfloodlight.openflow.protocol.OFPortStatsEntry;
import org.projectfloodlight.openflow.protocol.OFPortStatsReply;

import net.floodlightcontroller.core.IOFSwitchBackend;

public class NetworkStore {
	
	protected static NetworkStore ns;
	protected MinPlusConvolution mpc;
	public static ArrayList<Long> arriveCurve = new ArrayList<>();
	public static ArrayList<Long> departCurve = new ArrayList<>();
	public static int T = 0;
	public static double b;
	public static double jitter;
	
	//指定网络分区交换机的入端口port
	public static int port = 2;
	
	public NetworkStore(){
		mpc = new MinPlusConvolution();
		b=mpc.b5;
	}
	
	public static synchronized NetworkStore getInstance(){
		if(ns==null){
			ns = new NetworkStore();
		}
		return ns;	
	}
	
	public ArrayList<Long> handlePortStatsReply(OFPortStatsReply reply, IOFSwitchBackend sw){
		
		BigInteger arriveBite = null;
		List<OFPortStatsEntry> entries = reply.getEntries();
		for(OFPortStatsEntry e:entries){
			if(e.getPortNo().getPortNumber() == port){
//					System.out.println("----" + e.getRxBytes());
					arriveBite =  e.getRxBytes().getBigInteger();
					break;
			}
		}
		/**
		 * 到达曲线的数值累加
		 */
		if(arriveCurve.size() == 0){
			arriveCurve.add((long) (arriveBite.intValue()/8.0));
			departCurve.add((long) mpc.minPlusConvolution(T));
			T ++;
		}else if(arriveCurve.size() >= 1){
			arriveCurve.add((long) (arriveCurve.get(arriveCurve.size()-1) + arriveBite.intValue()/8.0));
			departCurve.add((long) mpc.minPlusConvolution(T));
			T ++;
		}
//		System.out.println("--arriveCurve--" + arriveCurve.get(T-1));
//		System.out.println("--departCurve--" + departCurve.get(T-1));
		
		if(T>=2) {
			jitter=(arriveCurve.get(T-1)-departCurve.get(T-1)-arriveCurve.get(T-2)+departCurve.get(T-2))/b;
//			System.out.println("--jitter in 1 second--" + jitter);
		}
//		if(jitter>=0.09)
//			mpc.changeService(true);
//		if(jitter<=0.05)
//			mpc.changeService(false);
		return departCurve;
	}

	public static Long getDepartCurve() {
		return departCurve.get(T-1);
	}

	public static Long getArriveCurve() {
		return arriveCurve.get(T-1);
	}
}
