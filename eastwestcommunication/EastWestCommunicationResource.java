package net.floodlightcontroller.eastwestcommunication;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class EastWestCommunicationResource extends ServerResource{
	@Get
	public String getUser() {
		System.out.println("get:");
		return DealPacketInMessage.getList().toString();
	}
	
	@Post("json")
	public String addUser(String info) {
		EastWestCommunication.getNmt().sendPacketOutMessage(info);
		return "post flow info";
	}
}
