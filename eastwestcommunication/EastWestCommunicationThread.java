package net.floodlightcontroller.eastwestcommunication;

public class EastWestCommunicationThread extends Thread {
	protected EastWestCommunication nm;
	
	public EastWestCommunicationThread(EastWestCommunication nm){
		this.nm=nm;
	}
	
	public void run(){
		while(true){
			System.out.println("start thread...");
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//发送PacketOut数据包
			nm.getPacketOutMessage().doSendPacketOutMessage(nm);
		}
		
	}

}
