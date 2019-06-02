package net.floodlightcontroller.eastwestcommunication;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class EastWestCommunicationRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router();
		router.attach("getinfo/json", EastWestCommunicationResource.class);
		router.attach("postinfo/json", EastWestCommunicationResource.class);
		// TODO Auto-generated method stub
		return router;
	}

	@Override
	public String basePath() {
		return "wm/eastwestcommunication";
	}

}
