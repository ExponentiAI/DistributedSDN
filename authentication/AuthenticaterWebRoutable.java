package net.floodlightcontroller.authenticater;
 
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;
 
public class AuthenticaterWebRoutable implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/authenticate", AuthenticaterResource.class);
        return router;
    }
 
    @Override
    public String basePath() {
        return "/sdn";
    }
}