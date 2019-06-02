package net.floodlightcontroller.eastwestcommunication;

import net.floodlightcontroller.networkmeterlog.Log;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;

public class DealPacketInMessage {
	protected static DealPacketInMessage ns;
	public static byte[] switchID = {0x00,0x00,0x00,0x00,0x00,0x00};
	public static String neighbourController = "10.0.0.1";

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
		if(mess.length !=4){
			Log.error("length is not 4!");
			return;
		}
		if(mess[1].equals(switchID) && mess[2].equals(neighbourController) && mess[3].equals(SendPacketOutMessage.GetIpAddress())){
			
		}
	}
}