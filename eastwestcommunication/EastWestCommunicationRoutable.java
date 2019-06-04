package net.floodlightcontroller.eastwestcommunication;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class EastWestCommunicationRoutable implements RestletRoutable {

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
        router.attach("/getinfo", EastWestCommunicationResource.class);
        router.attach("/postinfo", EastWestCommunicationResource.class);
        return router;
	}

	@Override
	public String basePath() {

		return "/wm/eastwest";
	}
	
}
