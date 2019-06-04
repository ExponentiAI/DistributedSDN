package net.floodlightcontroller.networkcalculus;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class NetworkCalculusWebRoutable implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/json", NetworkCalculusResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/wm/getnetworkcalculusresult";
	}
}
