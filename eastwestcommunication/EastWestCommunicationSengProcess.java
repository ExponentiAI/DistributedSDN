package net.floodlightcontroller.eastwestcommunication;

public class EastWestCommunicationSengProcess{
	protected EastWestCommunication nm;
	
	public EastWestCommunicationSengProcess(EastWestCommunication nm){
		this.nm=nm;
	}
	
	public void sendPacketOutMessage(String info){
		System.out.println("send eastwest PacketOut Message");
		nm.getPacketOutMessage().doSendPacketOutMessage(nm, info);
	}
}
