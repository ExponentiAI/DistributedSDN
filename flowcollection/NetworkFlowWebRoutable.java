package net.floodlightcontroller.flow;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class NetworkFlowWebRoutable implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/getentries/json", NetworkFlowResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/networkchord";
	}
	
}
