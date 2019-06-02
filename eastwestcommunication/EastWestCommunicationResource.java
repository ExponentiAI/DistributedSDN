package net.floodlightcontroller.eastwestcommunication;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class EastWestCommunicationResource extends ServerResource{
	
	@Post("json")
	public String postFlow(String user) {
		
		System.out.println("post:"+user);
		return "post user";
	}
	
	@Get("json")
	public String getFlow(){
		System.out.println("get:");
		return "get info";
	}
}
