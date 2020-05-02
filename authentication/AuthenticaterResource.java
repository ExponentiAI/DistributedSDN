package net.floodlightcontroller.authenticater;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.floodlightcontroller.authenticater.types.AuthenticateMessage;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;


 
public class AuthenticaterResource extends ServerResource {
    @Get("json")
    public List<AuthenticateMessage> retrieve() {
        IAuthenticaterService pihr = (IAuthenticaterService)getContext().getAttributes().get(IAuthenticaterService.class.getCanonicalName());
        List<AuthenticateMessage> l = new ArrayList<AuthenticateMessage>();
        l.addAll(Arrays.asList(pihr.getBuffer().snapshot()));
        return l;
    }
}