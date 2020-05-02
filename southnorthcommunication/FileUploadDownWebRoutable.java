package net.floodlightcontroller.fileuploaddown;


import net.floodlightcontroller.restserver.RestletRoutable;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class FileUploadDownWebRoutable implements RestletRoutable {
    @Override
    public Restlet getRestlet(Context context) {
        Router router = new Router(context);
        router.attach("/file", FileUploadDownResource.class);
        return router;
    }

    @Override
    public String basePath() {
        return "/uploaddown";
    }
}
