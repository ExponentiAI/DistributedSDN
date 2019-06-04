package net.floodlightcontroller.eastwestcommunication;

import java.util.ArrayList;
import net.floodlightcontroller.networkmeterlog.Log;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;

public class DealPacketInMessage {
	protected static DealPacketInMessage ns;
	public static String neighbourController = "10.0.0.1";
	public static String localController = "10.0.0.2";
	public static  ArrayList<String> list = new ArrayList<String>();
	
	public DealPacketInMessage(){
	}

	public static synchronized DealPacketInMessage getInstance(){
		if(ns==null){
			ns = new DealPacketInMessage();
		}
		return ns;	
	}

	public void handlePacketIn(IPacket iPacket){
		IPv4 ip = (IPv4)iPacket;
		Data data = (Data)ip.getPayload();
		String mess[] = new String(data.getData()).split("<>");
		if(mess.length != 6){
			Log.error("length is not 6!");
			return;
		}
		if(mess[3].equals(localController) && mess[4].equals(neighbourController)){
			System.out.println("receive info from Post process：  " + mess[5]);
			System.out.println(!list.contains(mess[5]));
			if(!list.contains(mess[5] + "<>")){
				list.add(mess[5] + "<>");
			}	
		}
	}

	public static ArrayList<String> getList() {
		return list;
	}

	public static void setList(ArrayList<String> list) {
		DealPacketInMessage.list = list;
	}
	
}