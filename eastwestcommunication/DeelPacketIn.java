package net.floodlightcontroller.eastwestcommunication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.floodlightcontroller.networkmeterlog.Log;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;

public class DeelPacketIn {
	protected static DeelPacketIn ns;

	public DeelPacketIn(){
	}

	public static synchronized DeelPacketIn getInstance(){
		if(ns==null){
			ns = new DeelPacketIn();
		}
		return ns;	
	}

	public void handlePacketIn(IPacket iPacket){
		IPv4 ip = (IPv4)iPacket;
		Data data = (Data)ip.getPayload();
		String mess[] = new String(data.getData()).split("<>");
		if(mess.length !=2){
			Log.error("length is not 5!");
			return;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		Date currentTime = new Date();
	}
}